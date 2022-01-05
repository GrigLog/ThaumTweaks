package griglog.thaumtweaks.mixins.blocks.workbench;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import griglog.thaumtweaks.blocks.workbench.InventoryArcaneResult;
import griglog.thaumtweaks.blocks.workbench.itemhandlers.CrystalHandler;
import griglog.thaumtweaks.blocks.workbench.itemhandlers.ExitHandler;
import griglog.thaumtweaks.blocks.workbench.itemhandlers.GridHandler;
import griglog.thaumtweaks.items.ItemFiller;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.ItemStackHelper;
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

    private static final EnumFacing[] FACES_GRID = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.WEST};
    private static final EnumFacing[] FACES_CRYSTALS = new EnumFacing[]{EnumFacing.SOUTH};
    private static final EnumFacing[] FACES_EXIT = new EnumFacing[]{EnumFacing.DOWN};

    private static final HashMap<Aspect, Integer> aspectSlots = new HashMap<>();
    CrystalHandler crystals;
    GridHandler grid;
    ExitHandler exit;
    public boolean preview = true;  //if true, hoppers cant extract result from exit slot

    int counter;

    @Inject(method = "<init>", at = @At("RETURN"))
    void adjustConstructor(CallbackInfo ci) {
        try {
            inventoryResult = new InventoryArcaneResult(this, (Container) container.get(inventoryCraft));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        crystals = new CrystalHandler(this, inventoryCraft);
        grid = new GridHandler(this, inventoryCraft);
        exit = new ExitHandler(this, inventoryResult);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && TTConfig.general.autoCraft) || super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && TTConfig.general.autoCraft) {
            for (EnumFacing f : FACES_GRID) {
                if (facing == f)
                    return (T) grid;
            }
            for (EnumFacing f : FACES_CRYSTALS) {
                if (facing == f)
                    return (T) crystals;
            }
            for (EnumFacing f : FACES_EXIT) {
                if (facing == f) {
                    if (!exit.getStackInSlot(0).isEmpty())
                        return (T) exit;
                    return (T) grid;
                }
            }
        }
        return super.getCapability(capability, facing);
    }

    public void checkCrafting(World world, EntityPlayer player) {
        if (player instanceof FakePlayer)   //our poor dummy needs more brains
            SF.copyKnowledge(this, player);

        boolean powered = world.isBlockPowered(getPos());
        IRecipe recipe = findRecipe(player, true);
        if (recipe instanceof IArcaneRecipe) {
            int vis = (int) (((IArcaneRecipe) recipe).getVis() * (1.0F - CasterManager.getTotalVisDiscount(player)));
            getAura();
            boolean hasVis = getWorld().isRemote ? auraVisClient >= vis : auraVisServer >= vis;
            if (hasVis && hasCrystals(recipe) && !powered && TTConfig.general.autoCraft) {
                autoCraftOneItem((IArcaneRecipe) recipe);
                return;
            }
        }
        recipe = findRecipe(player, false);
        if (recipe != null && (powered || !TTConfig.general.autoCraft)) {
            updateResultSlot(player, recipe, null);
        } else { //no recipe is formed on grid
            if (!inventoryResult.getStackInSlot(0).isEmpty() && preview)
                inventoryResult.setInventorySlotContents(0, ItemStack.EMPTY);
        }
    }


    private void updateResultSlot(EntityPlayer player, IRecipe recipe, Integer windowId) {
        preview = true;
        ItemStack itemstack = recipe.getCraftingResult(inventoryCraft);
        inventoryResult.setInventorySlotContents(0, itemstack);
        if (windowId != null)
            ((EntityPlayerMP)player).connection.sendPacket(new SPacketSetSlot(windowId, 0, itemstack));
    }

    private void autoCraftOneItem(IArcaneRecipe recipe) {
        InventoryCrafting parsedGrid = parseFillers(inventoryCraft);
        ItemStack itemstack = recipe.getCraftingResult(parsedGrid);
        ItemStack current = inventoryResult.getStackInSlot(0);
        int count = current.getCount();
        if (preview) {
            count = itemstack.getCount();
            consumeItems(recipe);
            preview = false;
        } else if (count + itemstack.getCount() <= current.getMaxStackSize()) {
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

    private IRecipe findRecipe(EntityPlayer player, boolean parsed) {
        InventoryCrafting grid;
        if (parsed)
            grid = parseFillers(inventoryCraft);
        else
            grid = inventoryCraft;
        IArcaneRecipe arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(grid, player);
        if (arecipe != null)
            return arecipe;
        InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);
        for (int i = 0; i < 9; ++i) {
            craftInv.setInventorySlotContents(i, grid.getStackInSlot(i));
        }
        return CraftingManager.findMatchingRecipe(craftInv, world);
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
            getAura();
            spendAura(vis);
        }
    }

    InventoryCrafting parseFillers(InventoryArcaneWorkbench inv) {
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
    public void update() {
        if (!world.isRemote && ++counter % 10 == 0){
            checkCrafting(world, SF.getFake((WorldServer)world));
        }
    }



    //interesting part ends here

    public void readFromNBT(NBTTagCompound nbtCompound) {
        super.readFromNBT(nbtCompound);
        NonNullList<ItemStack> stacks = NonNullList.withSize(this.inventoryCraft.getSizeInventory(), ItemStack.EMPTY);
        ItemStackHelper.loadAllItems(nbtCompound, stacks);

        for(int a = 0; a < stacks.size(); ++a) {
            this.inventoryCraft.setInventorySlotContents(a, stacks.get(a));
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

    @Shadow void getAura(){}
    @Shadow int auraVisClient;
    @Shadow int auraVisServer;

    private static Field container;
    static {
        for (int i = 0; i < CrystalHandler.aspectGrid.length; i++)
            aspectSlots.put(CrystalHandler.aspectGrid[i], i);
        try {
            container = InventoryCrafting.class.getDeclaredField((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ?
                    "eventHandler": "field_70465_c");
            container.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
