package net.shirojr.pulchra_occultorum.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.init.Blocks;

import java.util.concurrent.CompletableFuture;

public class TagProvider {
    public static class BlockTags extends FabricTagProvider.BlockTagProvider {
        private static final TagKey<Block> SUPPORTS_FLAG_POLE =
                TagKey.of(RegistryKeys.BLOCK, PulchraOccultorum.getId("supports_flag_pole"));
        private static final TagKey<Block> SENDS_UPDATE_POWER_VERTICALLY =
                TagKey.of(RegistryKeys.BLOCK, PulchraOccultorum.getId("sends_update_power_vertically"));


        public BlockTags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
            super(output, registriesFuture);
        }

        @Override
        protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
            getOrCreateTagBuilder(SUPPORTS_FLAG_POLE)
                    .add(Blocks.FLAG_POLE)
                    .add(Blocks.FLAG_POLE_BASE);
            getOrCreateTagBuilder(SENDS_UPDATE_POWER_VERTICALLY)
                    .add(Blocks.FLAG_POLE)
                    .add(net.minecraft.block.Blocks.IRON_BARS)
                    .addOptionalTag(net.minecraft.registry.tag.BlockTags.WALLS);
        }
    }
}
