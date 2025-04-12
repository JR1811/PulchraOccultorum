package net.shirojr.pulchra_occultorum.block.entity.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Stainable;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.RotationAxis;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.util.ArgbHelper;
import net.shirojr.pulchra_occultorum.util.RenderLayers;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unused", "FieldCanBeLocal"})
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
        rotator.pitch = blockEntity.getRotation().getY() * (float) (Math.PI / 180.0);
        horizontal.yaw = blockEntity.getRotation().getX() * (float) (Math.PI / 180.0);

        matrices.push();
        matrices.translate(0.5, 1.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180));
        if (blockEntity.getCachedState().contains(Properties.HORIZONTAL_FACING)) {
            int deg = 0;
            switch (blockEntity.getCachedState().get(Properties.HORIZONTAL_FACING)) {
                case EAST -> deg = 90;
                case SOUTH -> deg = 180;
                case WEST -> deg = 270;
            }
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(deg));
        }
        lamp.render(matrices, vertexConsumers.getBuffer(getRenderLayer(blockEntity)), light, overlay);

        //region rays
        matrices.push();
        RenderSystem.disableDepthTest();
        RenderSystem.disableBlend();
        matrices.translate(0, 0.95, 0);

        float height = blockEntity.getStrength() * 2;
        float side = 1.5f * height;
        Quaternionf rotation = new Quaternionf();

        float additionalPitchForBeam = (float) Math.toRadians(270);
        Quaternionf pitchRotation = new Quaternionf().rotateAxis(rotator.pitch + additionalPitchForBeam, 1, 0, 0);
        Quaternionf yawRotation = new Quaternionf().rotateAxis(horizontal.yaw, 0, 1, 0);
        yawRotation.mul(pitchRotation, rotation);

        matrices.multiply(rotation);
        matrices.translate(0, 0.25, 0);

        ItemStack colorStack = hasStackWithColor(blockEntity);
        if (colorStack != null && getStainable(colorStack) != null) {
            matrices.push();
            if (colorStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof StainedGlassBlock) {
                matrices.scale(1.2f, 0.2f, 1.2f);
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrices.translate(0.0f, 0.0f, 1.0f);
            } else {
                matrices.multiply(RotationAxis.NEGATIVE_X.rotationDegrees(90));
                matrices.translate(0.0f, 0.0f, 0.2f);
                matrices.scale(0.6f, 0.6f, 1.7f);

            }
            MinecraftClient.getInstance().getItemRenderer().renderItem(colorStack,    // get Entity color
                    ModelTransformationMode.FIXED, light, overlay,
                    matrices, vertexConsumers,
                    blockEntity.getWorld(), (int) blockEntity.getPos().asLong());
            matrices.pop();
        }

        if (blockEntity.getStrength() <= 0) {
            matrices.pop();
            matrices.pop();
            return;
        }

        Vector3f a = new Vector3f(0, 0, 0);
        Vector3f b = new Vector3f(side / 2, height, side / 2);
        Vector3f c = new Vector3f(-side / 2, height, side / 2);
        Vector3f d = new Vector3f(-side / 2, height, -side / 2);
        Vector3f e = new Vector3f(side / 2, height, -side / 2);

        MatrixStack.Entry entry = matrices.peek();
        int primColor = ColorHelper.Argb.fromFloats(0.2f, 1.0F, 1.0F, 0.7F);
        int secColor = ColorHelper.Argb.fromFloats(0.0f, 1.0F, 0.6F, 0.3F);

        Stainable stainable = getStainable(colorStack);
        if (stainable != null) {
            int argbFromItem = stainable.getColor().getEntityColor();
            ArgbHelper primArgb = new ArgbHelper(argbFromItem).setBrightness(0.5f).setAlpha(0.6f);
            ArgbHelper secArgb = new ArgbHelper(argbFromItem).setBrightness(0.2f).setAlpha(0.001f);

            primColor = primArgb.getArgb();
            secColor = secArgb.getArgb();
        }

        RenderLayer[] layers = new RenderLayer[]{/*RenderLayers.SPOTLIGHT_LAMP_RAY_DEPTH,*/ RenderLayers.SPOTLIGHT_LAMP_RAY};
        for (RenderLayer layer : layers) {
            var vertexConsumer = vertexConsumers.getBuffer(layer);

            vertexConsumer.vertex(entry, a).color(primColor);
            vertexConsumer.vertex(entry, b).color(secColor);
            vertexConsumer.vertex(entry, c).color(secColor);

            vertexConsumer.vertex(entry, a).color(primColor);
            vertexConsumer.vertex(entry, c).color(secColor);
            vertexConsumer.vertex(entry, d).color(secColor);

            vertexConsumer.vertex(entry, a).color(primColor);
            vertexConsumer.vertex(entry, d).color(secColor);
            vertexConsumer.vertex(entry, e).color(secColor);

            vertexConsumer.vertex(entry, a).color(primColor);
            vertexConsumer.vertex(entry, e).color(secColor);
            vertexConsumer.vertex(entry, b).color(secColor);
        }
        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        matrices.pop();
        //endregion

        matrices.pop();
    }

    private RenderLayer getRenderLayer(T blockEntity) {
        String texturePath;
        if (isLit(blockEntity)) texturePath = "textures/entity/spotlight_lamp.png";
        else texturePath = "textures/entity/spotlight_lamp_off.png";
        return RenderLayer.getEntityCutout(PulchraOccultorum.getId(texturePath));
    }

    private boolean isLit(T blockEntity) {
        BlockState state = blockEntity.getCachedState();
        if (!state.contains(Properties.POWER)) return false;
        return state.get(Properties.POWER) > 0;
    }

    @Nullable
    private static ItemStack hasStackWithColor(SpotlightLampBlockEntity blockEntity) {
        if (blockEntity.getColorStack().isEmpty()) return null;
        if (!(blockEntity.getColorStack().getItem() instanceof BlockItem blockItem)) return null;
        if (!(blockItem.getBlock() instanceof Stainable)) return null;
        return blockEntity.getColorStack();
    }

    @Nullable
    public static Stainable getStainable(@Nullable ItemStack stack) {
        if (stack == null) return null;
        if (!(stack.getItem() instanceof BlockItem blockItem)) return null;
        if (!(blockItem.getBlock() instanceof Stainable stainable)) return null;
        return stainable;
    }

    @Override
    public int getRenderDistance() {
        return 256;
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
