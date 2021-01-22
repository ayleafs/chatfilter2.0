package me.leafs.cf.gui;

import me.leafs.cf.filters.BaseFilter;
import me.leafs.cf.gui.elements.FilterButton;
import me.leafs.cf.utils.ChatFilterGuis;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

import java.util.UUID;

public class GuiFilterHome extends GuiScreen {
    @Override
    public void initGui() {
        buttonList.add(new FilterButton(1, width / 2 - ChatFilterGuis.BTN_W - 5, height / 2 + 10, I18n.format("cf.gui.home.new")));
        buttonList.add(new FilterButton(2, width / 2 + 5, height / 2 + 10, I18n.format("cf.gui.home.manage")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        // draw the basic background
        drawDefaultBackground();

        FontRenderer renderer = mc.fontRendererObj;
        // draw the title
        drawCenteredString(renderer, I18n.format("cf.title"), width / 2, height / 2 - 10, 0xffffff);

        // draw buttons and basic stuff (handled by Minecraft)
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            // open a filter editor with a new base filter
            mc.displayGuiScreen(new GuiEditFilter(this, new BaseFilter("", "", UUID.randomUUID())));
            return;
        }

        if (button.id == 2) {
            mc.displayGuiScreen(new GuiFilterList(this));
        }
    }
}
