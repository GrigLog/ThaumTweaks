package griglog.thaumtweaks.mixins.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.tiles.crafting.TilePedestal;

@Mixin(BlockPedestal.class)
public class PedestalMixin extends BlockTCTile {
    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        if (((TilePedestal)world.getTileEntity(pos)).getStackInSlot(0).isEmpty())
            return 0;
        return 15;
    }

    public PedestalMixin(Material mat, Class tc, String name) {
        super(mat, tc, name);
    }
}
