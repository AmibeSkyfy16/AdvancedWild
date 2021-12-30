package ch.skyfy.advancedwild.impl;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;

@SuppressWarnings("unused")
public abstract class WildImpl<C> {
    public void registerEvents(){}

    public abstract int implement(CommandContext<ServerCommandSource> context);
}
