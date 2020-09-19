package griglog.thaumtweaks.mixins.blocks.workbench.itemhandlers;

import griglog.thaumtweaks.ThaumTweaks;
import griglog.thaumtweaks.mixins.blocks.workbench.InventoryArcaneResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class ExitHandler extends ArcaneHandler implements INBTSerializable<NBTTagCompound> {
    public ExitHandler(TileEntity t, InventoryArcaneResult inv) {
        super(t, inv);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public int getSlots() {
        return 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return inv.getStackInSlot(slot);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return false;
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!((InventoryArcaneResult)inv).canExtractItem(slot, null, null))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound item = new NBTTagCompound();
        getStackInSlot(0).writeToNBT(item);
        nbt.setTag("ExitHandlers item", item);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        NBTTagCompound item = (NBTTagCompound) nbt.getTag("ExitHandlers item");
        if (item != null) {
            setStackInSlot(0, new ItemStack(item));
        }
    }
}
