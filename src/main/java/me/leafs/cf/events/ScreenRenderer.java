package me.leafs.cf.events;

import me.leafs.cf.ChatFilter;
import me.leafs.cf.filters.BaseFilter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class ScreenRenderer {
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Text event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        ChatFilter filter = ChatFilter.instance;
        // if not in a world
        if (minecraft.theWorld == null) {
            return;
        }

        Map.Entry<BaseFilter, Long> nextFilter = filter.getActionQueue().runNextFilter();
        // if the filter is null
        if (nextFilter.getKey() == null) {
            return;
        }

        int timeLeft = (int) ((nextFilter.getValue() - System.currentTimeMillis()) / 1000L);
        String displayString = I18n.format("cf.confirm", timeLeft, GameSettings.getKeyDisplayString(filter.getQuickSend().getKeyCode()), GameSettings.getKeyDisplayString(filter.getCancelSend().getKeyCode()));

        ScaledResolution res = event.resolution;
        FontRenderer renderer = minecraft.fontRendererObj;

        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        renderer.drawStringWithShadow(displayString, width / 2 - renderer.getStringWidth(displayString) / 2, (float) (height * .75), 0xffffff);
    }
}
