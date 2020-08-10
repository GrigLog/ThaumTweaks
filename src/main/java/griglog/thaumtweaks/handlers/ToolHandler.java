package griglog.thaumtweaks.handlers;

import griglog.thaumtweaks.SF;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.tools.ItemPrimalCrusher;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;
import thaumcraft.common.lib.utils.BlockUtils;

@Mod.EventBusSubscriber
public class ToolHandler {
    static boolean blocksBreaking = false;

    @SubscribeEvent()
    public static void onBlockBreak(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player;
        if ((player = event.getHarvester()) == null) {
            return; }
        ItemStack heldItem = player.getHeldItem(player.getActiveHand());
        if (heldItem.getUnlocalizedName().equals(ItemsTC.primalCrusher.getUnlocalizedName()) && !blocksBreaking && !player.isSneaking()) {
            blocksBreaking = true;
            EnumFacing facing = EnumFacing.getDirectionFromEntityLiving(event.getPos(), player);

            if (facing == EnumFacing.DOWN || facing == EnumFacing.UP) {
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        tryBreakBlock(event, x, 0, z, heldItem);
                    }
                }
            }
            else if (facing == EnumFacing.SOUTH || facing == EnumFacing.NORTH){
                for (int x = -2; x <= 2; x++) {
                    for (int y = -1; y <= 3; y++) {
                        tryBreakBlock(event, x, y, 0, heldItem);
                    }
                }
            }
            else {
                for (int z = -2; z <= 2; z++) {
                    for (int y = -1; y <= 3; y++) {
                        tryBreakBlock(event, 0, y, z, heldItem);
                    }
                }
            }
            blocksBreaking = false;
        }
    }
    static void tryBreakBlock(BlockEvent.HarvestDropsEvent event, int xx, int yy, int zz, ItemStack heldItem) {
        IBlockState bl = event.getWorld().getBlockState(event.getPos().add(xx, yy, zz));
        if (bl.getBlockHardness(event.getWorld(), event.getPos().add(xx, yy, zz)) >= 0.0F && (ForgeHooks.isToolEffective(event.getWorld(), event.getPos().add(xx, yy, zz), heldItem) || heldItem.getItem() instanceof ItemTool && ((ItemTool) heldItem.getItem()).getDestroySpeed(heldItem, bl) > 1.0F)) {
            heldItem.damageItem(1, event.getHarvester());
            BlockUtils.harvestBlock(event.getWorld(), event.getHarvester(), event.getPos().add(xx, yy, zz));
        }
    }
}
