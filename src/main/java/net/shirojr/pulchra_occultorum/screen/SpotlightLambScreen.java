package net.shirojr.pulchra_occultorum.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.shirojr.pulchra_occultorum.screen.handler.SpotlightLampScreenHandler;

public class SpotlightLambScreen extends HandledScreen<SpotlightLampScreenHandler> {
    public SpotlightLambScreen(SpotlightLampScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }
}
