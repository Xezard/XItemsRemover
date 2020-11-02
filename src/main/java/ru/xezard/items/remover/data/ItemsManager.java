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
import org.bukkit.entity.Item;
import org.bukkit.scheduler.BukkitTask;
import ru.xezard.items.remover.ItemsRemoverPlugin;
import ru.xezard.items.remover.configurations.Configurations;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemsManager
{
    private List<Map<Item, Long>> items = new ArrayList<Map<Item, Long>> (20)
    {{
        for (int i = 0; i < 20; i++)
        {
            this.add(i, new ConcurrentHashMap<> ());
        }
    }};

    private Map<Integer, Integer> ids = new HashMap<> ();

    private Configurations configurations;

    private ItemsRemoverPlugin plugin;

    private BukkitTask removerTask;

    private AtomicInteger tick = new AtomicInteger(0);

    public ItemsManager(Configurations configurations, ItemsRemoverPlugin plugin)
    {
        this.configurations = configurations;

        this.plugin = plugin;
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

            long time = entry.getValue();

            if (time > 0)
            {
                entry.setValue(time - 1);

                item.setCustomName(this.configurations.get("config.yml").getString("Items.Display-name-format")
                        .replace("{time}", Long.toString(time)));
                continue;
            }

            this.removeItem(item);

            item.remove();
        }
    }

    public void updateTimeForAll(long time)
    {
        this.items.forEach((map) ->
        {
            map.replaceAll((item, itemTime) -> time);
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

    public void addItem(Item item, long time)
    {
        if (this.ids.containsKey(item.getEntityId()))
        {
            return;
        }

        int currentTick = this.tick.get();

        item.setCustomNameVisible(true);

        this.items.get(currentTick).put(item, time);
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

    public void setItemTimer(Item item, long time)
    {
        int tick = this.ids.getOrDefault(item.getEntityId(), -1);

        if (tick == -1)
        {
            return;
        }

        this.items.get(tick).replace(item, time);
    }

    public void mergeItems(Item merged, Item target)
    {
        this.removeItem(merged);
        this.setItemTimer(target, this.configurations.get("config.yml").getLong("Items.Remove-timer.Default"));
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