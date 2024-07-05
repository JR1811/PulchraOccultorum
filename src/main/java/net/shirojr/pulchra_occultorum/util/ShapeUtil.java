package net.shirojr.pulchra_occultorum.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.math.MathHelper;

public class ShapeUtil {
    public static class Position {
        private float x, y;

        public Position(int x, int y) {
            this.x = (float) x;
            this.y = (float) y;
        }

        public Position(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public void setX(float x) {
            this.x = x;
        }

        public float getY() {
            return y;
        }

        public void setY(float y) {
            this.y = y;
        }

        public Position add(Position input) {
            return new Position(this.getX() + input.getX(), this.getY() + input.getY());
        }

        public Position add(float x, float y) {
            Position position = new Position(x, y);
            return add(position);
        }

        public Position getShiftedValue(Position input) {
            return new Position(input.getX() - this.getX(), input.getY() - this.getY());
        }

        public boolean equals(Position otherPosition) {
            return this.getX() == otherPosition.getX() && this.getY() == otherPosition.getY();
        }

        public static Position fromNbt(NbtCompound nbt) {
            NbtCompound compound = nbt.getCompound("position");
            return new Position(compound.getFloat("x"), compound.getFloat("y"));
        }

        public String toString() {
            return "x: %s | y: %s".formatted(this.getX(), this.getY());
        }

        public void toNbt(NbtCompound nbt) {
            NbtCompound compound = new NbtCompound();
            compound.putFloat("x", this.getX());
            compound.putFloat("y", this.getY());
            nbt.put("position", compound);
        }

        public static PacketCodec<RegistryByteBuf, Position> CODEC_POSITION = PacketCodec.tuple(
                PacketCodecs.FLOAT, Position::getX,
                PacketCodecs.FLOAT, Position::getY,
                Position::new);
    }

    public static class Square {
        private Position squareStart, squareEnd;

        /**
         * Data type to make working with square hit registration easier
         *
         * @param posStart top left corner of square
         * @param posEnd   bottom right corner of square
         */
        public Square(Position posStart, Position posEnd) {
            this.squareStart = posStart;
            this.squareEnd = posEnd;
        }

        public Square (int startX, int startY, int endX, int endY) {
            this(new Position(startX, startY), new Position(endX, endY));
        }

        /**
         * Data type to make working with square hit registration easier
         *
         * @param squareStart top left corner of square
         * @param width       width of square
         * @param height      height of square
         */
        public Square(Position squareStart, int width, int height) {
            this.squareStart = squareStart;
            this.squareEnd = new Position(squareStart.x + width, squareStart.y + height);
        }

        public boolean isPositionInSquare(Position position) {
            return position.x >= this.getSquareStart().x && position.y >= this.getSquareStart().y &&
                    position.x <= this.getSquareEnd().x && position.y <= this.getSquareEnd().y;
        }

        public Position getSquareStart() {
            return squareStart;
        }

        public void setSquareStart(Position squareStart) {
            this.squareStart = squareStart;
        }

        public Position getSquareEnd() {
            return squareEnd;
        }

        public void setSquareEnd(Position squareEnd) {
            this.squareEnd = squareEnd;
        }

        public float getWidth() {
            return MathHelper.abs(getSquareEnd().x - getSquareStart().x);
        }

        public float getHeight() {
            return MathHelper.abs(getSquareEnd().y - getSquareStart().y);
        }

        public void moveSquare(int x, int y) {
            this.moveSquareToTarget(this.getSquareStart().add(x, y));
        }

        public void moveSquareWithBoundaries(int x, int y, Position start, Position end) {
            float newStartX = this.getSquareStart().getX() + x;
            float newStartY = this.getSquareStart().getY() + y;
            float newEndX = this.getSquareEnd().getX() + x;
            float newEndY = this.getSquareEnd().getY() + y;

            if (newStartX < start.getX()) {
                newStartX = start.getX();
                newEndX = start.getX() + this.getWidth();
            }
            if (newStartY < start.getY()) {
                newStartY = start.getY();
                newEndY = start.getY() + this.getHeight();
            }
            if (newEndX > end.getX()) {
                newEndX = end.getX();
                newStartX = end.getX() - this.getWidth();
            }
            if (newEndY > end.getY()) {
                newEndY = end.getY();
                newStartY = end.getY() - this.getHeight();
            }
            this.getSquareStart().setX(newStartX);
            this.getSquareStart().setY(newStartY);
            this.getSquareEnd().setX(newEndX);
            this.getSquareEnd().setY(newEndY);
        }

        public void moveSquareToTarget(Position newStartPosition) {
            Position movingDistance = this.getSquareStart().getShiftedValue(newStartPosition);
            this.setSquareStart(newStartPosition);
            this.setSquareEnd(this.getSquareEnd().add(movingDistance));
        }

        public void moveSquareToTarget(int newStartX, int newStartY) {
            this.moveSquareToTarget(new Position(newStartX, newStartY));
        }

        public boolean equals(Square otherSquare) {
            return this.getSquareStart().equals(otherSquare.getSquareStart()) && this.getSquareEnd().equals(otherSquare.getSquareEnd());
        }

        public static Square fromNbt(NbtCompound nbt) {
            NbtCompound startNbt = nbt.getCompound("start");
            NbtCompound endNbt = nbt.getCompound("end");
            Position start = Position.fromNbt(startNbt);
            Position end = Position.fromNbt(endNbt);
            return new Square(start, end);
        }

        public void toNbt(NbtCompound nbt) {
            NbtCompound startNbt = new NbtCompound();
            NbtCompound endNbt = new NbtCompound();
            this.getSquareStart().toNbt(startNbt);
            this.getSquareEnd().toNbt(endNbt);
            nbt.put("start", startNbt);
            nbt.put("end", endNbt);
        }

        public static PacketCodec<RegistryByteBuf, Square> CODEC_SQUARE = PacketCodec.tuple(
                Position.CODEC_POSITION, Square::getSquareStart,
                Position.CODEC_POSITION, Square::getSquareEnd,
                Square::new);
    }
}