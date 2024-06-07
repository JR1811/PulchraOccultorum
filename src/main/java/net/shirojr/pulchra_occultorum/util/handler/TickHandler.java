package net.shirojr.pulchra_occultorum.util.handler;

public interface TickHandler {
    int getTick();
    void setTick(int tick);
    void incrementTick();
    void resetTick();
}
