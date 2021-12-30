package ch.skyfy.advancedwild.commands;

import ch.skyfy.advancedwild.Configurator;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class CmdWild {

    static {
        Configurator.getInstance().wildImpl.registerEvents();
    }

    public static void registerWildCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("wild").executes(context -> {
            return Configurator.getInstance().wildImpl.implement(context);
        }));
    }

}
