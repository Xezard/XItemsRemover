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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import ru.xezard.items.remover.ItemsRemoverPlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ItemsManager
{
    // <item, timer in ticks>
    private Map<Item, Long> items = new ConcurrentHashMap<> ();

    private BukkitTask removerTask;

    private FileConfiguration configuration;

    private ItemsRemoverPlugin plugin;

    public ItemsManager(FileConfiguration configuration, ItemsRemoverPlugin plugin)
    {
        this.configuration = configuration;

        this.plugin = plugin;
    }

    private void clearItems()
    {
        for (Map.Entry<Item, Long> entry : this.items.entrySet())
        {
            Item item = entry.getKey();

            long time = entry.getValue();

            if (time > 0)
            {
                entry.setValue(time - 1);

                item.setCustomName(this.configuration.getString("Items.Display-name-format")
                        .replace("{time}", Long.toString((time + 20) / 20)));
                continue;
            }

            this.removeItem(item);

            item.remove();
        }
    }

    public void startRemoverTask()
    {
        Bukkit.getWorlds().forEach((world) ->
        {
            world.getEntities()
                 .stream()
                 .filter(Item.class::isInstance)
                 .peek((item) -> item.setCustomNameVisible(true))
                 .forEach((entity) -> this.addItem((Item) entity, this.configuration.getLong("Items.Remove.Default-timer")));
        });

        if (this.configuration.getBoolean("Items.Remove.Async"))
        {
            this.removerTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, this::clearItems, 0, 1);
        } else {
            this.removerTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this::clearItems, 0, 1);
        }
    }

    public void addItem(Item item, long timeInTicks)
    {
        this.items.put(item, timeInTicks);
    }

    public void removeItem(Item item)
    {
        this.items.remove(item);
    }

    public void setItemTimer(Item item, long timeInTicks)
    {
        this.items.replace(item, timeInTicks);
    }

    public void mergeItems(Item merged, Item target)
    {
        this.removeItem(merged);
        this.setItemTimer(target, this.configuration.getLong("Items.Remove.Default-timer"));
    }

    public void clear()
    {
        this.items.clear();
    }

    public void stopRemoverTask()
    {
        this.removerTask.cancel();
    }
}