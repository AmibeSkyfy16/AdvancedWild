package ch.skyfy.advancedwild.impl.msi;

@SuppressWarnings("ClassCanBeRecord")
public final class PlayerTimeRange {
    public final int min;
    public final int max;
    public final int delay;

    public PlayerTimeRange(int min, int max, int delay) {
        this.min = min;
        this.max = max;
        this.delay = delay;
    }

}
