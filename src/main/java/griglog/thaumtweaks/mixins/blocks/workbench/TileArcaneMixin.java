package griglog.thaumtweaks.mixins.blocks.workbench;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.ThaumTweaks;
import griglog.thaumtweaks.mixins.blocks.InventoryArcaneResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.items.resources.ItemCrystalEssence;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import java.lang.reflect.Field;
import java.util.HashMap;


@Mixin(TileArcaneWorkbench.class)
public abstract class TileArcaneMixin extends TileThaumcraft implements ISidedInventory, ITickable {
    @Shadow public abstract void spendAura(int vis);

    @Shadow public InventoryArcaneWorkbench inventoryCraft;
    public InventoryArcaneResult inventoryResult;
    private static final int[] SLOTS_GRID = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] SLOTS_CRYSTALS = new int[]{9, 10, 11, 12, 13, 14};
    private static final int[] SLOTS_EXIT = new int[]{15, 0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final EnumFacing[] FACES_GRID = new EnumFacing[] {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
    private static final EnumFacing[] FACES_CRYSTALS = new EnumFacing[] {EnumFacing.SOUTH};
    private static final Aspect[] aspectGrid = new Aspect[] {Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};
    private static final HashMap<Aspect, Integer> aspectSlots = new HashMap<>();
    public boolean preview = true;  //if true, hoppers cant extract result from exit slot
    int counter;
    FakePlayer fake;
    IArcaneRecipe savedRecipe;

    private static Field container;
    static {
        for (int i = 0; i < aspectGrid.length; i++)
            aspectSlots.put(aspectGrid[i], i);
        try {
            container = InventoryCrafting.class.getDeclaredField((boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment") ?
                            "eventHandler": "field_70465_c");
            container.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Inject(method="<init>", at=@At("RETURN"))
    void addResultSlot(CallbackInfo ci) {
        try {
            inventoryResult = new InventoryArcaneResult(this, (Container) container.get(inventoryCraft));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void tryCrafting(World world, EntityPlayer player, Integer windowId) {
        if (world.isRemote)
            return;
        fake = SF.getFake((WorldServer)world);
        SF.copyKnowledge(this, fake);
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(inventoryCraft, fake);
        savedRecipe = recipe;
        boolean hasVis = true;
        boolean hasCrystals = true;
        if (recipe != null) {
            int vis = (int)(recipe.getVis() * (1.0F - CasterManager.getTotalVisDiscount(fake)));
            AspectList crystals = recipe.getCrystals();
            getAura();
            hasVis = getWorld().isRemote ? auraVisClient >= vis : auraVisServer >= vis;
            if (crystals != null && crystals.size() > 0) {
                Aspect[] aspects = crystals.getAspects();
                for (Aspect aspect: aspects) {
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(inventoryCraft, EnumFacing.UP),
                            ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)),
                            ThaumcraftInvHelper.InvFilter.STRICT)
                            < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }

        if (hasVis && hasCrystals) {
            doCrafting(world, player, windowId);
        }
    }

    public void doCrafting(World world, EntityPlayer player, Integer windowId) {  //sets itemstack to exit slot
        if (world.isRemote)
            return;
        EntityPlayerMP entityplayermp = (EntityPlayerMP)player;
        ItemStack itemstack = ItemStack.EMPTY;
        IArcaneRecipe arecipe;
        FakePlayer fake = SF.getFake((WorldServer)world);
        SF.copyKnowledge(this, fake);
        arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(inventoryCraft, fake);

        boolean powered = world.isBlockPowered(getPos());
        boolean arecipeFound = false;
        IPlayerKnowledge fakeBrains = getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        if (arecipe != null && fakeBrains != null
                && (!powered || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(arecipe))
                && fakeBrains.isResearchKnown(arecipe.getResearch())) {
            inventoryResult.setRecipeUsed(arecipe);
            itemstack = arecipe.getCraftingResult(inventoryCraft);
            arecipeFound = true;
        } else if (powered) {
            InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);

            for(int a = 0; a < 9; ++a) {
                craftInv.setInventorySlotContents(a, inventoryCraft.getStackInSlot(a));
            }

            IRecipe irecipe = CraftingManager.findMatchingRecipe(craftInv, world);
            if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
                inventoryResult.setRecipeUsed(irecipe);
                itemstack = irecipe.getCraftingResult(inventoryCraft);
            }
        }

        if (!powered && arecipe != null && arecipeFound) {  //was not powered with redstone, craft things
            ItemStack current = inventoryResult.getStackInSlot(0);
            int count = current.getCount();
            if (preview) {
                count = itemstack.getCount();
                preview = false;
            }
            else if (count + itemstack.getCount() < current.getMaxStackSize()) {
                count += itemstack.getCount();
            }
            current = itemstack.copy();
            current.setCount(count);
            inventoryResult.setInventorySlotContents(0, current);
            inventoryResult.markDirty();
            consumeItems(arecipe);
        }
        else { //was opened with hands, show craft result
            if (inventoryResult.getStackInSlot(0).isEmpty())
                preview = true;
            if (preview) {
                inventoryResult.setInventorySlotContents(0, itemstack);
                if (windowId != null)
                    entityplayermp.connection.sendPacket(new SPacketSetSlot(windowId, 0, itemstack));
            }
        }

    }

    void consumeItems(IArcaneRecipe recipe){
        for (int i: SLOTS_GRID) {
            ItemStack is = getStackInSlot(i);
            if (is != null && !is.isEmpty())
                is.setCount(is.getCount() - 1);
        }
        if (recipe.getCrystals() != null && recipe.getCrystals().getAspects() != null) {
            for (Aspect a : recipe.getCrystals().getAspects()) {
                ItemStack is = getStackInSlot(aspectSlots.get(a) + 9);
                is.setCount(is.getCount() - recipe.getCrystals().getAmount(a));
            }
        }
        int vis = recipe.getVis();
        if (vis > 0){
            getAura();
            spendAura(vis);
        }

    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        for (EnumFacing f: FACES_GRID) {
            if (side == f)
                return SLOTS_GRID;
        }
        for (EnumFacing f: FACES_CRYSTALS) {
            if (side == f)
                return SLOTS_CRYSTALS;
        }
        return SLOTS_EXIT;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing side) {
        for (EnumFacing f: FACES_GRID) {
            if (side == f)
                return getStackInSlot(index).getCount() + itemStackIn.getCount() <= getInventoryStackLimit();
        }
        for (EnumFacing f: FACES_CRYSTALS) {
            if (side == f)
                return canInsertCrystal(index, itemStackIn);  //looks like this one is callid twice, but its inevitable
        }
        return inventoryResult.canInsertItem(index, itemStackIn, side);

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        for (int i : SLOTS_CRYSTALS){
            if (index == i) {
                return canInsertCrystal(index, stack);
            }
        }
        return true;
    }

    private boolean canInsertCrystal(int index, ItemStack stack) {
        Item item = stack.getItem();
        return (item instanceof ItemCrystalEssence  &&
                ((ItemCrystalEssence) item).getAspects(stack).getAspects()[0].getName().equals(aspectGrid[index - 9].getName()));
    }


    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        if (!world.isBlockPowered(getPos()) && direction == EnumFacing.DOWN){
            if (index == 15)
                return inventoryResult.canExtractItem(index, stack, direction);
            for (int i: SLOTS_GRID) {
                if (index == i)
                    return true;
            }
        }
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public int getSizeInventory() {
        return 16;
    }

    @Override
    public void update() {
        if (!world.isRemote && ++counter % 10 == 0){
            tryCrafting(world, SF.getFake((WorldServer)world), null);
        }
    }

    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtCompound, stacks);

        for(int a = 0; a < stacks.size(); ++a) {
            this.inventoryCraft.setInventorySlotContents(a, (ItemStack)stacks.get(a));
        }
        preview = nbtCompound.getBoolean("preview");

    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);

        for(int a = 0; a < stacks.size(); ++a) {
            stacks.set(a, this.inventoryCraft.getStackInSlot(a));
        }

        ItemStackHelper.saveAllItems(nbtCompound, stacks);
        nbtCompound.setBoolean("preview", preview);
        return nbtCompound;
    }

    //the rest of methods are just redirections

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public boolean isEmpty() {
        return inventoryCraft.isEmpty();
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        if (index == 15)
            return inventoryResult.getStackInSlot(index);
        return inventoryCraft.getStackInSlot(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index == 15)
            return inventoryResult.decrStackSize(index, count);
        return inventoryCraft.decrStackSize(index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        if (index == 15)
            return inventoryResult.removeStackFromSlot(index);
        return inventoryCraft.removeStackFromSlot(index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 15)
            inventoryResult.setInventorySlotContents(index, stack);
        inventoryCraft.setInventorySlotContents(index, stack);
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        return inventoryCraft.isUsableByPlayer(player);
    }

    @Override
    public void openInventory(EntityPlayer player) {
        inventoryCraft.openInventory(player);
    }

    @Override
    public int getField(int id) {
        return inventoryCraft.getField(id);
    }

    @Override
    public void setField(int id, int value) {
        inventoryCraft.setField(id, value);
    }

    @Override
    public int getFieldCount() {
        return inventoryCraft.getFieldCount();
    }

    @Override
    public void clear() {
        inventoryCraft.clear();
    }

    @Override
    public String getName() {
        return inventoryCraft.getName();
    }

    @Override
    public boolean hasCustomName() {
        return inventoryCraft.hasCustomName();
    }

    @Shadow
    void getAura(){}
    @Shadow
    int auraVisClient;
    @Shadow
    int auraVisServer;
}
