package griglog.thaumtweaks.mixins.entities;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.golems.EntityThaumcraftGolem;

@Mixin(EntityThaumcraftGolem.class)
public abstract class GolemMixin extends EntityOwnedConstruct {
    public GolemMixin(World worldIn) {
        super(worldIn);
    }

    protected void dropFewItems(boolean flag, int looting) {
        if (TTConfig.curiosities.allow)
            entityDropItem(new ItemStack(ItemsTC.curio, world.rand.nextInt(3), 2), 0.5F);
    }
}
