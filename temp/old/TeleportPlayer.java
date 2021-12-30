package ch.skyfy.advancedwild.features;

import ch.skyfy.advancedwild.model.Server;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.dimension.DimensionType;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TeleportPlayer {

    static byte PACKET_ID = 0;
    public static final Identifier CHANNEL_NAME = new Identifier("worldinfo", "world_id");
//    public static final Identifier CHANNEL_NAME = new Identifier("advancedportals:warp");

    private static boolean once = false;

    private static final List<Server> servers = createServers();

    public static final Map<String, Boolean> warningPlayers = new HashMap<>();

    public TeleportPlayer() {


    }

    private static List<Server> createServers() {
        var servers = new ArrayList<Server>();

        int count = 5;
        int portStart = 50002;
        int size = 5000;

        for (int i = 0; i < count; i++) {
            servers.add(new Server(portStart, size * (-1), size, "server" + i + 1));
            portStart++;
            size += size;
        }
//        servers.get(0).port = -1; // TODO DELETE THIS
        return servers;
    }

    // This method is called on OnInitialize from ModInitializer
    public static void initialize() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!once) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        for (ServerPlayerEntity serverPlayerEntity : server.getPlayerManager().getPlayerList()) {
                            implementTeleportation(server, serverPlayerEntity, server.getServerPort());
                        }
                    }
                }, 0, 2000);
            }
            once = true;
        });

        // Player would not be able to throw enderpearl when he is near a teleportation area
        // it is a security measure to avoid that he falls in the void during his teleportation on the next server
        UseItemCallback.EVENT.register((player, world, hand) -> {
            var consume = new AtomicBoolean(false);
            player.getItemsHand().iterator().forEachRemaining(itemStack -> {
                System.out.println("key: " + itemStack.getTranslationKey());
                if (itemStack.getTranslationKey().equals("item.minecraft.ender_pearl")) {
                    warningPlayers.forEach((uuid, warn) -> {
                        if (player.getUuidAsString().equals(uuid)) {
                            if (warn) {
                                System.out.println("Ender pearl cancel");
                                consume.set(true);
                            }
                        }
                    });
                }
            });
            if (consume.get()) {
                return TypedActionResult.consume(ItemStack.EMPTY);
            } else {
                return TypedActionResult.pass(ItemStack.EMPTY);
            }
        });
    }

    public static void implementTeleportation(MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity, int port) {
        var marge = 10; // Dix blocs de marge pour l'algorythme de détection de la téléportation (genre si joueur entre 1000 et 1000 + marge (1020))
        var playerLoc = serverPlayerEntity.getPos();
        var dimTypeOverworld = serverPlayerEntity.world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(DimensionType.OVERWORLD_REGISTRY_KEY);
        var dimTypeNether = serverPlayerEntity.world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(DimensionType.THE_NETHER_REGISTRY_KEY);
        var dimTypeEnd = serverPlayerEntity.world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).get(DimensionType.THE_END_REGISTRY_KEY);

        // ------------DEBUG------------ //
        debug2(playerLoc, port);
        // ------------DEBUG------------ //
        if (serverPlayerEntity.getWorld().getDimension() == dimTypeOverworld) { // Player is in overworld
            for (Server server : servers) {
                if (port == server.port) { // check on wich server is the player
                    // ------------DEBUG------------ //
                    debug(server, marge, "OVERWORLD");
                    // ------------DEBUG------------ //
                    conditionalTeleportPlayer(playerLoc, server.max, server.min, marge, server.serverDestination, minecraftServer, serverPlayerEntity);
                    break;
                }
            }
        } else if (serverPlayerEntity.getWorld().getDimension() == dimTypeNether) {
            for (Server server : servers) {
                if (port == server.port) { // check on wich server is the player
                    // ------------DEBUG------------ //
                    debug(server, marge, "NETHER");
                    // ------------DEBUG------------ //
                    conditionalTeleportPlayer(playerLoc, server.max / 8, server.min / 8, marge, server.serverDestination, minecraftServer, serverPlayerEntity); // Division par huit en raison de comment est fait le nether sur mc
                    break;
                }
            }
        } else if (serverPlayerEntity.getWorld().getDimension() == dimTypeEnd) {
            // TODO PROBLEME DE L'ENDER PEARL -> PAR MESURE DE SECURITE, EMPECHER LE JOUEUR DE LANCER UNE PEARL LORSQU'IL EST PRET D'UN ZONE DE CHANGEMENT DE SERVEUR

            for (Server server : servers) {
                if (port == server.port) { // check on wich server is the player
                    // ------------DEBUG------------ //
                    debug(server, marge, "END");
                    // ------------DEBUG------------ //
                    conditionalTeleportPlayer(playerLoc, server.max, server.min, marge, server.serverDestination, minecraftServer, serverPlayerEntity);
                    break;
                }
            }
        }
    }

    private static void conditionalTeleportPlayer(Vec3d playerLoc, double max, double min, double marge, String serverDestination, MinecraftServer minecraftServer, ServerPlayerEntity serverPlayerEntity) {
        var warningLevel = new int[]{50, 20, 10}; // Utilisé pour avertir le joueur qu'il s'approche de la zone de changement de serveur

        // On prévient le joueur avec un message lorsqu'il atteint une zone de changement de serveur
        var warningPlayer = new AtomicBoolean(false);
        for (int i : warningLevel) {
            if (max - playerLoc.getX() < i) {
                System.out.println("PLAYER " + serverPlayerEntity.getEntityName() + " est proche de la zone de tp (" + i + " block left) \tmax - playerLoc.getX(): " + (max - playerLoc.getX()));
                warningPlayer.set(true);
            }
            if (max - playerLoc.getZ() < i) {
                System.out.println("PLAYER " + serverPlayerEntity.getEntityName() + " est proche de la zone de tp (" + i + " block left) \tmax - playerLoc.getZ(): " + (max - playerLoc.getZ()));
                warningPlayer.set(true);
            }
            if (Math.abs(min - playerLoc.getX()) < i) {
                System.out.println("PLAYER " + serverPlayerEntity.getEntityName() + " est proche de la zone de tp (" + i + " block left) \tMath.abs(min - playerLoc.getX()): " + (Math.abs(min - playerLoc.getX())));
                warningPlayer.set(true);
            }
            if (Math.abs(min - playerLoc.getZ()) < i) {
                System.out.println("PLAYER " + serverPlayerEntity.getEntityName() + " est proche de la zone de tp (" + i + " block left) \tMath.abs(min - playerLoc.getZ()): " + (Math.abs(min - playerLoc.getZ())));
                warningPlayer.set(true);
            }
            warningPlayers.compute(serverPlayerEntity.getUuidAsString(), (uuid, warn) -> warningPlayer.get()); // Permettra de bloquer l'utilisation d'enderpearl proche d'une zone de tp
            if (warningPlayer.get()) {
                serverPlayerEntity.sendMessage(Text.of("Vous êtes proche d'une zone de téléportation"), true);
                break;
            }
        }

        // Check for X position
        if (playerLoc.getX() > max && playerLoc.getX() < max + marge) { // Si on se trouve entre 5000 et 5020 -> tp du player
            System.out.println("PLAYER COORD: X/Y/Z" + playerLoc.getX() + "/" + playerLoc.getY() + "/" + playerLoc.getZ());
            System.out.println("PLAYER GET TELEPORTED TO SERVER " + serverDestination);
//            minecraftServer.getCommandManager().sendCommandTree(serverPlayerEntity);
//            minecraftServer.getCommandManager().execute(serverPlayerEntity.getCommandSource(), "/op " + serverPlayerEntity.getEntityName());
//            minecraftServer.getCommandManager().execute(serverPlayerEntity.getCommandSource(), "/xp set " + serverPlayerEntity.getEntityName() + " 400 levels");
//            minecraftServer.getCommandManager().execute(serverPlayerEntity.getCommandSource(), "deop " + serverPlayerEntity.getEntityName());
//            try {
//                minecraftServer.getCommandManager().getDispatcher().execute("server map2", serverPlayerEntity.getCommandSource());
//            } catch (CommandSyntaxException e) {
//                e.printStackTrace();
//            }
//            minecraftServer.getCommandManager().execute(serverPlayerEntity.getCommandSource(), "/kick");
//            minecraftServer.getCommandManager().execute(serverPlayerEntity.getCommandSource(), "e");

            connectPlayerToAnotherServer(serverPlayerEntity);

            // TODO TELEPORT THE PLAYER !!!
        }
        if (playerLoc.getX() < min && playerLoc.getX() > min - marge) { // MEME CHOSE POUR LES COORDS NEGATIVE:
            System.out.println("PLAYER COORD: X/Y/Z" + playerLoc.getX() + "/" + playerLoc.getY() + "/" + playerLoc.getZ());
            System.out.println("PLAYER GET TELEPORTED TO SERVER " + serverDestination);
            // TODO TELEPORT THE PLAYER !!!
        }

        // Check for Z position
        if (playerLoc.getZ() > max && playerLoc.getZ() < max + marge) {
            System.out.println("PLAYER COORD: X/Y/Z" + playerLoc.getX() + "/" + playerLoc.getY() + "/" + playerLoc.getZ());
            System.out.println("PLAYER GET TELEPORTED TO SERVER " + serverDestination);
            // TODO TELEPORT THE PLAYER !!!
        }
        if (playerLoc.getZ() < min && playerLoc.getZ() > min - marge) { // MEME CHOSE POUR LES COORDS NEGATIVE:
            System.out.println("PLAYER COORD: X/Y/Z" + playerLoc.getX() + "/" + playerLoc.getY() + "/" + playerLoc.getZ());
            System.out.println("PLAYER GET TELEPORTED TO SERVER " + serverDestination);
            // TODO TELEPORT THE PLAYER !!!
        }

    }

    static boolean registerOnce = false;
    private static void connectPlayerToAnotherServer(ServerPlayerEntity serverPlayerEntity) {
        if(!registerOnce) {
            ServerPlayNetworking.registerGlobalReceiver(CHANNEL_NAME, (server, player, handler, buf, responseSender) -> {
                ServerWorld serverWorld = player.getWorld();
                MinecraftDedicatedServer dedicatedServer = (MinecraftDedicatedServer) serverWorld.getServer();
                String levelName = dedicatedServer.getLevelName();
                System.out.println("WorldNamePacket: sending levelName: " + levelName);

                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
                passedData.writeByte(PACKET_ID);
                passedData.writeByteArray(levelName.getBytes());
                ServerPlayNetworking.send(player, CHANNEL_NAME, passedData);
            });
            // Deprecated
//            ServerSidePacketRegistry.INSTANCE.register(CHANNEL_NAME, (packetContext, attachedData) -> {
//                ServerWorld serverWorld = ((ServerPlayerEntity) packetContext.getPlayer()).getServerWorld();
//                MinecraftDedicatedServer dedicatedServer = (MinecraftDedicatedServer) serverWorld.getServer();
//                String levelName = dedicatedServer.getLevelName();
//                System.out.println("WorldNamePacket: sending levelName: " + levelName);
//
//                PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
//                passedData.writeByte(PACKET_ID);
//                passedData.writeByteArray(levelName.getBytes());
//                ServerSidePacketRegistry.INSTANCE.sendToPlayer(packetContext.getPlayer(), CHANNEL_NAME, passedData);
//            });
            registerOnce = true;
        }

        PacketByteBuf passedData = new PacketByteBuf(Unpooled.buffer());
        passedData.writeByteArray("serverTWO".getBytes()); // This is the name of server TWO
        passedData.writeByteArray("MTEA_MAP_TEST".getBytes()); // This is the map name
        passedData.writeByteArray(serverPlayerEntity.getUuidAsString().getBytes());
        ServerPlayNetworking.send(serverPlayerEntity, CHANNEL_NAME, passedData);
//        ServerSidePacketRegistry.INSTANCE.sendToPlayer(serverPlayerEntity, CHANNEL_NAME, passedData);

    }

    private static void debug(Server server, int marge, String dim) {
//        System.out.println("\n");
//        System.out.println("If Dimension == " + dim);
//        System.out.println("server.max / 8: " + server.max / 8);
//        System.out.println("(server.max / 8) + marge " + (server.max / 8) + marge);
//        System.out.println("server.min / 8: " + server.min / 8);
//        System.out.println("(server.min / 8) - marge " + ((server.min / 8) - marge));
    }

    private static void debug2(Vec3d playerLoc, int port) {
//        System.out.println("\nPLAYER LOCATION X/Y/Z " + playerLoc.getX() + "/" + playerLoc.getY() + "/" + playerLoc.getZ());
//        System.out.println("serveur port: " + port);
//        servers.forEach(System.out::println);
    }

}
