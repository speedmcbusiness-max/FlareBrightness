package net.flarebrightness.mixin;

import net.flarebrightness.FlareBrightness;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.renderer.LightmapRenderStateExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightmapRenderStateExtractor.class)
public class MixinLightmapRenderStateExtractor {
    /**
     * Replaces only the gamma (Brightness) option read. Level 0 = untouched vanilla value.
     * No allocations besides the boxed Double, no extra work per frame.
     */
    @Redirect(method = "extract",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;get()Ljava/lang/Object;"))
    private Object flarebrightness$overrideGamma(OptionInstance<?> option) {
        if (FlareBrightness.getLevel() != 0
                && option == Minecraft.getInstance().options.gamma()) {
            return FlareBrightness.gammaForLevel();
        }
        return option.get();
    }
}
