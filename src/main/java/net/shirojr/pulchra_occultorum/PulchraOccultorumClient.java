package net.shirojr.pulchra_occultorum;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.shirojr.pulchra_occultorum.entity.client.model.UnicycleEntityModel;
import net.shirojr.pulchra_occultorum.entity.client.renderer.UnicycleEntityRenderer;
import net.shirojr.pulchra_occultorum.network.CustomS2CNetworking;
import net.shirojr.pulchra_occultorum.init.Entities;
import net.shirojr.pulchra_occultorum.sound.SoundManager;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.ModelPredicateProviders;

public class PulchraOccultorumClient implements ClientModInitializer {
    public static SoundManager soundManager = SoundManager.getInstance();

    public static final EntityModelLayer UNICYCLE_ENTITY_LAYER =
            new EntityModelLayer(PulchraOccultorum.identifierOf("unicycle_entity_layer"), "main");

    @Override
    public void onInitializeClient() {
        ModelPredicateProviders.initialize();
        CustomS2CNetworking.initialize();

        EntityRendererRegistry.register(Entities.UNICYCLE, UnicycleEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(UNICYCLE_ENTITY_LAYER, UnicycleEntityModel::getTexturedModelData);

        LoggerUtil.devLogger("Initialized client entrypoint");
    }
}
