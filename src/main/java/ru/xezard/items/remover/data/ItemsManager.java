/*
 *  This file is part of XItemsRemover,
 *  licensed under the Apache License, Version 2.0.
 *
 *  Copyright (c) Xezard (Zotov Ivan)
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package ru.xezard.items.remover.data;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;
import ru.xezard.items.remover.ItemsRemoverPlugin;
import ru.xezard.items.remover.configurations.Configurations;
import ru.xezard.items.remover.utils.Chat;
import ru.xezard.items.remover.utils.Materials;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class ItemsManager
{
    private List<Map<Item, Long>> items = new ArrayList<Map<Item, Long>> (20)
    {{
        for (int i = 0; i < 20; i++)
        {
            this.add(i, new ConcurrentHashMap<> ());
        }
    }};

    private NavigableMap<Long, String> displayNames = new TreeMap<> ();

    private Map<Integer, Integer> ids = new HashMap<> ();

    private Map<Material, DropData> dropData = new HashMap<> ();

    private Configurations configurations;

    private ItemsRemoverPlugin plugin;

    private BukkitTask removerTask;

    private AtomicInteger tick = new AtomicInteger(0);

    private Logger logger;

    public ItemsManager(Configurations configurations, ItemsRemoverPlugin plugin, Logger logger)
    {
        this.configurations = configurations;

        this.plugin = plugin;

        this.logger = logger;
    }

    public void load(FileConfiguration config)
    {
        ConfigurationSection customMaterialsSection = config.getConfigurationSection("Items.Remove-timer.Custom-materials"),
                             displayNamesSection = config.getConfigurationSection("Items.Display-name-formats");

        if (customMaterialsSection == null)
        {
            this.logger.warning("Custom drop data was not loaded.");
            return;
        }

        for (String materialName : customMaterialsSection.getKeys(false))
        {
            String sectionKey = "Items.Remove-timer.Custom-materials." + materialName + ".",
                   displayName = config.getString(sectionKey + "Display-name");

            Material material = Material.matchMaterial(materialName);

            if (material == null)
            {
                this.logger.warning("Can't find material with name '" + materialName + "'. Custom drop section has been skipped!");
                continue;
            }

            long timer = config.getLong(sectionKey + "Timer", -1);

            this.dropData.put(material, new DropData(displayName, timer));
        }

        if (displayNamesSection == null) 
        {
            this.logger.warning("Can't find display names section for items!");
            return;
        }

        for (String timerString : displayNamesSection.getKeys(false))
        {
            this.displayNames.put(Long.parseLong(timerString), config.getString("Items.Display-name-formats." + timerString));
        }
    }

    private synchronized void clearItems()
    {
        int tick = this.tick.incrementAndGet();

        if (tick == 20)
        {
            tick = 0;
            this.tick.set(0);
        }

        Map<Item, Long> items = this.items.get(tick);

        for (Map.Entry<Item, Long> entry : items.entrySet())
        {
            Item item = entry.getKey();

            // fix for slimefun
            if (item.hasMetadata("no_pickup")) {
                this.removeItem(item);
                continue;
            }

            ItemStack itemStack = item.getItemStack();

            ItemMeta itemMeta = itemStack.getItemMeta();

            Material material = itemStack.getType();

            DropData data = this.dropData.get(material);

            String materialName = Materials.toString(itemStack.getType()),
                   displayName = itemMeta.hasDisplayName() ? itemMeta.getDisplayName() :
                           data == null ? materialName : data.getCustomName() == null ?
                                   materialName : data.getCustomName();

            long time = entry.getValue();

            if (time > 0)
            {
                entry.setValue(time - 1);

                item.setCustomName(Chat.colorize(Optional.ofNullable(this.displayNames.ceilingEntry(time).getValue())
                        .orElse(this.displayNames.firstEntry().getValue()))
                        .replace("{time}", Long.toString(time))
                        .replace("{amount}", Integer.toString(itemStack.getAmount()))
                        .replace("{display_name}", displayName)));
                continue;
            }

            this.removeItem(item);

            item.remove();
        }
    }

    public void updateTimeForAll()
    {
        this.items.forEach((map) ->
        {
            map.replaceAll((item, itemTime) ->
            {
                return this.getTimeByMaterial(item.getItemStack().getType(), false);
            });
        });
    }

    public void startRemoverTask()
    {
        if (this.configurations.get("config.yml").getBoolean("Items.Remove.Async"))
        {
            this.removerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::clearItems, 0, 1);
        } else {
            this.removerTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this::clearItems, 0, 1);
        }
    }

    private long getTimeByMaterial(Material material, boolean afterDeath)
    {
        DropData data = this.dropData.get(material);

        FileConfiguration config = this.configurations.get("config.yml");

        long time = afterDeath ? config.getLong("Items.Remove-timer.After-player-death") :
                                 config.getLong("Items.Remove-timer.Default");

        if (data != null)
        {
            long timer = data.getTimer();

            if (timer > 0)
            {
                time = timer;
            }
        }

        return time;
    }

    public void addItem(Item item, boolean afterDeath)
    {
        if (this.ids.containsKey(item.getEntityId()) || 
            item.hasMetadata("no_pickup")) // fix for slimefun
        {
            return;
        }

        item.setCustomNameVisible(true);

        int currentTick = this.tick.get();

        this.items.get(currentTick).put(item, this.getTimeByMaterial(item.getItemStack().getType(), afterDeath));
        this.ids.put(item.getEntityId(), currentTick);
    }

    public void removeItem(Item item)
    {
        int tick = this.ids.getOrDefault(item.getEntityId(), -1);

        if (tick == -1)
        {
            return;
        }

        this.items.get(tick).remove(item);
        this.ids.remove(item.getEntityId());
    }

    public void setItemTimer(Item item)
    {
        int tick = this.ids.getOrDefault(item.getEntityId(), -1);

        if (tick == -1)
        {
            return;
        }

        this.items.get(tick).replace(item, this.getTimeByMaterial(item.getItemStack().getType(), false));
    }

    public void mergeItems(Item merged, Item target)
    {
        this.removeItem(merged);
        this.setItemTimer(target);
    }

    public void clearData()
    {
        this.displayNames.clear();
        this.dropData.clear();
    }

    public void clear()
    {
        this.items.forEach(Map::clear);
    }

    public void stopRemoverTask()
    {
        this.removerTask.cancel();
    }
}
