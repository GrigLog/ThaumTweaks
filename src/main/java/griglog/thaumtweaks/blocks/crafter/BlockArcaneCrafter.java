package griglog.thaumtweaks.blocks.crafter;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BlockArcaneCrafter extends Block implements ITileEntityProvider {
    public BlockArcaneCrafter() {
        super(Material.WOOD);
        this.setUnlocalizedName(ThaumTweaks.MODID + ":crafter");
        this.setRegistryName("crafter");
        this.setCreativeTab(ThaumTweaks.tab);
        this.setResistance(2.0F);
        this.setHardness(1.5F);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        SF.copyKnowledge(placer, worldIn.getTileEntity(pos));
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            //it then calls GuiHandler methods on both client and server. Its the only way to sync our gui (and make it show a cursor)
            player.openGui(ThaumTweaks.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity != null && tileEntity instanceof TileArcaneCrafter) {
            InventoryHelper.dropInventoryItems(world, pos, ((TileArcaneCrafter)tileEntity).inventoryCraft);
        }

        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) { return new TileArcaneCrafter(); }
}
