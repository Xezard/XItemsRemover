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

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.xezard.items.remover.commands.ItemsRemoverCommand;
import ru.xezard.items.remover.configurations.Configurations;
import ru.xezard.items.remover.data.ItemsManager;
import ru.xezard.items.remover.listeners.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

public class ItemsRemoverPlugin
extends JavaPlugin
{
    private Configurations configurations = new Configurations(this, "config.yml", "messages.yml");

    private ItemsManager itemsManager;

    @Override
    public void onEnable()
    {
        new Metrics(this, 9285);

        this.configurations.loadConfigurations();

        this.itemsManager = new ItemsManager(this.configurations, this, this.getLogger());
        this.itemsManager.load(this.configurations.get("config.yml"));

        this.registerListeners();
        this.registerCommands();

        this.itemsManager.startRemoverTask();

        this.checkUpdates(this.getLogger());
    }

    @Override
    public void onDisable()
    {
        this.configurations = null;
        this.itemsManager = null;
    }

    private void registerListeners()
    {
        PluginManager pluginManager = Bukkit.getPluginManager();

        pluginManager.registerEvents(new ChunkLoadListener(this.itemsManager), this);
        pluginManager.registerEvents(new EntityPickupItemListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemDespawnListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemMergeListener(this.itemsManager), this);
        pluginManager.registerEvents(new ItemSpawnListener(this.itemsManager), this);
        pluginManager.registerEvents(new PlayerDeathListener(this.configurations, this.itemsManager), this);
        pluginManager.registerEvents(new ProjectileHitListener(this.itemsManager), this);
    }

    private void registerCommands()
    {
        this.getCommand("itemsremover").setExecutor(new ItemsRemoverCommand
        (
                this.configurations,
                this
        ));
    }

    private void checkUpdates(Logger logger)
    {
        Bukkit.getScheduler().runTaskAsynchronously(this, () ->
        {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=85123").openStream();
                 Scanner scanner = new Scanner(inputStream))
            {
                if (!scanner.hasNext())
                {
                    logger.warning("Can't check for updates: no respond from spigotmc.org API!");
                    return;
                }
                
                String newVersion = scanner.next(),
                       currentVersion = this.getDescription().getVersion();

                if (currentVersion.equals(newVersion))
                {
                    logger.info(ChatColor.GREEN + "The latest version of the plugin is used.");
                    return;
                }

                logger.info(ChatColor.YELLOW + "Found a new version '" + newVersion + "' of XItemsRemover!");
                logger.info(ChatColor.YELLOW + "Current version: '" + currentVersion + "'");
                logger.info(ChatColor.YELLOW + "Check out new version here: " + ChatColor.GREEN +
                        "https://www.spigotmc.org/resources/xitemsremover.85123/");
            } catch (IOException exception) {
                logger.warning("Can't check for updates: " + exception.getMessage());
            }
        });
    }

    public void reload()
    {
        this.itemsManager.clearData();

        this.configurations.reloadConfigurations();

        this.itemsManager.load(this.configurations.get("config.yml"));
        this.itemsManager.updateTimeForAll();
    }
}
