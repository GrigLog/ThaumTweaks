package griglog.thaumtweaks.mixins.golems;

import com.mojang.authlib.GameProfile;
import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.golems.IGolemAPI;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.GolemInteractionHelper;
import thaumcraft.common.lib.network.FakeNetHandlerPlayServer;

@Mixin(GolemInteractionHelper.class)
public class GolemHelperMixin {
    @Inject(method = "golemClick", at=@At("HEAD"), cancellable = true, remap=false)
    private static void golemClick(World world, IGolemAPI golem, BlockPos pos, EnumFacing face, ItemStack clickStack, boolean sneaking, boolean rightClick, CallbackInfo ci) {
        FakePlayer fp = FakePlayerFactory.get((WorldServer)world, new GameProfile(null, "FakeThaumcraftGolem"));
        fp.connection = new FakeNetHandlerPlayServer(fp.mcServer, new NetworkManager(EnumPacketDirection.CLIENTBOUND), fp);
        fp.setPositionAndRotation(golem.getGolemEntity().posX, golem.getGolemEntity().posY, golem.getGolemEntity().posZ, golem.getGolemEntity().rotationYaw, golem.getGolemEntity().rotationPitch);
        world.getBlockState(pos);
        fp.setHeldItem(EnumHand.MAIN_HAND, clickStack);
        fp.setSneaking(sneaking);
        fp.setWorld(world);

        if (golem instanceof EntityThaumcraftGolem && TTConfig.general.autoInfusion) {
            SF.copyKnowledge(golem, fp);
        }

        if (!rightClick) {
            try {
                fp.interactionManager.onBlockClicked(pos, face);
            } catch (Exception var11) {
            }
        } else {
            if (fp.getHeldItemMainhand().getItem() instanceof ItemBlock && !mayPlace(world, ((ItemBlock)fp.getHeldItemMainhand().getItem()).getBlock(), pos, face)) {
                golem.getGolemEntity().setPosition(golem.getGolemEntity().posX + (double)face.getFrontOffsetX(), golem.getGolemEntity().posY + (double)face.getFrontOffsetY(), golem.getGolemEntity().posZ + (double)face.getFrontOffsetZ());
            }

            try {
                fp.interactionManager.processRightClickBlock(fp, world, fp.getHeldItemMainhand(), EnumHand.MAIN_HAND, pos, face, 0.5F, 0.5F, 0.5F);
            } catch (Exception var10) {
            }
        }

        golem.addRankXp(1);
        if (!fp.getHeldItemMainhand().isEmpty() && fp.getHeldItemMainhand().getCount() <= 0) {
            fp.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
        }

        dropSomeItems(fp, golem);
        golem.swingArm();

        ci.cancel();
    }

    private static EntityPlayer getPlayerFromUsername(String username)
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            return null;
        return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerByUsername(username);
    }

    @Shadow
    private static boolean mayPlace(World world, Block blockIn, BlockPos pos, EnumFacing side) { return false;};
    @Shadow
    private static void dropSomeItems(FakePlayer fp2, IGolemAPI golem) {}
}
