package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

import java.util.Iterator;
import java.util.List;

@Mixin(TileVoidSiphon.class)
public abstract class SiphonMixin extends TileThaumcraftInventory {
    public SiphonMixin(int size) {
        super(size);
    }

    public void update() {
        super.update();
        ++this.counter;
        if (!this.getWorld().isRemote && BlockStateUtils.isEnabled(this.getBlockMetadata()) &&
                this.counter % 20 == 0 && this.progress < cap && notFull()) {
            List<EntityFluxRift> frl = this.getValidRifts();
            boolean b = false;

            double speed;
            for(Iterator var3 = frl.iterator(); var3.hasNext(); b = speed >= 1.0D) {
                EntityFluxRift fr = (EntityFluxRift)var3.next();
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

    void tryAddItem(ItemStack is) {
        if (getStackInSlot(0).isEmpty())
            setInventorySlotContents(0, is);
        else if (getStackInSlot(0).getItem() == is.getItem())
            getStackInSlot(0).setCount(getStackInSlot(0).getCount() + 1);
    }

    boolean notFull() {
        return this.getStackInSlot(0).isEmpty() || this.getStackInSlot(0).getItem() == ItemsTC.voidSeed && this.getStackInSlot(0).getCount() < this.getStackInSlot(0).getMaxStackSize();
    }

    @Shadow
    protected abstract List<EntityFluxRift> getValidRifts();

    @Shadow
    int counter;
    @Shadow
    public int progress;

    public final int cap = (int) (2000 / TTConfig.voidSiphon.speed);
    public final double reduceChance = 0.03 / TTConfig.voidSiphon.endurance;
}
