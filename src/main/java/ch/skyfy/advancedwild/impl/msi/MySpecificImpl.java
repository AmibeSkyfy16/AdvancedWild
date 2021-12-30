package ch.skyfy.advancedwild.impl.msi;

import ch.skyfy.advancedwild.impl.WildImpl;
import ch.skyfy.advancedwild.impl.cgi.ClassicGlobalConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

/**
 * Implementation of a more complexe wild command
 *
 * Description
 *
 * Each time a player uses the /wild command, the scope will be enlarged. But also, the waiting time before using /wild will be increased too
 *
 * There are three things that can be configured
 * The player time range                        -> A list of delay and range
 * The excludedRange                            -> If it is necessary to exclude the row of the previous index from the list<PlayerTimeRanges> when calculating the new random appearance point
 * The shouldContinueAfterAllTimeRangeDid       -> if the player will still be able to use /wild after using the number of times == to the size of the list<PlayerTimeRanges>
 */
public class MySpecificImpl extends WildImpl<ClassicGlobalConfig> {

    private final MySpecificImplConfig mySpecificImplConfig;

    public MySpecificImpl(MySpecificImplConfig config) {
        mySpecificImplConfig = config;
    }

    @Override
    public int implement(CommandContext<ServerCommandSource> context) {
        var playerTimeRanges = mySpecificImplConfig.playerTimeRanges();
        var excludedRange = mySpecificImplConfig.excludedRange();
        var shouldContinueAfterAllTimeRangeDid = mySpecificImplConfig.shouldContinueAfterAllTimeRangeDid();
        // TODO do
        return Command.SINGLE_SUCCESS;
    }
}
