package griglog.thaumtweaks.mixins.items;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
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
                giveVis((EntityPlayer) player, calculateVis(amuletCount.get(id)));
                amuletCount.put(id, 0);
            }
        }
    }

    private int calculateVis(Integer count) {
        return (int)Math.sqrt(count);
    }

    private void giveVis (EntityPlayer player, int amount){
        NonNullList<ItemStack> mainInv = player.inventory.mainInventory;
        for (int i = 0; i < InventoryPlayer.getHotbarSize(); i++) {
            if (RechargeHelper.rechargeItem(player.world, mainInv.get(i), player.getPosition(), player, amount) > 0.0F) {
                return;
            }
        }
        IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);
        for (int i = 0; i < baubles.getSlots(); ++i) {
            if (RechargeHelper.rechargeItem(player.world, baubles.getStackInSlot(i), player.getPosition(), player, amount) > 0.0F) {
                return;
            }
        }
        NonNullList<ItemStack> armorInv = player.inventory.armorInventory;
        for (int i = 0; i < armorInv.size(); ++i) {
            if (RechargeHelper.rechargeItem(player.world, armorInv.get(i), player.getPosition(), player, amount) > 0.0F) {
                return;
            }
        }
    }
}


