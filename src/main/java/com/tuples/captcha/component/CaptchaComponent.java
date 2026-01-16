package com.tuples.captcha.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.Nullable;

import static com.hypixel.hytale.math.util.MathUtil.randomInt;

public class CaptchaComponent implements Component<EntityStore> {

    public static final BuilderCodec<CaptchaComponent> CODEC =
            BuilderCodec.builder(CaptchaComponent.class, CaptchaComponent::new)
                    .append(new KeyedCodec<>("SelectedCells", Codec.INT_ARRAY),
                            (captchaComponent, selectedCells) -> captchaComponent.selectedCells = selectedCells,
                            (captchaComponent) -> captchaComponent.selectedCells)
                    .add()
                    .append(new KeyedCodec<>("CurrentChallenge", Codec.INTEGER),
                            (captchaComponent, currentChallenge) -> captchaComponent.currentChallenge = currentChallenge,
                            (captchaComponent) -> captchaComponent.currentChallenge)
                    .add()
                    .build();

    private int[] selectedCells = new int[9];
    private int currentChallenge = 0;

    public CaptchaComponent() {};

    public boolean submit() {
        //TODO: Check logic
        this.reset();
        return true;
    }

    public void reset() {
        this.clearSelectedCells();
        this.newChallenge();
    }

    public int[] getSelectedCells() {
        return selectedCells;
    }

    public void toggleCellSelected(int index) {
        if (index >= 0 && index < selectedCells.length) {
            selectedCells[index] = (selectedCells[index] == 0) ? 1 : 0;
        }
    }

    public void clearSelectedCells() {
        this.selectedCells = new int[9];
    }

    public void newChallenge() {
        this.currentChallenge = 0;
//        this.currentChallenge = randomInt(1, 5);
    }

    @Override
    public @Nullable Component<EntityStore> clone() {
        CaptchaComponent copy = new CaptchaComponent();
        copy.selectedCells = this.selectedCells;
        return copy;
    }
}