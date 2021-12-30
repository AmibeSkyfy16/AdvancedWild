package ch.skyfy.advancedwild.bigbigtrash;

import net.minecraft.util.math.Vec3i;

import java.util.*;

public final class WildConfig {
    public final List<PlayerTimeRange> playerTimeRanges;
    public final List<GlobalTimeRange> globalTimeRanges;
    public final int maxWild;
    public final boolean perPlayerWild;

    public final List<PlayerWild> playerWilds;
    public final GlobalWild globalWild;

    public WildConfig(List<PlayerTimeRange> playerTimeRanges, List<GlobalTimeRange> globalTimeRanges, int maxWild, boolean perPlayerWild) {
        this.playerTimeRanges = playerTimeRanges;
        this.globalTimeRanges = globalTimeRanges;
        this.maxWild = maxWild;
        this.perPlayerWild = perPlayerWild;
        playerWilds = new ArrayList<>();

        var generatedRandomData = new HashMap<Integer, Vec3i>();

        for (var gtr : globalTimeRanges) {
            var randomX = new Random().nextInt(gtr.min(), gtr.max());
            var randomZ = new Random().nextInt(gtr.min(), gtr.max());
            generatedRandomData.put(gtr.count(), new Vec3i(randomX, 255, randomZ));
        }

        globalWild = new GlobalWild(generatedRandomData);
    }
}
