package net.shirojr.pulchra_occultorum.mixin;

import net.minecraft.entity.ai.goal.AmbientStandGoal;
import net.minecraft.entity.ai.goal.Goal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AmbientStandGoal.class)
public abstract class AmbientStandGoalMixin extends Goal {
    @ModifyConstant(method = "canStart", constant = @Constant(intValue = 1000))
    private int test(int constant) {
        return 40;
    }
}
