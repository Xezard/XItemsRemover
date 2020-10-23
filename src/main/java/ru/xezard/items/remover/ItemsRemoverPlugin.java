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
import ru.xezard.items.remover.configurations.ItemsRemoverConfiguration;
import ru.xezard.items.remover.configurations.MessagesConfiguration;
import ru.xezard.items.remover.data.ItemsManager;
import ru.xezard.items.remover.listeners.ItemDespawnListener;
import ru.xezard.items.remover.listeners.ItemMergeListener;
import ru.xezard.items.remover.listeners.ItemSpawnListener;
import ru.xezard.items.remover.listeners.PlayerPickupItemListener;

public class ItemsRemoverPlugin
extends JavaPlugin
{
    private ItemsRemoverConfiguration configuration = new ItemsRemoverConfiguration(this.getDataFolder());
    private MessagesConfiguration messagesConfiguration = new MessagesConfiguration(this.getDataFolder());

    private ItemsManager itemsManager;

    @Override
    public void onEnable()
    {
        this.loadConfigurations();

        this.itemsManager = new ItemsManager(this.configuration, this);

        this.registerListeners();
        this.registerCommands();

        this.itemsManager.startRemoverTask();
    }

    @Override
    public void onDisable()
    {
        this.messagesConfiguration = null;
        this.itemsManager = null;
    }

    private void registerListeners()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ItemDespawnListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemMergeListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemSpawnListener(this.configuration, this.itemsManager), this);
        pluginManager.registerEvents(new PlayerPickupItemListener(this.itemsManager), this);
    }

    private void registerCommands()
    {
        this.getCommand("itemsremover").setExecutor(new ItemsRemoverCommand
        (
                this.messagesConfiguration,
                this
        ));
    }

    private void loadConfigurations()
    {
        this.configuration.load();
        this.messagesConfiguration.load();
    }

    public void reload()
    {
        this.itemsManager.clear();
        this.itemsManager.stopRemoverTask();

        this.loadConfigurations();

        this.itemsManager.startRemoverTask();
    }
}