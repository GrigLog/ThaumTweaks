package griglog.thaumtweaks.blocks.crafter;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.blocks.crafter.helpers.CrystalHandler;
import griglog.thaumtweaks.blocks.crafter.helpers.ExitHandler;
import griglog.thaumtweaks.blocks.crafter.helpers.GridHandler;
import griglog.thaumtweaks.items.ItemFiller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.items.CapabilityItemHandler;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.container.InventoryArcaneWorkbench;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.world.aura.AuraChunk;
import thaumcraft.common.world.aura.AuraHandler;

import javax.annotation.Nullable;
import java.util.HashMap;

public class TileArcaneCrafter extends TileEntity {
    public InventoryArcaneWorkbench inventoryCraft = new InventoryArcaneWorkbench(this, new ContainerDummy());
    public InventoryCraftResult inventoryResult = new InventoryCraftResult();
    public int auraVisServer = 0;
    public int auraVisClient = 0;
    private static final EnumFacing[] FACES_GRID = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
    private static final EnumFacing[] FACES_CRYSTALS = new EnumFacing[]{EnumFacing.SOUTH};
    private static final EnumFacing[] FACES_EXIT = new EnumFacing[]{EnumFacing.DOWN};
    private static final HashMap<Aspect, Integer> aspectSlots = new HashMap<Aspect, Integer>() {{
        put(Aspect.AIR, 0); put(Aspect.FIRE, 1); put(Aspect.WATER, 2); put(Aspect.EARTH, 3); put(Aspect.ORDER, 4); put(Aspect.ENTROPY, 5);
    }};
    CrystalHandler crystals;
    GridHandler grid;
    ExitHandler exit;
    boolean ignoreInvUpdates = false;

    public TileArcaneCrafter(){
        crystals = new CrystalHandler(this, inventoryCraft);
        grid = new GridHandler(this, inventoryCraft);
        exit = new ExitHandler(this, inventoryResult);
    }

    public void onInventoryUpdate(){
        //ThaumTweaks.LOGGER.info("update");
        if (!ignoreInvUpdates) {
            ignoreInvUpdates = true;
            checkCrafting();
            ignoreInvUpdates = false;
        }
    }

    public void checkCrafting() {
        if (world.isRemote)
            return;
        InventoryCrafting grid = parseFillers(inventoryCraft);
        FakePlayer player = SF.getFake((WorldServer)world);
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(grid, player);
        if (recipe == null)
            return;
        if (!getCapability(ThaumcraftCapabilities.KNOWLEDGE, null).isResearchKnown(recipe.getResearch()))
            return;
        int vis = recipe.getVis();
        updateAura();
        boolean hasVis = getWorld().isRemote ? auraVisClient >= vis : auraVisServer >= vis;
        if (hasVis && hasCrystals(recipe)) {
            autoCraftOneItem(recipe);
        }
    }




    private void autoCraftOneItem(IArcaneRecipe recipe) {
        InventoryCrafting parsedGrid = parseFillers(inventoryCraft);
        ItemStack itemstack = recipe.getCraftingResult(parsedGrid);
        ItemStack current = inventoryResult.getStackInSlot(0);
        int count = current.getCount();
        if (count + itemstack.getCount() <= current.getMaxStackSize()) {
            count += itemstack.getCount();
            consumeItems(recipe);
        }
        current = itemstack.copy();
        current.setCount(count);
        inventoryResult.setInventorySlotContents(0, current);
    }

    private boolean hasCrystals(IRecipe recipe) {
        boolean hasCrystals = true;
        AspectList crystals = ((IArcaneRecipe)recipe).getCrystals();
        if (crystals != null && crystals.size() > 0) {
            Aspect[] aspects = crystals.getAspects();
            for (Aspect aspect : aspects) {
                if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(parseFillers(inventoryCraft), EnumFacing.UP),
                        ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)),
                        ThaumcraftInvHelper.InvFilter.STRICT)
                        < crystals.getAmount(aspect)) {
                    hasCrystals = false;
                    break;
                }
            }
        }
        return hasCrystals;
    }


    private void consumeItems(IArcaneRecipe recipe){
        for (int i = 0; i < grid.getSlots(); i++) {
            ItemStack is = grid.getStackInSlot(i);
            if (!is.isEmpty() && !(is.getItem() instanceof ItemFiller))
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
            updateAura();
            spendAura(vis);
        }
    }

    private InventoryCrafting parseFillers(InventoryArcaneWorkbench inv) {
        InventoryCrafting res = new InventoryArcaneWorkbench(null, new ContainerDummy());
        for (int i = 0; i < inv.getSizeInventory(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() instanceof ItemFiller) {
                stack = ItemStack.EMPTY;
            }
            res.setInventorySlotContents(i, stack);
        }
        return res;
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (facing == EnumFacing.DOWN)
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(exit);
            else if (facing == EnumFacing.getFront(getBlockMetadata()))
                return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(crystals);
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(grid);
        }
        return super.getCapability(capability, facing);
    }


    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NonNullList<ItemStack> contents = NonNullList.withSize(16, ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbt, contents);
        for(int i = 0; i < 15; ++i) {
            inventoryCraft.setInventorySlotContents(i, contents.get(i));
        }
        inventoryResult.setInventorySlotContents(0, contents.get(15));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NonNullList<ItemStack> contents = NonNullList.withSize(16, ItemStack.EMPTY);
        for(int i = 0; i < 15; ++i) {
            contents.set(i, inventoryCraft.getStackInSlot(i));
        }
        contents.set(15, inventoryResult.getStackInSlot(0));
        ItemStackHelper.saveAllItems(nbt, contents);
        return nbt;
    }





    public void updateAura() {
        if (!this.getWorld().isRemote) {
            int t = 0;
            if (this.world.getBlockState(this.getPos().up()).getBlock() != BlocksTC.arcaneWorkbenchCharger) {
                t = (int) AuraHandler.getVis(this.getWorld(), this.getPos());
            } else {
                int sx = this.pos.getX() >> 4;
                int sz = this.pos.getZ() >> 4;

                for(int xx = -1; xx <= 1; ++xx) {
                    for(int zz = -1; zz <= 1; ++zz) {
                        AuraChunk ac = AuraHandler.getAuraChunk(this.world.provider.getDimension(), sx + xx, sz + zz);
                        if (ac != null) {
                            t = (int)((float)t + ac.getVis());
                        }
                    }
                }
            }

            this.auraVisServer = t;
        }

    }

    public void spendAura(int vis) {
        if (!this.getWorld().isRemote) {
            if (this.world.getBlockState(this.getPos().up()).getBlock() == BlocksTC.arcaneWorkbenchCharger) {
                int q = vis;
                int z = Math.max(1, vis / 9);
                int attempts = 0;

                while(q > 0) {
                    ++attempts;

                    for(int xx = -1; xx <= 1; ++xx) {
                        for(int zz = -1; zz <= 1; ++zz) {
                            if (z > q) {
                                z = q;
                            }

                            q = (int)((float)q - AuraHandler.drainVis(this.getWorld(), this.getPos().add(xx * 16, 0, zz * 16), (float)z, false));
                            if (q <= 0 || attempts > 1000) {
                                return;
                            }
                        }
                    }
                }
            } else {
                AuraHandler.drainVis(this.getWorld(), this.getPos(), (float)vis, false);
            }
        }
    }
}
