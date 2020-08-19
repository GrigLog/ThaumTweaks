package griglog.thaumtweaks.events;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.ThaumTweaks;
import griglog.thaumtweaks.crafts.RecipeMergePearls;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.text.DecimalFormat;

@Mod.EventBusSubscriber(Side.CLIENT)
public class TTEvents {
    static String buffer;

    @SubscribeEvent
    public static void registerCrafts(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new RecipeMergePearls());
    }

    @SubscribeEvent()
    public static void playerGotHitBeforeArmor(LivingHurtEvent event) {
        if (!ThaumTweaks.DEBUG)
            return;
        Entity target = event.getEntityLiving();
        if (target instanceof EntityPlayer) {
            buffer = "(" + new DecimalFormat("#.###").format(event.getAmount()) + " : ";
        }
    }

    @SubscribeEvent()
    public static void playerGotHitAfterArmor(LivingDamageEvent event) {
        if (!ThaumTweaks.DEBUG)
            return;
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