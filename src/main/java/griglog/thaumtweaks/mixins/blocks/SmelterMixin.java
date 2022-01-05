package griglog.thaumtweaks.mixins.blocks;

import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.essentia.TileSmelter;

@Mixin(TileSmelter.class)
public abstract class SmelterMixin extends TileThaumcraftInventory {
    private static final int[] slotFuel = new int[]{1};
    private static final int[] slotItems = new int[]{0};

    public int[] getSlotsForFace(EnumFacing face) {
        return (face == EnumFacing.DOWN || face == getFacing().getOpposite()) ? slotFuel : slotItems;
    }

    public SmelterMixin(int size) {
        super(size);
    }
}
