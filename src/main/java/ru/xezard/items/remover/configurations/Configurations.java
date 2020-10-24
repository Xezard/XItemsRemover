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
package ru.xezard.items.remover.configurations;

import java.io.File;
import java.io.IOException;
import java.util.*;

import com.google.common.collect.Lists;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class Configurations
{
	private Map<String, Map.Entry<FileConfiguration, File>> configurations = new HashMap<> ();

	private List<String> configurationsNames;

	private Plugin plugin;

	public Configurations(Plugin plugin, String... configurationsNames)
	{
		this.plugin = plugin;

		this.configurationsNames = Lists.newArrayList(configurationsNames);

		this.loadConfigurations();
	}

	private File generateDefaultFile(String name)
	{
		File file = new File(this.plugin.getDataFolder(), name);

		if (!file.exists())
		{
			this.plugin.saveResource(name, false);
		}

		return file;
	}

	public void loadConfigurations()
	{
		for (String configurationName : this.configurationsNames)
		{
			if (this.configurations.containsKey(configurationName))
			{
				continue;
			}

			File configurationFile = this.generateDefaultFile(configurationName);

			FileConfiguration configuration = YamlConfiguration.loadConfiguration(configurationFile);

			this.configurations.put(configurationName, new AbstractMap.SimpleEntry<> (configuration, configurationFile));
		}
	}

	public void reloadConfigurations()
	{
		this.configurations.clear();

		this.loadConfigurations();
	}

	private Optional<Map.Entry<FileConfiguration, File>> getEntry(String configurationName)
	{
		return Optional.ofNullable(this.configurations.get(configurationName));
	}

	public FileConfiguration get(String configurationName)
	{
		return this.getEntry(configurationName)
				   .map(Map.Entry::getKey)
				   .orElse(null);
	}

	private File getFile(String configurationName)
	{
		return this.getEntry(configurationName)
				   .map(Map.Entry::getValue)
				   .orElse(null);
	}

	public void save(String configurationName)
	{
		this.getEntry(configurationName).ifPresent((entry) ->
		{
			try {
				entry.getKey().save(entry.getValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}
}