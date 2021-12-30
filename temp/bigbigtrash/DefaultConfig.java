package ch.skyfy.advancedwild.bigbigtrash;

import java.util.ArrayList;
import java.util.List;

public class DefaultConfig {

    public static final List<PlayerTimeRange> PLAYER_TIME_RANGES = new ArrayList<>(){{
        add(new PlayerTimeRange(0, 2000, 0));
        add(new PlayerTimeRange(2000, 4000, 3_600_000));
        add(new PlayerTimeRange(4000, 6000, 7_200_000));
        add(new PlayerTimeRange(6000, 8000, 14_400_000));
        add(new PlayerTimeRange(20_000, 40_000, 864_00_000));
    }};;

    public static final List<GlobalTimeRange> GLOBAL_TIME_RANGES = new ArrayList<>(){{
        add(new GlobalTimeRange(0, 2000, 1));
        add(new GlobalTimeRange(-2000, 4000, 2));
        add(new GlobalTimeRange(-4000, 6000, 4));
        add(new GlobalTimeRange(-6000, 8000, 6));
        add(new GlobalTimeRange(-20_000, 40_000, 16));
        add(new GlobalTimeRange(-20_000, 40_000, 17));
    }};

    public static final int maxWild = Integer.MAX_VALUE;;

    public static final boolean perPlayerWild = true;
}
