package com.tuples.simplecaptcha.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tuples.simplecaptcha.gui.CaptchaPage;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ShowCaptchaCommand extends AbstractAsyncCommand {
    private final OptionalArg<PlayerRef> targetPlayerArg;

    public ShowCaptchaCommand() {
        super("simplecaptcha", "captcha.command.desc");
        this.addAliases("captcha");
        this.setPermissionGroup(GameMode.Adventure);

        targetPlayerArg = withOptionalArg("player", "captcha.command.player", ArgTypes.PLAYER_REF);
    }

    private CompletableFuture<Void> openCaptchaPageAsync(Ref<EntityStore> ref) {
        if (ref != null && ref.isValid()) {
            Store<EntityStore> store = ref.getStore();

            World world = store.getExternalData().getWorld();
            return CompletableFuture.runAsync(() -> {
                PlayerRef playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
                Player playerComponent = store.getComponent(ref, Player.getComponentType());
                if (playerRefComponent != null && playerComponent != null) {
                    playerComponent.getPageManager().openCustomPage(ref, store, new CaptchaPage(playerRefComponent));
                }
            }, world);
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }

    @Nonnull
    @Override
    protected CompletableFuture<Void> executeAsync(CommandContext context) {
        CommandSender sender = context.sender();

        if (sender instanceof Player player) {
            Ref<EntityStore> ref = targetPlayerArg.provided(context) ? targetPlayerArg.get(context).getReference() : player.getReference();
            return openCaptchaPageAsync(ref);
        } else {
            if (targetPlayerArg.provided(context)) {
                return openCaptchaPageAsync(targetPlayerArg.get(context).getReference());
            }
            return CompletableFuture.completedFuture(null);
        }
    }
}
