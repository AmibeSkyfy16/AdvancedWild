package ch.skyfy.advancedwild.bigbigtrash;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public abstract class WildImpl  {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public String name;

    public WildImpl(String name) {
        this.name = name;
    }

//    private WildImpl createOrGetWildConfig() {
//        var advancedWildConfigFile = MOD_CONFIG_DIR.resolve(name+".json").toFile();
//        try {
//            var defaultadvancedWildConfig = this.getDefault();
//            if (advancedWildConfigFile.exists()) {
//                return getConfig(advancedWildConfigFile, this.getClass());
//            } else {
//                saveRewards(advancedWildConfigFile, WildImpl.class, defaultadvancedWildConfig);
//                return defaultadvancedWildConfig;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//            DISABLED.set(true);
//        }
//        return null;
//    }

    private <R> R getConfig(File file, Class<R> rClass) throws IOException {
        try (var reader = new FileReader(file)) {
            return gson.fromJson(reader, rClass);
        }
    }

    private <R extends WildImpl> void saveRewards(File file, Class<R> rClass, R r) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(r, rClass, writer);
        }
    }

}
