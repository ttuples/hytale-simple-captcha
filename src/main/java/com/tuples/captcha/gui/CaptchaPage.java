package com.tuples.captcha.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class CaptchaPage extends InteractiveCustomUIPage<CaptchaPage.PageEventData> {
    public CaptchaPage(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, PageEventData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
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
        commands.set("#PageTitle.Text", "Solve the Captcha");

        // Set up the captcha grid
        commands.clear("#CaptchaGrid");
        for (int i = 0; i < 9; i++) {
            var selector = "#CaptchaGrid[" + i + "] ";
            commands.append("#CaptchaGrid", "Components/Tuples_SimpleCaptcha_Cell.ui");

//            commands.set(selector + "#Button.BackgroundImage", "Captchas/template.png");

            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    selector + "#Button",
                    (new EventData())
                            .append("Action", "CELL_CLICK")
                            .append("CellId", Integer.toString(i))
            );
        }
    }

    protected void sendUpdate() {
        this.sendUpdate(null, false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        switch (data.action) {
            case "CELL_CLICK" -> {
                System.out.println("Captcha cell clicked: " + data.cellId);
                this.rebuild();
            }
            case "SUBMIT" -> {
                System.out.println("Submit button clicked");
                this.close();
            }
            case "REFRESH" -> {
                System.out.println("Refresh button clicked");
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
