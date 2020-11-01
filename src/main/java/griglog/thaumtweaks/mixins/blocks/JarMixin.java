package griglog.thaumtweaks.mixins.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTCTile;
import thaumcraft.common.blocks.essentia.BlockJar;
import thaumcraft.common.blocks.essentia.BlockJarItem;
import thaumcraft.common.tiles.essentia.TileJarFillable;

import java.lang.reflect.Field;

@Mixin(BlockJar.class)
public abstract class JarMixin extends BlockTCTile {

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase ent, ItemStack stack) {
        int l = MathHelper.floor((double)(ent.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileJarFillable) {
            if (l == 0)
                ((TileJarFillable)tile).facing = 2;
            if (l == 1)
                ((TileJarFillable)tile).facing = 5;
            if (l == 2)
                ((TileJarFillable)tile).facing = 3;
            if (l == 3)
                ((TileJarFillable)tile).facing = 4;

            try {
                if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("display"))
                    customName.set(tile, stack.getDisplayName());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void spawnFilledJar(World world, BlockPos pos, IBlockState state, TileJarFillable te) {
        ItemStack drop = new ItemStack(this, 1, this.getMetaFromState(state));
        if (te.amount > 0)
            ((BlockJarItem) drop.getItem()).setAspects(drop, (new AspectList()).add(te.aspect, te.amount));
        if (te.aspectFilter != null) {
            if (!drop.hasTagCompound()) {
                drop.setTagCompound(new NBTTagCompound());
            }

            drop.getTagCompound().setString("AspectFilter", te.aspectFilter.getTag());
        }
        if (te.blocked)
            spawnAsEntity(world, pos, new ItemStack(ItemsTC.jarBrace));

        try {
            String name = (String) customName.get(te);
            if (name != null) {
                drop.setRepairCost(0);
                drop.setStackDisplayName(name);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        spawnAsEntity(world, pos, drop);
    }



    private static Field customName;
    static {
        try {
            customName = TileJarFillable.class.getField("customName");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public JarMixin(Material mat, Class tc, String name) {
        super(mat, tc, name);
    }
}
