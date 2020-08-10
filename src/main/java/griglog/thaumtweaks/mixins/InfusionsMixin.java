package griglog.thaumtweaks.mixins;

import com.google.common.collect.ImmutableSet;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import java.util.ArrayList;
import java.util.Set;

@Mixin(EnumInfusionEnchantment.class)
public class InfusionsMixin {
    private InfusionsMixin(Set<String> toolClasses, int ml, String research) {
        this.toolClasses = toolClasses;
        this.maxLevel = ml + 1;
        this.research = research;
    }

    @Shadow
    public Set<String> toolClasses;
    @Shadow
    public int maxLevel;
    @Shadow
    public String research;

}
