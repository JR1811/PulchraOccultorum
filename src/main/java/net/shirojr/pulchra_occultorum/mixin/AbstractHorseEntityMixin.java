package net.shirojr.pulchra_occultorum.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.entity.passive.AbstractHorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractHorseEntity.class)
public class AbstractHorseEntityMixin {
    @ModifyReturnValue(method = "getMinAmbientStandDelay", at = @At(value = "RETURN"))
    private int test(int original) {
        return 40;
    }
}
