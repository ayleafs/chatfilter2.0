package me.leafs.cf.gui.elements;

import lombok.Getter;
import lombok.Setter;
import me.leafs.cf.utils.ChatFilterGuis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

import java.awt.*;

@Getter
public class FilterCheckbox {
    @Setter private boolean checked = false;
    @Setter private boolean visible = true;

    @Setter private int x;
    @Setter private int y;

    private final int width;
    private final int height;
    private final String label;

    public FilterCheckbox(String label, int x, int y) {
        this(label, x, y, 10, 10);
    }

    public FilterCheckbox(String label, int x, int y, int width, int height) {
        this.label = label;

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawCheckbox() {
        if (!visible) {
            return;
        }

        // render green or gray depending on whether it's checked
        Gui.drawRect(x, y, x + width, y + height, (checked ? new Color(20, 250, 110) : ChatFilterGuis.MAIN_COLOR).getRGB());

        // draw label next to it
        FontRenderer renderer = Minecraft.getMinecraft().fontRendererObj;
        renderer.drawStringWithShadow(label, x + width + 2, y + height / 2 - renderer.FONT_HEIGHT / 2, 0xffffff);
    }

    public boolean mouseClick(int mouseX, int mouseY) {
        // can't click what "isn't there"
        if (!visible) {
            return false;
        }

        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            checked = !checked;
            return true;
        }

        return false;
    }
}
