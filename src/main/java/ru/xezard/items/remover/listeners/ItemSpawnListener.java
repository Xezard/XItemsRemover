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
package ru.xezard.items.remover.listeners;

import lombok.AllArgsConstructor;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import ru.xezard.items.remover.configurations.ItemsRemoverConfiguration;
import ru.xezard.items.remover.data.ItemsManager;

@AllArgsConstructor
public class ItemSpawnListener
implements Listener
{
    private ItemsRemoverConfiguration configuration;

    private ItemsManager itemsManager;

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        Item item = event.getEntity();

        item.setCustomNameVisible(true);

        this.itemsManager.addItem(item, this.configuration.getItemRemoveDefaultTimer());
    }
}