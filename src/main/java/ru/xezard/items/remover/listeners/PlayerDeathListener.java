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

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import ru.xezard.items.remover.configurations.Configurations;
import ru.xezard.items.remover.data.TrackingManager;

@AllArgsConstructor
public class PlayerDeathListener
implements Listener
{
    private Configurations configurations;

    private TrackingManager trackingManager;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(PlayerDeathEvent event)
    {
        if (event.getKeepInventory() || 
            event.getDrops().isEmpty() ||
            this.configurations.get("config.yml")
                               .getStringList("Restricted-worlds")
                               .contains(event.getEntity().getWorld().getName())
           )
        {
            return;
        }

        List<ItemStack> drop = event.getDrops(),
                        items = new ArrayList<> (drop);

        drop.clear();
        drop.addAll
        (
                items.stream()
                     .peek((itemStack) ->
                     {
                         if (!this.trackingManager.tracked(itemStack.getType().name())) 
                         {
                             return;
                         }

                         ItemMeta itemMeta = itemStack.getItemMeta();

                         List<String> lore = itemMeta.hasLore() ? itemMeta.getLore() : new ArrayList<> ();

                         lore.add("[pdd]");

                         itemMeta.setLore(lore);
                       
                         itemStack.setItemMeta(itemMeta);
                     }).collect(Collectors.toList())
        );
    }
}
