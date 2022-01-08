package griglog.thaumtweaks.blocks.crafter.helpers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.container.slot.SlotCrystal;

public class LockedSlotCrystal extends SlotCrystal {
    public LockedSlotCrystal(Aspect aspect, IInventory par2IInventory, int par3, int par4, int par5) {
        super(aspect, par2IInventory, par3, par4, par5);
    }
    @Override
    public boolean isItemValid(ItemStack stack) {
        return false;
    }
    @Override
    public boolean canTakeStack(EntityPlayer playerIn) {
        return false;
    }
}
