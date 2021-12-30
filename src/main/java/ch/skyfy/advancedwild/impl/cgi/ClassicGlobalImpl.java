package ch.skyfy.advancedwild.impl.cgi;

import ch.skyfy.advancedwild.impl.WildImpl;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Implementation of a global wild command
 * <p>
 * There are four things that can be configured
 * The player delay between two /wild commands                              -> default: 60 secondes (i recommand using the same value as global delay)
 * The global delay before the server changes the random spawn point again  -> default: 60 secondes (i recommand using the same value as player delay)
 * The range of the /wild                                                   -> default: between -5000 and 5000
 * The maximum number of /wild commands a player can do                     -> default: 5
 */
public class ClassicGlobalImpl extends WildImpl<ClassicGlobalConfig> {

    private final ClassicGlobalConfig classicGlobalConfig;

    public ClassicGlobalImpl(ClassicGlobalConfig config) {
        classicGlobalConfig = config;
    }

    @Override
    public int implement(CommandContext<ServerCommandSource> context) {
        var playerDelay = classicGlobalConfig.playerDelayBetweenWild;
        var globalDelay = classicGlobalConfig.globalDelayBetweenWild;
        var maximumWild = classicGlobalConfig.maximumWild;
        var min = classicGlobalConfig.min;
        var max = classicGlobalConfig.max;
        // TODO do
        return Command.SINGLE_SUCCESS;
    }
}
