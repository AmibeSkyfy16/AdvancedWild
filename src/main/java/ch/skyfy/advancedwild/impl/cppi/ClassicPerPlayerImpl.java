package ch.skyfy.advancedwild.impl.cppi;

import ch.skyfy.advancedwild.impl.WildImpl;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Implementation of a classic wild command
 *
 * There are three things that can be configured
 * The delay between two /wild commands                      -> default: 60 secondes
 * The range of the /wild                                    -> default: between -5000 and 5000
 * The maximum number of /wild commands a player can do      -> default: 5
 */
public class ClassicPerPlayerImpl extends WildImpl<ClassicPerPlayerImplConfig> {

    private final ClassicPerPlayerImplConfig classicPerPlayerImplConfig;

    public ClassicPerPlayerImpl(ClassicPerPlayerImplConfig config) {
        classicPerPlayerImplConfig = config;
    }

    @Override
    public int implement(CommandContext<ServerCommandSource> context) {
        var delay = classicPerPlayerImplConfig.delayBetweenWild();
        var maximumWild = classicPerPlayerImplConfig.maximumWild();
        var min = classicPerPlayerImplConfig.min();
        var max = classicPerPlayerImplConfig.max();
        // TODO do
        return Command.SINGLE_SUCCESS;
    }
}
