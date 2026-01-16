package com.tuples.captcha.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tuples.captcha.CaptchaPlugin;
import com.tuples.captcha.component.CaptchaComponent;
import com.tuples.captcha.gui.CaptchaPage;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static com.hypixel.hytale.server.core.command.commands.player.inventory.InventorySeeCommand.MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD;

public class ShowCaptchaCommand extends AbstractAsyncCommand {
    public ShowCaptchaCommand() {
        super("test", "Opens the template GUI.");
        this.setPermissionGroup(GameMode.Adventure);
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext commandContext) {
        CommandSender sender = commandContext.sender();

        if (sender instanceof Player player) {
            player.getWorldMapTracker().tick(0); // Ensure the player is fully loaded
            Ref<EntityStore> ref = player.getReference();
            if (ref != null && ref.isValid()) {
                Store<EntityStore> store = ref.getStore();
                World world = store.getExternalData().getWorld();
                return CompletableFuture.runAsync(() -> {
                    PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                    store.ensureAndGetComponent(ref, CaptchaPlugin.get().getCaptchaComponentType()).reset();
                    if (playerRefComponent != null) {
                        player.getPageManager().openCustomPage(ref, store, new CaptchaPage(playerRefComponent));
                    }
                }, world);
            } else {
                commandContext.sendMessage(MESSAGE_COMMANDS_ERRORS_PLAYER_NOT_IN_WORLD);
                return CompletableFuture.completedFuture(null);
            }
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}
