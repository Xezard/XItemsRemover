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
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;

@UtilityClass
public class Chat
{
    public static final Pattern RAW_HEX_PATTERN = Pattern.compile("\\{#[A-Fa-f0-9]{6}\\}");

    public void message(CommandSender sender, Iterable<String> playerMessage, Iterable<String> consoleMessage)
    {
        if (sender instanceof Player)
        {
            playerMessage.forEach((string) -> sender.sendMessage(colorize(string)));
            return;
        }

        consoleMessage.forEach(sender::sendMessage);
    }

    public void message(CommandSender sender, String playerMessage, String consoleMessage)
    {
        sender.sendMessage(sender instanceof Player ? colorize(playerMessage) : consoleMessage);
    }

    public static List<String> colorize(List<String> strings)
    {
        return strings.stream()
                      .map(Chat::colorize)
                      .collect(Collectors.toList());
    }

    public static String colorize(String string)
    {
        List<String> formatCodes = Arrays.asList("&k", "&l", "&m", "&n", "&o", "&r");

        for (String code : formatCodes)
        {
            string = string.replaceAll(code, ChatColor.getByChar(code.charAt(1)).toString());
        }

        string = hex(string);

        return string;
    }

    private static String hex(String string)
    {
        if (!Bukkit.getVersion().contains("1.16"))
        {
            return string;
        }

        Matcher matcher = RAW_HEX_PATTERN.matcher(string);

        int hexAmount = 0;

        while (matcher.find())
        {
            matcher.region(matcher.end() - 1, string.length());

            ++hexAmount;
        }

        int startIndex = 0;

        for (int hexIndex = 0; hexIndex < hexAmount; ++hexIndex)
        {
            int msgIndex = string.indexOf("{#", startIndex);

            String hex = string.substring(msgIndex + 1, msgIndex + 8);

            startIndex = msgIndex + 2;

            string = string.replace("{" + hex + "}", ChatColor.of(hex) + "");
        }

        return string;
    }
}