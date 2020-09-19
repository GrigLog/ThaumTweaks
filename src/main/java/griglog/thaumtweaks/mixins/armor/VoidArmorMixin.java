package griglog.thaumtweaks.mixins.armor;

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
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.armor.ItemVoidArmor;

@Mixin(ItemVoidArmor.class)
public abstract class VoidArmorMixin extends ItemArmor {
    public VoidArmorMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && stack.isItemDamaged() && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-3, (EntityLivingBase)entity);
        }
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (!world.isRemote && player.ticksExisted % 100 == 0  && hasSet(player)) {
            addRegen(player);
        }
    }

    void addRegen(EntityPlayer player) {
        player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 100));
    }

    public boolean hasSet(EntityPlayer player) {
        for (int i = 0; i < 4; i++) {
            ItemStack slot = player.inventory.armorInventory.get(i);
            if (slot.isEmpty() || !(slot.getItem() instanceof ItemVoidArmor)) {
                return false;
            }
        }
        return true;
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        int q = 0;
        int ar = 0;

        for(int a = 1; a < 4; ++a) {
            ItemStack piece = player.inventory.armorInventory.get(a);
            if (!piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask")) {
                    ++ar;
                }

                ++q;
                if (q <= 1) {
                    ++ar;
                }
            }
        }

        return ar;
    }
}
