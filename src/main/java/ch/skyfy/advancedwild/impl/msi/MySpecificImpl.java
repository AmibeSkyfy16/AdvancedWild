package ch.skyfy.advancedwild.impl.msi;

import ch.skyfy.advancedwild.AdvancedWild;
import ch.skyfy.advancedwild.impl.WildImpl;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ch.skyfy.advancedwild.AdvancedWild.MOD_CONFIG_DIR;

/**
 * Implementation of a more complexe wild command
 * <p>
 * Description
 * <p>
 * Each time a player uses the /wild command, the scope will be enlarged. But also, the waiting time before using /wild will be increased too
 * <p>
 * There are three things that can be configured
 * The player time range                        -> A list of delay and range
 * The excludedRange                            -> If it is necessary to exclude the row of the previous index from the list<PlayerTimeRanges> when calculating the new random appearance point
 * The shouldContinueAfterAllTimeRangeDid       -> if the player will still be able to use /wild after using the number of times == to the size of the list<PlayerTimeRanges>
 */
public class MySpecificImpl extends WildImpl<MySpecificImplConfig> {

    private final MySpecificImplConfig mySpecificImplConfig;

    private final List<PlayerTimeRange> playerTimeRanges;

    private final List<PlayerWild> playerWilds;


    private final Random randomizer;

    public MySpecificImpl(MySpecificImplConfig config) {
        mySpecificImplConfig = config;
        playerTimeRanges = config.playerTimeRanges;
        playerWilds = readPlayersFromNbt();

        System.out.println("Debug printing list without sort");
        playerTimeRanges.forEach(playerTimeRange -> System.out.println(playerTimeRange.delay));
//        var gtrs = Configurator.getInstance().wildConfig.globalTimeRanges;
//        gtrs.sort((o1, o2) -> Integer.compare(o2.count(), o1.count()));

        randomizer = new Random();
    }

    @Override
    public int implement(CommandContext<ServerCommandSource> context) {
        var excludedRange = mySpecificImplConfig.excludedRange;
        var shouldContinueAfterAllTimeRangeDid = mySpecificImplConfig.shouldContinueAfterAllTimeRangeDid;

        try {
            var player = context.getSource().getPlayer();

            if (playerWilds.stream().noneMatch(playerWild1 -> playerWild1.uuid.equals(player.getUuidAsString()))) {
                var range = playerTimeRanges.get(0);
                var x = randomizer.nextDouble(range.min, range.max + 1);
                var z = randomizer.nextDouble(range.min, range.max + 1);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1800, 1)); // I will change with a mixin
                player.teleport(x, 320, z);

                playerWilds.add(new PlayerWild(player.getUuidAsString(), System.currentTimeMillis(), 1));
            } else {
                @SuppressWarnings("OptionalGetWithoutIsPresent") var playerWild = playerWilds.stream().filter(playerWild1 -> playerWild1.uuid.equals(player.getUuidAsString())).findFirst().get();

                PlayerTimeRange range;
                PlayerTimeRange previousRange;

                // if the player has used as many /wild as there are items in the list
                if (playerWild.count > playerTimeRanges.size() - 1) {
                    if (shouldContinueAfterAllTimeRangeDid) {
                        range = playerTimeRanges.get(playerTimeRanges.size() - 1);
                        previousRange = playerTimeRanges.get(playerTimeRanges.size() - 1 - 1);
                    } else {
                        player.sendMessage(Text.of("I have used your all /wild command"), false);
                        return Command.SINGLE_SUCCESS;
                    }
                } else {
                    range = playerTimeRanges.get(playerWild.count);
                    previousRange = playerTimeRanges.get(playerWild.count - 1);
                }

                if (System.currentTimeMillis() - playerWild.startTime < range.delay) {
                    player.sendMessage(Text.of("You have to wait more bedore use /wild"), false);
                    return Command.SINGLE_SUCCESS;
                }

                Vec3i vec;
                if (excludedRange) {
                    vec = getCorrectRandom(previousRange, range);
                } else {
                    vec = new Vec3i(randomizer.nextInt(range.min, range.max), 320, randomizer.nextInt(range.min, range.max));
                }

                playerWild.startTime = System.currentTimeMillis();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1800, 1)); // I will change with a mixin
                player.teleport(vec.getX(), vec.getY(), vec.getZ());
            }

        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }

        return Command.SINGLE_SUCCESS;
    }

    @Override
    public void registerEvents() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            savePlayersToNbt();
        });
    }

    private Vec3i getCorrectRandom(PlayerTimeRange previous, PlayerTimeRange range) {
        boolean foundCorrectRandom = false;
        int x, z;
        do {
            x = randomizer.nextInt(range.min, range.max);
            z = randomizer.nextInt(range.min, range.max);

            if (x <= previous.min && x <= previous.max ||
                    z <= previous.min && z <= previous.max) continue;

            foundCorrectRandom = true;
        } while (!foundCorrectRandom);
        return new Vec3i(x, 320, z);
    }

    @SuppressWarnings("ConstantConditions")
    private List<PlayerWild> readPlayersFromNbt() {
        var file = MOD_CONFIG_DIR.resolve("mySpecificImplData").toFile();
        if (file.exists()) {
            try {
                var nbt = NbtIo.read(file);
                var playerWilds = new ArrayList<PlayerWild>();
                for (var uuid : AdvancedWild.playersConnected) {
                    var data = (NbtCompound) nbt.get(uuid);
                    playerWilds.add(new PlayerWild(uuid, data.getLong("startTime"), data.getInt("count")));
                }
                return playerWilds;
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("ConstantConditions")
    private void savePlayersToNbt() {
        var file = MOD_CONFIG_DIR.resolve("mySpecificImplData").toFile();
        if (file.exists()) {
            try {
                var nbt = NbtIo.read(file);
                for (var playerWild : playerWilds) {
                    var data = (NbtCompound) nbt.get(playerWild.uuid);
                    data.putLong("startTime", playerWild.startTime);
                    data.putInt("count", playerWild.count);
                    nbt.put(playerWild.uuid, data);
                }
                NbtIo.write(nbt, file);
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            var nbt = new NbtCompound();
            for (var playerWild : playerWilds) {
                var data = new NbtCompound();
                data.putLong("startTime", playerWild.startTime);
                data.putInt("count", playerWild.count);
                nbt.put(playerWild.uuid, data);
            }
            try {
                NbtIo.write(nbt, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
