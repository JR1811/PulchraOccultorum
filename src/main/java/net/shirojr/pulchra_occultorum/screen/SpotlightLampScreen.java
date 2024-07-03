package net.shirojr.pulchra_occultorum.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.network.packet.PositionPacket;
import net.shirojr.pulchra_occultorum.screen.handler.SpotlightLampScreenHandler;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.ShapeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpotlightLampScreen extends HandledScreen<SpotlightLampScreenHandler> {
    private int tick = 0;
    private int prevX = -1, prevY = -1;

    private final List<ScreenElement> screenElementList = new ArrayList<>();

    public SpotlightLampScreen(SpotlightLampScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        ShapeUtil.Square bigHandle = new ShapeUtil.Square(74, 66, 83, 75);
        ShapeUtil.Square smallHandle = new ShapeUtil.Square(159, 17, 165, 26);

        screenElementList.add(
                new ScreenElement("big_handle", false,
                        ScreenElement.getShapeWithOffset(bigHandle, x, y),
                        ScreenElement.getShapeWithOffset(bigHandle, x, y),
                        ScreenElement.getPositionWithOffset(bigHandle.getSquareStart().add(-66, -48), x, y),
                        ScreenElement.getPositionWithOffset(bigHandle.getSquareEnd().add(66, 48), x, y)
                )
        );

        screenElementList.add(
                new ScreenElement("small_handle", false,
                        ScreenElement.getShapeWithOffset(smallHandle, x, y),
                        ScreenElement.getShapeWithOffset(smallHandle, x, y),
                        ScreenElement.getPositionWithOffset(smallHandle.getSquareStart().add(0, 0), x, y),
                        ScreenElement.getPositionWithOffset(smallHandle.getSquareEnd().add(0, 98), x, y)
                )
        );
        for (ScreenElement entry : screenElementList) {
            sendPacket(entry, 0f, 0f);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        if (this.client == null) return;

        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(PulchraOccultorum.identifierOf("textures/gui/spotlight.png"),
                x, y, 0, 0, this.backgroundWidth, this.backgroundHeight);

        ScreenElement bigHandle = ScreenElement.fromList("big_handle", screenElementList);
        ScreenElement smallHandle = ScreenElement.fromList("small_handle", screenElementList);

        if (bigHandle != null) renderScreenElement(bigHandle, context, 183, 0);
        if (smallHandle != null) renderScreenElement(smallHandle, context, 176, 0);
    }

    private static void renderScreenElement(ScreenElement element, DrawContext context, int u, int v) {
        int pressedSpriteOffset = element.isPressed() ? v + 9 : v;
        context.drawTexture(PulchraOccultorum.identifierOf("textures/gui/spotlight.png"),
                (int) element.getShape().getSquareStart().getX(), (int) element.getShape().getSquareStart().getY(),
                u, pressedSpriteOffset, (int) element.getShape().getWidth(), (int) element.getShape().getHeight());
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        this.tick++;

        for (ScreenElement entry : this.screenElementList) {
            if (entry.isPressed() || entry.isInDefaultPosition()) continue;
            if (entry.getName().equals("big_handle")) {
                float normalizedX = (entry.getShape().getSquareStart().getX() - entry.getMin().getX()) /
                        (entry.getMax().getX() - entry.getMin().getX());
                float normalizedY = (entry.getShape().getSquareStart().getY() - entry.getMin().getY()) /
                        (entry.getMax().getY() - entry.getMin().getY());

                ClientPlayNetworking.send(new PositionPacket(entry.getName(), handler.getBlockEntity().getPos(),
                        Optional.of(normalizedX), Optional.of(normalizedY)));
            }
            if (entry.getName().equals("small_handle")) {
                float normalizedY = (entry.getShape().getSquareStart().getY() - entry.getMin().getY()) /
                        (entry.getMax().getY() - entry.getMin().getY());
                sendPacket(entry, null, normalizedY);
            }
        }
    }

    private void sendPacket(ScreenElement entry, @Nullable Float x, @Nullable Float y) {
        ClientPlayNetworking.send(new PositionPacket(entry.getName(), handler.getBlockEntity().getPos(),
                Optional.ofNullable(x), Optional.ofNullable(y)));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ScreenElement entry : screenElementList) {
            if (entry.getShape().isPositionInSquare(new ShapeUtil.Position((int) mouseX, (int) mouseY))) {
                entry.setPressed(true);
                this.prevX = (int) mouseX;
                this.prevY = (int) mouseY;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_LEVER_CLICK, 0.5f));
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (var entry : screenElementList) {
            if (entry.isPressed()) {
                this.prevX = -1;
                this.prevY = -1;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_LEVER_CLICK, 0.6f));
            }
            entry.setPressed(false);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        for (ScreenElement entry : screenElementList) {
            if (!entry.isPressed()) continue;

            int draggedHorizontalDistance = (int) mouseX - this.prevX;
            int draggedVerticalDistance = (int) mouseY - this.prevY;
            entry.getShape().moveSquareWithBoundaries(
                    draggedHorizontalDistance, draggedVerticalDistance,
                    entry.getMin(), entry.getMax()
            );

            this.prevX = (int) mouseX;
            this.prevY = (int) mouseY;

            /*LoggerUtil.devLogger("%s %s | %s %s".formatted(entry.shape.getSquareStart().getX(), entry.shape.getSquareStart().getY(),
                    entry.defaultShape.getSquareStart().getX(), entry.defaultShape.getSquareStart().getY()));*/
        }
    }


    static class ScreenElement {
        private final String name;
        private ShapeUtil.Square shape;
        private final ShapeUtil.Square defaultShape;
        private boolean isPressed;
        private final ShapeUtil.Position min;
        private final ShapeUtil.Position max;

        public ScreenElement(String name, boolean isPressed, ShapeUtil.Square shape, ShapeUtil.Square defaultShape, ShapeUtil.Position min, ShapeUtil.Position max) {
            this.name = name;
            this.shape = shape;
            this.defaultShape = defaultShape;
            this.isPressed = isPressed;
            this.min = min;
            this.max = max;
        }

        @Nullable
        public static ScreenElement fromList(String name, List<ScreenElement> elements) {
            for (ScreenElement element : elements) {
                if (element.getName().equals(name)) return element;
            }
            return null;
        }

        public String getName() {
            return name;
        }

        public ShapeUtil.Square getShape() {
            return shape;
        }

        public static ShapeUtil.Square getShapeWithOffset(ShapeUtil.Square shape, int offsetX, int offsetY) {
            return new ShapeUtil.Square(shape.getSquareStart().add(offsetX, offsetY), shape.getSquareEnd().add(offsetX, offsetY));
        }

        public static ShapeUtil.Position getPositionWithOffset(ShapeUtil.Position position, int offsetX, int offsetY) {
            return position.add(offsetX, offsetY);
        }

        public void setShape(ShapeUtil.Square shape) {
            this.shape = shape;
        }

        public ShapeUtil.Square getDefaultShape() {
            return defaultShape;
        }

        public boolean isPressed() {
            return isPressed;
        }

        public void setPressed(boolean pressed) {
            isPressed = pressed;
        }

        public ShapeUtil.Position getMin() {
            return min;
        }

        public ShapeUtil.Position getMax() {
            return max;
        }

        public boolean isInDefaultPosition() {
            return this.shape.equals(defaultShape);
        }
    }
}
