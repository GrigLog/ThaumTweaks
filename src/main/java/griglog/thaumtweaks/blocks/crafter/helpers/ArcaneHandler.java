package griglog.thaumtweaks.blocks.crafter.helpers;

import griglog.thaumtweaks.ThaumTweaks;
import griglog.thaumtweaks.blocks.crafter.TileArcaneCrafter;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;

public abstract class ArcaneHandler implements IItemHandler, IItemHandlerModifiable {
    TileArcaneCrafter tile;
    IInventory inv;
    public ArcaneHandler(TileEntity t, IInventory inv) {
        tile = (TileArcaneCrafter)t;
        this.inv = inv;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!isItemValid(slot, stack)) {
            if (slot < getSlots() - 1)
                return insertItem(slot + 1, stack, simulate);
            return stack;  //nothing to insert
        }
        return baseInsert(slot, stack, simulate);

    }

    public ItemStack baseInsert(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;
        validateSlotIndex(slot);
        ItemStack existing = inv.getStackInSlot(slot);
        int limit = getSlotLimit(slot);
        if (!existing.isEmpty()) {
            if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                return stack;
            limit -= existing.getCount();
        }
        if (limit <= 0)
            return stack;
        boolean reachedLimit = stack.getCount() > limit;
        if (!simulate) {
            if (existing.isEmpty())
                inv.setInventorySlotContents(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
            else
                existing.grow(reachedLimit ? limit : stack.getCount());
        }
        onContentsChanged(slot);
        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @Nonnull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;
        validateSlotIndex(slot);
        ItemStack existing = inv.getStackInSlot(slot);
        if (existing.isEmpty())
            return ItemStack.EMPTY;
        int toExtract = Math.min(amount, existing.getMaxStackSize());
        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                inv.setInventorySlotContents(slot, ItemStack.EMPTY);
                onContentsChanged(slot);
            }
            return existing;
        } else {
            if (!simulate) {
                inv.setInventorySlotContents(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }
            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= inv.getSizeInventory())
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + inv.getSizeInventory() + ")");
    }

    void onContentsChanged(int slot) {
        tile.onInventoryUpdate();
    }
}
