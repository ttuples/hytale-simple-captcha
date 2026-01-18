package dev.tuples.simplecaptcha.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import dev.tuples.simplecaptcha.CaptchaPlugin;
import dev.tuples.simplecaptcha.component.CaptchaComponent;

import javax.annotation.Nonnull;

public class CaptchaPage extends InteractiveCustomUIPage<CaptchaPage.PageEventData> {
    public CaptchaPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CantClose, PageEventData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        CaptchaComponent component = store.ensureAndGetComponent(ref, CaptchaPlugin.get().getCaptchaComponentType());

        commands.append("Pages/Tuples_SimpleCaptcha_Container.ui");

        // Setup event bindings
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#SubmitButton",
                (new EventData()).append("Action", "SUBMIT")
        );
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#RefreshButton",
                (new EventData()).append("Action", "REFRESH")
        );

        // Setup page
        commands.set("#CaptchaInstruction.Text", Message.translation(component.getChallengeInstruction()));
        commands.set("#Difficulty.Text", Message.translation("captcha.ui.difficulty." + component.getDifficulty()));
        commands.set("#Difficulty.Style.TextColor", component.getDifficultyColor());

        // Set up the simplecaptcha grid
        commands.clear("#CaptchaGrid");
        for (int i = 0; i < CaptchaComponent.CELL_COUNT; i++) {
            String selector = "#CaptchaGrid[" + i + "]";
            commands.append("#CaptchaGrid", "Components/Tuples_SimpleCaptcha_Cell.ui");

            commands.set(selector + " #Icon.Background", component.getChallengeImagePath(i));

            if (component.isCellSelected(i)) {
                commands.set(selector + " #Button.Visible", false);
                commands.set(selector + " #ButtonSelected.Visible", true);

                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        selector + " #ButtonSelected",
                        (new EventData())
                                .append("Action", "CELL_CLICK")
                                .append("CellId", Integer.toString(i))
                );
            } else {
                events.addEventBinding(
                        CustomUIEventBindingType.Activating,
                        selector + " #Button",
                        (new EventData())
                                .append("Action", "CELL_CLICK")
                                .append("CellId", Integer.toString(i))
                );
            }
        }
    }

    protected void sendUpdate(UICommandBuilder commands) {
        this.sendUpdate(commands, false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        CaptchaComponent component = store.ensureAndGetComponent(ref, CaptchaPlugin.get().getCaptchaComponentType());
        switch (data.action) {
            case "CELL_CLICK" -> {
                component.toggleCellSelected(Integer.parseInt(data.cellId));
                this.rebuild();
            }
            case "SUBMIT" -> {
                if (component.submit()) {
                    component.reset();
                    this.close();
                }
                else { this.rebuild(); }
            }
            case "REFRESH" -> {
                component.reset();
                this.rebuild();
            }
        }
    }

    public static class PageEventData {
        public String action;
        public String cellId;

        public static final BuilderCodec<PageEventData> CODEC =
                BuilderCodec.builder(PageEventData.class, PageEventData::new)
                    .append(new KeyedCodec<>("Action", Codec.STRING),
                            (pageEventData, pageAction) -> pageEventData.action = pageAction,
                            (pageEventData) -> pageEventData.action).add()
                    .append(new KeyedCodec<>("CellId", Codec.STRING),
                            (pageEventData, id) -> pageEventData.cellId = id,
                            (pageEventData) -> pageEventData.cellId).add()
                    .build();
    }
}
