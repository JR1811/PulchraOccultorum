package net.shirojr.pulchra_occultorum.block.entity.client.renderer;

import net.minecraft.block.WallBannerBlock;
import net.minecraft.client.model.*;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BannerBlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BannerPatternsComponent;
import net.minecraft.item.BannerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.shirojr.pulchra_occultorum.block.entity.FlagPoleBlockEntity;
import net.shirojr.pulchra_occultorum.network.packet.HoistedFlagStatePacket;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class FlagPoleBlockEntityRenderer<T extends FlagPoleBlockEntity> implements BlockEntityRenderer<T> {
    private final ModelPart flag;

    public FlagPoleBlockEntityRenderer(ModelPart root) {
        this.flag = root.getChild("flag");
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        modelPartData.addChild("flag", ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(0.0F, 0.0F, 0.0F, 20.0F, 40.0F, 1.0F),
                ModelTransform.NONE);
        return TexturedModelData.of(modelData, 64, 64);
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if (blockEntity.getFlagInventory().isEmpty()) return;
        if (blockEntity.isFullyHoisted()) {
            if (blockEntity.flagAnimationProgress > 1) blockEntity.flagAnimationProgress = 0;
            blockEntity.flagAnimationProgress += tickDelta / 500;
        }

        float hoistedState = blockEntity.getHoistedState();
        float hoistedTargetState = blockEntity.getHoistedTargetState();
        float hoistedStepSize = 0.001f;
        float offsetToPole = 0.2f;
        if (hoistedState < hoistedTargetState) {
            float newState = Math.min(hoistedState + hoistedStepSize, hoistedTargetState);
            blockEntity.setHoistedState(newState);
            HoistedFlagStatePacket packet = new HoistedFlagStatePacket(newState, blockEntity.getPos());
            packet.send();
        }
        else if (hoistedState > hoistedTargetState) {
            float newState = Math.max(hoistedState - hoistedStepSize, hoistedTargetState);
            blockEntity.setHoistedState(newState);
            HoistedFlagStatePacket packet = new HoistedFlagStatePacket(newState, blockEntity.getPos());
            packet.send();
        }

        ItemStack bannerStack = blockEntity.getFlagInventory().getStack(0).copy();
        BannerPatternsComponent bannerPatternsComponent = bannerStack.get(DataComponentTypes.BANNER_PATTERNS);

        VertexConsumer vertexConsumer = ModelLoader.BANNER_BASE.getVertexConsumer(vertexConsumers, RenderLayer::getEntitySolid);

        float scale = 1.2f;
        double minHeight = 1;
        double maxHeight = blockEntity.getFlagPoleCount() - 1;

        matrices.push();
        double flagHeightOnPole = MathHelper.lerp(blockEntity.getHoistedState(), minHeight, maxHeight);
        matrices.translate(0f, flagHeightOnPole + 0.2f, 0f);

        matrices.translate(0.5F, -0.16666667F, 0.5F);
        matrices.multiply(RotationAxis.NEGATIVE_Z.rotationDegrees(90));

        verticalRotation(matrices, blockEntity, 0, true);
        horizontalRotation(matrices, blockEntity, 0, true);
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));

        float directionAngle = 0f;
        switch (blockEntity.getDirection()) {
            case EAST -> directionAngle = 90;
            case SOUTH -> directionAngle = 180;
            case WEST -> directionAngle = 270;
        }
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(directionAngle));

        matrices.scale(scale, scale, scale);
        matrices.translate(0.0F, offsetToPole, 0.1F);

        if (bannerPatternsComponent == null) {
            LoggerUtil.LOGGER.warn("Couldn't find Banner Pattern from ItemStack for Flag");
            this.flag.render(matrices, vertexConsumer, light, overlay);
        } else {
            DyeColor color = DyeColor.GRAY;
            if (bannerStack.getItem() instanceof BannerItem bannerItem) color = bannerItem.getColor();
            BannerBlockEntityRenderer.renderCanvas(
                    matrices,
                    vertexConsumers,
                    15728880,
                    OverlayTexture.DEFAULT_UV,
                    this.flag,
                    ModelLoader.BANNER_BASE,
                    true,
                    color,
                    bannerPatternsComponent
            );
        }
        matrices.pop();

    }

    private void centerBannerMatrices(MatrixStack matrices, float scale) {
        matrices.translate(0.4f, 0.0f, 0.5f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));
        matrices.scale(scale, scale, scale);
        matrices.translate(0.0f, 0.7f, 0.0f);
    }

    @SuppressWarnings("SameParameterValue")
    private void verticalRotation(MatrixStack matrices, FlagPoleBlockEntity blockEntity, int baseRotation, boolean shouldSway) {
        matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(baseRotation));
        if (shouldSway) {
            float offsetFromPole = -0.5f;
            double swaySpeed = 0.06;
            // matrices.translate(0.0f, 0.0f, -0.5);
            float verticalSwing = MathHelper.sin(blockEntity.flagAnimationProgress * MathHelper.TAU) * 3;
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(verticalSwing));
            // matrices.translate(0.0f, 0.0f, -offsetFromPole);
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void horizontalRotation(MatrixStack matrices, FlagPoleBlockEntity blockEntity, int baseRotation, boolean shouldSway) {
        float horizontalSwing = MathHelper.cos(blockEntity.flagAnimationProgress * MathHelper.TAU) * 5;
        float horizontalRotation = shouldSway ? baseRotation + horizontalSwing : baseRotation;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(horizontalRotation));
    }
}
