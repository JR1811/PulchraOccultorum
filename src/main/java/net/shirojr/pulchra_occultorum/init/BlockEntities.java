package net.shirojr.pulchra_occultorum.init;

import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.blockentity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class BlockEntities {
    public static final BlockEntityType<SpotlightLampBlockEntity> SPOTLIGHT_LAMP_BLOCK_ENTITY =
            register("spotlight_lamp", SpotlightLampBlockEntity::new, Blocks.SPOTLIGHT_LAMP);

    private static <T extends BlockEntity> BlockEntityType<T> register(String name,
                                                                       BlockEntityType.BlockEntityFactory<? extends T> entityFactory,
                                                                       Block... blocks) {
        return Registry.register(Registries.BLOCK_ENTITY_TYPE, PulchraOccultorum.identifierOf(name),
                BlockEntityType.Builder.<T>create(entityFactory, blocks).build());
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized block entities");
    }
}