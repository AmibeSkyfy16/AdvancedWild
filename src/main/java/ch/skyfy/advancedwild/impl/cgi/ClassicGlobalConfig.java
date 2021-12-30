package ch.skyfy.advancedwild.impl.cgi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

public record ClassicGlobalConfig(Long playerDelayBetweenWild, Long globalDelayBetweenWild, int maximumWild, int min, int max) implements WildImplConfig {
    private static final Long defaultPlayerDelayBetweenWild = 60_000L;
    private static final Long defaultGlobalDelayBetweenWild = 60_000L;
    private static final int defaultMaxWild = 5;
    private static final int defaultMin = -10_000;
    private static final int defaultMax = 10_000;
    @SuppressWarnings("unused") // This constructor is used by using reflection
    public ClassicGlobalConfig() { // Return the defaultConfiguration
        this(defaultPlayerDelayBetweenWild,defaultGlobalDelayBetweenWild, defaultMaxWild, defaultMin, defaultMax);
    }
}
