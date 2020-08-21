package griglog.thaumtweaks.mixins.golems;

import griglog.thaumtweaks.SF;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.golems.GolemProperties;
import thaumcraft.common.golems.ItemGolemPlacer;
import thaumcraft.common.items.ItemTCBase;

import java.lang.reflect.Field;

@Mixin(ItemGolemPlacer.class)
public class ItemPlacerMixin extends ItemTCBase {
    public ItemPlacerMixin(String name, String... variants) {
        super(name, variants);
    }

    private static Field f;
    static {
        try {
            f = EntityThaumcraftGolem.class.getDeclaredField("rankXp");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        f.setAccessible(true);
    }

    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        IBlockState bs = world.getBlockState(pos);
        if (!bs.getMaterial().isSolid()) {
            return EnumActionResult.FAIL;
        } else if (world.isRemote) {
            return EnumActionResult.PASS;
        } else {
            pos = pos.offset(side);
            world.getBlockState(pos);
            if (!player.canPlayerEdit(pos, side, player.getHeldItem(hand))) {
                return EnumActionResult.FAIL;
            } else {
                EntityThaumcraftGolem golem = new EntityThaumcraftGolem(world);
                golem.setPositionAndRotation((double)pos.getX() + 0.5D, (double)pos.getY(), (double)pos.getZ() + 0.5D, 0.0F, 0.0F);
                if (golem != null && world.spawnEntity(golem)) {
                    golem.setOwned(true);
                    golem.setValidSpawn();
                    golem.setOwnerId(player.getUniqueID());

                    IPlayerKnowledge golemBrains = golem.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
                    IPlayerKnowledge ownerBrains = player.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
                    if (golemBrains != null && ownerBrains != null){
                        for (String k : ownerBrains.getResearchList()) {
                            golemBrains.addResearch(k);
                        }
                    }

                    if (player.getHeldItem(hand).hasTagCompound() && player.getHeldItem(hand).getTagCompound().hasKey("props")) {
                        golem.setProperties(GolemProperties.fromLong(player.getHeldItem(hand).getTagCompound().getLong("props")));
                    }

                    if (player.getHeldItem(hand).hasTagCompound() && player.getHeldItem(hand).getTagCompound().hasKey("xp")) {
                        try {
                            f.set(golem, player.getHeldItem(hand).getTagCompound().getInteger("xp"));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }

                    golem.onInitialSpawn(world.getDifficultyForLocation(pos), (IEntityLivingData)null);
                    if (!player.capabilities.isCreativeMode) {
                        player.getHeldItem(hand).shrink(1);
                    }
                }

                return EnumActionResult.SUCCESS;
            }
        }
    }
}
