package me.leafs.cf.filters;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.leafs.cf.ChatFilter;
import me.leafs.cf.filters.actions.FilterAction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.util.EnumChatFormatting;

import java.util.regex.Pattern;

import static me.leafs.cf.utils.BitMask.isAnd;

@Data
@RequiredArgsConstructor
public class BaseFilter {
    private String pattern;
    private String replacement;

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
        String output = input;

        // check if the output is supposed to be hidden
        // if it isn't then replace using regex
        if (isAnd(flags, Flag.HIDE)) {
            output = "";
        } else if (isAnd(flags, Flag.REPLACE) && replacement != null && replacement.isEmpty()) {
            // do regex replacement even if not on regex detection, could create interesting outcomes
            output = toPattern().matcher(output).replaceAll(replacement);
        }

        // run an action if there's one to run (and enabled)
        if (isAnd(flags, Flag.ACTION) && action != null) {
            action.performAction(toPattern().matcher(input));
        }

        return output;
    }

    private Pattern toPattern() {
        // get the regex flags from config
        int flags = ChatFilter.instance.getConfig().getRegexFlags().stream()
                .reduce((total, i) -> total | i)
                .orElse(Pattern.CASE_INSENSITIVE);

        return Pattern.compile(pattern, flags);
    }

    public static class Flag {
        public static final int REPLACE = 0x01;
        public static final int HIDE    = 0x02;
        public static final int ACTION  = 0x04;
    }
}
