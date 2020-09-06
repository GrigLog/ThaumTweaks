package griglog.thaumtweaks.mixins.blocks.workbench;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.InventoryCraftResult;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import java.lang.reflect.Field;
import java.util.Collections;

public class InventoryArcaneResult extends InventoryCraftResult implements ISidedInventory {
    public final int[] SLOT = new int[] {0};
    TileEntity tile;
    public Container eventHandler;

    public InventoryArcaneResult(TileEntity te, Container c) {
        super();
        eventHandler = c;
        tile = te;
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return SLOT;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return true;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        try {
            return !(boolean) (preview.get(tile));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }


    @Override
    public ItemStack decrStackSize(int index, int count) {
        ItemStack itemstack = ItemStackHelper.getAndSplit(Collections.singletonList(getStackInSlot(0)), 0, count);
        if (!itemstack.isEmpty())
        {
            eventHandler.onCraftMatrixChanged(this);
        }

        return itemstack;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        tile.markDirty();
    }

    public String getName() {
        return "container.arcaneworkbench";
    }

    private static Field preview;
    static {
        try {
            preview = TileArcaneWorkbench.class.getDeclaredField("preview");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
