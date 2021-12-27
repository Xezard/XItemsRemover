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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.xezard.items.remover.data.TrackingManager;

import java.util.List;

@AllArgsConstructor
public class ItemSpawnListener
implements Listener
{
    private TrackingManager trackingManager;

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event)
    {
        Item item = event.getEntity();

        ItemStack itemStack = item.getItemStack();

        ItemMeta itemMeta = itemStack.getItemMeta();

        if (itemMeta.hasLore())
        {
            List<String> lore = itemMeta.getLore();

            String tag = lore.get(lore.size() - 1);

            if (tag.equals("[pdd]"))
            {
                lore.remove(lore.size() - 1);

                itemMeta.setLore(lore);

                itemStack.setItemMeta(itemMeta);

                this.trackingManager.addEntity(item, true);
                return;
            }
        }

        this.trackingManager.addEntity(item, false);
    }
}
