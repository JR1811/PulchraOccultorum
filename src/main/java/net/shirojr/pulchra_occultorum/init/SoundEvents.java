package net.shirojr.pulchra_occultorum.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class SoundEvents {
    public static SoundEvent UNICYCLE_LAND = register("unicycle_land");
    public static SoundEvent UNICYCLE_ROLL = register("unicycle_roll");

    private static SoundEvent register(String name) {
        Identifier identifier = PulchraOccultorum.identifierOf(name);
        SoundEvent soundEvent = SoundEvent.of(identifier);
        return Registry.register(Registries.SOUND_EVENT, identifier, soundEvent);
    }

    public static void initialize() {
        LoggerUtil.devLogger("Initialized Sounds");
    }
}
