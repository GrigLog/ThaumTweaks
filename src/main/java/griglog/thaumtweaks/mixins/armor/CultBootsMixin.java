package griglog.thaumtweaks.mixins.armor;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.items.armor.ItemCultistBoots;

@Mixin(ItemCultistBoots.class)
public class CultBootsMixin {
    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        if (TTConfig.general.armor)
            return 3;
        return 1;
    }
}
