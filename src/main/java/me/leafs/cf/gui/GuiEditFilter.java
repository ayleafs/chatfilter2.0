package me.leafs.cf.gui;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.leafs.cf.ChatFilter;
import me.leafs.cf.config.ChatFilterConfig;
import me.leafs.cf.filters.BaseFilter;
import me.leafs.cf.filters.FilterStyle;
import me.leafs.cf.filters.actions.FilterAction;
import me.leafs.cf.gui.elements.FilterButton;
import me.leafs.cf.gui.elements.FilterCheckbox;
import me.leafs.cf.gui.elements.FilterInput;
import me.leafs.cf.utils.BitMask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static me.leafs.cf.utils.ChatFilterGuis.BTN_H;
import static me.leafs.cf.utils.ChatFilterGuis.BTN_W;

@RequiredArgsConstructor
public class GuiEditFilter extends GuiScreen {
    private final GuiScreen lastScreen;
    private final BaseFilter localFilter;

    private final List<FilterInput> textFields = new ArrayList<>();

    @Getter private FilterInput matcher;
    private final List<FilterCheckbox> linkedBoxes = new ArrayList<>();

    @Getter private FilterInput replace;

    @Getter private FilterInput actions;
    @Getter private FilterCheckbox confirmation;

    @Getter private final List<FilterCheckbox> checkboxes = new ArrayList<>();
    @Getter private FilterCheckbox hideMessage;
    @Getter private FilterCheckbox thisServer;

    private boolean save = false;

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);

        // the way I wrote this GUI doesn't feel right
        // PRs to fix up are appreciated
        textFields.clear();
        linkedBoxes.clear();
        checkboxes.clear();

        // back and completion button
        buttonList.add(new FilterButton(1, 5, 5, BTN_H, BTN_H, "<-"));
        buttonList.add(new FilterButton(69, width - 5 - BTN_W / 2, height - 5 - BTN_H, BTN_W / 2, BTN_H, I18n.format("cf.gui.done")));

        int w = (int) (BTN_W * 1.5);
        int h = BTN_H;

        // make the input fields
        matcher = new FilterInput(I18n.format("cf.gui.editing.matcher"), width / 2 - w - 2, height / 2 - h - 4, w, h);
        matcher.setText(localFilter.getPattern());

        // created linked style settings
        int i = 0;
        int y = 0;

        for (FilterStyle value : FilterStyle.values()) {
            String name = StringUtils.capitalize(value.name().toLowerCase());

            // box wrapping
            int ni = i + fontRendererObj.getStringWidth(name) + 4 + 10; // 10 is the width of the checkbox
            if (ni > w) {
                y += 4 + 10;
                i = 0;
            }

            FilterCheckbox box = new FilterCheckbox(name, matcher.xPosition + i, height / 2 + y);

            // use the default style or the current value
            box.setChecked(localFilter.getStyle() == value);

            linkedBoxes.add(box);
            i = ni; // set i to next i
        }

        replace = new FilterInput(I18n.format("cf.gui.editing.replace"), width / 2 + 2, matcher.yPosition, w, h);
        replace.setText(localFilter.getReplacement());

        // repurposing y
        y = height / 2 + h / 2;

        actions = new FilterInput(I18n.format("cf.gui.editing.actions"), width / 2 + 2, y - 2, w, h);
        confirmation = new FilterCheckbox(I18n.format("cf.gui.editing.preconfirm"), width / 2 + 2, y + h + 2);

        FilterAction action = localFilter.getAction();
        if (action != null) {
            actions.setText(action.getChat());
            confirmation.setChecked(action.isConfirm());
        }

        // final options
        hideMessage = new FilterCheckbox(I18n.format("cf.gui.editing.hide"), width / 2 - w - 2, confirmation.getY());
        thisServer  = new FilterCheckbox(I18n.format("cf.gui.editing.serveronly"), hideMessage.getX() + hideMessage.getWidth() + fontRendererObj.getStringWidth(hideMessage.getLabel()) + 4, confirmation.getY());

        hideMessage.setChecked(BitMask.isAnd(localFilter.getFlags(), BaseFilter.Flag.HIDE));
        thisServer.setChecked(localFilter.getServer() != null);

        // add all the text fields and extra options
        textFields.addAll(Arrays.asList(matcher, replace, actions));
        checkboxes.addAll(Arrays.asList(confirmation, hideMessage, thisServer));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        // render the title
        drawCenteredString(fontRendererObj, I18n.format("cf.gui.editing.title"), width / 2, height / 3 - 10, 0xffffff);

        // draw all the elements needed
        checkboxes.forEach(FilterCheckbox::drawCheckbox);
        linkedBoxes.forEach(FilterCheckbox::drawCheckbox);
        textFields.forEach(GuiTextField::drawTextBox);

        // show the confirmation button if there's an action
        confirmation.setVisible(!actions.getText().isEmpty());

        // only show the server toggle button if the player is on the server
        // there isn't one set, and they aren't in singleplayer
        boolean serverEnabled = localFilter.getServer() != null;
        ServerData data = mc.getCurrentServerData();
        boolean visible = !serverEnabled && !mc.isSingleplayer() || serverEnabled && data != null && localFilter.getServer().equalsIgnoreCase(data.serverIP);

        thisServer.setVisible(visible);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        if (button.id == 1) {
            mc.displayGuiScreen(lastScreen);
            return;
        }

        if (button.id == 69) {
            save = true;
            mc.displayGuiScreen(lastScreen);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        // allow for focusing
        textFields.forEach(field -> field.mouseClicked(mouseX, mouseY, mouseButton));

        // click checkboxes
        checkboxes.forEach(box -> box.mouseClick(mouseX, mouseY));

        // when one checkbox is clicked make all the others turn off
        FilterCheckbox checked = null;
        for (FilterCheckbox checkbox : linkedBoxes) {
            if (!checkbox.mouseClick(mouseX, mouseY)) {
                continue;
            }

            // check if it's checked on
            if (checkbox.isChecked()) {
                checked = checkbox;
            }
        }

        if (checked == null) {
            return;
        }

        // ofc
        FilterCheckbox finalChecked = checked;
        linkedBoxes.stream()
                .filter(box -> !box.getLabel().equalsIgnoreCase(finalChecked.getLabel()))
                .forEach(box -> box.setChecked(false));
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);

        // enables typing in the text fields
        textFields.forEach(field -> field.textboxKeyTyped(typedChar, keyCode));
        
        // enable tabbing to the next input
        if (keyCode != Keyboard.KEY_TAB) {
            return;
        }

        for (int i = 0; i < textFields.size(); i++) {
            FilterInput input = textFields.get(i);
            if (!input.isFocused()) {
                continue;
            }

            // blur the currently selected
            input.setFocused(false);

            // get the next filter forwards or backward depending on whether
            // shift is held at the time of pressing, focus it
            FilterInput next = textFields.get(MathHelper.clamp_int(i + (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) ? 1 : -1), 0, textFields.size() - 1));
            next.setFocused(true);

            break;
        }
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);

        // set basic stuff
        String pattern = matcher.getText();
        if (!save || pattern.isEmpty()) {
            return; // don't do anything if nothing was entered
        }

        // try to find an existing filter
        ChatFilter cf = ChatFilter.instance;
        ChatFilterConfig config = cf.getConfig();
        BaseFilter filter = config.filterById(localFilter.getId());

        // add the new local filter if there isn't one already
        if (filter == null) {
            filter = localFilter;
            config.getFilters().add(filter);
        }

        // create bit mask flags
        int flags = 0;

        filter.setPattern(pattern);

        // add the replacement flag if there's a replacement
        String replaceText = replace.getText();
        if (!replaceText.isEmpty()) flags |= BaseFilter.Flag.REPLACE;
        filter.setReplacement(replaceText);

        // set the actions it's available
        FilterAction action = null;
        if (!actions.getText().isEmpty()) {
            action = new FilterAction();

            action.setChat(actions.getText());
            action.setConfirm(confirmation.isChecked());

            flags |= BaseFilter.Flag.ACTION;
        }

        // add the hide flag if necessary
        if (hideMessage.isChecked()) flags |= BaseFilter.Flag.HIDE;

        filter.setAction(action);
        filter.setFlags(flags);

        // find the style based on the index of the checkbox checked
        FilterStyle[] values = FilterStyle.values();
        for (int i = 0; i < values.length; i++) {
            // find the right checkbox
            if (!linkedBoxes.get(i).isChecked()) {
                continue;
            }

            filter.setStyle(values[i]);
            break;
        }

        // set the server if enabled
        ServerData data = Minecraft.getMinecraft().getCurrentServerData();
        if (data != null && (data.serverIP.equalsIgnoreCase(filter.getServer()) || filter.getServer() == null)) {
            filter.setServer(thisServer.isChecked() ? data.serverIP : null);
        }

        // re-enable
        cf.getFilterHandler().remove(filter.getId());
        cf.getFilterHandler().attemptEnable(filter);
    }
}
