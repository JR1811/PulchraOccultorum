package net.shirojr.pulchra_occultorum.entity.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.PulchraOccultorumClient;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.entity.client.model.UnicycleEntityModel;

public class UnicycleEntityRenderer extends LivingEntityRenderer<UnicycleEntity, UnicycleEntityModel<UnicycleEntity>> {

    public UnicycleEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new UnicycleEntityModel<>(ctx.getPart(PulchraOccultorumClient.UNICYCLE_ENTITY_LAYER)), 0.4f);
    }

    @Override
    public Identifier getTexture(UnicycleEntity entity) {
        return PulchraOccultorum.getId("textures/entity/unicycle.png");
    }

    @Override
    public void render(UnicycleEntity livingEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(livingEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    protected void setupTransforms(UnicycleEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta, float scale) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta, scale);
    }

    @Override
    protected boolean hasLabel(UnicycleEntity livingEntity) {
        return false;
    }
}
