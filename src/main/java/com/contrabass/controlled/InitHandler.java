package com.contrabass.controlled;

import com.contrabass.controlled.config.Callbacks;
import com.contrabass.controlled.config.Configs;
import com.contrabass.controlled.event.InputHandler;
import com.contrabass.controlled.gui.ControlledRenderHandler;
import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;

public class InitHandler implements IInitializationHandler {

    @Override
    public void registerModHandlers() {
        ConfigManager.getInstance().registerConfigHandler(ControlledInit.MOD_ID, new Configs());

        InputEventHandler.getInputManager().registerKeyboardInputHandler(InputHandler.getInstance());
        InputEventHandler.getKeybindManager().registerKeybindProvider(InputHandler.getInstance());

        ControlledRenderHandler renderHandler = new ControlledRenderHandler();
        RenderEventHandler.getInstance().registerWorldLastRenderer(renderHandler);

        Callbacks.init();
    }
}
