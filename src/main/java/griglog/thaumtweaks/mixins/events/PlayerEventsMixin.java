package griglog.thaumtweaks.mixins.events;

import baubles.api.BaublesApi;
import griglog.thaumtweaks.TTConfig;
import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.lib.events.PlayerEvents;

import java.util.ArrayList;
import java.util.HashMap;

@Mixin(PlayerEvents.class)
public class PlayerEventsMixin {
    private static final double XP_MULT = TTConfig.curBand.xpMult;
    private static final int THEORY_MULT = TTConfig.curBand.theorMult;

    @Inject(method="pickupXP", at=@At("HEAD"), cancellable = true, remap=false)
    private static void pickupXP(PlayerPickupXpEvent event, CallbackInfo ci) {
        if (TTConfig.curBand.allow && event.getEntityPlayer() != null && !event.getEntityPlayer().world.isRemote && BaublesApi.isBaubleEquipped(event.getEntityPlayer(), ItemsTC.bandCuriosity) >= 0 && event.getOrb().getXpValue() > 1) {
            int exp = event.getOrb().xpValue / 2;
            EntityXPOrb orb = event.getOrb();
            orb.xpValue -= exp;
            addTheories(event.getEntityPlayer(), exp);
        }
        ci.cancel();
    }

    @Inject(method="handleRunicArmor", at=@At(value="FIELD", target="Lthaumcraft/common/lib/events/PlayerEvents;runicInfo:Ljava/util/HashMap;", ordinal=3), cancellable = true, remap=false)
    private static void tryRechargeFromInventory(EntityPlayer player, CallbackInfo ci) {
        long time = System.currentTimeMillis();
        int charge = (int) player.getAbsorptionAmount();
        if (charge < runicInfo.get(player.getEntityId()) && nextCycle.get(player.getEntityId()) < time) {
            ArrayList<ItemStack> equip = getRechargables(player);
            if (equip.size() > 0) {
                ItemStack chosen = equip.get(player.world.rand.nextInt(equip.size()));
                if (RechargeHelper.consumeCharge(chosen, player, 5)) {
                    double boost = TTConfig.runShield.allow ? TTConfig.runShield.invBoost : 1;
                    recoverShield(player, charge, time, (int) (ModConfig.CONFIG_MISC.shieldRecharge / boost));
                    ci.cancel();
                }
            }
        }
        //then it will try to subtract from aura
    }

    private static void recoverShield(EntityPlayer player, int charge, long time, int cd) {
        nextCycle.put(player.getEntityId(), time + (long)cd);
        player.setAbsorptionAmount((float) (charge + 1));
        lastCharge.put(player.getEntityId(), charge + 1);
    }

    private static ArrayList<ItemStack> getRechargables(EntityPlayer player) {
        ArrayList<ItemStack> equip = new ArrayList();
        for (ItemStack is : player.getArmorInventoryList()) {
            if (is.getItem() instanceof IRechargable)
                equip.add(is);
        }
        IInventory baubles = BaublesApi.getBaubles(player);
        for(int i = 0; i < baubles.getSizeInventory(); i++) {
            ItemStack is = baubles.getStackInSlot(i);
            if (is.getItem() instanceof IRechargable)
                equip.add(is);
        }
        ItemStack held = player.getHeldItemMainhand();
        if (held.getItem() instanceof IRechargable)
            equip.add(held);
        held = player.getHeldItemOffhand();
        if (held.getItem() instanceof IRechargable)
            equip.add(held);
        return equip;
    }

    private static void addTheories(EntityPlayer player, double d) {
        double r = player.getRNG().nextFloat();
        String[] s;
        String cat;
        if (r < 0.05D * d * XP_MULT) {
            s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
            cat = s[player.getRNG().nextInt(s.length)];
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.THEORY, ResearchCategories.getResearchCategory(cat), THEORY_MULT);
        } else if (r < 0.2D * d * XP_MULT) {
            s = ResearchCategories.researchCategories.keySet().toArray(new String[0]);
            cat = s[player.getRNG().nextInt(s.length)];
            ThaumcraftApi.internalMethods.addKnowledge(player, IPlayerKnowledge.EnumKnowledgeType.OBSERVATION, ResearchCategories.getResearchCategory(cat), THEORY_MULT);
        }
    }


    @Shadow(remap = false)
    static HashMap<Integer, Long> nextCycle;
    @Shadow(remap = false)
    static HashMap<Integer, Integer> lastCharge;
    @Shadow(remap = false)
    static HashMap<Integer, Integer> runicInfo;

}
