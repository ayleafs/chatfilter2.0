package me.leafs.cf.events;

import me.leafs.cf.ChatFilter;
import me.leafs.cf.config.ChatFilterConfig;
import me.leafs.cf.filters.BaseFilter;
import me.leafs.cf.filters.FilterStyle;
import me.leafs.cf.filters.actions.ActionQueue;
import me.leafs.cf.utils.BitMask;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.*;
import java.util.stream.Collectors;

public class FilterHandler {
    private final List<UUID> enabledFilters = new ArrayList<>();

    public void attemptEnable(BaseFilter filter) {
        // already enabled
        if (enabledFilters.contains(filter.getId())) {
            return;
        }

        // add to the enabled if it is eligible
        if (filter.isEnabled()) {
            enabledFilters.add(filter.getId());
        }
    }

    public void remove(UUID id) {
        enabledFilters.remove(id);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        if (event.type == 0x2) {
            return; // ignore actionbar messages
        }

        Set<FilterStyle> styles = new HashSet<>();

        // get unformatted chat message
        String message = event.message.getUnformattedText();
        for (BaseFilter filter : getEnabledFilters()) {
            if (!filter.matches(message)) {
                continue;
            }

            // allow only one of each style
            if (styles.size() == FilterStyle.values().length) {
                break;
            }
            styles.add(filter.getStyle());

            // process the message and set it
            String processed = filter.process(message);
            if (processed.isEmpty()) {
                event.setCanceled(true);
                break;
            }

            // change message and color new one
            ChatComponentText output = new ChatComponentText(processed);
            if (!BitMask.isAnd(filter.getFlags(), BaseFilter.Flag.REPLACE)) {
                continue;
            }

            event.message = output;
        }
    }

    @SubscribeEvent
    public void onLogin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        // ignore singleplayer
        if (mc.isSingleplayer()) {
            return;
        }

        // clear the enabled filters
        enabledFilters.clear();

        ChatFilterConfig config = ChatFilter.instance.getConfig();

        // add all chat filters
        config.getFilters().forEach(this::attemptEnable);
    }

    @SubscribeEvent
    public void onQuit(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        // clear the queue and cancel running
        ActionQueue queue = ChatFilter.instance.getActionQueue();
        queue.getFilterQueue().clear();
        queue.setFilter(null);
    }

    public List<BaseFilter> getEnabledFilters() {
        return enabledFilters.stream()
                .map(ChatFilter.instance.getConfig()::filterById)
                .collect(Collectors.toList());
    }
}
