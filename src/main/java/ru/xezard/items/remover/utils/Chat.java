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
package ru.xezard.items.remover.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@UtilityClass
public class Chat
{
    public void message(CommandSender sender, Iterable<String> playerMessage, Iterable<String> consoleMessage)
    {
        if (sender instanceof Player)
        {
            playerMessage.forEach(sender::sendMessage);
            return;
        }

        consoleMessage.forEach(sender::sendMessage);
    }

    public void message(CommandSender sender, String playerMessage, String consoleMessage)
    {
        sender.sendMessage(sender instanceof Player ? playerMessage : consoleMessage);
    }
}