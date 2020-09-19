package griglog.thaumtweaks.mixins.armor;

import com.google.common.collect.Multimap;
import griglog.thaumtweaks.TTConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.armor.ItemFortressArmor;

import javax.annotation.Nonnull;
import java.util.UUID;

@Mixin(ItemFortressArmor.class)
public abstract class FortressArmorMixin extends ItemArmor implements IRechargable {
    public FortressArmorMixin(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> attrib = super.getAttributeModifiers(slot, stack);
        UUID uuid = new UUID((getUnlocalizedName() + slot.toString()).hashCode(), 0);
        if (slot == armorType) {
            attrib.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
                    new AttributeModifier(uuid, "Fortress armor modifier " + armorType,
                            (slot == EntityEquipmentSlot.CHEST ? 0.4 : 0.3), 0));
        }
        return attrib;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack armor) {
        super.onArmorTick(world, player, armor);
        if (!world.isRemote && player.ticksExisted % 100 == 0  && hasSet(player)) {
            addStrength(player);
        }
    }

    //yes, rewriting this one WAS necessary
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
        ISpecialArmor.ArmorProperties ap = new ISpecialArmor.ArmorProperties(0, 0, armor.getMaxDamage() + 1 - armor.getItemDamage());
        if (player instanceof EntityPlayer) {
            int q = 0;
            for(int a = 1; a < 4; ++a) {
                ItemStack piece = ((EntityPlayer)player).inventory.armorInventory.get(a);
                if (!piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                    if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask")) {
                        ++ap.Armor;
                    }
                    if (++q <= 1) {
                        ++ap.Armor;
                        ++ap.Toughness;
                    }
                }
            }
        }

        int priority = 0;
        double ratio = (this.damageReduceAmount + ap.Armor) * TTConfig.fortArmor.ratio / 19; //70%
        if (source.isMagicDamage()) {
            priority = 1;
            ratio = (this.damageReduceAmount + ap.Armor) * TTConfig.fortArmor.magicRatio / 19; // 50%
        } else if (!source.isFireDamage() && !source.isExplosion()) {
            if (source.isUnblockable()) {
                priority = 0;
                ratio = 0.0D;
            }
        }

        ratio += tryAddRatio(player, armor, damage); //76% / 81% if enough vis
        ap.AbsorbRatio = ratio;
        ap.Priority = priority;
        return ap;
    }

    void addStrength(EntityPlayer player) {
        if (TTConfig.fortArmor.str > 0)
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 100, TTConfig.fortArmor.str - 1));
    }

    public boolean hasSet(EntityPlayer player) {
        for (int i = 1; i < 4; i++) {
            ItemStack slot = player.inventory.armorInventory.get(i);
            if (slot.isEmpty() || !(slot.getItem() instanceof ItemFortressArmor)) {
                return false;
            }
        }
        return true;
    }

    double tryAddRatio(EntityLivingBase entity, ItemStack is, double damage) {
        EntityPlayer player = (EntityPlayer) entity;
        if (player == null)
            return 0;
        if (RechargeHelper.getChargePercentage(is, player) > 0.70 &&
                RechargeHelper.consumeCharge(is, player,
                        Math.round((float)(Math.log(damage) / Math.log(TTConfig.fortArmor.logBase)))))
            return TTConfig.fortArmor.protec;
        return 0;
    }

    public boolean handleUnblockableDamage(EntityLivingBase entity, @Nonnull ItemStack armor, DamageSource source, double damage, int slot) {
        return true;  //have no clue why azanor didnt use it
    }

    public int getMaxCharge(ItemStack var1, EntityLivingBase var2) {
        return TTConfig.fortArmor.vis;
    }

    public IRechargable.EnumChargeDisplay showInHud(ItemStack var1, EntityLivingBase var2) {
        return IRechargable.EnumChargeDisplay.NORMAL;
    }

    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        int q = 0;
        int ar = 0;

        for(int a = 1; a < 4; ++a) {
            ItemStack piece = player.inventory.armorInventory.get(a);
            if (!piece.isEmpty() && piece.getItem() instanceof ItemFortressArmor) {
                if (piece.hasTagCompound() && piece.getTagCompound().hasKey("mask"))
                    ++ar;
                ++q;
                if (q <= 1)
                    ++ar;
            }
        }

        return ar;
    }
}
