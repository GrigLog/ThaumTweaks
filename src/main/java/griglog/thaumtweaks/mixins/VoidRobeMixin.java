package griglog.thaumtweaks.mixins;

import com.google.common.collect.Multimap;
import griglog.thaumtweaks.SF;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.armor.ItemVoidArmor;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;
import thaumcraft.common.items.baubles.ItemAmuletVis;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Mixin(ItemVoidRobeArmor.class)
public abstract class VoidRobeMixin extends ItemArmor implements IRechargable {
    public VoidRobeMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn); //idk if this constructor is really necessary...
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.onUpdate(stack, world, entity, slot, selected);
        if (!world.isRemote && stack.isItemDamaged() && entity.ticksExisted % 20 == 0 && entity instanceof EntityLivingBase) {
            stack.damageItem(-4, (EntityLivingBase)entity);
        }
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (!world.isRemote && hasSet(player)) {
            if (armorType == EntityEquipmentSlot.HEAD) {
                if (player.ticksExisted % 40 == 0)
                    clearDebuffs(player, armor);
            }
            else if (armorType == EntityEquipmentSlot.CHEST) {
                if (player.ticksExisted % 20 == 0)
                    tryHeal(player, armor);
            }
            else if (armorType == EntityEquipmentSlot.LEGS) {
                if (player.ticksExisted % 60 == 0)
                    tryFeed(player, armor);
            }

        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> attrib = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID((getUnlocalizedName() + slot.toString()).hashCode(), 0);
        if (slot == armorType) {
            attrib.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(uuid, "Void robe modifier " + armorType,
                            0.25, 0));
        }
        return attrib;
    }

    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        int priority = 0;
        double ratio = this.damageReduceAmount * 0.04;  //80%
        if (source.isMagicDamage() || source.isUnblockable()) {
            priority = 1;
            ratio = this.damageReduceAmount * 0.035; //70%
        }
        ratio = (RechargeHelper.consumeCharge(armor, player, Math.round((float)(Math.log(damage) / Math.log(2)))) ?
                ratio + (armorType == EntityEquipmentSlot.HEAD ? 0.01 : 0.02) : ratio);
        //85% / 75% if enough vis
        return new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }

    public boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
        return true;  //have no clue why azanor didnt use it
    }

    public int getMaxCharge(ItemStack var1, EntityLivingBase var2) {
        return 480;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack var1, EntityLivingBase var2) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 0;  //fix display bug
    }

    void clearDebuffs(EntityPlayer player, ItemStack is) {
        List<Potion> badPotions = player.getActivePotionEffects().stream()
                .map(PotionEffect::getPotion)
                .filter(Potion::isBadEffect)
                .collect(Collectors.toList());
        for (Potion p : badPotions) {
            player.removePotionEffect(p);
            RechargeHelper.consumeCharge(is, player, 10);
        }
    }

    void tryHeal(EntityPlayer player, ItemStack is) {
        if (player.getHealth() < player.getMaxHealth() && RechargeHelper.consumeCharge(is, player, 1)) {
            player.heal(1);
        }
    }

    void tryFeed(EntityPlayer player, ItemStack is) {
        if (player.canEat(false) && RechargeHelper.consumeCharge(is, player, 1))
            player.getFoodStats().addStats(1, 0.3F);
    }

    boolean hasSet(EntityPlayer player) {
        for (int i = 1; i < 4; i++) {
            ItemStack slot = player.inventory.armorInventory.get(i);
            if (slot.isEmpty() || !(slot.getItem() instanceof ItemVoidRobeArmor)) {
                return false;
            }
        }
        return true;
    }

}

