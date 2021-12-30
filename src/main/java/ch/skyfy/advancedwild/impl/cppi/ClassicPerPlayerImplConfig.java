package ch.skyfy.advancedwild.impl.cppi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

/**
 *
 */
public record ClassicPerPlayerImplConfig(Long delayBetweenWild, int maximumWild, int min, int max) implements WildImplConfig {
    private static final Long defaultDelayBetweenWild = 60_000L;
    private static final int defaultMaxWild = 5;
    private static final int defaultMin = -10_000;
    private static final int defaultMax = 10_000;
    @SuppressWarnings("unused") // This constructor is used by using reflection
    public ClassicPerPlayerImplConfig() { // Return the defaultConfiguration
        this(defaultDelayBetweenWild, defaultMaxWild, defaultMin, defaultMax);
    }
}
