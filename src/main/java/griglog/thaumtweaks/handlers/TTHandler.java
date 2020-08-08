package griglog.thaumtweaks.handlers;

import griglog.thaumtweaks.SF;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.Entity;
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
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
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
import thaumcraft.common.lib.events.PlayerEvents;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.fx.PacketFXShield;
import thaumcraft.common.lib.utils.EntityUtils;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber(Side.CLIENT)
public class TTHandler {
    static String buffer;

    @SubscribeEvent()
    public static void playerGotHitBeforeArmor(LivingHurtEvent event) {
        Entity target = event.getEntityLiving();
        if (target instanceof EntityPlayer) {
            buffer = "(" + new DecimalFormat("#.###").format(event.getAmount()) + " : ";
        }
    }

    @SubscribeEvent()
    public static void playerGotHitAfterArmor(LivingDamageEvent event) {
        Entity target = event.getEntityLiving();
        if (target instanceof EntityPlayer) {
            buffer += new DecimalFormat("#.###").format(event.getAmount()) + ") ";
            DamageSource ds = event.getSource();
            if (ds.isMagicDamage())
                buffer += "magic ";
            if (ds.isUnblockable())
                buffer += "unbl ";
            if (ds.isDamageAbsolute())
                buffer += "abs ";
            SF.printChat(buffer);
            buffer = "";
        }
    }
}