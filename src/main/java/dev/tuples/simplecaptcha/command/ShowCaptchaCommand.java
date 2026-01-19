package dev.tuples.simplecaptcha.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.CommandSender;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractAsyncCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.tuples.simplecaptcha.gui.CaptchaPage;
import dev.tuples.simplecaptcha.util.PermissionList;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

public class ShowCaptchaCommand extends AbstractAsyncCommand {
    public ShowCaptchaCommand() {
        super("simplecaptcha", "captcha.command.desc");
        this.addAliases("captcha");
        this.requirePermission(PermissionList.USE.getPermission());
        this.addSubCommand(new TargetCaptchaCommand());
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
            return openCaptchaPageAsync(player.getReference());
        } else {
            sender.sendMessage(Message.raw("This command can only be executed by a player."));
            return CompletableFuture.completedFuture(null);
        }
    }

    private class TargetCaptchaCommand extends AbstractAsyncCommand {
        private final RequiredArg<PlayerRef> targetPlayerArg;

        public TargetCaptchaCommand() {
            super("target", "captcha.command.target.desc");
            this.requirePermission(PermissionList.TARGET.getPermission());
            this.targetPlayerArg = withRequiredArg("player", "captcha.command.target.player", ArgTypes.PLAYER_REF);
        }

        @Nonnull
        @Override
        protected CompletableFuture<Void> executeAsync(CommandContext context) {
            CommandSender sender = context.sender();

            if (sender.hasPermission(PermissionList.TARGET.getPermission())) {
                Ref<EntityStore> ref = targetPlayerArg.get(context).getReference();
                return openCaptchaPageAsync(ref);
            } else {
                sender.sendMessage(PermissionList.TARGET.getMessage());
                return CompletableFuture.completedFuture(null);
            }
        }
    }
}
