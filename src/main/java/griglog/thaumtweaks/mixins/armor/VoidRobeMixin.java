package griglog.thaumtweaks.mixins.armor;

import com.google.common.collect.Multimap;
import griglog.thaumtweaks.TTConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;

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
                if (player.ticksExisted % 60 == 0)
                    tryFeed(player, armor);
            }
            else if (armorType == EntityEquipmentSlot.CHEST) {
                if (player.ticksExisted % 20 == 0)
                    tryHeal(player, armor);
            }
            else if (armorType == EntityEquipmentSlot.LEGS) {
                if (player.ticksExisted % 40 == 0)
                    clearDebuffs(player, armor);
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
        double ratio = this.damageReduceAmount * TTConfig.voidRobe.ratio / 20;  //80%
        if (source.isMagicDamage() || source.isUnblockable()) {
            priority = 1;
            ratio = this.damageReduceAmount * TTConfig.voidRobe.magicRatio / 20; //65%
        }

        ratio += tryAddRatio(player, armor, damage);  //90% / 75% if enough vis
        return new ISpecialArmor.ArmorProperties(priority, ratio, armor.getMaxDamage() + 1 - armor.getItemDamage());
    }

    public boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
        return true;  //have no clue why azanor didnt use it
    }

    public int getVisDiscount(ItemStack stack, EntityPlayer player) {
        return (this.armorType == EntityEquipmentSlot.HEAD ? 8 : 7);
    }

    public int getMaxCharge(ItemStack var1, EntityLivingBase var2) {
        return TTConfig.voidRobe.vis;
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
            if (TTConfig.voidRobe.canClear)
            player.removePotionEffect(p);
            RechargeHelper.consumeCharge(is, player, TTConfig.voidRobe.clearVis);
        }
    }

    void tryHeal(EntityPlayer player, ItemStack is) {
        if (player.getHealth() < player.getMaxHealth()
                && RechargeHelper.consumeCharge(is, player, (int) (TTConfig.voidRobe.heal * TTConfig.voidRobe.healVis))) {
            player.heal((float) TTConfig.voidRobe.heal);
        }
    }

    void tryFeed(EntityPlayer player, ItemStack is) {
        if (TTConfig.voidRobe.canFeed && player.canEat(false)
                && RechargeHelper.consumeCharge(is, player, TTConfig.voidRobe.feedVis))
            player.getFoodStats().addStats(1, 0.3F);
    }

    double tryAddRatio(EntityLivingBase entity, ItemStack is, double damage) {
        EntityPlayer player = (EntityPlayer) entity;
        if (player == null)
            return 0;
        if (RechargeHelper.getChargePercentage(is, player) > 0.75 &&
                RechargeHelper.consumeCharge(is, player,
                        Math.round((float)(Math.log(damage) / Math.log(TTConfig.voidRobe.logBase)))))
            return TTConfig.voidRobe.protec;
        return 0;
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

