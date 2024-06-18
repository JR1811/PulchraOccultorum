package net.shirojr.pulchra_occultorum.api;

import net.minecraft.util.Identifier;
import net.shirojr.pulchra_occultorum.init.CustomRegistries;
import org.jetbrains.annotations.Nullable;

public class OccultEventUtil {

    @Nullable
    public static OccultEvent fromIdentifier(Identifier identifier) {
        return CustomRegistries.OCCULT_EVENTS.get(identifier);
    }

}
