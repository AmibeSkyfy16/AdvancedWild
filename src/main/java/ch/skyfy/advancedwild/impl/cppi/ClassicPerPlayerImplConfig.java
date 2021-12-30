package ch.skyfy.advancedwild.impl.cppi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

/**
 *
 */
public final class ClassicPerPlayerImplConfig implements WildImplConfig {
    private static final Long defaultDelayBetweenWild = 60_000L;
    private static final int defaultMaxWild = 5;
    private static final int defaultMin = -10_000;
    private static final int defaultMax = 10_000;

    public final Long delayBetweenWild;
    public final int maximumWild;
    public final int min;
    public final int max;

    public ClassicPerPlayerImplConfig(Long delayBetweenWild, int maximumWild, int min, int max) {
        this.delayBetweenWild = delayBetweenWild;
        this.maximumWild = maximumWild;
        this.min = min;
        this.max = max;
    }

    @SuppressWarnings("unused") // This constructor is used by using reflection
    public ClassicPerPlayerImplConfig() { // Return the defaultConfiguration
        this(defaultDelayBetweenWild, defaultMaxWild, defaultMin, defaultMax);
    }
}
