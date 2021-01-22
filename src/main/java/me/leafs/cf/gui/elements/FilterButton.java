package me.leafs.cf.gui.elements;

import me.leafs.cf.utils.ChatFilterGuis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;

public class FilterButton extends GuiButton {
    public FilterButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, ChatFilterGuis.BTN_W, ChatFilterGuis.BTN_H, buttonText);
    }

    public FilterButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        // don't render if not visible
        if (!visible) {
            return;
        }

        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

        // render background state
        drawRect(xPosition, yPosition, xPosition + width, yPosition + height, (hovered ? ChatFilterGuis.HL_COLOR : ChatFilterGuis.MAIN_COLOR).getRGB());

        // draw the button text
        FontRenderer fontRenderer = mc.fontRendererObj;
        drawCenteredString(fontRenderer, displayString, xPosition + width / 2, yPosition + height / 2 - fontRenderer.FONT_HEIGHT / 2, 0xffffff);
    }
}
