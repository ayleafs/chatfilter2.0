package me.leafs.cf.filters;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.leafs.cf.ChatFilter;
import me.leafs.cf.filters.actions.FilterAction;
import me.leafs.cf.utils.ChatUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;

import java.util.UUID;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static me.leafs.cf.utils.BitMask.isAnd;

@Data
@RequiredArgsConstructor
public class BaseFilter {
    @NonNull private String pattern;
    @NonNull private String replacement;

    private final UUID id;

    private FilterStyle style = FilterStyle.REGEX;
    private FilterAction action = null;

    private int flags = 0x0;

    private String server = null;

    public boolean isEnabled() {
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();

        // returns true if the server isn't specified (global) or the current server is right
        return server == null || (data != null && data.serverIP.equalsIgnoreCase(server));
    }

    public boolean matches(String chatMessage) {
        // if the style isn't in regex mode
        // then use either the EQUALS mode or CONTAINS mode
        if (style != FilterStyle.REGEX) {
            return style == FilterStyle.EQUALS ?
                    chatMessage.equalsIgnoreCase(pattern) :
                    chatMessage.contains(pattern);
        }

        // strip the chat message to make it raw ;)
        chatMessage = EnumChatFormatting.getTextWithoutFormattingCodes(chatMessage);

        // match against the pattern
        return toPattern().matcher(chatMessage).matches();
    }

    public String process(String input) {
        input = EnumChatFormatting.getTextWithoutFormattingCodes(input);
        String output = input;

        // check if the output is supposed to be hidden
        // if it isn't then replace using regex
        if (isAnd(flags, Flag.HIDE)) {
            output = "";
        } else if (isAnd(flags, Flag.REPLACE) && replacement != null && !replacement.isEmpty()) {
            // do regex replacement even if not on regex detection, could create interesting outcomes
            try {
                output = toPattern().matcher(output).replaceAll(ChatUtils.color(replacement));
            } catch (PatternSyntaxException e) {
                e.printStackTrace();
                output = replacement;
            }
        }

        // run an action if there's one to run (and enabled)
        if (isAnd(flags, Flag.ACTION) && action != null) {
            ChatFilter.instance.getActionQueue().queueAction(this, style == FilterStyle.REGEX ? toPattern().matcher(input) : null);
        }

        return output;
    }

    private Pattern toPattern() {
        // get the regex flags from config
        int flags = ChatFilter.instance.getConfig().getRegexFlags().stream()
                .reduce((total, i) -> total | i)
                .orElse(Pattern.CASE_INSENSITIVE);

        Pattern output;
        try {
            output = Pattern.compile(pattern, flags);
        } catch (PatternSyntaxException e) {
            e.printStackTrace();
            // easy fix
            output = Pattern.compile(Pattern.quote(pattern));
        }

        return output;
    }

    public static class Flag {
        public static final int REPLACE = 0x1;
        public static final int HIDE    = 0x2;
        public static final int ACTION  = 0x4;
    }
}
