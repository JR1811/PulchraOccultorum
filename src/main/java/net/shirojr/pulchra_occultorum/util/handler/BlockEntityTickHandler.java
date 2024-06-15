package net.shirojr.pulchra_occultorum.util.handler;

public interface BlockEntityTickHandler {
    int getTick();
    void setTick(int tick);
    void incrementTick(boolean saveToNbt);
    void resetTick();

    default boolean hasMaxTicks() {
        return getMaxTicks() > -1;
    }

    default int getMaxTicks() {
        return -1;
    }
}
