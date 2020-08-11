package griglog.thaumtweaks.mixins.events;

import baubles.api.BaublesApi;
import griglog.thaumtweaks.SF;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.lib.events.PlayerEvents;

@Mixin(PlayerEvents.class)
public class PlayerEventsMixin {
    private static final double XP_MULT = 2;
    private static final int THEORY_MULT = 4;

    @Inject(method="pickupXP", at=@At("HEAD"), cancellable = true, remap=false)
    private static void pickupXP(PlayerPickupXpEvent event, CallbackInfo ci) {
        if (event.getEntityPlayer() != null && !event.getEntityPlayer().world.isRemote && BaublesApi.isBaubleEquipped(event.getEntityPlayer(), ItemsTC.bandCuriosity) >= 0 && event.getOrb().getXpValue() > 1) {
            int exp = event.getOrb().xpValue / 2;
            EntityXPOrb orb = event.getOrb();
            orb.xpValue -= exp;
            addTheories(event.getEntityPlayer(), exp);
        }
        ci.cancel();
    }

    private static void addTheories(EntityPlayer player, double d) {
        float r = player.getRNG().nextFloat();
        String[] s;
        String cat;
        if ((double)r < 0.05D * (double)d * XP_MULT) {
            s = (String[]) ResearchCategories.researchCategories.keySet().toArray(new String[0]);
            cat = s[player.getRNG().nextInt(s.length)];
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory(cat), THEORY_MULT);
        } else if ((double)r < 0.2D * (double)d * XP_MULT) {
            s = (String[])ResearchCategories.researchCategories.keySet().toArray(new String[0]);
            cat = s[player.getRNG().nextInt(s.length)];
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory(cat), THEORY_MULT);
        }
    }

}
