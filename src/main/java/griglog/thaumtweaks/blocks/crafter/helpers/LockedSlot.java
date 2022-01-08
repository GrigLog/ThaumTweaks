package griglog.thaumtweaks.blocks.crafter.helpers;

import griglog.thaumtweaks.items.TTItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class LockedSlot extends Slot {
    public LockedSlot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Override
    public boolean isItemValid(ItemStack stack) {
        return stack.getItem() == TTItems.filler;
    }

    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return true;
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return 1;
    }

    public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
        this.onSlotChanged();
        if (stack.getItem() == TTItems.filler)
            return stack;
        return ItemStack.EMPTY;
    }
}
