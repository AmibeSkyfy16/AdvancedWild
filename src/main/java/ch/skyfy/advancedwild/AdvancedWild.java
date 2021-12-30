package ch.skyfy.advancedwild;

import ch.skyfy.advancedwild.commands.CmdWild;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public class AdvancedWild implements ModInitializer {

    public static AtomicBoolean DISABLED = new AtomicBoolean(false);

    public static final String MOD_ID = "advanced_wild";

    public static Path MOD_CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("AdvancedWild");

    @Override
    public void onInitialize() {
        if (createConfigDir()) return;
        Configurator.initialize();
        if(DISABLED.get())return; // If an error occured in Configurator.initialize() DISABLED will be true, and mod will don't run anymore
        registerCommand();
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
