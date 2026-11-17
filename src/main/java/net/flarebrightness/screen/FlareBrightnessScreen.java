package net.flarebrightness.screen;

import net.flarebrightness.FlareBrightness;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Plain vanilla-widget settings dialog: slider + Apply + Cancel. */
public class FlareBrightnessScreen extends Screen {
    private static final int RANGE = FlareBrightness.MAX_LEVEL - FlareBrightness.MIN_LEVEL;

    private final Screen parent;
    private final int originalLevel;
    private boolean applied = false;

    public FlareBrightnessScreen(Screen parent) {
        super(Component.translatable("flarebrightness.title"));
        this.parent = parent;
        this.originalLevel = FlareBrightness.getLevel();
    }

    @Override
    protected void init() {
        int w = 200;
        int x = this.width / 2 - w / 2;
        int y = Math.max(40, this.height / 2 - 30);

        this.addRenderableWidget(new LevelSlider(x, y, w, 20));

        this.addRenderableWidget(Button.builder(Component.translatable("flarebrightness.apply"), b -> {
            applied = true;
            FlareBrightness.save();
            this.onClose();
        }).bounds(x, y + 30, 98, 20).build());

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_CANCEL, b -> this.onClose())
                .bounds(x + 102, y + 30, 98, 20).build());
    }

    /** Closing without Apply (Cancel button or Esc) restores the previous level. */
    @Override
    public void onClose() {
        if (!applied) {
            FlareBrightness.setLevel(originalLevel);
        }
        this.minecraft.gui.setScreen(parent);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, Math.max(40, this.height / 2 - 30) - 20, 0xFFFFFFFF);
    }

    private static Component label(int level) {
        if (level == FlareBrightness.MIN_LEVEL) return Component.translatable("flarebrightness.level.darkest", level);
        if (level == FlareBrightness.MAX_LEVEL) return Component.translatable("flarebrightness.level.max", level);
        if (level == 0) return Component.translatable("flarebrightness.level.normal");
        return Component.translatable("flarebrightness.level", level);
    }

    private static double toSlider(int level) {
        return (level - FlareBrightness.MIN_LEVEL) / (double) RANGE;
    }

    private static int toLevel(double v) {
        return FlareBrightness.MIN_LEVEL + (int) Math.round(v * RANGE);
    }

    private static class LevelSlider extends AbstractSliderButton {
        LevelSlider(int x, int y, int w, int h) {
            super(x, y, w, h, label(FlareBrightness.getLevel()), toSlider(FlareBrightness.getLevel()));
        }

        @Override
        protected void updateMessage() {
            this.setMessage(label(toLevel(this.value)));
        }

        /** Live preview while dragging; only persisted on Apply. */
        @Override
        protected void applyValue() {
            FlareBrightness.setLevel(toLevel(this.value));
        }
    }
}
