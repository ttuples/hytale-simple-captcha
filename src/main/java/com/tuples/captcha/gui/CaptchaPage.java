package com.tuples.captcha.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
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
import com.tuples.captcha.Main;
import com.tuples.captcha.component.CaptchaComponent;
import org.jetbrains.annotations.Nullable;

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
            String selector = "#CaptchaGrid[" + i + "]";
            commands.append("#CaptchaGrid", "Components/Tuples_SimpleCaptcha_Cell.ui");

            events.addEventBinding(
                    CustomUIEventBindingType.Activating,
                    selector + " #Button",
                    (new EventData())
                            .append("Action", "CELL_CLICK")
                            .append("CellId", Integer.toString(i))
            );
        }
    }

//    public void updateCells(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands, @Nonnull Store<EntityStore> store) {
//        CaptchaComponent component = store.ensureAndGetComponent(ref, Main.get().captchaComponentType);
//        int[] selectedCells = component.getSelectedCells();
//
//        for (int i = 0; i < 9; i++) {
//            String selector = "#CaptchaGrid[" + i + "] #Button.Background";
//            if (selectedCells[i] == 1) {
//                commands.set(selector + ".AssetPath", "Captcha/selected_cell.png");
//            } else {
//                commands.set(selector + ".AssetPath", "Captcha/unselected_cell.png");
//            }
//        }
//        this.sendUpdate(commands);
//    }

    protected void sendUpdate(UICommandBuilder commands) {
        this.sendUpdate(commands, false);
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull PageEventData data) {
        CaptchaComponent component = store.ensureAndGetComponent(ref, Main.get().captchaComponentType);
        switch (data.action) {
            case "CELL_CLICK" -> {
                System.out.println("Captcha cell clicked: " + data.cellId);
                component.toggleCellSelected(Integer.parseInt(data.cellId));
                this.rebuild();
            }
            case "SUBMIT" -> {
                System.out.println("Submit button clicked");
                if (component.submit()) { this.close(); }
                else { this.rebuild(); }
            }
            case "REFRESH" -> {
                System.out.println("Refresh button clicked");
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
