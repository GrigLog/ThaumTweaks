package griglog.thaumtweaks.mixins.events;

import baubles.api.BaublesApi;
import griglog.thaumtweaks.SF;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.api.research.ResearchCategories;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.lib.events.PlayerEvents;
import thaumcraft.common.world.aura.AuraHandler;

import java.util.ArrayList;
import java.util.HashMap;

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

    private static void handleRunicArmor(EntityPlayer player) {
        int charge;
        if (player.ticksExisted % 20 == 0) {
            int max = 0;

            for(int a = 0; a < 4; ++a) {
                max += getRunicCharge((ItemStack)player.inventory.armorInventory.get(a));
            }

            IInventory baubles = BaublesApi.getBaubles(player);

            for(charge = 0; charge < baubles.getSizeInventory(); ++charge) {
                max += getRunicCharge(baubles.getStackInSlot(charge));
            }

            if (lastMaxCharge.containsKey(player.getEntityId())) {
                charge = (Integer)lastMaxCharge.get(player.getEntityId());
                if (charge > max) {
                    player.setAbsorptionAmount(player.getAbsorptionAmount() - (float)(charge - max));
                }

                if (max <= 0) {
                    lastMaxCharge.remove(player.getEntityId());
                }
            }

            if (max > 0) {
                runicInfo.put(player.getEntityId(), max);
                lastMaxCharge.put(player.getEntityId(), max);
            } else {
                runicInfo.remove(player.getEntityId());
            }
        }

        if (runicInfo.containsKey(player.getEntityId())) {
            if (!nextCycle.containsKey(player.getEntityId())) {
                nextCycle.put(player.getEntityId(), 0L);
            }

            long time = System.currentTimeMillis();
            charge = (int)player.getAbsorptionAmount();
            if (charge == 0 && lastCharge.containsKey(player.getEntityId()) && (Integer)lastCharge.get(player.getEntityId()) > 0) {
                nextCycle.put(player.getEntityId(), time + (long) ModConfig.CONFIG_MISC.shieldWait);
                lastCharge.put(player.getEntityId(), 0);
            }

            tryRecoverShield(player, charge, time);
        }

    }

    private static void tryRecoverShield(EntityPlayer player, int charge, long time) {
        if (charge < runicInfo.get(player.getEntityId()) && nextCycle.get(player.getEntityId()) < time) {
            ArrayList<ItemStack> equip = getRechargables(player);
            //try to recharge from inventory
            if (equip.size() > 0) {
                ItemStack chosen = equip.get(player.world.rand.nextInt(equip.size()));
                if (RechargeHelper.consumeCharge(chosen, player, 5))
                    recoverShield(player, charge, time, 500);
            //try to recharge from aura
            } else if (!AuraHandler.shouldPreserveAura(player.world, player, player.getPosition()) &&
                    AuraHelper.getVis(player.world, new BlockPos(player)) >= (float) ModConfig.CONFIG_MISC.shieldCost) {
                AuraHandler.drainVis(player.world, new BlockPos(player), (float) ModConfig.CONFIG_MISC.shieldCost, false);
                recoverShield(player, charge, time, ModConfig.CONFIG_MISC.shieldRecharge);
            }
        }
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


    @Shadow
    public static int getRunicCharge(ItemStack itemStack) {
        return 0;
    }
    @Shadow
    static HashMap<Integer, Long> nextCycle;
    @Shadow
    static HashMap<Integer, Integer> lastCharge;
    @Shadow
    static HashMap<Integer, Integer> lastMaxCharge;
    @Shadow
    static HashMap<Integer, Integer> runicInfo;
    @Shadow
    static HashMap<String, Long> upgradeCooldown;

}
