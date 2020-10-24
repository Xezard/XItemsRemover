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
package ru.xezard.items.remover.commands;

import lombok.AllArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import ru.xezard.items.remover.ItemsRemoverPlugin;
import ru.xezard.items.remover.utils.Chat;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public class ItemsRemoverCommand
implements CommandExecutor
{
    private static final List<String> CONSOLE_HELP_MESSAGE = Arrays.asList
    (
            "---------------------- [XItemsRemover] -----------------------",
            "'[]', '<>' - required and optional arguments",
            "",
            "> 'ir <help>' - show help page",
            "> 'ir reload' - reload plugin"
    );

    private static final String CONSOLE_PLUGIN_RELOADED_MESSAGE = "[XItemsRemover] Plugin successfully reloaded!";

    private FileConfiguration messagesConfiguration;

    private ItemsRemoverPlugin plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] arguments)
    {
        switch (arguments.length)
        {
            case 0:
                this.sendHelp(sender);
                break;

            case 1:
                switch (arguments[0].toLowerCase())
                {
                    case "help":
                        this.sendHelp(sender);
                        break;

                    case "reload":
                        this.reload(sender);
                        break;
                }
                break;
        }

        return true;
    }

    private void reload(CommandSender sender)
    {
        if (!sender.hasPermission("items.remover.commands.reload"))
        {
            sender.sendMessage(this.messagesConfiguration.getString("Chat-messages.You-dont-have-enough-permissions"));
            return;
        }

        this.plugin.reload();

        Chat.message(sender, this.messagesConfiguration.getString("Chat-messages.Plugin-reloaded"), CONSOLE_PLUGIN_RELOADED_MESSAGE);
    }

    private void sendHelp(CommandSender sender)
    {
        if (!sender.hasPermission("items.remover.commands.help"))
        {
            sender.sendMessage(this.messagesConfiguration.getString("Chat-messages.You-dont-have-enough-permissions"));
            return;
        }

        Chat.message(sender, this.messagesConfiguration.getStringList("Chat-messages.Help"), CONSOLE_HELP_MESSAGE);
    }
}