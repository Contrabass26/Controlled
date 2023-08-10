package com.contrabass.controlled.gui;

import com.contrabass.controlled.ControlledInit;
import com.contrabass.controlled.config.Configs;
import com.contrabass.controlled.config.Hotkeys;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.button.ButtonBase;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.IButtonActionListener;
import fi.dy.masa.malilib.util.StringUtils;

import java.util.List;

public final class GuiConfigs extends GuiConfigsBase {

    private static ConfigGuiTab tab = ConfigGuiTab.GENERIC;

    public GuiConfigs() {
        super(10, 50, ControlledInit.MOD_ID, null, "controlled.gui.title.configs");
    }

    @Override
    public void initGui() {
        super.initGui();
        this.clearOptions();

        int x = 10;
        int y = 26;

        for (ConfigGuiTab tab : ConfigGuiTab.values()) {
            x += this.createButton(x, y, tab);
        }
    }

    private int createButton(int x, int y, ConfigGuiTab tab) {
        ButtonGeneric button = new ButtonGeneric(x, y, -1, 20, tab.getDisplayName());
        button.setEnabled(GuiConfigs.tab != tab);
        this.addButton(button, new ButtonListener(tab, this));
        return button.getWidth() + 2;
    }

    @Override
    protected int getConfigWidth() {
        return 150;
    }

    @Override
    protected boolean useKeybindSearch() {
        return GuiConfigs.tab == ConfigGuiTab.GENERIC_HOTKEYS;
    }

    @Override
    public List<ConfigOptionWrapper> getConfigs() {
        ConfigGuiTab tab = GuiConfigs.tab;
        List<? extends IConfigBase> configs = switch (tab) {
            case GENERIC -> Configs.Generic.OPTIONS;
            case GENERIC_HOTKEYS -> Hotkeys.getHotkeys();
        };
        return ConfigOptionWrapper.createFor(configs);
    }

    public enum ConfigGuiTab {

        GENERIC("controlled.gui.button.config_gui.generic"),
        GENERIC_HOTKEYS("controlled.gui.button.config_gui.hotkeys");

        private final String translationKey;

        ConfigGuiTab(String translationKey) {
            this.translationKey = translationKey;
        }

        public String getDisplayName() {
            return StringUtils.translate(this.translationKey);
        }
    }

    private record ButtonListener(ConfigGuiTab tab, GuiConfigs parent) implements IButtonActionListener {

        @Override
        public void actionPerformedWithButton(ButtonBase button, int mouseButton) {
            GuiConfigs.tab = this.tab;
            this.parent.reCreateListWidget(); // apply the new config width
            this.parent.getListWidget().resetScrollbarPosition();
            this.parent.initGui();
        }
    }
}
