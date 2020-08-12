package griglog.thaumtweaks.mixins.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.blocks.BlockTC;
import thaumcraft.common.blocks.world.ore.BlockOreTC;

import java.util.List;
import java.util.Random;

@Mixin(BlockOreTC.class)
public abstract class OreMixin extends BlockTC {
    public OreMixin(Material material, String name) {
        super(material, name);
    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> drops = super.getDrops(world, pos, state, fortune);
        if (this == BlocksTC.oreAmber) {
            Random rand = world instanceof World ? ((World)world).rand : RANDOM;

            for(int a = 0; a < drops.size(); ++a) {
                ItemStack is = (ItemStack)drops.get(a);
                //yes, not fair.
                double mult = Math.sqrt(1D / (fortune + 2) + (fortune + 1) / 2D);
                if (is != null && !is.isEmpty() && is.getItem() == ItemsTC.amber && (double)rand.nextFloat() < 0.066D * mult) {
                    drops.set(a, new ItemStack(ItemsTC.curio, 1, 1));
                }
            }
        }

        return drops;
    }
}
