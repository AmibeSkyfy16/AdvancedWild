//package ch.skyfy.advancedwild.bigbigtrash;
//
//import ch.skyfy.advancedwild.Configurator;
//import com.mojang.brigadier.context.CommandContext;
//import com.mojang.brigadier.exceptions.CommandSyntaxException;
//import net.minecraft.server.command.ServerCommandSource;
//import net.minecraft.server.network.ServerPlayerEntity;
//
//import java.util.Collections;
//
//public class AdvancedWildImpl {
//
//    public static void implement(CommandContext<ServerCommandSource> context) {
//        var wildConfig = Configurator.getInstance().wildConfig;
//
//        var gtrs = wildConfig.globalTimeRanges;
//        gtrs.sort((o1, o2) -> Integer.compare(o2.count(), o1.count()));
//        Collections.reverse(gtrs);
//
//        ServerPlayerEntity player;
//        try {
//            player = context.getSource().getPlayer();
//        } catch (CommandSyntaxException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        if (wildConfig.perPlayerWild) {
//            if (wildConfig.playerWilds.stream().noneMatch(playerWild -> playerWild.uuid.equalsIgnoreCase(player.getUuidAsString()))) {
//                wildConfig.playerWilds.add(new PlayerWild(player.getUuidAsString()));
//
//            }
//
//        }
//
//    }
//
//    @SuppressWarnings("ConstantConditions")
//    private static int getCorrectGlobalTimeRange(int count) {
//        var gtrs = Configurator.getInstance().wildConfig.globalTimeRanges;
//        gtrs.sort((o1, o2) -> Integer.compare(o2.count(), o1.count()));
//        Collections.reverse(gtrs);
//        for (int i = 0; i < gtrs.size(); i++) {
//            var item = gtrs.get(i);
//            if (count <= item.count())
//                return gtrs.get(i - 1).count();
//        }
//        return -1;
//    }
//
//}
