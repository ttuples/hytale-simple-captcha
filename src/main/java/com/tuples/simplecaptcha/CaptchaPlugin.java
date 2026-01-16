package com.tuples.simplecaptcha;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tuples.simplecaptcha.command.ShowCaptchaCommand;
import com.tuples.simplecaptcha.component.CaptchaComponent;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class CaptchaPlugin extends JavaPlugin {

    private static CaptchaPlugin instance;
    private static final Gson GSON = new Gson();

    private ComponentType<EntityStore, CaptchaComponent> captchaComponentType;
    private static Map<String, CaptchaChallenge> captchas;

    public CaptchaPlugin(@Nonnull JavaPluginInit init) {
        super(init);
    }

    public static CaptchaPlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        instance = this;
        this.loadCaptchas();

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

    public ComponentType<EntityStore, CaptchaComponent> getCaptchaComponentType() {
        return captchaComponentType;
    }

    public void loadCaptchas() {
        try (InputStream stream =
                     getClass().getClassLoader().getResourceAsStream("captchas.json")) {
            if (stream == null) {
                throw new IllegalStateException("captchas.json not found");
            }

            try (InputStreamReader reader =
                         new InputStreamReader(stream, StandardCharsets.UTF_8)) {

                Type type = new TypeToken<Map<String, CaptchaChallenge>>() {}.getType();
                captchas = GSON.fromJson(reader, type);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load simplecaptcha definitions", e);
        }
    }

    public String getRandomCaptcha() {
        return new ArrayList<>(captchas.keySet())
                .get(ThreadLocalRandom.current().nextInt(captchas.size()));
    }

    public CaptchaChallenge getCaptchaByName(String name) {
        return captchas.get(name);
    }
}
