package griglog.thaumtweaks.mixins.entities;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.container.ContainerPech;
import thaumcraft.common.container.InventoryPech;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.lib.SoundsTC;

import java.util.ArrayList;
import java.util.List;

@Mixin(ContainerPech.class)
public abstract class PechContainerMixin extends Container {
    @Inject(method = "generateContents", at = @At("HEAD"), remap = false, cancellable = true)
    private void genContents(CallbackInfo ci) {
        if (TTConfig.general.pechs && !this.theWorld.isRemote && !this.inventory.getStackInSlot(0).isEmpty() && this.inventory.getStackInSlot(1).isEmpty() && this.inventory.getStackInSlot(2).isEmpty() && this.inventory.getStackInSlot(3).isEmpty() && this.inventory.getStackInSlot(4).isEmpty() && this.pech.isValued(this.inventory.getStackInSlot(0))) {
            int totalValue = this.pech.getValue(this.inventory.getStackInSlot(0));
            if (this.theWorld.rand.nextInt(100) <= Math.min(10, 50 / totalValue)) {
                this.pech.setTamed(false);
                this.pech.playSound(SoundsTC.pech_trade, 0.4F, 1.0F);
            }

            if (this.theWorld.rand.nextInt(5) == 0)
                totalValue += this.theWorld.rand.nextInt(3);
            else if (this.theWorld.rand.nextBoolean()) 
                totalValue -= this.theWorld.rand.nextInt(3);

            while(totalValue > 0) {
                int value = Math.min(5, Math.max((totalValue + 1) / 2, this.theWorld.rand.nextInt(totalValue) + 1));
                //number [v / 2, 5]
                totalValue -= value;
                if (this.hasStuffInPack() && value == 1 && this.theWorld.rand.nextBoolean())
                    giveOwned();
                else if (value < 4 || this.theWorld.rand.nextInt(3) == 0)  //to avoid spamming rare loot
                    giveTrade(value);
            }
            this.inventory.decrStackSize(0, 1);

            ci.cancel();
        }
    }

    void giveOwned() {
        ArrayList<Integer> lootCopy = new ArrayList();

        for(int i = 0; i < this.pech.loot.size(); ++i) {
            if (!(this.pech.loot.get(i)).isEmpty() && (this.pech.loot.get(i)).getCount() > 0) {
                lootCopy.add(i);
            }
        }

        int ind = lootCopy.get(this.theWorld.rand.nextInt(lootCopy.size()));
        ItemStack randItem = (this.pech.loot.get(ind)).copy();
        randItem.setCount(1);
        this.addStack(randItem);
        (this.pech.loot.get(ind)).shrink(1);
        if ((this.pech.loot.get(ind)).getCount() <= 0) {
            this.pech.loot.set(ind, ItemStack.EMPTY);
        }
    }

    void giveTrade(int value){
        ArrayList tradeList = EntityPech.tradeInventory.get(this.pech.getPechType());
        List trade;
        do {
            trade = (List)tradeList.get(this.theWorld.rand.nextInt(tradeList.size()));
        } while ((Integer)trade.get(0) != value);

        ItemStack is = ((ItemStack)trade.get(1)).copy();
        is.onCrafting(this.theWorld, this.player, 0);
        this.addStack(is);
    }

    @Shadow
    abstract void addStack(ItemStack is);
    @Shadow
    abstract boolean hasStuffInPack();
    @Shadow
    EntityPech pech;
    @Shadow
    InventoryPech inventory;
    @Shadow
    EntityPlayer player;
    @Shadow
    World theWorld;
}
