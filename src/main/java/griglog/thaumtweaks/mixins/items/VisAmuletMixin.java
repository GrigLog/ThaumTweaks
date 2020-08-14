package griglog.thaumtweaks.mixins.items;

import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import griglog.thaumtweaks.SF;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.ItemTCBase;
import thaumcraft.common.items.baubles.ItemAmuletVis;

import java.util.HashMap;

@Mixin(ItemAmuletVis.class)
public abstract class VisAmuletMixin extends ItemTCBase {
    public VisAmuletMixin(String name, String... variants) {
        super(name, variants);
    }

    private static HashMap<Integer, Integer> amuletCount = new HashMap();
    private static int lastTick = 0;

    public void onWornTick(ItemStack itemstack, EntityLivingBase player) {
        if (player instanceof EntityPlayer && !player.world.isRemote && player.ticksExisted % (itemstack.getItemDamage() == 0 ? 40 : 5) == 0) {
            int id = player.getEntityId();
            if (!amuletCount.containsKey(id))
                amuletCount.put(id, 0);
            amuletCount.put(id, amuletCount.get(id) + 1);

            if (lastTick != player.ticksExisted) {
                lastTick = player.ticksExisted;
                giveVis(player, calculateVis(amuletCount.get(id)));
                amuletCount.put(id, 0);
            }
        }
    }

    private int calculateVis(Integer count) {
        return (int)Math.sqrt(count);
    }

    private void giveVis (EntityLivingBase player,int amount){
        NonNullList<ItemStack> inv = ((EntityPlayer) player).inventory.mainInventory;
        int a = 0;

        while (true) {
            InventoryPlayer var10001 = ((EntityPlayer) player).inventory;
            if (a >= InventoryPlayer.getHotbarSize()) {
                IBaublesItemHandler baubles = BaublesApi.getBaublesHandler((EntityPlayer) player);

                for (a = 0; a < baubles.getSlots(); ++a) {
                    if (RechargeHelper.rechargeItem(player.world, baubles.getStackInSlot(a), player.getPosition(), (EntityPlayer) player, amount) > 0.0F) {
                        return;
                    }
                }
                inv = ((EntityPlayer) player).inventory.armorInventory;
                for (a = 0; a < inv.size(); ++a) {
                    if (RechargeHelper.rechargeItem(player.world, (ItemStack) inv.get(a), player.getPosition(), (EntityPlayer) player, amount) > 0.0F) {
                        return;
                    }
                }
                break;
            }
            if (RechargeHelper.rechargeItem(player.world, (ItemStack) inv.get(a), player.getPosition(), (EntityPlayer) player, amount) > 0.0F) {
                return;
            }

            ++a;
        }
    }
}


