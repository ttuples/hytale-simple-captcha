package com.tuples.captcha;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tuples.captcha.command.ShowCaptchaCommand;
import com.tuples.captcha.component.CaptchaComponent;

import javax.annotation.Nonnull;
import java.util.logging.Level;

public class Main extends JavaPlugin {

    private static Main instance;
    public ComponentType<EntityStore, CaptchaComponent> captchaComponentType;

    public Main(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static Main get() {
        return instance;
    }

    @Override
    protected void setup() {
        instance = this;
        this.getCommandRegistry().registerCommand(new ShowCaptchaCommand());
        captchaComponentType = getEntityStoreRegistry().registerComponent(
                CaptchaComponent.class,
                CaptchaComponent::new
        );

        getLogger().at(Level.INFO).log("Setup complete!");
    }

    @Override
    protected void start() {
        getLogger().at(Level.INFO).log("Plugin started!");
    }

    @Override
    protected void shutdown() {
        getLogger().at(Level.INFO).log("Plugin shutting down!");
    }
}
