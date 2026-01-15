package com.tuples.templateplugin.gui;

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

public class TemplateGui extends InteractiveCustomUIPage<TemplateGui.TemplateGuiEventData> {
    public TemplateGui(@Nonnull PlayerRef playerRef) {
        super(playerRef, CustomPageLifetime.CanDismiss, TemplateGuiEventData.CODEC);
    }

    @Override
    public void build(@Nonnull Ref<EntityStore> ref, @Nonnull UICommandBuilder commands, @Nonnull UIEventBuilder events, @Nonnull Store<EntityStore> store) {
        commands.append("Pages/Tuples_TemplatePlugin_Gui.ui");

        commands.set("#PageTitle.Text", "A new page title!");

        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#CloseButton",
                EventData.of("Action", "close")
        );
    }

    @Override
    public void handleDataEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull TemplateGui.TemplateGuiEventData data) {
        if ("close".equals(data.action)) {
            close();
        }
    }

    public static class TemplateGuiEventData {
        public static final BuilderCodec<TemplateGuiEventData> CODEC =
                BuilderCodec.builder(TemplateGuiEventData.class, TemplateGuiEventData::new)
                        .append(new KeyedCodec<>("Action", Codec.STRING),
                                (d, v) -> d.action = v, d -> d.action)
                        .add()
                        .append(new KeyedCodec<>("@value", Codec.INTEGER),
                                (d, v) -> d.value = v, d -> d.value)
                        .add()
                        .build();

        public String action;
        public Integer value;
    }
}
