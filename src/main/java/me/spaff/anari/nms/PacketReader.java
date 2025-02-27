package me.spaff.anari.nms;

import io.netty.channel.*;
import me.spaff.anari.Main;
import me.spaff.anari.utils.ReflectionUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.vehicle.MinecartChest;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class PacketReader {
    private static final String pipelinePrefix = "[Anari]-";
    private static Channel channel;

    public static void injectPlayer(Player player) {
        UUID uuid = player.getUniqueId();

        ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext channelHandlerContext, Object packet) throws Exception {


                super.channelRead(channelHandlerContext, packet);
            }

            @Override
            public void write(ChannelHandlerContext channelHandlerContext, Object packet, ChannelPromise promise) throws Exception {
                /*if (packet instanceof ClientboundUpdateMobEffectPacket mobEffectPacket) {
                    System.out.println("------------------------------------------");
                    System.out.println("getRegisteredName: " + mobEffectPacket.getEffect().getRegisteredName());
                    System.out.println("getEffectAmplifier: " + mobEffectPacket.getEffectAmplifier());
                    System.out.println("isEffectVisible: " + mobEffectPacket.isEffectVisible());
                    System.out.println("getEffectDurationTicks: " + mobEffectPacket.getEffectDurationTicks());
                    System.out.println("isEffectAmbient: " + mobEffectPacket.isEffectAmbient());
                    System.out.println("shouldBlend: " + mobEffectPacket.shouldBlend());
                }*/

                if (packet instanceof ClientboundSetEntityDataPacket entityDataPacket) {
                    for (SynchedEntityData.DataValue<?> datawatcher_c : entityDataPacket.packedItems()) {
                        /*if (datawatcher_c.id() == 15) {
                            System.out.println("ignored field 15");
                            super.write(channelHandlerContext, packet, promise);
                        }

                        System.out.println(
                                "Field ID: " + datawatcher_c.id() +
                                        ", Expected: " + datawatcher_c.serializer().createAccessor(0).toString() +
                                        ", Received: " + datawatcher_c.serializer());*/

                        /*//*SynchedEntityData.DataItem<?>[] itemsById = (SynchedEntityData.DataItem<?>[])
                                ReflectionUtils.getField(SynchedEntityData.class, "e", null); // e -> itemsById

                        int datawatcher_id = datawatcher_c.id(); // (int) ReflectionUtils.getField(SynchedEntityData.DataValue.class, "a", datawatcher_c); // a -> id

                        SynchedEntityData.DataItem<?> datawatcher_item = itemsById[datawatcher_id];

                        EntityDataAccessor<?> accessor = (EntityDataAccessor<?>) ReflectionUtils.
                                getField(SynchedEntityData.DataItem.class, "a", datawatcher_item); // a -> accessor

                        // Check if invalid
                        if (!Objects.equals(datawatcher_c.serializer(), accessor.serializer())) {
                            System.out.println("saved from invalid entity data item type.");
                            super.write(channelHandlerContext, packet, promise);
                            return;
                        }*//*

                        //SynchedEntityData.DataItem<?> datawatcher_item = SynchedEntityData.itemsById[datawatcher_c.id];
                    }

                    if (!Objects.equals(datawatcher_c.serializer(), datawatcher_item.accessor.serializer())) {*/

                    }
                }

                /*if (packet instanceof ClientboundLevelChunkWithLightPacket) {
                    ClientboundLevelChunkWithLightPacket chunkData = (ClientboundLevelChunkWithLightPacket) packet;

                    int chunkX = chunkData.getX();
                    int chunkZ = chunkData.getZ();

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            Entity[] entities = player.getWorld().getChunkAt(chunkX, chunkZ).getEntities();
                            int count = entities.length;

                            if (count == 0)
                                return;

                            //System.out.println("------------------");
                            //System.out.println("count: " + count);
                            for (Entity ent : entities) {
                                Location loc = ent.getLocation();

                                if (ent.getType().equals(EntityType.CHEST_MINECART)) {
                                    ent.remove();

                                    Block block = loc.getBlock();
                                    block.setType(Material.CHEST);

                                    ((Chest) block).getBlockInventory().setItem(10, new ItemStack(Material.DIRT));

                                    player.teleport(ent.getLocation());

                                    System.out.println("------------------");
                                    System.out.println("Found minecart chest teleported!");
                                    System.out.println("------------------");
                                }
                                else {
                                    System.out.println("entity: " + ent.getType());
                                }

                                count++;
                            }
                        }
                    }.runTaskLater(Main.getInstance(), 1);
                }*/

                super.write(channelHandlerContext, packet, promise);
            }
        };

        // ServerCommonPacketListenerImpl: e -> connection
        // Connection: n -> channel

        Connection conn = null;
        
        // Get player connection
        ServerCommonPacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;

        // Get connection field
        conn = (Connection) ReflectionUtils.getField(ServerCommonPacketListenerImpl.class, "e", connection);

        // Get channel field
        channel = (Channel) ReflectionUtils.getField(Connection.class, "n", conn);

        String pipelineName = pipelinePrefix + uuid.toString();
        ChannelPipeline pipeline = channel.pipeline();

        if (isInjected(player))
            pipeline.remove(pipelineName);

        pipeline.addBefore("packet_handler", pipelineName, channelDuplexHandler);
    }

    public static void uninjectPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        if (channel == null)
            return;

        String pipelineName = pipelinePrefix + uuid.toString();

        channel.eventLoop().submit(() -> {
            if (channel.pipeline().get(pipelineName) != null)
                channel.pipeline().remove(pipelineName);
            return null;
        });
    }

    public static void pipelineHandler(Player player) {
        if (isInjected(player))
            uninjectPlayer(player);
        else
            injectPlayer(player);
    }

    public static void pipelineHandler() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            pipelineHandler(player);
        }
    }

    private static boolean isInjected(Player player) {
        UUID uuid = player.getUniqueId();
        String pipelineName = pipelinePrefix + uuid.toString();

        if (channel == null)
            return false;

        ChannelPipeline pipeline = channel.pipeline();
        if (pipeline.get(pipelineName) != null)
            return true;

        return false;
    }
}