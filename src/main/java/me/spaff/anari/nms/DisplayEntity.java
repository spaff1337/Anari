package me.spaff.anari.nms;

import me.spaff.anari.utils.StringUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DisplayEntity {
    private GenericDisplay<?> display;

    private final Display displayEntity;
    private Location location;

    private Vector3f scale;
    private Quaternionf leftRotation;
    private Quaternionf rightRotation;

    public DisplayEntity(GenericDisplay<?> builder) {
        this.display = builder;
        this.displayEntity = builder.displayEntity;
        this.location = builder.location;

        this.scale = builder.scale;
        this.leftRotation = builder.leftRotation;
        this.rightRotation = builder.rightRotation;
    }

    // Show
    public void show(Player player) {
        display.show(player);
    }

    public void show() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            show(player);
        }
    }

    // Remove
    public void remove(Player player) {
        Packets.sendRemoveEntityPacket(player, displayEntity);
    }

    public void remove() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            remove(player);
        }
    }

    // Teleport
    public void teleport(Player player, Location location) {
        this.location = location;
        Packets.sendTeleportPacket(player, displayEntity, location);
    }

    public void teleport(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            teleport(player, location);
        }
    }

    // Getters
    public Location getLocation() {
        return location;
    }

    public GenericDisplay<?> getDisplay() {
        return display;
    }

    // Generic
    public Vector3f getScale() {
        return scale;
    }

    public Quaternionf getLeftRotation() {
        return leftRotation;
    }

    public Quaternionf getRightRotation() {
        return rightRotation;
    }

    public DisplayEntity clone() {
        return new DisplayEntity(display);
    }

    private static class GenericDisplay<T extends GenericDisplay<T>> {
        protected final Display displayEntity;
        protected final Location location;

        protected Vector3f scale;
        protected Quaternionf leftRotation;
        protected Quaternionf rightRotation;

        public GenericDisplay(Display displayEntity, Location location) {
            this.displayEntity = displayEntity;
            this.location = location;

            this.scale = new Vector3f(1.F, 1.F ,1.F);
            this.leftRotation = new Quaternionf();
            this.rightRotation = new Quaternionf();
        }

        // Scale
        public T scale(Vector3f scale) {
            this.scale = scale;
            return (T) this;
        }

        public void updateScale(Player player, Vector3f scale) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.class, "t", scale); // t -> DATA_SCALE_ID
        }

        public void updateScale(Vector3f scale) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateScale(player, scale);
            }
        }

        // Rotations
        public T leftRotation(Quaternionf rotation) {
            this.leftRotation = rotation;
            return (T) this;
        }

        public void updateLeftRotation(Player player, Quaternionf rotation) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.class, "u", rotation.normalize()); // u -> DATA_LEFT_ROTATION_ID
        }

        public void updateLeftRotation(Quaternionf rotation) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateLeftRotation(player, rotation);
            }
        }

        public T rightRotation(Quaternionf rotation) {
            this.rightRotation = rotation;
            return (T) this;
        }

        public void updateRightRotation(Player player, Quaternionf rotation) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.class, "ay", rotation.normalize()); // ay -> DATA_RIGHT_ROTATION_ID
        }

        public void updateRightRotation(Quaternionf rotation) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateRightRotation(player, rotation);
            }
        }

        public DisplayEntity show(Player player) {
            updateScale(player, scale);
            updateLeftRotation(player, leftRotation.normalize());
            updateRightRotation(player, rightRotation.normalize());
            return new DisplayEntity(this);
        }

        public DisplayEntity show() {
            for (Player player : Bukkit.getOnlinePlayers()) {
                show(player);
            }
            return new DisplayEntity(this);
        }
    }

    // Block Display
    public static class BlockDisplay extends GenericDisplay<BlockDisplay> {
        private Block displayBlock;

        public BlockDisplay(Location location) {
            super(new Display.BlockDisplay(EntityType.BLOCK_DISPLAY, ((CraftWorld) location.getWorld()).getHandle()), location);
            this.displayBlock = Blocks.STONE;
        }

        public BlockDisplay displayedBlock(Block block) {
            displayBlock = block;
            return this;
        }

        public void updateDisplayedBlock(Player player, Block block) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.BlockDisplay.class, "p", block.defaultBlockState()); // p -> DATA_BLOCK_STATE_ID
        }

        public void updateDisplayedBlock(Block block) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateDisplayedBlock(player, block);
            }
        }

        @Override
        public DisplayEntity show(Player player) {
            Packets.sendAddEntityPacket(player, displayEntity, location);
            super.show(player);

            updateDisplayedBlock(player, displayBlock);

            return new DisplayEntity(this);
        }
    }

    // Item Display
    public static class ItemDisplay extends GenericDisplay<ItemDisplay> {
        private ItemStack displayItem;
        private DisplayContext displayContext;

        public ItemDisplay(Location location) {
            super(new Display.ItemDisplay(EntityType.ITEM_DISPLAY, ((CraftWorld) location.getWorld()).getHandle()), location);
            this.displayItem = new ItemStack(Material.DIRT);
            this.displayContext = DisplayContext.GUI;
        }

        public ItemDisplay displayedItem(ItemStack item) {
            Validate.notNull(item, "Item cannot be null!");
            this.displayItem = item;
            return this;
        }

        public void updateDisplayedItem(Player player, ItemStack item) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.ItemDisplay.class, "q", CraftItemStack.asNMSCopy(item)); // q -> DATA_ITEM_STACK_ID
        }

        public void updateDisplayedItem(ItemStack item) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateDisplayedItem(player, item);
            }
        }

        public ItemDisplay displayContext(DisplayContext context) {
            this.displayContext = context;
            return this;
        }

        public void updateDisplayContext(Player player, DisplayContext context) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.ItemDisplay.class, "r", context.asByte()); // r -> DATA_ITEM_DISPLAY_ID
        }

        public void updateDisplayContext(DisplayContext context) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateDisplayContext(context);
            }
        }

        @Override
        public DisplayEntity show(Player player) {
            Packets.sendAddEntityPacket(player, displayEntity, location);
            super.show(player);

            updateDisplayedItem(player, displayItem);
            updateDisplayContext(player, displayContext);

            return new DisplayEntity(this);
        }
    }

    // Text Display
    public static class TextDisplay extends GenericDisplay<TextDisplay> {
        private String displayText;
        private BillboardType billboardType;
        private int backgroundColor;

        public TextDisplay(Location location) {
            super(new Display.TextDisplay(EntityType.TEXT_DISPLAY, ((CraftWorld) location.getWorld()).getHandle()), location);
            this.displayText = "";
            this.billboardType = BillboardType.CENTER;
            this.backgroundColor = 1073741824; // Black
        }

        public TextDisplay displayText(String text) {
            this.displayText = text;
            return this;
        }

        public void updateDisplayText(Player player, String text) {
            MutableComponent component = Component.literal(StringUtils.getColoredText(text));
            Packets.sendDisplayDataPacket(player, displayEntity, Display.TextDisplay.class, "aG", component); // aG -> DATA_TEXT_ID
        }

        public void updateDisplayText(String text) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateDisplayText(player, text);
            }
        }

        public TextDisplay backgroundColor(int r, int g, int b, int a) {
            this.backgroundColor = Color.fromARGB(a, r, g, b).asARGB();
            return this;
        }

        public void updateBackgroundColor(Player player, int r, int g, int b, int a) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.TextDisplay.class, "aI", Color.fromARGB(a, r, g, b).asARGB()); // aI -> DATA_BACKGROUND_COLOR_ID
        }

        public void updateBackgroundColor(int r, int g, int b, int a) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateBackgroundColor(player, r, g, b, a);
            }
        }

        public TextDisplay billboardType(BillboardType type) {
            this.billboardType = type;
            return this;
        }

        public void updateBillboardType(Player player, BillboardType type) {
            Packets.sendDisplayDataPacket(player, displayEntity, Display.class, "az", type.asByte()); // az -> DATA_BILLBOARD_RENDER_CONSTRAINTS_ID
        }

        public void updateBillboardType(BillboardType type) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                updateBillboardType(player, type);
            }
        }

        @Override
        public DisplayEntity show(Player player) {
            Packets.sendAddEntityPacket(player, displayEntity, location);
            super.show(player);

            updateDisplayText(player, displayText);

            int red = Color.fromARGB(backgroundColor).getRed();
            int green = Color.fromARGB(backgroundColor).getGreen();
            int blue = Color.fromARGB(backgroundColor).getBlue();
            int alpha = Color.fromARGB(backgroundColor).getAlpha();
            updateBackgroundColor(player, red, green, blue, alpha);

            updateBillboardType(player, billboardType);

            return new DisplayEntity(this);
        }
    }
}