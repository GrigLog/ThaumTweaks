package griglog.thaumtweaks.blocks.crafter.helpers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.items.resources.ItemCrystalEssence;

import javax.annotation.Nonnull;

public class CrystalHandler extends ArcaneHandler{
    public static final Aspect[] aspectGrid = new Aspect[] {Aspect.AIR, Aspect.FIRE, Aspect.WATER, Aspect.EARTH, Aspect.ORDER, Aspect.ENTROPY};

    public CrystalHandler(TileEntity t, InventoryCrafting inv) {
        super(t, inv);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inv.setInventorySlotContents(slot + 9, stack);
    }

    @Override
    public int getSlots() {
        return 6;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot + 9);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return canInsertCrystal(slot, stack) && (getStackInSlot(slot).getCount() < getSlotLimit(slot));
    }

    @Override
    public ItemStack baseInsert(int slot, ItemStack stack, boolean simulate) {
        return super.baseInsert(slot + 9, stack, simulate);
    }

    private boolean canInsertCrystal(int index, ItemStack stack) {

        Item item = stack.getItem();
        return (item instanceof ItemCrystalEssence &&
                ((ItemCrystalEssence) item).getAspects(stack).getAspects()[0].getName().equals(aspectGrid[index].getName()));
    }
}
