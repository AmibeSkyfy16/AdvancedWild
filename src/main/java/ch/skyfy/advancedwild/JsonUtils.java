package ch.skyfy.advancedwild;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import static ch.skyfy.advancedwild.AdvancedWild.MOD_CONFIG_DIR;

public class JsonUtils {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <R> R get(File file, Type type) throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, type);
        }
    }

    public static <R> void save(File file, Type type, R r) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(r, type, writer);
        }
    }

    public static @Nullable <R> R createOrGetConfig(String fileName, Class<R> configClass) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, IOException {
        var advancedWildConfigFile = MOD_CONFIG_DIR.resolve(fileName).toFile();
        R config = configClass.getConstructor().newInstance();
        if (advancedWildConfigFile.exists())
            config = get(advancedWildConfigFile, configClass);
        else
            save(advancedWildConfigFile, configClass, config);
        return config;
    }

}
