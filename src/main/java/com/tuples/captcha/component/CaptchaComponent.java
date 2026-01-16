package com.tuples.captcha.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.tuples.captcha.CaptchaChallenge;
import com.tuples.captcha.CaptchaPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CaptchaComponent implements Component<EntityStore> {

    public static final BuilderCodec<CaptchaComponent> CODEC =
            BuilderCodec.builder(CaptchaComponent.class, CaptchaComponent::new)
                    .append(new KeyedCodec<>("SelectedCells", Codec.INT_ARRAY),
                            (captchaComponent, selectedCells) -> captchaComponent.selectedCells = selectedCells,
                            (captchaComponent) -> captchaComponent.selectedCells)
                    .add()
                    .append(new KeyedCodec<>("CurrentChallenge", Codec.STRING),
                            (captchaComponent, currentChallenge) -> captchaComponent.currentChallenge = currentChallenge,
                            (captchaComponent) -> captchaComponent.currentChallenge)
                    .add()
                    .build();

    public static final int CELL_COUNT = 16;
    private int[] selectedCells = new int[CELL_COUNT];
    private String currentChallenge;

    public CaptchaComponent() {
        this.newChallenge();
    };

    public boolean submit() {
        var challenge = CaptchaPlugin.get().getCaptchaByName(currentChallenge);

        List<Boolean> expected =
                new ArrayList<>(challenge.images.values());

        for (int i = 0; i < selectedCells.length; i++) {
            boolean selected = selectedCells[i] == 1;
            boolean shouldSelect = expected.get(i);

            if (selected != shouldSelect) {
                this.reset();
                return false;
            }
        }

        return true;
    }

    public void reset() {
        this.clearSelectedCells();
        this.newChallenge();
    }

    public String getChallengeImagePath(int cellId) {
        CaptchaChallenge challenge =
                CaptchaPlugin.get().getCaptchaByName(currentChallenge);

        return new ArrayList<>(challenge.images.keySet()).get(cellId);
    }

    public String getChallengeInstruction() {
        CaptchaChallenge challenge =
                CaptchaPlugin.get().getCaptchaByName(currentChallenge);

        return challenge.text;
    }

    public boolean isCellSelected(int index) {
        if (index >= 0 && index < selectedCells.length) {
            return selectedCells[index] == 1;
        }
        return false;
    }

    public void toggleCellSelected(int index) {
        if (index >= 0 && index < selectedCells.length) {
            selectedCells[index] = (selectedCells[index] == 0) ? 1 : 0;
        }
    }

    public void clearSelectedCells() {
        this.selectedCells = new int[CELL_COUNT];
    }

    public void newChallenge() {
        this.currentChallenge = CaptchaPlugin.get().getRandomCaptcha();
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        CaptchaComponent copy = new CaptchaComponent();
        copy.selectedCells = this.selectedCells;
        copy.currentChallenge = this.currentChallenge;
        return copy;
    }
}