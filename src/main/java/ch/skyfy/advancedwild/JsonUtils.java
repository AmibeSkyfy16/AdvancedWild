package ch.skyfy.advancedwild;

import ch.skyfy.advancedwild.impl.WildImpl;
import ch.skyfy.advancedwild.impl.WildImplConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static ch.skyfy.advancedwild.AdvancedWild.MOD_CONFIG_DIR;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <R extends WildImplConfig> R getConfig(File file, Type type) throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        }
    }

    public static <R extends WildImplConfig> void saveRewards(File file, Type type, R r) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(r, type, writer);
        }
    }

    public static @Nullable <R extends WildImpl<? extends WildImplConfig>> R createOrGetConfig(String fileName, Class<R> wildImplClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        var advancedWildConfigFile = MOD_CONFIG_DIR.resolve(fileName + ".json").toFile();
        var type = ((ParameterizedType) wildImplClass.getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class<?> cclass) {
            var wildConfig = (WildImplConfig) cclass.getConstructor().newInstance();
            if (advancedWildConfigFile.exists()) {
                wildConfig = getConfig(advancedWildConfigFile, type);
            } else {
                saveRewards(advancedWildConfigFile, type, wildConfig);
            }
            return wildImplClass.getConstructor(cclass).newInstance(wildConfig);
        }
        return null;
    }

}