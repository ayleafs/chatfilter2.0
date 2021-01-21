package me.leafs.cf.events;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FilterHandler {
    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        // TODO: 1/21/2021 create method to track what filters are enabled on the server and properly execute against filters
    }
}
