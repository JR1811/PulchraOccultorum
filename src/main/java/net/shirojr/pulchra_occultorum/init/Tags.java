package net.shirojr.pulchra_occultorum.init;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class Tags {
    public static class Blocks {
        public static TagKey<Block> SUPPORTS_FLAG_POLE = createTag("supports_flag_pole");
        public static TagKey<Block> SENDS_UPDATE_POWER_VERTICALLY = createTag("sends_update_power_vertically");

        @SuppressWarnings("SameParameterValue")
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, PulchraOccultorum.identifierOf(name));
        }

        private static void register() {
            LoggerUtil.devLogger("initialized tags for entities");
        }
    }


    public static void initialize() {
        Blocks.register();
        LoggerUtil.devLogger("initialized tags");
    }
}
