package me.leafs.cf.filters.actions;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import me.leafs.cf.utils.ChatUtils;
import net.minecraft.client.Minecraft;

import java.util.regex.Matcher;

@Data
@RequiredArgsConstructor
public class FilterAction {
    private String chat;
    private boolean confirm = false;

    public void performAction(Matcher matcher) {
        // no!
        if (Minecraft.getMinecraft().theWorld == null) {
            return;
        }

        String toSend = chat;

        // if the matcher is null then
        // the filter is not a REGEX
        if (matcher != null) {
            // it should match but
            // without a call made to this, we can't
            // execute the code below
            if (!matcher.matches()) {
                return;
            }

            // setup and format the chat message ( hello $1 ) -> ( hello REPLACED )
            for (int i = 0; i <= matcher.groupCount(); i++) {
                String group = matcher.group(i);

                // replace for each group in the match ($1, $2, $3, etc.)
                toSend = toSend.replace(String.format("$%d", i), group);
            }
        }

        // send the final compiled message
        ChatUtils.chatAsPlayer(toSend);
    }
}
