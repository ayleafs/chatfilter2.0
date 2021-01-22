package me.leafs.cf.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.leafs.cf.ChatFilter;
import me.leafs.cf.config.ChatFilterConfig;
import me.leafs.cf.filters.BaseFilter;
import me.leafs.cf.gui.elements.FilterButton;
import me.leafs.cf.gui.elements.FilterTile;
import me.leafs.cf.utils.ChatFilterGuis;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static me.leafs.cf.utils.ChatFilterGuis.BTN_H;

@RequiredArgsConstructor
public class GuiFilterList extends GuiScreen {
    private final GuiScreen lastScreen;

    @Getter private int scroll = 0;
    @Getter private boolean scrollingEnabled = false;

    private final List<FilterTile> tiles = new ArrayList<>();

    @Override
    public void initGui() {
        tiles.clear();

        // simple back button
        buttonList.add(new FilterButton(1, 5, 5, BTN_H, BTN_H, "<-"));

        // add all the filters to a tile
        for (BaseFilter filter : ChatFilter.instance.getConfig().getFilters()) {
            tiles.add(new FilterTile(filter, this));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        drawCenteredString(fontRendererObj, I18n.format("cf.gui.list.title"), width / 2, height / 3 - 10, 0xffffff);

        // draw background
        int right = width / 2 + width / 4;
        drawRect(width / 4, height / 3 + 10, right, height, ChatFilterGuis.MAIN_COLOR.getRGB());

        ScaledResolution res = new ScaledResolution(mc);
        int factor = res.getScaleFactor();
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        // tile dimensions
        int h = 50;

        // render each tile with spacing between them
        int scrollProg = scroll * 10;
        int y = scrollProg;
        for (FilterTile tile : tiles) {
            tile.renderTile(mouseX, mouseY, width / 4 + 5, height / 3 + 20 + y, right - width / 4 - 10, h);
            y += h + 5;
        }

        // crop the list to be within the bounds
        GL11.glScissor(factor * (width / 4), 0, factor * right, factor * height - factor * height / 3 - factor * 10);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        scrollingEnabled = height / 3 + 20 + (y - scrollProg) > height;
        if (scrollingEnabled) {
            scroll = 0;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int dWheel = Mouse.getDWheel();
        if (dWheel == 0 || !scrollingEnabled) {
            return;
        }

        scroll += dWheel > 1 ? 1 : -1;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // click on the tiles
        FilterTile deleted = null;

        for (FilterTile tile : tiles) {
            boolean isDeleted = tile.mouseClick(mouseX, mouseY);
            if (!isDeleted) {
                continue;
            }

            deleted = tile;
            break;
        }

        // remove the filter and tile if one was deleted
        if (deleted != null) {
            ChatFilter cf = ChatFilter.instance;

            cf.getConfig().getFilters().remove(deleted.getFilter());
            cf.getFilterHandler().remove(deleted.getFilter().getId());
            tiles.remove(deleted);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            mc.displayGuiScreen(lastScreen);
        }
    }
}
