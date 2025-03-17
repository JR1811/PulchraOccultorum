package net.shirojr.pulchra_occultorum.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

/**
 * This Interface is used for dynamic sound instances, which include volume and pitch modulation based on many different factors.
 */
public interface SoundOrigin {
    /**
     * Make sure to choose unique values for each instance.<br>
     * For example:
     * <ul>
     *     <li>LivingEntities: UUID</li>
     *     <li>BlockEntities: BlockPos</li>
     * </ul>
     *
     * @return Unique ID for identifying a specific running SoundInstances
     * for the custom {@link net.shirojr.pulchra_occultorum.sound.SoundManager sound instance manager}
     */
    String getUniqueId();

    World getSoundOriginWorld();

    Vec3d getSoundPos();

    @Nullable
    Vec3d getVelocity();

    boolean stoppedExisting();
}
