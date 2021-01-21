package me.leafs.cf.events;

import me.leafs.cf.ChatFilter;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeybindHandler {
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        ChatFilter cf = ChatFilter.instance;

        KeyBinding quickSend = cf.getQuickSend();
        KeyBinding cancel = cf.getCancelSend();

        if (quickSend.isPressed()) {

            return;
        }
    }
}
