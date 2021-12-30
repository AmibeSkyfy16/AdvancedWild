package ch.skyfy.advancedwild.impl;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

public abstract class WildImpl<C> {

    public void registerEvents(){}// For some future implementation, we will need to register some events

    public abstract int implement(CommandContext<ServerCommandSource> context);
}
