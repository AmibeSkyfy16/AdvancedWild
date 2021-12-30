package ch.skyfy.advancedwild;

import ch.skyfy.advancedwild.commands.CmdWild;
import ch.skyfy.advancedwild.feature.PlayerTimeMeter;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public class AdvancedWild implements ModInitializer {

    public static AtomicBoolean DISABLED = new AtomicBoolean(false);

    public static final String MOD_ID = "advanced_wild";

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("AdvancedWild");

    public static List<String> playersConnected = new ArrayList<>();

    @Override
    public void onInitialize() {
        if (createConfigDir()) return;
        Configurator.initialize();
        PlayerTimeMeter.initialize();
        if (DISABLED.get())
            return; // If an error occured in Configurator.initialize() DISABLED will be true, and mod will don't run anymore
        registerCommand();
        registerEvents();
    }

    public void registerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!playersConnected.contains(handler.player.getUuidAsString())) {
                playersConnected.add(handler.player.getUuidAsString());
            }
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            playersConnected.removeIf(uuid -> uuid.equals(handler.player.getUuidAsString()));
        });
    }

    public void registerCommand() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            CmdWild.registerWildCommand(dispatcher);
        });
    }

    private boolean createConfigDir() {
        var configDir = MOD_CONFIG_DIR.toFile();
        if (!configDir.exists()) {
            var result = configDir.mkdir();
            if (!result) {
                System.out.println("[AntiCheater] The configuration cannot be created");
                DISABLED.set(true);
            }
        }
        return DISABLED.get();
    }
}
