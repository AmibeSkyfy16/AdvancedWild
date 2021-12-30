package ch.skyfy.advancedwild.impl.cgi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

public final class ClassicGlobalImplConfig implements WildImplConfig {
    private static final Long defaultPlayerDelayBetweenWild = 60_000L;
    private static final Long defaultGlobalDelayBetweenWild = 60_000L;
    private static final int defaultMaxWild = 5;
    private static final int defaultMin = -10_000;
    private static final int defaultMax = 10_000;
    public final Long playerDelayBetweenWild;
    public final Long globalDelayBetweenWild;
    public final int maximumWild;
    public final int min;
    public final int max;

    public ClassicGlobalImplConfig(Long playerDelayBetweenWild, Long globalDelayBetweenWild, int maximumWild, int min, int max) {
        this.playerDelayBetweenWild = playerDelayBetweenWild;
        this.globalDelayBetweenWild = globalDelayBetweenWild;
        this.maximumWild = maximumWild;
        this.min = min;
        this.max = max;
    }

    @SuppressWarnings("unused")
    public ClassicGlobalImplConfig() {
        this(defaultPlayerDelayBetweenWild, defaultGlobalDelayBetweenWild, defaultMaxWild, defaultMin, defaultMax);
    }

    @Override
    public boolean isValid() {
        return true;
    }
}
