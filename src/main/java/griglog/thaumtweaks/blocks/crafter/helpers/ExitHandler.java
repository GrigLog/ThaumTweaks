package griglog.thaumtweaks.blocks.crafter.helpers;

import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;

public class ExitHandler extends ArcaneHandler implements INBTSerializable<NBTTagCompound> {
    public ExitHandler(TileEntity t, InventoryCraftResult inv) {
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


    @Override  //TODO: I dont have to (de)serialize anything in handlers, as the tile already saves its contents. Right?
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
