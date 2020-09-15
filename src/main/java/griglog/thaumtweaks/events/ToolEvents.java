package griglog.thaumtweaks.events;

import griglog.thaumtweaks.TTConfig;
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
import thaumcraft.common.lib.utils.BlockUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mod.EventBusSubscriber
public class ToolEvents {
    public static HashMap<Integer, EnumFacing> lastFaceClicked;

    static {
        try {
            Field f = thaumcraft.common.lib.events.ToolEvents.class.getDeclaredField("lastFaceClicked");
            f.setAccessible(true);
            lastFaceClicked = (HashMap<Integer, EnumFacing>) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static boolean blocksBreaking = false;

    @SubscribeEvent()
    public static void onBlockBreak(BlockEvent.HarvestDropsEvent event) {
        EntityPlayer player;
        if ((player = event.getHarvester()) == null)
            return;
        ItemStack heldItem = player.getHeldItem(player.getActiveHand());
        if (heldItem.getUnlocalizedName().equals(ItemsTC.primalCrusher.getUnlocalizedName()) && !blocksBreaking && !player.isSneaking()) {
            blocksBreaking = true;
            EnumFacing facing = lastFaceClicked.get(event.getHarvester().getEntityId());
            if (TTConfig.tools.zone_5)
                mineExtended(facing, event, heldItem);
            else
                mineUsual(facing, event, heldItem);
            blocksBreaking = false;
        }
    }

    private static void mineUsual(EnumFacing facing, BlockEvent.HarvestDropsEvent event, ItemStack heldItem) {
        switch (facing) {
            case UP:
            case DOWN:
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        tryBreakBlock(event, x, 0, z, heldItem);
                    }
                }
                break;

            case SOUTH:
            case NORTH:
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        tryBreakBlock(event, x, y, 0, heldItem);
                    }
                }
                break;

            case EAST:
            case WEST:
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        tryBreakBlock(event, 0, y, z, heldItem);
                    }
                }
                break;
        }
    }


    static void mineExtended(EnumFacing facing, BlockEvent.HarvestDropsEvent event, ItemStack heldItem) {
        switch (facing) {
            case UP:
            case DOWN:
                for (int x = -2; x <= 2; x++) {
                    for (int z = -2; z <= 2; z++) {
                        tryBreakBlock(event, x, 0, z, heldItem);
                    }
                }
                break;

            case SOUTH:
            case NORTH:
                for (int x = -2; x <= 2; x++) {
                    for (int y = -1; y <= 3; y++) {
                        tryBreakBlock(event, x, y, 0, heldItem);
                    }
                }
                break;

            case EAST:
            case WEST:
                for (int z = -2; z <= 2; z++) {
                    for (int y = -1; y <= 3; y++) {
                        tryBreakBlock(event, 0, y, z, heldItem);
                    }
                }
                break;
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
