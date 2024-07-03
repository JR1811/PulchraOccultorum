package net.shirojr.pulchra_occultorum.init;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.screen.handler.SpotlightLampScreenHandler;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class ScreenHandlers {
    public static final ScreenHandlerType<SpotlightLampScreenHandler> SPOTLIGHT_LAMP_SCREEN_HANDLER =
            registerExtended("spotlight_lamp", new ExtendedScreenHandlerType<>(
                    SpotlightLampScreenHandler::new,
                    SpotlightLampBlockEntity.Data.CODEC)
            );

    private static <T extends ScreenHandler> ScreenHandlerType<T> registerExtended(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, PulchraOccultorum.identifierOf(name), screenHandlerType);
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized ScreenHandlers");
    }
}
