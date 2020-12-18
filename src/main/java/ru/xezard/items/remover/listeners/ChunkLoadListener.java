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
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import ru.xezard.items.remover.configurations.Configurations;
import ru.xezard.items.remover.data.ItemsManager;

@AllArgsConstructor
public class ChunkLoadListener
implements Listener
{
    private ItemsManager itemsManager;

    @EventHandler
    public void onLoad(ChunkLoadEvent event)
    {
        for (Entity entity : event.getChunk().getEntities())
        {
            if (!(entity instanceof Item))
            {
                continue;
            }

            this.itemsManager.addItem((Item) entity, false);
        }
    }
}