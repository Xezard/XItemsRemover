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
import lombok.Setter;
import ru.xezard.configurations.Configuration;
import ru.xezard.configurations.ConfigurationField;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
public class MessagesConfiguration
extends Configuration
{
    public MessagesConfiguration(File folder)
    {
        super(folder.getAbsolutePath() + File.separator + "messages.yml");
    }

    @ConfigurationField("Chat-messages.You-dont-have-enough-permissions")
    private String youDontHaveEnoughPermissionsMessage = "§7[§c§li§7] §cУ вас недостаточно прав.";

    @ConfigurationField("Chat-messages.Plugin-successfully-reloaded")
    private String pluginReloadedMessage = "§7[§c§li§7] §aПлагин успешно перезагружен!";

    @ConfigurationField("Chat-messages.Help")
    private List<String> helpMessage = Arrays.asList
    (
            "§7§m╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴§7 [§6§lXItemsRemover§7] §7§m╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴",
            " §7'§d§l[]§7', '§e§l<>§7' §f- обязательные и не обязательные аргументы",
            "",
            " §6❯ §7/ir §e§l<§7help§e§l> §7- показать эту подсказку",
            " §6❯ §7/ir reload §f- перезагрузить конфигурацию плагина",
            "§7§m╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴╴"
    );
}