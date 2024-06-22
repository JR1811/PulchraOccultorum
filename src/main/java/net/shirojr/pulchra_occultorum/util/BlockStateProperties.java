package net.shirojr.pulchra_occultorum.util;

import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.StringIdentifiable;

import java.util.Optional;

public class BlockStateProperties {
    public static final EnumProperty<FlagPoleState> FLAG_POLE_STATE =
            EnumProperty.of("part", FlagPoleState.class);

    public enum FlagPoleState implements StringIdentifiable {
        MIDDLE("flag_pole"),
        TOP("flag_pole_top");

        private final String name;

        FlagPoleState(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }

        public static Optional<FlagPoleState> fromString(String name) {
            for (FlagPoleState entry : FlagPoleState.values()) {
                if (entry.asString().equals(name)) return Optional.of(entry);
            }
            return Optional.empty();
        }
    }
}
