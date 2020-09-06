package griglog.thaumtweaks.mixins.blocks.workbench.itemhandlers;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

public class GridHandler extends ArcaneHandler{
    public GridHandler(TileEntity t, InventoryCrafting inv) {
        super(t, inv);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }


    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return true;
    }
}
