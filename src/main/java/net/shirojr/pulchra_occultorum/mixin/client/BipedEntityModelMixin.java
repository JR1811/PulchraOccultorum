package net.shirojr.pulchra_occultorum.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.ModelWithArms;
import net.minecraft.client.render.entity.model.ModelWithHead;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BipedEntityModel.class)
public abstract class BipedEntityModelMixin<T extends LivingEntity> extends AnimalModel<T> implements ModelWithArms, ModelWithHead {
    @Shadow
    @Final
    public ModelPart rightLeg;

    @Shadow
    @Final
    public ModelPart leftLeg;

    @Shadow
    @Final
    public ModelPart rightArm;

    @Shadow
    @Final
    public ModelPart leftArm;

    @Shadow @Final public ModelPart body;

    @Shadow @Final public ModelPart head;

    @Unique private float releasedStateRight = 0;
    @Unique private float releasedStateLeft = 0;

    @ModifyExpressionValue(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;riding:Z", opcode = Opcodes.GETFIELD))
    private boolean ridingPoseExceptions(boolean original, @Local(ordinal = 0, argsOnly = true) T livingEntity) {
        if (livingEntity.getVehicle() instanceof UnicycleEntity) return false;
        return original;
    }

    @Inject(method = "setAngles(Lnet/minecraft/entity/LivingEntity;FFFFF)V", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/entity/model/BipedEntityModel;riding:Z", opcode = Opcodes.GETFIELD))
    private void unicycleRiding(T livingEntity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch, CallbackInfo ci) {
        if (!(livingEntity.getVehicle() instanceof UnicycleEntity unicycleEntity)) return;
        float legSplit = 0.2f;
        float armSplit = 0.3f;
        if (unicycleEntity.isInAir()) {
            armSplit = 1.3f;
            rightArm.pitch = MathHelper.sin(animationProgress * 0.3f) * 0.5f;
            leftArm.pitch = MathHelper.sin(animationProgress * 0.3f) * 0.5f;
            head.pitch = -0.4f;
        }
        UnicycleEntity.DirectionInput inputLeft = unicycleEntity.getDirectionInputs()[0];
        UnicycleEntity.DirectionInput inputRight = unicycleEntity.getDirectionInputs()[1];
        if (inputLeft != null) {
            leftArm.roll += 0.5f;
        }
        if (inputRight != null) {
            releasedStateRight = 0.5f;
        }
        if (releasedStateRight > 0) releasedStateRight = releasedStateRight - 0.03f;
        if (releasedStateLeft > 0) releasedStateLeft = releasedStateLeft - 0.03f;
        leftArm.roll -= releasedStateLeft;
        rightArm.roll -= releasedStateRight;

        rightArm.roll += armSplit;
        leftArm.roll -= armSplit;
        rightLeg.roll = legSplit;
        leftLeg.roll = -legSplit;

        rightLeg.pitch = MathHelper.cos(animationProgress * 0.2f) * 0.07f;
        leftLeg.pitch = MathHelper.sin(animationProgress * 0.2f) * 0.07f;
    }
}
