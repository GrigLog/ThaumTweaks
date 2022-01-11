package griglog.thaumtweaks.blocks.crafter;

import com.google.common.collect.UnmodifiableIterator;
import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
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
import thaumcraft.Thaumcraft;
import thaumcraft.common.blocks.*;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.items.IThaumcraftItems;
import thaumcraft.common.lib.utils.BlockStateUtils;

import javax.annotation.Nullable;

public class BlockArcaneCrafter extends Block implements ITileEntityProvider {
    public static PropertyDirection FACING = PropertyDirection.create("facing");
    public BlockArcaneCrafter() {
        super(Material.WOOD);
        setUnlocalizedName(ThaumTweaks.MODID + ":crafter");
        setRegistryName("crafter");
        setResistance(2.0F);
        setHardness(1.5F);
        setCreativeTab(ConfigItems.TABTC);

    }

    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        IBlockState state = world.getBlockState(pos);
        UnmodifiableIterator var5 = state.getProperties().keySet().iterator();
        IProperty prop;
        do {
            if (!var5.hasNext()) {
                return false;
            }

            prop = (IProperty)var5.next();
        } while(!prop.getName().equals("facing"));
        world.setBlockState(pos, state.cycleProperty(prop));
        return true;
    }

    //TODO: why is it deprecated?...
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING,  EnumFacing.getFront(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex();
    }



    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        IBlockState bs = this.getDefaultState();
        bs = bs.withProperty(FACING, placer.getHorizontalFacing());
        return bs;
    }


    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }


    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        SF.copyKnowledge(placer, worldIn.getTileEntity(pos));
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote && !player.isSneaking()) {
            //it then calls GuiHandler methods on both client and server. Its the only way to sync our gui (and make it show a cursor)
            player.openGui(Thaumcraft.instance, 23, world, pos.getX(), pos.getY(), pos.getZ());
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
