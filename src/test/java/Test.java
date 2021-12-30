import ch.skyfy.advancedwild.impl.cppi.ClassicPerPlayerImpl;
import ch.skyfy.advancedwild.impl.msi.PlayerTimeRange;
import net.minecraft.util.math.Vec3i;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Random;

public class Test {

    private final Random randomizer = new Random();

    @org.junit.jupiter.api.Test
    public void test3() {
        if(0 == 0)return;
        var pre  = new PlayerTimeRange(-4000, 4000, 0);
        var range  = new PlayerTimeRange(-4500, 4500, 0);

        var v = getCorrectRandom(pre, range);
        System.out.println(v);

        if(0 == 0)return;
        var r = ((ParameterizedType) ClassicPerPlayerImpl.class.getGenericSuperclass()).getActualTypeArguments()[0];

        if (r instanceof Class<?> cclass) {
            try {
                var r2 = cclass.getConstructor().newInstance();
                System.out.println();
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

    }

    private Vec3i getCorrectRandom(PlayerTimeRange previous, PlayerTimeRange range) {
        boolean foundCorrectRandom = false;
        int x, z;
        do {
            x = randomizer.nextInt(range.min, range.max);
            z = randomizer.nextInt(range.min, range.max);

            if((x <= previous.min || x >= previous.max) && (z <= previous.min || z >= previous.max)){
                System.out.println();
                foundCorrectRandom = true;
                break;
            }
//            if(z <= previous.min || z >= previous.max){
//                System.out.println();
//                foundCorrectRandom = true;
//                break;
//            }
//            if(x > previous.min && x < previous.max){
//                continue;
//            }
//            if(z > previous.min && z < previous.max){
//                continue;
//            }

//            if (x <= previous.min && x <= previous.max ||
//                    z <= previous.min && z <= previous.max) continue;

        } while (!foundCorrectRandom);
        return new Vec3i(x, 320, z);
    }

}
