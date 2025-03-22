package net.shirojr.pulchra_occultorum.entity.client.renderer;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.entity.MonolithEntity;

public class MonolithEntityRenderer extends EntityRenderer<MonolithEntity> {
    public MonolithEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(MonolithEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        // render nothing
    }

    @Override
    public Identifier getTexture(MonolithEntity entity) {
        return PulchraOccultorum.getId("textures/block/unicycle.png");
    }
}
