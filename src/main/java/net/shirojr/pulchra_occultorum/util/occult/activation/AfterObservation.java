package net.shirojr.pulchra_occultorum.util.occult.activation;

public class AfterObservation {
    private boolean observed;
    private final boolean canRepeat;

    public AfterObservation(boolean wasObserved, boolean canRepeat) {
        this.observed = wasObserved;
        this.canRepeat = canRepeat;
    }

    public boolean wasObserved() {
        return this.observed;
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
    }

    public boolean canRepeat() {
        return this.canRepeat;
    }
}
