package net.shirojr.pulchra_occultorum.screen.widget;

import net.shirojr.pulchra_occultorum.util.ShapeUtil;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScreenElement {
    private final String name;
    private ShapeUtil.Square shape;
    private boolean isPressed, canBeDoubleClicked;
    private int ticksAfterClicked = 0;
    private int hoveredTicks = 0;

    private final ShapeUtil.Square defaultShape;
    private final ShapeUtil.Position minBoundary;
    private final ShapeUtil.Position maxBoundary;
    private final int maxTicksUntilToolTip;

    public ScreenElement(String name, boolean isPressed, int maxTicksUntilToolTip,
                         ShapeUtil.Square shape, ShapeUtil.Square defaultShape,
                         ShapeUtil.Position minBoundary, ShapeUtil.Position maxBoundary) {
        this.name = name;
        this.shape = shape;
        this.defaultShape = defaultShape;
        this.isPressed = isPressed;
        this.minBoundary = minBoundary;
        this.maxBoundary = maxBoundary;
        this.maxTicksUntilToolTip = maxTicksUntilToolTip;
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

    public boolean canBeDoubleClicked() {
        return this.canBeDoubleClicked;
    }

    public void setCanBeDoubleClicked(boolean canBeDoubleClicked) {
        this.canBeDoubleClicked = canBeDoubleClicked;
    }

    public int getTicksAfterClicked() {
        return this.ticksAfterClicked;
    }

    public void setTicksAfterClicked(int ticksAfterClicked) {
        this.ticksAfterClicked = ticksAfterClicked;
    }

    public int getHoveredTicks() {
        return hoveredTicks;
    }

    public void setHoveredTicks(int hoveredTicks) {
        this.hoveredTicks = hoveredTicks;
    }

    public void incrementHoverTicks(int amount) {
        this.setHoveredTicks(this.getHoveredTicks() + amount);
    }

    public int getMaxTicksUntilToolTip() {
        return maxTicksUntilToolTip;
    }

    public ShapeUtil.Position getMinBoundary() {
        return minBoundary;
    }

    public ShapeUtil.Position getMaxBoundary() {
        return maxBoundary;
    }

    public boolean isInDefaultPosition() {
        return this.shape.equals(defaultShape);
    }

    public void setToDefaultPosition() {
        this.shape.moveSquareToTarget(defaultShape.getSquareStart());
    }

    public ShapeUtil.Position getNormalized() {
        float x = this.getShape().getSquareStart().getX();
        float y = this.getShape().getSquareStart().getY();
        float minX = this.getMinBoundary().getX();
        float maxX = this.getMaxBoundary().getX() - this.getShape().getWidth();
        float minY = this.getMinBoundary().getY();
        float maxY = this.getMaxBoundary().getY() - this.getShape().getHeight();
        float normalizedX = (x - minX) / (maxX - minX);
        float normalizedY = (y - minY) / (maxY - minY);
        return new ShapeUtil.Position(normalizedX, normalizedY);
    }
}
