package net.shirojr.pulchra_occultorum.screen.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;

public class ButtonScreenElement {
    private final Identifier texture;
    private final int width, height, u, v, uShift, vShift;
    private final int ticksUntilToolTip;
    private final Runnable onRelease;

    private int x, y;
    private int hoveredTicks;
    private boolean isHovered;


    public ButtonScreenElement(Identifier texture, int x, int y, int width, int height, int u, int v, int uShift, int vShift,
                               int ticksUntilToolTop, Runnable onRelease) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.uShift = uShift;
        this.vShift = vShift;
        this.width = width;
        this.height = height;
        this.ticksUntilToolTip = Math.max(ticksUntilToolTop, 0);
        this.hoveredTicks = 0;
        this.isHovered = false;
        this.onRelease = onRelease;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void moveTo(int x, int y) {
        setX(x);
        setY(y);
    }

    public boolean isHovered() {
        return isHovered;
    }

    public void setHovered(boolean pressed) {
        isHovered = pressed;
    }

    public int getTicksUntilToolTip() {
        return ticksUntilToolTip;
    }

    public int getHoveredTicks() {
        return hoveredTicks;
    }

    public void setHoveredTicks(int hoveredTicks) {
        this.hoveredTicks = hoveredTicks;
    }

    public void incrementHoverTicks() {
        this.setHoveredTicks(this.getHoveredTicks() + 1);
    }

    public boolean isInShape(int x, int y) {
        if (x < this.x) return false;
        if (x > this.x + this.width) return false;
        if (y < this.y) return false;
        return y <= this.y + this.height;
    }

    public void draw(DrawContext context, boolean hidden) {
        if (hidden) return;
        int u = this.isHovered() ? this.u + this.uShift : this.u;
        int v = this.isHovered() ? this.v + this.vShift : this.v;
        context.drawTexture(this.texture, this.x, this.y, u, v, this.width, this.height);
    }

    public void run() {
        this.onRelease.run();
        MinecraftClient client = MinecraftClient.getInstance();
        if (client != null) {
            client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK.value(), 1.0f));
        }
    }
}
