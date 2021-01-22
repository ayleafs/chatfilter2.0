package me.leafs.cf.gui.elements;

import me.leafs.cf.utils.ChatFilterGuis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiTextField;

public class FilterInput extends GuiTextField {
    private static final FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
    private final String placeholder;

    public FilterInput(String placeholder, int x, int y) {
        this(placeholder, x, y, ChatFilterGuis.BTN_W, ChatFilterGuis.BTN_H);
    }

    public FilterInput(String placeholder, int x, int y, int width, int height) {
        super(0, renderer, x, y, width, height);

        this.placeholder = placeholder;
        setMaxStringLength(10000);
    }

    public void drawTextBox() {
        if (!getVisible()) {
            return;
        }

        // draw background
        setEnableBackgroundDrawing(false);
        Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, ChatFilterGuis.MAIN_COLOR.getRGB());

        // render placeholder
        if (placeholder != null && !placeholder.isEmpty()) {
            boolean state = isFocused() || !getText().isEmpty();

            // if focused or the text is empty, render the text in the box
            int y = state ?
                    yPosition - renderer.FONT_HEIGHT :
                    yPosition + height / 2 - renderer.FONT_HEIGHT / 2;

            // finally render
            renderer.drawStringWithShadow(placeholder, state ? xPosition : xPosition + 4, y, (state ? ChatFilterGuis.TEXT_BRIGHT : ChatFilterGuis.TEXT_LOW).getRGB());
        }

        // this is a stupid and lazy solution
        // don't talk to me about it

        int x = xPosition;
        int y = yPosition;

        xPosition += 4;
        yPosition += (this.height - 8) / 2;

        super.drawTextBox();

        xPosition = x;
        yPosition = y;
    }
}
