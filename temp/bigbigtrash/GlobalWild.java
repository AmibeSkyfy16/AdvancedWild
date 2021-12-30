package ch.skyfy.advancedwild.bigbigtrash;

import net.minecraft.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;

public class GlobalWild {



    public final Map<String, Integer> playerMap;

    public final Map<Integer, Vec3i> generatedRandomData;

    public GlobalWild(Map<Integer, Vec3i> generatedRandomData) {
        this.generatedRandomData = generatedRandomData;
        this.playerMap = new HashMap<>();
    }
}
