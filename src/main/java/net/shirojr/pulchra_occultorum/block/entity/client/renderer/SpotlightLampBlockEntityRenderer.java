package net.shirojr.pulchra_occultorum.block.entity.client.renderer;

import net.minecraft.block.BlockState;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.SpotlightLampBlock;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.RenderLayers;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SpotlightLampBlockEntityRenderer<T extends SpotlightLampBlockEntity> implements BlockEntityRenderer<T> {
    private final List<ModelPart> modelParts = new ArrayList<>();
    private final ModelPart lamp, horizontal, rotator, blades;

    public SpotlightLampBlockEntityRenderer(ModelPart root) {
        this.lamp = root.getChild("lamp");
        this.horizontal = lamp.getChild("horizontal");
        this.rotator = horizontal.getChild("rotator");
        this.blades = rotator.getChild("blades");
        this.modelParts.addAll(List.of(lamp, horizontal, rotator, blades));
    }

    @Override
    public void render(T blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        rotator.pitch = -0.4f;
        // rotator.pitch = (float) Math.sin(rotationInDeg(blockEntity.getTick()) * 8);
        horizontal.yaw = 0.2f;
        // horizontal.yaw = rotationInDeg(blockEntity.getTick());

        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        lamp.render(matrices, vertexConsumers.getBuffer(getRenderLayer(blockEntity)), light, overlay);
        if (!blockEntity.isLit()) {
            matrices.pop();
            return;
        }

        //region rays
        matrices.push();
        matrices.translate(0.0f, 1.0f, 0.0f);
        int amount = 1;
        Quaternionf rotation = new Quaternionf();
        Vector3f vertex1 = new Vector3f();
        Vector3f vertex2 = new Vector3f();
        Vector3f vertex3 = new Vector3f();
        Vector3f vertex4 = new Vector3f();

        for (int i = 0; i < amount; i++) {
            float additionalPitchForBeam = (float) Math.toRadians(260);
            Quaternionf pitchRotation = new Quaternionf().rotateAxis(rotator.pitch + additionalPitchForBeam, 1, 0, 0);
            Quaternionf yawRotation = new Quaternionf().rotateAxis(horizontal.yaw, 0, 1, 0);
            yawRotation.mul(pitchRotation, rotation);

            matrices.multiply(rotation);

            float halfSquareRootOfThree = (float)(Math. sqrt(3.0) / 2.0);
            float width = 13.0f, length = 25f;

            vertex2.set(-halfSquareRootOfThree * width, length, -0.5F * width);
            vertex3.set(halfSquareRootOfThree * width, length, -0.5F * width);
            vertex4.set(0.0F, length, width);

            MatrixStack.Entry entry = matrices.peek();
            int primColor = ColorHelper.Argb.fromFloats(0.1f, 1.0F, 1.0F, 0.7F);
            int secColor = ColorHelper.Argb.fromFloats(0.0f, 1.0F, 0.6F, 0.3F); // original: 16711935
            var vertexConsumer = vertexConsumers.getBuffer(RenderLayers.SPOTLIGHT_LAMP_RAY);
            vertexConsumer.vertex(entry, vertex1).color(primColor);
            vertexConsumer.vertex(entry, vertex2).color(secColor);
            vertexConsumer.vertex(entry, vertex3).color(secColor);

            vertexConsumer.vertex(entry, vertex1).color(primColor);
            vertexConsumer.vertex(entry, vertex4).color(secColor);
            vertexConsumer.vertex(entry, vertex2).color(secColor);

            vertexConsumer.vertex(entry, vertex1).color(primColor);
            vertexConsumer.vertex(entry, vertex3).color(secColor);
            vertexConsumer.vertex(entry, vertex4).color(secColor);
        }

        matrices.pop();
        //endregion


        matrices.pop();
    }

    private float rotationInDeg(int degrees) {
        return degrees * (float) (Math.PI / 180.0);
    }

    private RenderLayer getRenderLayer(T blockEntity) {
        String texturePath;
        if (isLit(blockEntity)) texturePath = "textures/entity/spotlight_lamp.png";
        else texturePath = "textures/entity/spotlight_lamp_off.png";
        return RenderLayer.getEntityCutout(PulchraOccultorum.identifierOf(texturePath));
    }

    private boolean isLit(T blockEntity) {
        BlockState state = blockEntity.getCachedState();
        if (!state.contains(SpotlightLampBlock.LIT)) return false;
        return state.get(SpotlightLampBlock.LIT);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData lamp = modelPartData.addChild("lamp", ModelPartBuilder.create()
                        .uv(0, 12).cuboid(-1.0F, -5.0F, -1.0F, 2.0F, 5.0F, 2.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData horizontal = lamp.addChild("horizontal", ModelPartBuilder.create()
                        .uv(27, 0).cuboid(2.0F, -10.0F, -1.0F, 1.0F, 6.0F, 2.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-3.0F, -10.0F, -1.0F, 1.0F, 6.0F, 2.0F, new Dilation(0.0F))
                        .uv(16, 14).cuboid(-3.0F, -4.0F, -2.0F, 6.0F, 1.0F, 4.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F));

        ModelPartData rotator = horizontal.addChild("rotator", ModelPartBuilder.create()
                        .uv(0, 14).cuboid(-2.0F, -2.0F, -4.0F, 4.0F, 4.0F, 8.0F, new Dilation(0.0F))
                        .uv(24, 19).cuboid(-3.0F, -3.0F, -5.0F, 6.0F, 6.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -9.0F, 0.0F));

        ModelPartData blades = rotator.addChild("blades", ModelPartBuilder.create(), ModelTransform.pivot(0.0F, 3.0F, -4.0F));

        ModelPartData cube_r1 = blades.addChild("cube_r1", ModelPartBuilder.create()
                        .uv(0, 7).cuboid(-5.0F, 0.0F, -6.0F, 10.0F, 0.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, 0.0F, -1.0F, 0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r2 = blades.addChild("cube_r2", ModelPartBuilder.create()
                        .uv(0, 19).cuboid(0.0F, -5.0F, -6.0F, 0.0F, 10.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(3.0F, -3.0F, -1.0F, 0.0F, -0.3927F, 0.0F));

        ModelPartData cube_r3 = blades.addChild("cube_r3", ModelPartBuilder.create()
                        .uv(0, 0).cuboid(-5.0F, 0.0F, -6.0F, 10.0F, 0.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -6.0F, -1.0F, -0.3927F, 0.0F, 0.0F));

        ModelPartData cube_r4 = blades.addChild("cube_r4", ModelPartBuilder.create()
                        .uv(14, 19).cuboid(0.0F, -5.0F, -6.0F, 0.0F, 10.0F, 7.0F, new Dilation(0.0F)),
                ModelTransform.of(-3.0F, -3.0F, -1.0F, 0.0F, 0.3927F, 0.0F));
        return TexturedModelData.of(modelData, 64, 64);
    }
}
