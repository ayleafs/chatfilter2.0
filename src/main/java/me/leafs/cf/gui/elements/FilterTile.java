package me.leafs.cf.gui.elements;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.leafs.cf.filters.BaseFilter;
import me.leafs.cf.gui.GuiEditFilter;
import me.leafs.cf.utils.ChatFilterGuis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;

import static me.leafs.cf.utils.ChatFilterGuis.BTN_H;
import static me.leafs.cf.utils.ChatFilterGuis.BTN_W;

@Getter
@RequiredArgsConstructor
public class FilterTile {
    private final BaseFilter filter;
    private final GuiScreen parent;

    private final Minecraft mc = Minecraft.getMinecraft();

    private final FilterButton edit = new FilterButton(0, -69, -69, BTN_W / 2, BTN_H, "Edit");
    private final FilterButton delete = new FilterButton(0, -69, -69, BTN_H, BTN_H, "§cX");

    public void renderTile(int mouseX, int mouseY, int x, int y, int w, int h) {
        int r = x + w;
        int b = y + h;

        // render the background of the tile
        Gui.drawRect(x, y, r, b, ChatFilterGuis.HL_COLOR.getRGB());

        FontRenderer renderer = mc.fontRendererObj;
        renderer.drawString(filter.getPattern(), x + 5, y + 5, 0xffffff);

        // draw the buttons
        edit.xPosition = r - edit.width - delete.width - 5;
        edit.yPosition = b - edit.height - 5;
        edit.drawButton(mc, mouseX, mouseY);

        delete.xPosition = r - delete.width - 5;
        delete.yPosition = b - delete.height - 5;
        delete.drawButton(mc, mouseX, mouseY);
    }

    public boolean mouseClick(int x, int y) {
        Minecraft mc = Minecraft.getMinecraft();
        // I'm falling asleep
        if (edit.mousePressed(mc, x, y)) {
            mc.displayGuiScreen(new GuiEditFilter(parent, filter));
            return false;
        }

        return delete.mousePressed(mc, x, y);
    }
}
