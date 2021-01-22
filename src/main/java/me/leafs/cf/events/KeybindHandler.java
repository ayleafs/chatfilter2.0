package me.leafs.cf.events;

import me.leafs.cf.ChatFilter;
import me.leafs.cf.filters.actions.ActionQueue;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

public class KeybindHandler {
    @SubscribeEvent
    public void onKey(InputEvent.KeyInputEvent event) {
        ChatFilter cf = ChatFilter.instance;
        ActionQueue queue = cf.getActionQueue();

        KeyBinding quickSend = cf.getQuickSend();
        KeyBinding cancel = cf.getCancelSend();

        // set the run time to be lower to make it run
        if (quickSend.isPressed()) {
            queue.setTimerRun(0L);
            return;
        }

        // nullify the filter about to run
        if (cancel.isPressed()) {
            queue.setFilter(null);
        }
    }
}
