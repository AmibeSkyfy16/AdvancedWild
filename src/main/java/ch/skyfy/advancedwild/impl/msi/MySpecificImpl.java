package ch.skyfy.advancedwild.impl.msi;

import ch.skyfy.advancedwild.AdvancedWild;
import ch.skyfy.advancedwild.feature.PlayerTimeMeter;
import ch.skyfy.advancedwild.impl.WildImpl;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3i;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
 * The basedDelay                               -> Two value available, either PLAYER_PLAYTIME_BASED or REAL_TIME_BASED
 * <p>
 * PLAYER_PLAYTIME_BASED mean /wild command is based on player total time on the server
 * REAL_TIME_BASED mean /wild command is based on normal time in real life
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
        randomizer = new Random();
    }

    @Override
    public int implement(CommandContext<ServerCommandSource> context) {
        var excludedRange = mySpecificImplConfig.excludedRange;
        var shouldContinueAfterAllTimeRangeDid = mySpecificImplConfig.shouldContinueAfterAllTimeRangeDid;

        try {
            var player = context.getSource().getPlayer();

            // If this is the first time the player use /wild
            if (playerWilds.stream().noneMatch(playerWild1 -> playerWild1.uuid.equals(player.getUuidAsString()))) {
                var range = playerTimeRanges.get(0);
                var x = randomizer.nextDouble(range.min, range.max + 1);
                var z = randomizer.nextDouble(range.min, range.max + 1);
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1800, 1)); // I will change with a mixin
                player.teleport(x, 320, z);
                playerWilds.add(new PlayerWild(player.getUuidAsString(), System.currentTimeMillis(), 1));
                savePlayersToNbt(player.getUuidAsString());
            } else {
                @SuppressWarnings("OptionalGetWithoutIsPresent") var playerWild = playerWilds.stream().filter(playerWild1 -> playerWild1.uuid.equals(player.getUuidAsString())).findFirst().get();

                PlayerTimeRange range;
                PlayerTimeRange previousRange;

                // if the player has used as many /wild as there are items in the list
                if (playerWild.count > playerTimeRanges.size() - 1) {
                    // If he can continue or not to use /wild
                    if (shouldContinueAfterAllTimeRangeDid) {
                        range = playerTimeRanges.get(playerTimeRanges.size() - 1);
                        previousRange = playerTimeRanges.get(playerTimeRanges.size() - 1 - 1);
                    } else {
                        player.sendMessage(Text.of("You have used your all /wild command"), false);
                        return Command.SINGLE_SUCCESS;
                    }
                } else {
                    range = playerTimeRanges.get(playerWild.count);
                    previousRange = playerTimeRanges.get(playerWild.count - 1);
                }

                // Getting the correct elapsed time
                Long elapsed;
                if (mySpecificImplConfig.basedDelay == MySpecificImplConfig.BasedDelay.PLAYER_PLAYTIME_BASED)
                    elapsed = PlayerTimeMeter.getInstance().getTime(playerWild.uuid);
                else
                    elapsed = System.currentTimeMillis() - playerWild.startTime;

                if (elapsed < range.delay) {
                    var calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(PlayerTimeMeter.getInstance().getTime(playerWild.uuid));
                    var seconds = calendar.get(Calendar.SECOND);
                    var minutes = calendar.get(Calendar.MINUTE);
                    var hours = calendar.get(Calendar.HOUR);
                    var message = """
                            You have to wait another %d seconds, %d minutes and %d hours before you can use the /wild command again""";
                    player.sendMessage(Text.of(message.formatted(seconds, minutes, hours)), false);
                    return Command.SINGLE_SUCCESS;
                }

                Vec3i vec;
                if (excludedRange) {
                    vec = getCorrectRandom(previousRange, range);
                } else {
                    vec = new Vec3i(randomizer.nextInt(range.min, range.max), 320, randomizer.nextInt(range.min, range.max));
                }

                playerWild.count++;
                playerWild.startTime = System.currentTimeMillis();
                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 1)); // I will change with a mixin
                player.teleport(vec.getX(), vec.getY(), vec.getZ());
                savePlayersToNbt(playerWild.uuid);
            }
        } catch (CommandSyntaxException e) {
            e.printStackTrace();
        }
        return Command.SINGLE_SUCCESS;
    }

    @Override
    public void registerEvents() {
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            playerWilds.forEach(playerWild -> savePlayersToNbt(playerWild.uuid));
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            playerWilds.stream().filter(playerWild -> playerWild.uuid.equals(handler.getPlayer().getUuidAsString())).findFirst().ifPresent(playerWild -> {
                savePlayersToNbt(playerWild.uuid);
            });
        });
    }

    private Vec3i getCorrectRandom(PlayerTimeRange previous, PlayerTimeRange range) {
        boolean foundCorrectRandom = false;
        int x, z;
        do {
            x = randomizer.nextInt(range.min, range.max);
            z = randomizer.nextInt(range.min, range.max);

            if ((x <= previous.min || x >= previous.max) && (z <= previous.min || z >= previous.max))
                foundCorrectRandom = true;
        } while (!foundCorrectRandom);
        System.out.println("previous was : " + previous.min + " / " + previous.max);
        System.out.println("random are : " + x + "  /  " + z);
        return new Vec3i(x, 320, z);
    }

    /**
     * For each player connected to the server, we will check if there is a configuration, if true, we load it
     */
    @SuppressWarnings("ConstantConditions")
    private List<PlayerWild> readPlayersFromNbt() {
        var file = MOD_CONFIG_DIR.resolve("mySpecificImplData.dat").toFile();
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
    private void savePlayersToNbt(String uuid) {
        var file = MOD_CONFIG_DIR.resolve("mySpecificImplData").toFile();
        playerWilds.stream().filter(playerWild -> playerWild.uuid.equals(uuid)).findFirst().ifPresent(playerWild -> {
            NbtCompound nbt;
            if (file.exists()) {
                try {
                    nbt = NbtIo.read(file);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            } else {
                nbt = new NbtCompound();
            }
            var data = new NbtCompound();
            data.putLong("startTime", playerWild.startTime);
            data.putInt("count", playerWild.count);
            nbt.put(playerWild.uuid, data);
            try {
                NbtIo.write(nbt, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
