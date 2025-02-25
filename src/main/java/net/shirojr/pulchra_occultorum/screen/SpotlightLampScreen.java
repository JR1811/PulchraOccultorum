package net.shirojr.pulchra_occultorum.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import net.shirojr.pulchra_occultorum.PulchraOccultorum;
import net.shirojr.pulchra_occultorum.block.entity.SpotlightLampBlockEntity;
import net.shirojr.pulchra_occultorum.network.packet.PositionPacket;
import net.shirojr.pulchra_occultorum.screen.handler.SpotlightLampScreenHandler;
import net.shirojr.pulchra_occultorum.screen.widget.ScreenElement;
import net.shirojr.pulchra_occultorum.util.LoggerUtil;
import net.shirojr.pulchra_occultorum.util.ShapeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SpotlightLampScreen extends HandledScreen<SpotlightLampScreenHandler> {
    private int prevX = -1, prevY = -1;
    private int originX = -1, originY = -1;

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
                new ScreenElement("big_handle", false, 20,
                        ScreenElement.getShapeWithOffset(bigHandle, x, y),
                        ScreenElement.getShapeWithOffset(bigHandle, x, y),
                        ScreenElement.getPositionWithOffset(bigHandle.getSquareStart().add(-66, -48), x, y),
                        ScreenElement.getPositionWithOffset(bigHandle.getSquareEnd().add(66, 48), x, y)
                )
        );

        screenElementList.add(
                new ScreenElement("small_handle", false, 20,
                        ScreenElement.getShapeWithOffset(smallHandle, x, y),
                        ScreenElement.getShapeWithOffset(smallHandle, x, y),
                        ScreenElement.getPositionWithOffset(smallHandle.getSquareStart().add(0, 0), x, y),
                        ScreenElement.getPositionWithOffset(smallHandle.getSquareEnd().add(0, 98), x, y)
                )
        );

        initScreenElementPositions();
    }

    private void initScreenElementPositions() {
        SpotlightLampBlockEntity blockEntity = handler.getBlockEntity();

        ScreenElement bigHandle = ScreenElement.fromList("big_handle", screenElementList);
        if (bigHandle != null) {
            float normalizedYaw = (blockEntity.getTargetRotation().getX() - SpotlightLampBlockEntity.MIN_YAW_RANGE) /
                    (SpotlightLampBlockEntity.MAX_YAW_RANGE - SpotlightLampBlockEntity.MIN_YAW_RANGE);
            float normalizedPitch = (blockEntity.getTargetRotation().getY() - SpotlightLampBlockEntity.MIN_PITCH_RANGE) /
                    (SpotlightLampBlockEntity.MAX_PITCH_RANGE - SpotlightLampBlockEntity.MIN_PITCH_RANGE);
            int yawOnScreen = (int) (MathHelper.lerp(normalizedYaw,
                    bigHandle.getMinBoundary().getX(), bigHandle.getMaxBoundary().getX() - bigHandle.getShape().getWidth()));
            int pitchOnScreen = (int) (MathHelper.lerp(normalizedPitch,
                    bigHandle.getMinBoundary().getY(), bigHandle.getMaxBoundary().getY() - bigHandle.getShape().getHeight()));

            bigHandle.getShape().moveSquareToTarget(yawOnScreen, pitchOnScreen);
        }

        ScreenElement smallHandle = ScreenElement.fromList("small_handle", screenElementList);
        if (smallHandle != null) {
            float normalizedSpeed = blockEntity.getSpeed() / SpotlightLampBlockEntity.MAX_TURNING_SPEED;
            int speedOnScreen = (int) (MathHelper.lerp(normalizedSpeed, smallHandle.getMinBoundary().getY(),
                    smallHandle.getMaxBoundary().getY() - smallHandle.getShape().getHeight()));

            smallHandle.getShape().moveSquareToTarget((int) smallHandle.getShape().getSquareStart().getX(), speedOnScreen);
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

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        SpotlightLampBlockEntity blockEntity = handler.getBlockEntity();
        int informationTextY = this.titleY + 123;

        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);

        drawInformationText(context, "Yaw:", blockEntity.getRotation().getX(), informationTextY);
        informationTextY += 10;
        drawInformationText(context, "Pitch:", blockEntity.getRotation().getY(), informationTextY);
        informationTextY += 10;
        drawInformationText(context, "Speed:", blockEntity.getSpeed(), informationTextY);
    }

    private void drawInformationText(DrawContext context, String display, float value, int y) {
        int horizontalValueOffset = 40;
        Text valueName = Text.of(display);
        Text formattedValue = Text.of(String.valueOf(value));
        context.drawText(this.textRenderer, valueName, this.titleX, y, 4210752, false);
        context.drawText(this.textRenderer, formattedValue, this.titleX + horizontalValueOffset, y, 4210752, false);
    }

    private static void renderScreenElement(ScreenElement element, DrawContext context, int u, int v) {
        int pressedSpriteOffset = element.isPressed() ? v + 9 : v;
        context.drawTexture(PulchraOccultorum.identifierOf("textures/gui/spotlight.png"),
                (int) element.getShape().getSquareStart().getX(), (int) element.getShape().getSquareStart().getY(),
                u, pressedSpriteOffset, (int) element.getShape().getWidth(), (int) element.getShape().getHeight());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
        for (ScreenElement entry : this.screenElementList) {
            if (entry.isPressed()) {
                entry.setHoveredTicks(0);
                continue;
            }
            if (!entry.getShape().isPositionInSquare(new ShapeUtil.Position(mouseX, mouseY))) {
                entry.setHoveredTicks(0);
                continue;
            }
            if (entry.getHoveredTicks() > entry.getMaxTicksUntilToolTip()) {
                // Text sneakKey = Text.translatable(MinecraftClient.getInstance().options.sneakKey.getBoundKeyTranslationKey());
                List<Text> lines = List.of(
                        Text.translatable("screen.pulchra-occultorum.spotlight_lamp.hover1"),
                        Text.translatable("screen.pulchra-occultorum.spotlight_lamp.hover2"),
                        Text.translatable("screen.pulchra-occultorum.spotlight_lamp.hover3")
                        // Text.translatable("screen.pulchra-occultorum.spotlight_lamp.hover3", sneakKey.getString())
                );
                context.drawTooltip(this.textRenderer, lines, Optional.empty(), mouseX, mouseY);
            }
        }
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        super.resize(client, width, height);
        //FIXME: handles shift in wrong places
    }


    @Override
    protected void handledScreenTick() {
        super.handledScreenTick();
        for (ScreenElement entry : this.screenElementList) {
            if (entry.canBeDoubleClicked()) {
                entry.setTicksAfterClicked(entry.getTicksAfterClicked() + 1);
            }
            if (entry.getTicksAfterClicked() > 5) {
                entry.setTicksAfterClicked(0);
                entry.setCanBeDoubleClicked(false);
            }

            entry.incrementHoverTicks(1);
        }
    }

    private void sendPacket(ScreenElement entry, @Nullable Float x, @Nullable Float y) {
        new PositionPacket(entry.getName(), handler.getBlockEntity().getPos(),
                Optional.ofNullable(x), Optional.ofNullable(y)).sendPacket();
    }

    private void resetPosition(ScreenElement element) {
        element.setToDefaultPosition();
        element.setCanBeDoubleClicked(false);
        sendPacket(element, element.getNormalized().getX(), element.getNormalized().getY());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ScreenElement entry : screenElementList) {
            if (entry.getShape().isPositionInSquare(new ShapeUtil.Position((int) mouseX, (int) mouseY))) {
                entry.setPressed(true);
                this.prevX = this.originX = (int) mouseX;
                this.prevY = this.originY = (int) mouseY;
                MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_LEVER_CLICK, 0.5f));
                break;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ScreenElement entry : screenElementList) {
            if (!entry.isPressed()) continue;

            if (entry.getName().equals("big_handle")) {
                sendPacket(entry, entry.getNormalized().getX(), entry.getNormalized().getY());
            }
            if (entry.getName().equals("small_handle")) {
                sendPacket(entry, null, entry.getNormalized().getY());
            }
            this.prevX = -1;
            this.prevY = -1;
            entry.setPressed(false);
            MinecraftClient.getInstance().getSoundManager().play(
                    PositionedSoundInstance.master(SoundEvents.BLOCK_LEVER_CLICK, 0.6f));

            if (!entry.canBeDoubleClicked()) entry.setCanBeDoubleClicked(true);
            else resetPosition(entry);
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);

        for (ScreenElement entry : this.screenElementList) {
            if (!entry.isPressed()) continue;
            entry.setCanBeDoubleClicked(false);
            int draggedHorizontalDistance = (int) mouseX - this.prevX;
            int draggedVerticalDistance = (int) mouseY - this.prevY;

            if (Screen.hasShiftDown()) {

                int distanceX = (int) (mouseX - this.originX);
                int distanceY = (int) (mouseY - this.originY);

                LoggerUtil.devLogger("distance X: %s | origin X: %s".formatted(distanceX, originX));
                LoggerUtil.devLogger("distance Y: %s | origin Y: %s".formatted(distanceY, originY + "\n"));


                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    draggedVerticalDistance = 0;
                    this.prevX = this.originX;
                } else {
                    draggedHorizontalDistance = 0;
                    this.prevY = this.originY;
                }
            }

            entry.getShape().moveElementWithinBoundaries(
                    draggedHorizontalDistance, draggedVerticalDistance,
                    entry.getMinBoundary(), entry.getMaxBoundary()
            );

            this.prevX = (int) mouseX;
            this.prevY = (int) mouseY;
        }
    }
}
