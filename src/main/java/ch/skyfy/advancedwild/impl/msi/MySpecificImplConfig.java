package ch.skyfy.advancedwild.impl.msi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

import java.util.ArrayList;
import java.util.List;

public final class MySpecificImplConfig implements WildImplConfig {
    public static final List<PlayerTimeRange> PLAYER_TIME_RANGES = new ArrayList<>() {{
        add(new PlayerTimeRange(-10_000, 10_000, 0));
        add(new PlayerTimeRange(-20_000, 20_000, 3_600_000));
        add(new PlayerTimeRange(-25_000, 25_000, 7_200_000));
        add(new PlayerTimeRange(-30_000, 30_000, 14_400_000));
        add(new PlayerTimeRange(-40_000, 40_000, 864_00_000));

        // ******************************** TEST ONLY ******************************** \\
//        add(new PlayerTimeRange(-500, 500, 0));
//        add(new PlayerTimeRange(-5000, 5000, 30_000));
//        add(new PlayerTimeRange(-10_000, 10_000, 60_000));
//        add(new PlayerTimeRange(-20_000, 20_000, 120_000));
//        add(new PlayerTimeRange(-40_000, 40_000, 140_000));
    }};
    private static final boolean defaultExcludedRange = true;
    private static final boolean defaultShouldContinueAfterAllTimeRangeDid = false;
    private static final BasedDelay defaultBasedDelay = BasedDelay.PLAYER_PLAYTIME_BASED;

    enum BasedDelay {
        PLAYER_PLAYTIME_BASED,
        REAL_TIME_BASED
    }

    public final List<PlayerTimeRange> playerTimeRanges;
    public final boolean excludedRange;
    public final boolean shouldContinueAfterAllTimeRangeDid;
    public final BasedDelay basedDelay;

    public MySpecificImplConfig(List<PlayerTimeRange> playerTimeRanges, boolean excludedRange, boolean shouldContinueAfterAllTimeRangeDid, BasedDelay basedDelay) {
        this.playerTimeRanges = playerTimeRanges;
        this.excludedRange = excludedRange;
        this.shouldContinueAfterAllTimeRangeDid = shouldContinueAfterAllTimeRangeDid;
        this.basedDelay = basedDelay;
    }

    @SuppressWarnings("unused")
    public MySpecificImplConfig() {
        this(PLAYER_TIME_RANGES, defaultExcludedRange, defaultShouldContinueAfterAllTimeRangeDid, defaultBasedDelay);
    }

    @Override
    public boolean isValid() {
        PlayerTimeRange previous = null;
        for (PlayerTimeRange playerTimeRange : playerTimeRanges) {
            if (previous != null) {
                // User enter data in a incorrect order
                if (previous.min < playerTimeRange.min || previous.max > playerTimeRange.max) {
                    return false;
                }
                if (previous.delay > playerTimeRange.delay) {
                    return false;
                }
            }
            previous = playerTimeRange;
        }
        return true;
    }

}
