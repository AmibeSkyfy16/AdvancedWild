package ch.skyfy.advancedwild.commands;

import ch.skyfy.advancedwild.Configurator;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static ch.skyfy.advancedwild.AdvancedWild.MOD_CONFIG_DIR;

public class CmdWild {

    private final static Integer[] delay = {1, 5, 10, 20, 40, 60, 120, 240, 480, 960, 1920, 3840};
    private final static Integer[] distance = {1000, 2000, 4000, 8000, 16000, 30000, 40000, 50000, 60000, 70000, 80000, 90000};

    private static Map<String, ArrayList<Long>> map;

    static {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            map = loadData();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> saveData());
    }

    public static void registerWildCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("wild").executes(context -> {
            Configurator.getInstance().wildImpl.implement(context);


            if(0 == 0)return Command.SINGLE_SUCCESS;
            if (map == null) {
                System.err.println("[SkyfyModServer] -> [ERROR] la variable map vaut NULL");
                return Command.SINGLE_SUCCESS;
            }
            var source = context.getSource();

            var dimType = source.getWorld().getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(DimensionType.OVERWORLD_REGISTRY_KEY);
            if (source.getWorld().getDimension().equals(dimType)) {

                map.compute(source.getPlayer().getUuidAsString(), (key, list) -> {
                    try {
                        var player = source.getPlayer();
                        var rand = new Random();

                        // Signigie que c'est la première fois que le joueur fait la commande /wild
                        if (list == null) {
                            list = new ArrayList<>();

                            int max = distance[0];
                            var min = max * -1;
                            var randomX = rand.nextInt((max - min) + 1) + min;
                            var randomZ = rand.nextInt((max - min) + 1) + min;

                            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 1));
                            player.teleport(randomX, 256, randomZ);
                            list.add(System.currentTimeMillis());
                            saveData();
                        } else {
                            var lastTime = list.get(list.size() - 1);
                            var elapsed = System.currentTimeMillis() - lastTime;
                            int playerDelay = delay[list.size() - 1]; // en minutes
                            if (elapsed >= (playerDelay * 1000 * 60)) {
                                System.out.println("liste size: " + list.size());
                                System.out.println("max = : " + distance[list.size()]);
                                int max = list.size() >= distance.length ? distance[distance.length-1] : distance[list.size()];
                                var min = max * -1;
                                var randomX = rand.nextInt((max - min) + 1) + min;
                                var randomZ = rand.nextInt((max - min) + 1) + min;

                                player.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOW_FALLING, 1200, 1));
                                player.teleport(randomX, 256, randomZ);
                                list.add(System.currentTimeMillis());
                                saveData();
                            } else {
                                var playerDelayInMillis = playerDelay * 1000 * 60;
                                var MillisRemaining = playerDelayInMillis - elapsed;

                                int secondsRemaining = (int) (MillisRemaining / 1000) % 60;
                                int minutesRemaining = (int) ((MillisRemaining / (1000 * 60)) % 60);
                                int hoursRemaining = (int) ((MillisRemaining / (1000 * 60 * 60)) % 24);

                                player.sendMessage(Text.of("Vous devez encore attendre " + hoursRemaining + " heures, " + minutesRemaining + " minutes et " + secondsRemaining + " secondes avant de pouvoir utiliser cette commande"), false);
                            }
                        }
                        System.out.println(System.currentTimeMillis());
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                    return list;
                });
            }
            return Command.SINGLE_SUCCESS;
        }));
    }

    private static void saveData() {
        try {
            var so = new ObjectOutputStream(new FileOutputStream(MOD_CONFIG_DIR.resolve("WildData").toFile()));
            so.writeObject(map);
            so.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, ArrayList<Long>> loadData() {
        Map<String, ArrayList<Long>> map = null;
        try {
            var dataFile = MOD_CONFIG_DIR.resolve("WildData").toFile();
            if(!dataFile.exists())return null;
            var so = new ObjectInputStream(new FileInputStream(MOD_CONFIG_DIR.resolve("WildData").toFile()));
            //noinspection unchecked
            map = (Map<String, ArrayList<Long>>) so.readObject();
            so.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return map == null ? new HashMap<>() : map;
    }

}
