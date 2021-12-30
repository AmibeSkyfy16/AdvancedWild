package ch.skyfy.advancedwild;

import ch.skyfy.advancedwild.impl.InvalidDataException;
import ch.skyfy.advancedwild.impl.WildImpl;
import ch.skyfy.advancedwild.impl.WildImplConfig;
import ch.skyfy.advancedwild.impl.WildImplConfigUtils;
import ch.skyfy.advancedwild.impl.cgi.ClassicGlobalImpl;
import ch.skyfy.advancedwild.impl.cppi.ClassicPerPlayerImpl;
import ch.skyfy.advancedwild.impl.msi.MySpecificImpl;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static ch.skyfy.advancedwild.AdvancedWild.DISABLED;

/**
 * This class will create default configuration if none exist
 * else this class will read default configuration file
 */
public class Configurator {

    private static class ConfiguratorHolder {
        public static final Configurator INSTANCE = new Configurator();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static Configurator getInstance() {
        return Configurator.ConfiguratorHolder.INSTANCE;
    }

    /**
     * Called on onInitialize
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initialize() {
        getInstance();
    }

    private static final Map<String, Class<? extends WildImpl<? extends WildImplConfig>>> type2 = new HashMap<>() {{
        put("classicPerPlayerImpl", ClassicPerPlayerImpl.class);
        put("ClassicGlobalImpl", ClassicGlobalImpl.class);
        put("MySpecificImpl", MySpecificImpl.class);
    }};

    public WildImpl<? extends WildImplConfig> wildImpl;

    @SuppressWarnings("ConstantConditions")
    public Configurator() {

        try {
            var defaultAdvancedWildConfig = JsonUtils.createOrGetConfig("config.json", AdvancedWildConfig.class);
            var choseImplType = type2.get(defaultAdvancedWildConfig.typeImpl);
            wildImpl = WildImplConfigUtils.createOrGetConfig(defaultAdvancedWildConfig.typeImpl, choseImplType);
        } catch (InvalidDataException | NullPointerException | NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
            System.out.println("\n[Advanced Wilds] An error occured");
            System.out.println("[Advanced Wilds] Please check if key value in config.json file are one of this.");
            type2.keySet().forEach(System.out::println);
            System.out.println("[Advanced Wilds] If you changed settings, you have to restart the server");
            DISABLED.set(true);
        }
    }
}
