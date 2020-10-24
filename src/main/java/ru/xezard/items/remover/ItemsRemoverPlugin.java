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
package ru.xezard.items.remover;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.xezard.items.remover.commands.ItemsRemoverCommand;
import ru.xezard.items.remover.configurations.Configurations;
import ru.xezard.items.remover.data.ItemsManager;
import ru.xezard.items.remover.listeners.ItemDespawnListener;
import ru.xezard.items.remover.listeners.ItemMergeListener;
import ru.xezard.items.remover.listeners.ItemSpawnListener;
import ru.xezard.items.remover.listeners.EntityPickupItemListener;

public class ItemsRemoverPlugin
extends JavaPlugin
{
    private Configurations configurations = new Configurations(this, "config.yml", "messages.yml");

    private ItemsManager itemsManager;

    @Override
    public void onEnable()
    {
        this.configurations.loadConfigurations();

        this.itemsManager = new ItemsManager(this.configurations.get("config.yml"), this);

        this.registerListeners();
        this.registerCommands();

        this.itemsManager.startRemoverTask();
    }

    @Override
    public void onDisable()
    {
        this.itemsManager = null;
    }

    private void registerListeners()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new EntityPickupItemListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemDespawnListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemMergeListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemSpawnListener(this.getConfig(), this.itemsManager), this);
    }

    private void registerCommands()
    {
        this.getCommand("itemsremover").setExecutor(new ItemsRemoverCommand
        (
                this.configurations.get("messages.yml"),
                this
        ));
    }

    public void reload()
    {
        this.itemsManager.clear();
        this.itemsManager.stopRemoverTask();

        this.configurations.reloadConfigurations();

        this.itemsManager.startRemoverTask();
    }
}