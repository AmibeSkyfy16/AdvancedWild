package ch.skyfy.advancedwild.impl.msi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

import java.util.ArrayList;
import java.util.List;

public final class MySpecificImplConfig implements WildImplConfig {
    public static final List<PlayerTimeRange> PLAYER_TIME_RANGES = new ArrayList<>() {{
        add(new PlayerTimeRange(-2000, 2000, 0));

//        add(new PlayerTimeRange(2000, 4000, 3_600_000));
//        add(new PlayerTimeRange(4000, 6000, 7_200_000));
//        add(new PlayerTimeRange(6000, 8000, 14_400_000));
//        add(new PlayerTimeRange(20_000, 40_000, 864_00_000));

        // test only
        add(new PlayerTimeRange(-4000, 4000, 20_000));
        add(new PlayerTimeRange(-6000, 6000, 60_000));
        add(new PlayerTimeRange(-8000, 8000, 80_000));
        add(new PlayerTimeRange(-20_000, 20_000, 90_000));
    }};

    private static final boolean defaultExcludedRange = true;
    private static final boolean defaultShouldContinueAfterAllTimeRangeDid = true;

    public final List<PlayerTimeRange> playerTimeRanges;
    public final boolean excludedRange;
    public final boolean shouldContinueAfterAllTimeRangeDid;

    public MySpecificImplConfig(List<PlayerTimeRange> playerTimeRanges, boolean excludedRange, boolean shouldContinueAfterAllTimeRangeDid) {
        this.playerTimeRanges = playerTimeRanges;
        this.excludedRange = excludedRange;
        this.shouldContinueAfterAllTimeRangeDid = shouldContinueAfterAllTimeRangeDid;
    }

    @SuppressWarnings("unused") // This constructor is used by using reflection
    public MySpecificImplConfig() { // Return the defaultConfiguration
        this(PLAYER_TIME_RANGES, defaultExcludedRange, defaultShouldContinueAfterAllTimeRangeDid);
    }

}
