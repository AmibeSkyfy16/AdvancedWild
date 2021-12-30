package ch.skyfy.advancedwild.impl.msi;

import ch.skyfy.advancedwild.impl.WildImplConfig;

import java.util.ArrayList;
import java.util.List;

public record MySpecificImplConfig(List<PlayerTimeRange> playerTimeRanges, boolean excludedRange, boolean shouldContinueAfterAllTimeRangeDid) implements WildImplConfig {
    public static final List<PlayerTimeRange> PLAYER_TIME_RANGES = new ArrayList<>() {{
        add(new PlayerTimeRange(0, 2000, 0));
        add(new PlayerTimeRange(2000, 4000, 3_600_000));
        add(new PlayerTimeRange(4000, 6000, 7_200_000));
        add(new PlayerTimeRange(6000, 8000, 14_400_000));
        add(new PlayerTimeRange(20_000, 40_000, 864_00_000));
    }};

    private static final boolean defaultExcludedRange = true;
    private static final boolean defaultShouldContinueAfterAllTimeRangeDid = true;
    @SuppressWarnings("unused") // This constructor is used by using reflection
    public MySpecificImplConfig() { // Return the defaultConfiguration
        this(PLAYER_TIME_RANGES, defaultExcludedRange, defaultShouldContinueAfterAllTimeRangeDid);
    }
}
