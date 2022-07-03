package griglog.thaumtweaks.mixins.armor;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.devices.BlockRechargePedestal;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.devices.TileRechargePedestal;

@Mixin(BlockRechargePedestal.class)
public class RechargePedestalMixin extends BlockTCDevice {
    public RechargePedestalMixin(Material mat, Class tc, String name) {
        super(mat, tc, name);
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return true;
        } else {
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null && tile instanceof TileRechargePedestal) {
                TileRechargePedestal ped = (TileRechargePedestal)tile;
                if (ped.getStackInSlot(0).isEmpty() && SF.isRechargeable(player.inventory.getCurrentItem().getItem())) {
                    ItemStack i = player.getHeldItem(hand).copy();
                    i.setCount(1);
                    ped.setInventorySlotContents(0, i);
                    player.getHeldItem(hand).shrink(1);
                    if (player.getHeldItem(hand).getCount() == 0) {
                        player.setHeldItem(hand, ItemStack.EMPTY);
                    }

                    player.inventory.markDirty();
                    world.playSound((EntityPlayer)null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.6F);
                    return true;
                }

                if (!ped.getStackInSlot(0).isEmpty()) {
                    InventoryUtils.dropItemsAtEntity(world, pos, player);
                    world.playSound((EntityPlayer)null, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 0.2F, ((world.rand.nextFloat() - world.rand.nextFloat()) * 0.7F + 1.0F) * 1.5F);
                    return true;
                }
            }

            return super.onBlockActivated(world, pos, state, player, hand, side, hitX, hitY, hitZ);
        }
    }
}
