package griglog.thaumtweaks.mixins.entities;

import griglog.thaumtweaks.SF;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.entities.monster.tainted.EntityTaintSeed;


@Mixin(EntityTaintSeed.class)
public abstract class TaintSeedMixin extends EntityMob {
    protected void dropFewItems(boolean flag, int looting) {
        entityDropItem(ConfigItems.FLUX_CRYSTAL.copy(), height / 2.0F);
        entityDropItem(new ItemStack(ItemsTC.curio, 1 + this.world.rand.nextInt(1 + looting), 5), height / 2);
    }

    public TaintSeedMixin(World worldIn) {
        super(worldIn);
    }
}
