import ch.skyfy.advancedwild.impl.cppi.ClassicPerPlayerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;

public class Test {


    @org.junit.jupiter.api.Test
    public void test3() {
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

}
