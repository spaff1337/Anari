package me.spaff.anari.cmd;

import me.spaff.anari.Main;
import me.spaff.anari.item.AnariID;
import me.spaff.anari.item.AnariItem;
import me.spaff.anari.item.AnariItems;
import me.spaff.anari.menu.SkillsMenu;
import me.spaff.anari.nms.*;
import me.spaff.anari.player.AnariPlayer;
import me.spaff.anari.player.AnariPlayerManager;
import me.spaff.anari.skills.Skill;
import me.spaff.anari.skills.SkillType;
import me.spaff.anari.utils.ItemUtils;
import me.spaff.anari.utils.StringUtils;
import net.minecraft.network.protocol.game.*;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang.WordUtils;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_21_R3.CraftEquipmentSlot;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.joml.Vector3f;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnariCommands implements CommandExecutor {
    private static NPC npc = null;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player player = (Player) sender;
        if (args.length == 0) return true;

        //---------------- Commands ----------------//

        if (args[0].equalsIgnoreCase("item")) {
            if (args.length > 2) {
                StringUtils.sendMessage(player, "&cInvalid arguments!");
                return false;
            }

            if (!EnumUtils.isValidEnum(AnariID.class, args[1])) {
                StringUtils.sendMessage(player, "&cItem " + args[1] + " does not exist!");
                return false;
            }

            player.getInventory().addItem(AnariItems.getItem(AnariID.valueOf(args[1])));
        }



        /*if (args[0].equalsIgnoreCase("chunkdata")) {
            Chunk playerChunk = player.getLocation().getChunk();

            if (args[1].equalsIgnoreCase("nukechunk")) {
                Chunk chunk = player.getLocation().getChunk();

                for (int x = 0; x < 16; x++) {
                    for (int y = -64; y < 320; y++) { // 320
                        for (int z = 0; z < 16; z++) {
                            chunk.getBlock(x, y, z).setType(Material.AIR);
                        }
                    }
                }
            }
            if (args[1].equalsIgnoreCase("fillchunk")) {
                Chunk chunk = player.getLocation().getChunk();

                System.out.println("filling chunk with data...");
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 320; y++) { // 320
                        for (int z = 0; z < 16; z++) {
                            String serializedChunkLoc2 = chunk.getX() + "-" + chunk.getZ();

                            Block block = chunk.getBlock(x, y, z);
                            String serializedLoc2 = block.getX() + "-" + block.getY() + "-" + block.getZ();

                            NamespacedKey key2 = new NamespacedKey(serializedChunkLoc2, serializedLoc2);
                            playerChunk.getPersistentDataContainer().set(key2, PersistentDataType.STRING, "some_block_id");
                        }
                    }
                }
                System.out.println("done.");
            }
            if (args[1].equalsIgnoreCase("showmodels")) {
                Chunk chunk = player.getLocation().getChunk();

                System.out.println("showing models...");
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 320; y++) { // 320
                        for (int z = 0; z < 16; z++) {
                            if (new Random().nextInt(10) != 0)
                                continue;

                            String serializedChunkLoc2 = chunk.getX() + "-" + chunk.getZ();

                            Block block = chunk.getBlock(x, y, z);
                            String serializedLoc2 = block.getX() + "-" + block.getY() + "-" + block.getZ();

                            NamespacedKey key2 = new NamespacedKey(serializedChunkLoc2, serializedLoc2);
                            String savedBlockId = playerChunk.getPersistentDataContainer().get(key2, PersistentDataType.STRING);

                            if (savedBlockId == null || savedBlockId.isEmpty()) {
                                //System.out.println("this block has NO data");
                                continue;
                            }

                            //Block block = chunk.getBlock(x, y, z);
                            DisplayEntityUtil displayEntity = new DisplayEntityUtil(block.getLocation(), EntityType.BLOCK_DISPLAY);

                            displayEntity.updateDisplayedBlock(player, Blocks.DIAMOND_BLOCK);
                            displayEntity.updateScale(player, new Vector3f(1.f, 1.f, 1.f));
                            displayEntity.show(player);
                        }
                    }
                }
                System.out.println("done.");
            }
        }*/

        /*if (args[0].equalsIgnoreCase("chunkdata")) {
            Block targetBlock = player.getTargetBlockExact(4);
            if (targetBlock == null) return false;

            Chunk targetChunk = targetBlock.getChunk();

            int blockChunkX = targetChunk.getX();
            int blockChunkZ = targetChunk.getZ();

            //NamespacedKey key = new NamespacedKey("anari", Constants.NAMESPACE_KEY);

            // chunk coords to get block coords and id
            String serializedChunkLoc = blockChunkX + "-" + blockChunkZ;
            String serializedLoc = targetBlock.getX() + "-" + targetBlock.getY() + "-" + targetBlock.getZ();

            NamespacedKey key = new NamespacedKey(serializedChunkLoc, serializedLoc);

            if (args[1].equalsIgnoreCase("fillchunk")) {
                Chunk chunk = player.getLocation().getChunk();

                System.out.println("filling chunk with data...");
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 320; y++) { // 320
                        for (int z = 0; z < 16; z++) {
                            String serializedChunkLoc2 = chunk.getX() + "-" + chunk.getZ();

                            Block block = chunk.getBlock(x, y, z);
                            String serializedLoc2 = block.getX() + "-" + block.getY() + "-" + block.getZ();

                            NamespacedKey key2 = new NamespacedKey(serializedChunkLoc2, serializedLoc2);
                            targetChunk.getPersistentDataContainer().set(key2, PersistentDataType.STRING, "some_block_id");
                        }
                    }
                }
                System.out.println("done.");
            }
            if (args[1].equalsIgnoreCase("showmodels")) {
                Chunk chunk = player.getLocation().getChunk();

                System.out.println("showing models...");
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < 320; y++) { // 320
                        for (int z = 0; z < 16; z++) {
                            String savedBlockId = targetChunk.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                            if (savedBlockId == null || savedBlockId.isEmpty()) {
                                //System.out.println("this block has NO data");
                                continue;
                            }

                            Block block = chunk.getBlock(x, y, z);
                            DisplayEntityUtil displayEntity = new DisplayEntityUtil(block.getLocation(), EntityType.BLOCK_DISPLAY);

                            displayEntity.updateDisplayedBlock(player, Blocks.GLASS);
                            //displayEntity.updateScale(player, Vec);
                            displayEntity.show(player);
                        }
                    }
                }
                System.out.println("done.");
            }
            *//*if (args[1].equalsIgnoreCase("clear")) {
                targetChunk.getPersistentDataContainer().remove(key);
            }*//*
            if (args[1].equalsIgnoreCase("set")) {
                //String serializedLoc = blockChunkX + ", " + blockChunkZ;

                targetChunk.getPersistentDataContainer().set(key, PersistentDataType.STRING, "some_block_id");
            }
            if (args[1].equalsIgnoreCase("get")) {
                //String deserializedLoc = blockChunkX + ", " + blockChunkZ;
                String savedBlockId = targetChunk.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (savedBlockId == null || savedBlockId.isEmpty()) {
                    System.out.println("this block has NO data");
                    return false;
                }

                //int chunkBlockX = Integer.parseInt(serializedLoc2.split(", ")[0]);
                //int chunkBlockZ = Integer.parseInt(serializedLoc2.split(", ")[1]);

                System.out.println("this block HAS data: " + savedBlockId);
                //System.out.println("saved location: x: " + chunkBlockX + ", z: " + chunkBlockZ);
            }
        }*/

        if (args[0].equalsIgnoreCase("sword")) {
            ItemStack item = new ItemStack(Material.DIAMOND_SWORD);
            ItemUtils.setName(item, "&aSlayer");
            //ItemUtil.setLore(item, List.of("&7Lore text", "&7lolez"));

            player.getInventory().addItem(item);
        }

        if (args[0].equalsIgnoreCase("displaytest")) {
            ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

            ServerLevel nmsWorld = ((CraftWorld) player.getWorld()).getHandle();
            Display displayEntity = new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, nmsWorld);

            Location location = player.getLocation();

            //

            updateDisplayData(player, displayEntity, Display.BlockDisplay.class, "p", Blocks.ACACIA_WOOD.defaultBlockState());

            //

            ClientboundAddEntityPacket addEntityPacket = new ClientboundAddEntityPacket(
                    displayEntity.getId(),
                    displayEntity.getUUID(),
                    location.getX(),
                    location.getY(),
                    location.getZ(),
                    0, // Pitch
                    location.getYaw(), // Yaw
                    displayEntity.getType(),
                    0,
                    Vec3.ZERO,
                    location.getYaw() // Yaw
            );

            connection.send(addEntityPacket);

            //

            //updateDisplayData(player, displayEntity, Display.BlockDisplay.class, "p", Blocks.ACACIA_WOOD.defaultBlockState());

//            Class<?> displayBlockClass = Display.BlockDisplay.class;
//            EntityDataAccessor<BlockState> accessor;
//
//            try {
//                Field field = displayBlockClass.getDeclaredField("p"); // p -> DATA_BLOCK_STATE_ID
//                field.setAccessible(true);
//
//                accessor = (EntityDataAccessor<BlockState>) field.get(null);
//
//            }
//            catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//
//            SynchedEntityData.DataItem<BlockState> dataItem = new SynchedEntityData.DataItem<>(accessor, Blocks.ANCIENT_DEBRIS.defaultBlockState());
//            ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
//                    displayEntity.getId(),
//                    List.of(dataItem.value())
//            );
//
//            connection.send(packet);
        }

        /*if (args[0].equalsIgnoreCase("dmgindicator")) {
            new BukkitRunnable() {
                int i = 0;

                @Override
                public void run() {
                    if (i >= 10) {
                        this.cancel();
                        return;
                    }

                    int dmg = new Random().nextInt(9999);
                    String color = StringUtils.getHexColor("#ff9a36");

                    DamageIndicator damageIndicator = new DamageIndicator(color + dmg, 15, player.getLocation().add(0, 0.5, 0));
                    damageIndicator.spawn();

                    i++;
                }
            }.runTaskTimer(Main.getInstance(), 0, 10);
        }*/

        if (args[0].equalsIgnoreCase("invis")) {
            /*MobEffectInstance mobEffectInstance = new MobEffectInstance(MobEffects.INVISIBILITY, 99999, 0, false, true, false);

            ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
            serverPlayer.activeEffects.put(MobEffects.INVISIBILITY, mobEffectInstance);

            Packets.sendMobEffectPacket(player, serverPlayer);*/
        }

        if (args[0].equalsIgnoreCase("npc")) {
            Location loc = player.getLocation();

            if (args[1].equalsIgnoreCase("animate")) {
                npc.playAnimation(NPCAction.valueOf(args[2]));
            }

            if (args[1].equalsIgnoreCase("spawn")) {
                npc = new NPC.Builder(player.getLocation())
                        .skin("ewogICJ0aW1lc3RhbXAiIDogMTY3MzczMjUzNjUwNiwKICAicHJvZmlsZUlkIiA6ICIzYjgwOTg1YWU4ODY0ZWZlYjA3ODg2MmZkOTRhMTVkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLaWVyYW5fVmF4aWxpYW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUzODI1YjlmZmFmMGI4MGFkYzRhODgwYTk2YTFjYWY2MDAzNGIyNjMzYWU0NDk5MGIyZmJkYTFmYTk1NzY1YyIKICAgIH0KICB9Cn0=",
                                "pMwz6j6WpGAEe5MkIwf7mePYKqYSTaTH8CgD1BuAS9ONZg3q6vTEHHSjdvE7mKfjKP1GOcEC+ayPvUPMipsNe2aFkRrR23wwxW7DjCPFftHAzu6f1CZap1LPNKBLLJgDpNOTwVLhvKQ9JZYxevKp8Z3Drm+s6+MIvmvHX7E2QzHWk9wHs7SHmY0NlHemGFjpAkydqbxRjuIFpV/1cbPpClkyP4jJHTn1pME8nIFHIyMP/YAyP4oUYDyvkCBq+As6n12DZ5UyTLBcsJOKM+kCGac4eHnQMDhj+LV/LZd3aEGKSzudpAx1tZ8mNvC8Egw7lXEwT72F9ou+DdENXBQIw5PUCmFR6ucMYvMYoWtz7GBdWinHbx3RiuqwN45QCcX43JjCTem9IYeN/+ArKTSo9jklOCxcnq+c+MACMYWFXFJlgRZPvuBWgr5XTthNMfiEK74VSatEkh6IDGB+waMIu+FC0RWkhB/JeYFov2fHbzkOVPG3H9gBXCjsnaBONRC0Y68ydLop4SH/iqWwQgOWWj42of6UhGeVZ5LvQMCzczPEiYP/hTEIwLnyw70LkZxWgjodgvc8UmJv08+0XN1Bf/OUOTOQo8cMyo7KV5Jlaw1YKqb5VuU8o2l2D+mjcK/vA4v/zmkoS4W1gJpA6vcs3w0/q/XBO3VD1BfFSBjsDIM=").build();
                npc.show();
            }

            if (args[1].equalsIgnoreCase("printids")) {
                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 15, 15, 15)) {
                    if (!(entity instanceof LivingEntity livingEntity)) continue;
                    System.out.println("--------------------");
                    System.out.println("type: " + livingEntity.getType());
                    System.out.println("id: " + livingEntity.getEntityId());
                }
            }

            if (args[1].equalsIgnoreCase("replace")) {
                org.bukkit.entity.EntityType type = org.bukkit.entity.EntityType.valueOf(args[2]);

                for (Entity entity : loc.getWorld().getNearbyEntities(loc, 15, 15, 15)) {
                    if (!(entity instanceof LivingEntity livingEntity)) continue;
                    if (!livingEntity.getType().equals(type)) continue;

                    int id = livingEntity.getEntityId();
                    EntityEquipment equipment = livingEntity.getEquipment();

                    npc = new NPC.Builder(entity.getLocation())
                            .skin("ewogICJ0aW1lc3RhbXAiIDogMTY3MzczMjUzNjUwNiwKICAicHJvZmlsZUlkIiA6ICIzYjgwOTg1YWU4ODY0ZWZlYjA3ODg2MmZkOTRhMTVkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJLaWVyYW5fVmF4aWxpYW4iLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmUzODI1YjlmZmFmMGI4MGFkYzRhODgwYTk2YTFjYWY2MDAzNGIyNjMzYWU0NDk5MGIyZmJkYTFmYTk1NzY1YyIKICAgIH0KICB9Cn0=",
                                    "pMwz6j6WpGAEe5MkIwf7mePYKqYSTaTH8CgD1BuAS9ONZg3q6vTEHHSjdvE7mKfjKP1GOcEC+ayPvUPMipsNe2aFkRrR23wwxW7DjCPFftHAzu6f1CZap1LPNKBLLJgDpNOTwVLhvKQ9JZYxevKp8Z3Drm+s6+MIvmvHX7E2QzHWk9wHs7SHmY0NlHemGFjpAkydqbxRjuIFpV/1cbPpClkyP4jJHTn1pME8nIFHIyMP/YAyP4oUYDyvkCBq+As6n12DZ5UyTLBcsJOKM+kCGac4eHnQMDhj+LV/LZd3aEGKSzudpAx1tZ8mNvC8Egw7lXEwT72F9ou+DdENXBQIw5PUCmFR6ucMYvMYoWtz7GBdWinHbx3RiuqwN45QCcX43JjCTem9IYeN/+ArKTSo9jklOCxcnq+c+MACMYWFXFJlgRZPvuBWgr5XTthNMfiEK74VSatEkh6IDGB+waMIu+FC0RWkhB/JeYFov2fHbzkOVPG3H9gBXCjsnaBONRC0Y68ydLop4SH/iqWwQgOWWj42of6UhGeVZ5LvQMCzczPEiYP/hTEIwLnyw70LkZxWgjodgvc8UmJv08+0XN1Bf/OUOTOQo8cMyo7KV5Jlaw1YKqb5VuU8o2l2D+mjcK/vA4v/zmkoS4W1gJpA6vcs3w0/q/XBO3VD1BfFSBjsDIM=")
                            .equipment(EquipmentSlot.HEAD, equipment.getItem(EquipmentSlot.HEAD))
                            .equipment(EquipmentSlot.CHEST, equipment.getItem(EquipmentSlot.CHEST))
                            .equipment(EquipmentSlot.LEGS, equipment.getItem(EquipmentSlot.LEGS))
                            .equipment(EquipmentSlot.FEET, equipment.getItem(EquipmentSlot.FEET))
                            .equipment(EquipmentSlot.HAND, equipment.getItem(EquipmentSlot.HAND))
                            .equipment(EquipmentSlot.OFF_HAND, equipment.getItem(EquipmentSlot.OFF_HAND))
                            .entityId(id)
                            .build();
                    npc.show();
                    System.out.println("replaced");
                }
            }
        }

        if (args[0].equalsIgnoreCase("wraptest")) {
            String text = "His ultimate dream fantasy consisted of being content and sleeping eight hours in a row.";

            System.out.println("UnWrapped: " + text);
            //System.out.println("Wrapped: " + Arrays.toString(StringUtils.wrapText(text, 50)));
            System.out.println("WorldUtils: " + WordUtils.wrap(text, 50));
        }

        if (args[0].equalsIgnoreCase("skill")) {
            AnariPlayer anariPlayer = AnariPlayerManager.getPlayer(player);
            Skill combatSkill = anariPlayer.getSkill(SkillType.COMBAT);

            if (args[1].equalsIgnoreCase("get")) {
                System.out.println("Combat level: " + combatSkill.getCurrentLevel());
            }
            if (args[1].equalsIgnoreCase("set")) {
                combatSkill.setLevel(Integer.valueOf(args[2]));
            }
        }

        if (args[0].equalsIgnoreCase("displayentity")) {
            DisplayEntity displayEntity = new DisplayEntity.TextDisplay(player.getLocation())
                    .scale(new Vector3f(5, 5, 5))
                    //.leftRotation(new Quaternionf(83, 56, 46, 1))
                    .displayText("&aJEBANIE &eNA &cKASZTANIE")
                    .billboardType(BillboardType.CENTER)
                    .backgroundColor(66, 135, 245, 128)
                    .show();

            DisplayEntity.TextDisplay textDisplay = (DisplayEntity.TextDisplay) displayEntity.getDisplay();

            /*new BukkitRunnable() {
                @Override
                public void run() {
                    textDisplay.updateDisplayText("KURWARARWARW");
                    textDisplay.updateBackgroundColor(255, 0, 0, 128);
                    textDisplay.updateBillboardType(BillboardType.HORIZONTAL);
                    textDisplay.updateScale(new Vector3f(2, 2, 2));
                }
            }.runTaskLater(Main.getInstance(), 40);*/
        }

        if (args[0].equalsIgnoreCase("nerftest")) {
            ItemStack item = player.getInventory().getItemInMainHand();

            ItemMeta meta = item.getItemMeta();
            //System.out.println("atts: " + meta.);

            /*PotionMeta potionMeta = (PotionMeta) meta;

            meta.addAttributeModifier(Attribute.ATTACK_DAMAGE, new
                    AttributeModifier(new NamespacedKey(Main.getInstance(),
                    "nerfedStrength"), -2.0D, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.HAND));*/

            System.out.println("modifiers: " + meta.getAttributeModifiers());

            item.setItemMeta(meta);
        }

        if (args[0].equalsIgnoreCase("die")) {
            player.playEffect(EntityEffect.TOTEM_RESURRECT);
        }

        if (args[0].equalsIgnoreCase("menutest")) {
            new SkillsMenu(player);
        }

        if (args[0].equalsIgnoreCase("acctest")) {
            if (args[1].equalsIgnoreCase("add")) {
                AnariPlayerManager.getPlayer(player).equipAccessory(AnariItems.getInstance(AnariID.TOTEM_OF_UNDYING));
            }
            if (args[1].equalsIgnoreCase("put")) {
                AnariPlayerManager.getPlayer(player).setAccessory(Integer.valueOf(args[2]), AnariItems.getInstance(AnariID.TOTEM_OF_UNDYING));
            }
            if (args[1].equalsIgnoreCase("print")) {
                System.out.println("accessories: " + AnariPlayerManager.getPlayer(player).getAccessories());
            }
            if (args[1].equalsIgnoreCase("has")) {
                System.out.println("has equipped: " + AnariPlayerManager.getPlayer(player).hasEquipped(AnariID.TOTEM_OF_UNDYING));
            }
        }

        if (args[0].equalsIgnoreCase("die")) {
            Location loc = player.getLocation();


        }

        if (args[0].equalsIgnoreCase("setslot")) {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);

            InventoryMenu menu = (((CraftPlayer) player).getHandle()).inventoryMenu;

            ClientboundContainerSetSlotPacket setSlotPacket =
                    new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(),
                            0, CraftItemStack.asNMSCopy(item));

            ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;
            connection.send(setSlotPacket);
        }

        if (args[0].equalsIgnoreCase("invcheck")) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    ItemStack item = new ItemStack(Material.PLAYER_HEAD);
                    ItemUtils.setName(item, "&eAccessories");

                    ItemMeta itemMeta = item.getItemMeta();
                    itemMeta.setLore(List.of("&7View the accessories menu."));
                    item.setItemMeta(itemMeta);

                    InventoryMenu menu = (((CraftPlayer) player).getHandle()).inventoryMenu;
                    if (!CraftItemStack.asBukkitCopy(menu.getResultSlot().getItem()).getType().equals(Material.AIR))
                        item = CraftItemStack.asBukkitCopy(menu.getResultSlot().getItem());

                    //System.out.println("crafting slots: " + menu.getResultSlot().getItem());

                    ClientboundContainerSetSlotPacket setSlotPacket =
                            new ClientboundContainerSetSlotPacket(menu.containerId, menu.getStateId(),
                                    0, CraftItemStack.asNMSCopy(item));

                    ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;
                    connection.send(setSlotPacket);
                }
            }.runTaskTimer(Main.getInstance(), 0, 5);
        }

        if (args[0].equalsIgnoreCase("updatenew")) {
            for (int slot = 0; slot < player.getInventory().getContents().length; slot++) {
                ItemStack item = player.getInventory().getItem(slot);
                if (ItemUtils.isNull(item)) continue;

                //AnariItem.update(item, player.getInventory(), slot);
            }
        }

        if (args[0].equalsIgnoreCase("update")) {
            ItemStack heldItem = player.getInventory().getItemInMainHand();
            if (ItemUtils.isNull(heldItem)) {
                StringUtils.sendMessage(player, "&cYou are not holding anything!");
                return false;
            }

            if (!AnariItem.isAnariItem(heldItem)) {
                StringUtils.sendMessage(player, "&cYou are not holding an Anari item!");
                return false;
            }


            if (AnariItem.getId(heldItem) == null) {
                StringUtils.sendMessage(player, "&cThis item has invalid Anari Id!");
                return false;
            }
            else {
                //player.getInventory().setItemInMainHand(AnariItem.getId(heldItem).getItem().get().clone());
                //AnariItem.getId(heldItem);

                //System.out.println("held it: " + AnariItem.getId(heldItem));
                //AnariItem.getId(heldItem).getItem();


                AnariID itemId = AnariItem.getId(heldItem);
                player.getInventory().setItemInMainHand(AnariItems.getItem(itemId));

                //player.getInventory().setItemInMainHand();
            }
        }

        return true;
    }

    private static final Map<String, EntityDataAccessor<?>> cachedAccessors = new HashMap<>();

    private <T> void updateDisplayData(Player player, Display displayEntity, Class<?> classField, String fieldName, T value) {
        ServerCommonPacketListenerImpl connection = (((CraftPlayer) player).getHandle()).connection;

        String cacheKey = classField.getName() + "." + fieldName;
        EntityDataAccessor<T> accessor = (EntityDataAccessor<T>) cachedAccessors.get(cacheKey);

        if (accessor == null) {
            try {
                Field field = classField.getDeclaredField(fieldName);
                field.setAccessible(true);

                accessor = (EntityDataAccessor<T>) field.get(null);

                cachedAccessors.put(cacheKey, accessor);
            } catch (Exception ex) {
                ex.printStackTrace();
                return;
            }
        }

        SynchedEntityData.DataItem<T> dataItem = new SynchedEntityData.DataItem<>(accessor, value);
        ClientboundSetEntityDataPacket packet = new ClientboundSetEntityDataPacket(
                displayEntity.getId(),
                List.of(dataItem.value())
        );

        connection.send(packet);
    }
}
