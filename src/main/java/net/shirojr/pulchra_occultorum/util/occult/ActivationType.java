package net.shirojr.pulchra_occultorum.util.occult;

@SuppressWarnings("ALL")
public interface ActivationType {




    public static class AfterObservation {
        private boolean observed;
        public AfterObservation(boolean wasObserved) {
            this.observed = wasObserved;
        }

        public boolean isObserved() {
            return this.observed;
        }

        public void setObserved(boolean observed) {
            this.observed = observed;
        }
    }

}
