package griglog.thaumtweaks.mixins.armor;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.items.armor.ItemVoidArmor;

@Mixin(ItemVoidArmor.class)
public abstract class VoidArmorMixin extends ItemArmor {
    public VoidArmorMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && stack.isItemDamaged() && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase)
            stack.damageItem(TTConfig.general.armor ? -3 : -1, (EntityLivingBase)entity);

    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (TTConfig.general.armor && !world.isRemote && player.ticksExisted % 100 == 0  && hasSet(player)) {
            addRegen(player);
        }
    }

    void addRegen(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100));
    }

    boolean hasSet(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack slot = player.inventory.armorInventory.get(i);
            if (slot.isEmpty() || !(slot.getItem() instanceof ItemVoidArmor)) {
                return false;
            }
        }
        return true;
    }
}
