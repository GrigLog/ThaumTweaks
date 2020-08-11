package griglog.thaumtweaks.mixins.events;

import griglog.thaumtweaks.SF;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityLlamaSpit;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.potions.PotionFluxTaint;
import thaumcraft.common.entities.construct.EntityOwnedConstruct;
import thaumcraft.common.entities.monster.mods.ChampionModifier;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.lib.SoundsTC;
import thaumcraft.common.lib.events.EntityEvents;
import thaumcraft.common.lib.events.PlayerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.utils.EntityUtils;

import java.lang.reflect.Field;
import java.util.HashMap;

@Mixin(EntityEvents.class)
public class EntityEventsMixin {
    //its easier to copy everything instead of finding a place to inject. Not the neatest method in original code, yes...
    @Inject(method="entityHurt", at=@At("HEAD"), cancellable = true, remap = false)
    private static void entityHurt(LivingHurtEvent event, CallbackInfo ci) {
        knowledgeStuff(event);

        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityPlayer)
            tryHealingMask(event);

        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntity();
            tryWitherMask(event, player);
            runicShielding(event, player);

        } else {
            if (tryTaint(event))
                return;
            if (event.getEntity() instanceof EntityMob) {
                tryChampionStuff(event);
            }
        }

    }

    private static void knowledgeStuff(LivingHurtEvent event) {
        IPlayerKnowledge knowledge;
        if (event.getSource().isFireDamage() && event.getEntity() instanceof EntityPlayer && ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer) event.getEntity(), new String[]{"BASEAUROMANCY@2"}) && !ThaumcraftCapabilities.knowsResearch((EntityPlayer) event.getEntity(), new String[]{"f_onfire"})) {
            knowledge = ThaumcraftCapabilities.getKnowledge((EntityPlayer) event.getEntity());
            knowledge.addResearch("f_onfire");
            knowledge.sync((EntityPlayerMP) event.getEntity());
            ((EntityPlayer) event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.onfire")), true);
        }

        if (event.getSource().getImmediateSource() != null && event.getEntity() instanceof EntityPlayer && ThaumcraftCapabilities.knowsResearchStrict((EntityPlayer) event.getEntity(), new String[]{"FOCUSPROJECTILE@2"})) {
            knowledge = ThaumcraftCapabilities.getKnowledge((EntityPlayer) event.getEntity());
            if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer) event.getEntity(), new String[]{"f_arrow"}) && event.getSource().getImmediateSource() instanceof EntityArrow) {
                knowledge.addResearch("f_arrow");
                knowledge.sync((EntityPlayerMP) event.getEntity());
                ((EntityPlayer) event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.projectile")), true);
            }

            if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer) event.getEntity(), new String[]{"f_fireball"}) && event.getSource().getImmediateSource() instanceof EntityFireball) {
                knowledge.addResearch("f_fireball");
                knowledge.sync((EntityPlayerMP) event.getEntity());
                ((EntityPlayer) event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.projectile")), true);
            }

            if (!ThaumcraftCapabilities.knowsResearch((EntityPlayer) event.getEntity(), new String[]{"f_spit"}) && event.getSource().getImmediateSource() instanceof EntityLlamaSpit) {
                knowledge.addResearch("f_spit");
                knowledge.sync((EntityPlayerMP) event.getEntity());
                ((EntityPlayer) event.getEntity()).sendStatusMessage(new TextComponentString(TextFormatting.DARK_PURPLE + I18n.translateToLocal("got.projectile")), true);
            }
        }
    }

    private static void runicShielding(LivingHurtEvent event, EntityPlayer player) {
        int charge = (int)player.getAbsorptionAmount();
        if (charge > 0 && runicInfo.containsKey(player.getEntityId()) && lastMaxCharge.containsKey(player.getEntityId())) {
            long time = System.currentTimeMillis();
            int target = -1;
            if (event.getSource().getTrueSource() != null) {
                target = event.getSource().getTrueSource().getEntityId();
            }

            if (event.getSource() == DamageSource.FALL) {
                target = -2;
            }

            if (event.getSource() == DamageSource.FALLING_BLOCK) {
                target = -3;
            }

            PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(event.getEntity().getEntityId(), target), new NetworkRegistry.TargetPoint(event.getEntity().world.provider.getDimension(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 32.0D));
        }
    }

    private static void tryWitherMask(LivingHurtEvent event, EntityPlayer player) {
        if (event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
            EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
            ItemStack helm = (ItemStack)player.inventory.armorInventory.get(3);
            if (helm != null && !helm.isEmpty() && helm.getItem() instanceof ItemFortressArmor && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 1) {
                try {
                    attacker.addPotionEffect(new PotionEffect(MobEffects.WITHER, 20*30, 1));
                } catch (Exception var6) {
                }
            }
        }
    }

    private static void tryHealingMask(LivingHurtEvent event) {
        EntityPlayer player = (EntityPlayer)event.getSource().getTrueSource();
        ItemStack helm = (ItemStack)player.inventory.armorInventory.get(3);
        if (helm != null && !helm.isEmpty() && helm.getItem() instanceof ItemFortressArmor && helm.hasTagCompound() && helm.getTagCompound().hasKey("mask") && helm.getTagCompound().getInteger("mask") == 2) {
            player.heal(event.getAmount() / 10);
        }
    }

    private static void tryChampionStuff(LivingHurtEvent event) {
        IAttributeInstance cai = ((EntityMob)event.getEntity()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD);
        EntityMob mob;
        int t;
        if (cai != null && cai.getAttributeValue() >= 0.0D || event.getEntity() instanceof IEldritchMob) {
            mob = (EntityMob)event.getEntity();
            t = (int)cai.getAttributeValue();
            if ((t == 5 || event.getEntity() instanceof IEldritchMob) && mob.getAbsorptionAmount() > 0.0F) {
                int target = -1;
                if (event.getSource().getTrueSource() != null) {
                    target = event.getSource().getTrueSource().getEntityId();
                }

                if (event.getSource() == DamageSource.FALL) {
                    target = -2;
                }

                if (event.getSource() == DamageSource.FALLING_BLOCK) {
                    target = -3;
                }

                PacketHandler.INSTANCE.sendToAllAround(new PacketFXShield(mob.getEntityId(), target), new NetworkRegistry.TargetPoint(event.getEntity().world.provider.getDimension(), event.getEntity().posX, event.getEntity().posY, event.getEntity().posZ, 32.0D));
                event.getEntity().playSound(SoundsTC.runicShieldCharge, 0.66F, 1.1F + event.getEntity().world.rand.nextFloat() * 0.1F);
            } else if (t >= 0 && ChampionModifier.mods[t].type == 2 && event.getSource().getTrueSource() != null && event.getSource().getTrueSource() instanceof EntityLivingBase) {
                EntityLivingBase attacker = (EntityLivingBase)event.getSource().getTrueSource();
                event.setAmount(ChampionModifier.mods[t].effect.performEffect(mob, attacker, event.getSource(), event.getAmount()));
            }
        }

        if (event.getAmount() > 0.0F && event.getSource().getTrueSource() != null && event.getEntity() instanceof EntityLivingBase && event.getSource().getTrueSource() instanceof EntityMob && ((EntityMob)event.getSource().getTrueSource()).getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue() >= 0.0D) {
            mob = (EntityMob)event.getSource().getTrueSource();
            t = (int)mob.getEntityAttribute(ThaumcraftApiHelper.CHAMPION_MOD).getAttributeValue();
            if (ChampionModifier.mods[t].type == 1) {
                event.setAmount(ChampionModifier.mods[t].effect.performEffect(mob, (EntityLivingBase)event.getEntity(), event.getSource(), event.getAmount()));
            }
        }
    }

    private static boolean tryTaint(LivingHurtEvent event) {
        if (!event.getEntityLiving().world.isRemote && event.getEntityLiving().getHealth() < 2.0F && !event.getEntityLiving().isEntityUndead() && !event.getEntityLiving().isDead && !(event.getEntityLiving() instanceof EntityOwnedConstruct) && !(event.getEntityLiving() instanceof ITaintedMob) && event.getEntityLiving().isPotionActive(PotionFluxTaint.instance) && event.getEntityLiving().getRNG().nextBoolean()) {
            EntityUtils.makeTainted(event.getEntityLiving());
            return true;
        }
        return false;
    }

    //obtain protected values
    private static HashMap<Integer, Integer> lastMaxCharge;
    private static HashMap<Integer, Integer> runicInfo;
    static {
        try {
            Field f = PlayerEvents.class.getField("lastMaxCharge");
            f.setAccessible(true);
            lastMaxCharge = (HashMap<Integer, Integer>) f.get(null);
            f = PlayerEvents.class.getField("runicInfo");
            f.setAccessible(true);
            runicInfo = (HashMap<Integer, Integer>) f.get(null);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
