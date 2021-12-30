package ch.skyfy.advancedwild.feature;

import ch.skyfy.advancedwild.AdvancedWild;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

import static ch.skyfy.advancedwild.AdvancedWild.DISABLED;

public class PlayerTimeMeter {

    private static class PlayerTimeMeterHolder {
        public static final PlayerTimeMeter INSTANCE = new PlayerTimeMeter();
    }

    @SuppressWarnings("UnusedReturnValue")
    public static PlayerTimeMeter getInstance() {
        return PlayerTimeMeter.PlayerTimeMeterHolder.INSTANCE;
    }

    /**
     * Called on onInitialize
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initialize() {
        getInstance();
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private final List<PlayerTime> playerTimes;

    private final File playerTimesFolder;

    public PlayerTimeMeter() {
        playerTimesFolder = createConfigDir();
        playerTimes = new ArrayList<>();
        registerEvent();
        startSaverTimer();
    }

    private File createConfigDir(){
        var playerTimesFolder = AdvancedWild.MOD_CONFIG_DIR.resolve("playerTimes").toFile();
        if(!playerTimesFolder.exists())
            if(!playerTimesFolder.mkdir())DISABLED.set(true);
        return playerTimesFolder;
    }

    private void registerEvent() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (playerTimes.stream().noneMatch(playerTime -> playerTime.uuid.equals(handler.player.getUuidAsString()))) {
                playerTimes.add(new PlayerTime(handler.player.getUuidAsString(), System.currentTimeMillis()));
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            System.out.println("total time for player " + handler.player.getEntityName());
            playerTimes.forEach(playerTime -> {
                if (playerTime.uuid.equals(handler.player.getUuidAsString())) {
                    System.out.println("total time is: " + playerTime.time);
                    playerTime.saveTime();
                }
            });
            playerTimes.removeIf(playerTime -> playerTime.uuid.equals(handler.player.getUuidAsString()));
        });
    }

    private void startSaverTimer() {
        new Timer(true).schedule(new TimerTask() {
            @Override
            public void run() {
                playerTimes.forEach(PlayerTime::saveTime);
            }
        }, 120_000, 120_000);
    }

    private boolean isPlayerOnline(String uuid){
       return AdvancedWild.playersConnected.stream().anyMatch(playerConnectedUUID -> playerConnectedUUID.equals(uuid));
    }

    private void saveTimeForSpecificPlayer(String uuid){
        playerTimes.stream().filter(playerTime -> playerTime.uuid.equals(uuid)).findFirst().ifPresent(PlayerTime::saveTime);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    public Long getTime(String uuid){
        saveTimeForSpecificPlayer(uuid);
        return playerTimes.stream().filter(playerTime -> playerTime.uuid.equals(uuid)).findFirst().get().time;
    }

    private static void save(File file, Long r) throws IOException {
        try (var writer = new FileWriter(file)) {
            gson.toJson(r, Long.TYPE, writer);
        }
    }

    private static void get(File file) throws IOException {
        try (var reader = new FileReader(file)) {
            gson.fromJson(reader, (Type) Long.TYPE);
        }
    }

    static class PlayerTime {
        final String uuid;
        private Long startTime, time;
        private final File file;
        public PlayerTime(String uuid, Long startTime) {
            this.uuid = uuid;
            this.startTime = startTime;
            file = getInstance().playerTimesFolder.toPath().resolve(uuid + ".json").toFile();
            this.time = getTime();
        }

        private Long getTime(){
            if(file.exists()){
                try {
                    get(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0L;
        }

        public void saveTime() {
            if(!getInstance().isPlayerOnline(uuid))return;
            var elapsed = System.currentTimeMillis() - startTime;
            time += elapsed;
            System.out.println("Temps écoulé: " + elapsed);
            System.out.println("Temps total: " + time);
            startTime = System.currentTimeMillis();
            try {
                save(file, time);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
