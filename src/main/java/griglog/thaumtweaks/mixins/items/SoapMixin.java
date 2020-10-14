package griglog.thaumtweaks.mixins.items;

import griglog.thaumtweaks.SF;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.capabilities.IPlayerWarp;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.client.fx.FXDispatcher;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.items.consumables.ItemSanitySoap;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.potions.PotionWarpWard;

@Mixin(ItemSanitySoap.class)
public abstract class SoapMixin extends ItemTCBase {
    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityLivingBase player, int timeLeft) {
        int qq = this.getMaxItemUseDuration(stack) - timeLeft;
        if (qq > 95 && player instanceof EntityPlayer) {
            stack.shrink(1);
            if (!world.isRemote) {
                IPlayerWarp warp = ThaumcraftCapabilities.getWarp((EntityPlayer)player);
                int effect = 1;
                if (player.isPotionActive(PotionWarpWard.instance)) {
                    ++effect;
                }

                int i = MathHelper.floor(player.posX);
                int j = MathHelper.floor(player.posY);
                int k = MathHelper.floor(player.posZ);
                if (world.getBlockState(new BlockPos(i, j, k)).getBlock() == BlocksTC.purifyingFluid) {
                    ++effect;
                }

                if (warp.get(IPlayerWarp.EnumWarpType.NORMAL) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)player, -effect, IPlayerWarp.EnumWarpType.NORMAL);
                }

                if (warp.get(IPlayerWarp.EnumWarpType.TEMPORARY) > 0) {
                    ThaumcraftApi.internalMethods.addWarpToPlayer((EntityPlayer)player, -warp.get(IPlayerWarp.EnumWarpType.TEMPORARY), IPlayerWarp.EnumWarpType.TEMPORARY);
                }

                SF.spawnItem(world,
                        new ItemStack(ItemsTC.curio,
                                world.rand.nextInt((int)
                                        (effect*Math.pow(ThaumcraftApi.internalMethods.getActualWarp((EntityPlayer)player), 0.4))),
                                0),
                        player.getPosition());
            } else {
                player.world.playSound(player.posX, player.posY, player.posZ, SoundsTC.craftstart, SoundCategory.PLAYERS, 0.25F, 1.0F, false);

                for(int a = 0; a < 40; ++a) {
                    FXDispatcher.INSTANCE.crucibleBubble((float)player.posX - 0.5F + player.world.rand.nextFloat() * 1.5F, (float)player.getEntityBoundingBox().minY + player.world.rand.nextFloat() * player.height, (float)player.posZ - 0.5F + player.world.rand.nextFloat() * 1.5F, 1.0F, 0.7F, 0.9F);
                }
            }
        }

    }


    public SoapMixin(String name, String... variants) {
        super(name, variants);
    }
}
