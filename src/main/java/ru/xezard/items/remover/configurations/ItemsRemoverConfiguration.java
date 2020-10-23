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

import lombok.Getter;
import ru.xezard.configurations.Configuration;
import ru.xezard.configurations.ConfigurationComments;
import ru.xezard.configurations.ConfigurationField;

import java.io.File;

@Getter
public class ItemsRemoverConfiguration
extends Configuration
{
    public ItemsRemoverConfiguration (File folder)
    {
        super(folder.getAbsolutePath() + File.separator + "config.yml");
    }

    @ConfigurationField("Items.Display-name-format")
    private String itemDisplayNameFormat = "§c⚠ {time}";

    @ConfigurationField("Items.Remove.Default-timer")
    @ConfigurationComments({ "# Specify default timer for item remove in ticks. 1 second = 20 ticks" })
    private long itemRemoveDefaultTimer = 20 * 30;

    @ConfigurationField("Items.Remove.Async")
    private boolean itemRemoveAsync = true;
}