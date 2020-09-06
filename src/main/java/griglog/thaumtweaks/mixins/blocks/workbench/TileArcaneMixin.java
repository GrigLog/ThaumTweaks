package griglog.thaumtweaks.mixins.blocks.workbench;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.mixins.blocks.workbench.itemhandlers.CrystalHandler;
import griglog.thaumtweaks.mixins.blocks.workbench.itemhandlers.ExitHandler;
import griglog.thaumtweaks.mixins.blocks.workbench.itemhandlers.GridHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.CapabilityItemHandler;
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
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.HashMap;


@Mixin(TileArcaneWorkbench.class)
public abstract class TileArcaneMixin extends TileThaumcraft implements ITickable {
    @Shadow public abstract void spendAura(int vis);

    @Shadow public InventoryArcaneWorkbench inventoryCraft;
    public InventoryArcaneResult inventoryResult;

    private static final EnumFacing[] FACES_GRID = new EnumFacing[] {EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
    private static final EnumFacing[] FACES_CRYSTALS = new EnumFacing[] {EnumFacing.SOUTH};
    private static final EnumFacing[] FACES_EXIT = new EnumFacing[] {EnumFacing.DOWN};

    private static final HashMap<Aspect, Integer> aspectSlots = new HashMap<>();
    CrystalHandler crystals;
    GridHandler grid;
    ExitHandler exit;
    public boolean preview = true;  //if true, hoppers cant extract result from exit slot
    int counter;
    IArcaneRecipe savedRecipe;

    @Inject(method="<init>", at=@At("RETURN"))
    void adjustConstructor(CallbackInfo ci) {
        try {
            inventoryResult = new InventoryArcaneResult(this, (Container) container.get(inventoryCraft));
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        crystals = new CrystalHandler(this, inventoryCraft);
        grid = new GridHandler(this, inventoryCraft);
        exit = new ExitHandler(this, inventoryResult);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            for (EnumFacing f : FACES_GRID) {
                if (facing == f)
                    return (T) grid;
            }
            for (EnumFacing f: FACES_CRYSTALS) {
                if (facing == f)
                    return (T) crystals;
            }
            for (EnumFacing f: FACES_EXIT) {
                if (facing == f) {
                    if (!exit.getStackInSlot(0).isEmpty())
                        return (T) exit;
                    return (T)grid;
                }
            }
        }
        return super.getCapability(capability, facing);
    }

    public void tryCrafting(World world, EntityPlayer player, Integer windowId) {
        if (world.isRemote)
            return;
        FakePlayer fake = SF.getFake((WorldServer)world);
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
                consumeItems(arecipe);
                preview = false;
            }
            else if (count + itemstack.getCount() <= current.getMaxStackSize()) {
                count += itemstack.getCount();
                consumeItems(arecipe);
            }
            current = itemstack.copy();
            current.setCount(count);
            inventoryResult.setInventorySlotContents(0, current);
            inventoryResult.markDirty();
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
        for (int i = 0; i < grid.getSlots(); i++) {
            ItemStack is = grid.getStackInSlot(i);
            if (!is.isEmpty())
                is.setCount(is.getCount() - 1);
        }
        if (recipe.getCrystals() != null && recipe.getCrystals().getAspects() != null) {
            for (Aspect a : recipe.getCrystals().getAspects()) {
                ItemStack is = crystals.getStackInSlot(aspectSlots.get(a));
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
    public void update() {
        if (!world.isRemote && ++counter % 10 == 0){
            tryCrafting(world, SF.getFake((WorldServer)world), null);
        }
    }



    //interesting part ends here

    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtCompound, stacks);

        for(int a = 0; a < stacks.size(); ++a) {
            this.inventoryCraft.setInventorySlotContents(a, (ItemStack)stacks.get(a));
        }
        preview = nbtCompound.getBoolean("preview");
        exit.deserializeNBT(nbtCompound);

    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbtCompound) {
        super.writeToNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);

        for(int a = 0; a < stacks.size(); ++a) {
            stacks.set(a, this.inventoryCraft.getStackInSlot(a));
        }

        ItemStackHelper.saveAllItems(nbtCompound, stacks);
        nbtCompound.setBoolean("preview", preview);
        nbtCompound.merge(exit.serializeNBT());
        return nbtCompound;
    }

    @Shadow
    void getAura(){}
    @Shadow
    int auraVisClient;
    @Shadow
    int auraVisServer;

    private static Field container;
    static {
        for (int i = 0; i < CrystalHandler.aspectGrid.length; i++)
            aspectSlots.put(CrystalHandler.aspectGrid[i], i);
        try {
            container = InventoryCrafting.class.getDeclaredField((boolean)Launch.blackboard.get("fml.deobfuscatedEnvironment") ?
                    "eventHandler": "field_70465_c");
            container.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
