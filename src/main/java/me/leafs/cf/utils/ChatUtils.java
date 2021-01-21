package me.leafs.cf.utils;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {
    // stolen straight from EnumChatFormatting (why'd they make it fucking private?)
    public static final Pattern COLOR_FORMAT = Pattern.compile("(?i)[§&][0-9A-FK-OR]");

    public static void printChat(String message) {
        GuiNewChat chatGUI = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        message = ChatUtils.color(message);
        chatGUI.printChatMessage(new ChatComponentText(message));
    }

    public static String color(String input) {
        Matcher matcher = COLOR_FORMAT.matcher(input);

        while (matcher.find()) {
            String fullMatch = matcher.group(0);
            input = input.replace(fullMatch, ChatFormatting.getByChar(fullMatch.charAt(1)).toString());
        }

        return input;
    }

    /**
     * simple alias method
     */
    public static void chatAsPlayer(String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }
}
