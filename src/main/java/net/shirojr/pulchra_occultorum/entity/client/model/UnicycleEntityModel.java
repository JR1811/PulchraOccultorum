package net.shirojr.pulchra_occultorum.entity.client.model;

import net.minecraft.client.model.*;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;

import java.util.ArrayList;
import java.util.List;

public class UnicycleEntityModel<T extends UnicycleEntity> extends SinglePartEntityModel<T> {
    private final List<ModelPart> parts = new ArrayList<>();
    private final ModelPart base, bottom, wheel, inner, holder, top;


    public UnicycleEntityModel(ModelPart root) {
        super(RenderLayer::getEntityCutoutNoCull);
        this.base = root.getChild("base");
        this.bottom = root.getChild("bottom");
        this.wheel = root.getChild("wheel");
        this.inner = root.getChild("inner");
        this.holder = root.getChild("holder");
        this.top = root.getChild("top");
        this.parts.addAll(List.of(base, top, bottom, wheel, inner, holder));
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();
        ModelPartData base = modelPartData.addChild("base", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 24.0F, 0.0F));

        ModelPartData bottom = base.addChild("bottom", ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, -1.0F, 0.0F));

        ModelPartData wheel = bottom.addChild("wheel", ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-1.0F, -4.0F, -4.0F, 1.0F, 8.0F, 8.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, -4.0F, -4.0F, 1.0F, 8.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.5F, -3.0F, 0.0F));

        ModelPartData inner = wheel.addChild("inner", ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-1.0F, -7.5F, -4.0F, 1.0F, 0.0F, 8.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 0.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 3.75F, 0.0F));

        ModelPartData cube_r1 = inner.addChild("cube_r1", ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 0.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.75F, 3.75F, -1.5708F, 0.0F, 0.0F));

        ModelPartData cube_r2 = inner.addChild("cube_r2", ModelPartBuilder.create().uv(0, 0)
                        .cuboid(-1.0F, 0.0F, -4.0F, 1.0F, 0.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.of(0.0F, -3.75F, -3.75F, -1.5708F, 0.0F, 0.0F));

        ModelPartData holder = bottom.addChild("holder", ModelPartBuilder.create().uv(4, 0)
                        .cuboid(1.0F, -10.0F, -0.6F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F))
                        .uv(10, 6).cuboid(-3.0F, -4.0F, -0.6F, 6.0F, 1.0F, 1.0F, new Dilation(0.0F))
                        .uv(0, 0).cuboid(-2.0F, -10.0F, -0.6F, 1.0F, 6.0F, 1.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 1.0F, 0.0F));

        ModelPartData top = bottom.addChild("top", ModelPartBuilder.create().uv(15, 13)
                        .cuboid(-1.0F, -1.0F, -1.6F, 2.0F, 2.0F, 3.0F, new Dilation(0.0F))
                        .uv(10, 0).cuboid(-0.5F, -5.0F, -0.6F, 1.0F, 4.0F, 1.0F, new Dilation(0.0F))
                        .uv(10, 0).cuboid(-1.0F, -6.0F, -2.6F, 2.0F, 1.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, -9.0F, 0.0F));

        return TexturedModelData.of(modelData, 32, 32);
    }

    @Override
    public ModelPart getPart() {
        return this.base;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {

    }

    @Override
    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, int color) {
        this.base.render(matrices, vertices, light, overlay, color);
    }
}
