package net.shirojr.pulchra_occultorum;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.shirojr.pulchra_occultorum.block.entity.client.renderer.FlagPoleBlockEntityRenderer;
import net.shirojr.pulchra_occultorum.block.entity.client.renderer.SpotlightLampBlockEntityRenderer;
import net.shirojr.pulchra_occultorum.entity.client.model.UnicycleEntityModel;
import net.shirojr.pulchra_occultorum.entity.client.renderer.MonolithEntityRenderer;
import net.shirojr.pulchra_occultorum.entity.client.renderer.UnicycleEntityRenderer;
import net.shirojr.pulchra_occultorum.init.*;
import net.shirojr.pulchra_occultorum.network.CustomS2CNetworking;
import net.shirojr.pulchra_occultorum.screen.SpotlightLampScreen;
import net.shirojr.pulchra_occultorum.sound.SoundManager;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class PulchraOccultorumClient implements ClientModInitializer {
    public static SoundManager soundManager = SoundManager.getInstance();

    public static final EntityModelLayer UNICYCLE_ENTITY_LAYER =
            new EntityModelLayer(PulchraOccultorum.getId("unicycle_entity_layer"), "main");
    public static final EntityModelLayer SPOTLIGHT_LAMP_BLOCK_ENTITY_LAYER =
            new EntityModelLayer(PulchraOccultorum.getId("spotlight_lamp_block_entity_layer"), "main");
    public static final EntityModelLayer FLAG_POLE_BLOCK_ENTITY_LAYER =
            new EntityModelLayer(PulchraOccultorum.getId("flag_pole_block_entity_layer"), "main");

    @Override
    public void onInitializeClient() {
        ModelPredicateProviders.initialize();
        CustomS2CNetworking.initialize();
        Events.registerClient();

        BlockRenderLayerMap.INSTANCE.putBlock(Blocks.SAFETY_NET, RenderLayer.getCutout());

        HandledScreens.register(ScreenHandlers.SPOTLIGHT_LAMP_SCREEN_HANDLER, SpotlightLampScreen::new);

        EntityRendererRegistry.register(Entities.UNICYCLE, UnicycleEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(UNICYCLE_ENTITY_LAYER, UnicycleEntityModel::getTexturedModelData);

        EntityRendererRegistry.register(Entities.MONOLITH, MonolithEntityRenderer::new);

        BlockEntityRendererFactories.register(BlockEntities.SPOTLIGHT_LAMP_BLOCK_ENTITY, context ->
                new SpotlightLampBlockEntityRenderer<>(context.getLayerModelPart(PulchraOccultorumClient.SPOTLIGHT_LAMP_BLOCK_ENTITY_LAYER)));
        EntityModelLayerRegistry.registerModelLayer(SPOTLIGHT_LAMP_BLOCK_ENTITY_LAYER, SpotlightLampBlockEntityRenderer::getTexturedModelData);

        BlockEntityRendererFactories.register(BlockEntities.FLAG_POLE_BLOCK_ENTITY, context ->
                new FlagPoleBlockEntityRenderer<>(context.getLayerModelPart((EntityModelLayers.BANNER))));
        EntityModelLayerRegistry.registerModelLayer(FLAG_POLE_BLOCK_ENTITY_LAYER, FlagPoleBlockEntityRenderer::getTexturedModelData);

        LoggerUtil.devLogger("Initialized client entrypoint");
    }
}
