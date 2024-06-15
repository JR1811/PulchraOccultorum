package net.shirojr.pulchra_occultorum.init;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class EntityAttributes {
    public static void initialize() {
        FabricDefaultAttributeRegistry.register(Entities.UNICYCLE, UnicycleEntity.setAttributes());
        LoggerUtil.devLogger("Initialized entity attributes");
    }
}
