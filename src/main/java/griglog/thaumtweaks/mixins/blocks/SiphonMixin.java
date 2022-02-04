package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(TileVoidSiphon.class)
public abstract class SiphonMixin extends TileThaumcraft {

    public void update() {
        if (this.initial) {
            this.initial = false;
            if (!this.world.isRemote) {
                this.syncSlots((EntityPlayerMP)null);
            } else {
                NBTTagCompound nbt = new NBTTagCompound();
                nbt.setBoolean("requestSync", true);
                this.sendMessageToServer(nbt);
            }
        }
        ++this.counter;
        if (!this.getWorld().isRemote && BlockStateUtils.isEnabled(this.getBlockMetadata()) &&
                this.counter % 20 == 0 && this.progress < cap && notFull()) {
            List<EntityFluxRift> rifts = this.getValidRifts();
            boolean b = false;

            double speed;
            for(Iterator<EntityFluxRift> i = rifts.iterator(); i.hasNext(); b = speed >= 1.0D) {
                EntityFluxRift fr = i.next();
                speed = Math.sqrt(fr.getRiftSize());
                this.progress += speed;
                fr.setRiftStability((float)(fr.getRiftStability() - speed / 15.0D));
                if (this.world.rand.nextDouble() < reduceChance) {
                    fr.setRiftSize(fr.getRiftSize() - 1);
                }
            }

            if (b && this.counter % 40 == 0) {
                this.world.addBlockEvent(this.pos, this.getBlockType(), 5, this.counter);
            }

            if (this.progress >= cap && notFull()) {
                b = true;
                this.progress -= cap;
                if (world.rand.nextInt(24) == 0)
                    tryAddItem(new ItemStack(ItemsTC.curio, 1, 3));
                else
                    tryAddItem(new ItemStack(ItemsTC.voidSeed));
            }
            if (b) {
                this.syncTile(false);
                this.markDirty();
            }
        }
    }

    private void tryAddItem(ItemStack is) {
        if (getStackInSlot(0).isEmpty())
            setInventorySlotContents(0, is);
        else if (getStackInSlot(0).getItem() == is.getItem())
            getStackInSlot(0).setCount(getStackInSlot(0).getCount() + 1);
    }

    private boolean notFull() {
        return this.getStackInSlot(0).isEmpty() || this.getStackInSlot(0).getItem() == ItemsTC.voidSeed && this.getStackInSlot(0).getCount() < this.getStackInSlot(0).getMaxStackSize();
    }


    @Shadow
    private List<EntityFluxRift> getValidRifts(){return new ArrayList<>();}
    @Shadow
    int counter;
    @Shadow
    public int progress;

    @Shadow
    boolean initial;
    @Shadow
    public abstract ItemStack getStackInSlot(int index);
    @Shadow
    public abstract void setInventorySlotContents(int a, ItemStack b);
    @Shadow
    protected abstract void syncSlots(EntityPlayerMP player);

    private static final int cap = (int) (2000 / (TTConfig.voidSiphon.allow ? TTConfig.voidSiphon.speed : 1));
    private static final double reduceChance = 0.03 / (TTConfig.voidSiphon.allow ? TTConfig.voidSiphon.endurance : 1);
}
