package net.shirojr.pulchra_occultorum.init;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.entity.UnicycleEntity;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class Entities {
    public static final EntityType<UnicycleEntity> UNICYCLE = register("unicycle",
            EntityType.Builder.create(UnicycleEntity::new, SpawnGroup.MISC)
                    .dimensions(0.3f, 0.7f)
                    .spawnableFarFromPlayer()
                    .trackingTickInterval(1)
                    .build());

    private static <E extends Entity, T extends EntityType<E>> T register(String name, T entityType) {
        return Registry.register(Registries.ENTITY_TYPE, PulchraOccultorum.identifierOf(name), entityType);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized entities");
    }
}
