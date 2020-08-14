package griglog.thaumtweaks.mixins.armor;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.items.armor.ItemCultistRobeArmor;

@Mixin(ItemCultistRobeArmor.class)
public abstract class CultRobeMixin extends ItemArmor {
    public CultRobeMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return (this.armorType == EntityEquipmentSlot.HEAD ? 6 : 4);
    }
}
