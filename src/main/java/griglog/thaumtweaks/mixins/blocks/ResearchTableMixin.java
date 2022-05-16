package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.crafting.BlockResearchTable;
import thaumcraft.common.tiles.crafting.TileResearchTable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mixin(BlockResearchTable.class)
public class ResearchTableMixin extends BlockTCDevice {
    public ResearchTableMixin(Material mat, Class tc, String name) {
        super(mat, tc, name);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        TileEntity tile = worldIn.getTileEntity(pos);
        if (tile instanceof TileResearchTable){
            try {
                checkInventories.invoke(tile);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static Method checkInventories;
    static {
        try {
            checkInventories = TileResearchTable.class.getDeclaredMethod("checkInventories");
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
