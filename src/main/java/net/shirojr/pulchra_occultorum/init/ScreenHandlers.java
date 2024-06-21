package net.shirojr.pulchra_occultorum.init;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.screen.handler.SpotlightLampScreenHandler;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;

public class ScreenHandlers {
    public static ScreenHandlerType<SpotlightLampScreenHandler> SPOTLIGHT_LAMP_SCREEN_HANDLER =
            register("spotlight_lamp", new ScreenHandlerType<>(SpotlightLampScreenHandler::new, FeatureFlags.VANILLA_FEATURES));


    private static <T extends ScreenHandler> ScreenHandlerType<T> register(String name, ScreenHandlerType<T> screenHandlerType) {
        return Registry.register(Registries.SCREEN_HANDLER, PulchraOccultorum.identifierOf(name), screenHandlerType);
    }

    public static void initialize() {
        LoggerUtil.devLogger("initialized ScreenHandlers");
    }
}
