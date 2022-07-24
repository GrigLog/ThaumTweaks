package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.NonNullList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.lib.utils.BlockStateUtils;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.crafting.TileVoidSiphon;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Mixin(TileVoidSiphon.class)
public abstract class SiphonMixin extends TileThaumcraft {
    private static final int cap = (int) (2000 / (TTConfig.voidSiphon.allow ? TTConfig.voidSiphon.speed : 1));
    private static final double reduceChance = 0.03 / (TTConfig.voidSiphon.allow ? TTConfig.voidSiphon.endurance : 1);

    @Inject(method="update", cancellable = true, at=@At(value="INVOKE", target="Lthaumcraft/common/tiles/TileThaumcraftInventory;update()V", shift=At.Shift.AFTER))
    public void update(CallbackInfo ci) {
        TileVoidSiphon thisRuntime = (TileVoidSiphon)(Object)this;
        ++this.counter;
        if (!this.getWorld().isRemote && BlockStateUtils.isEnabled(this.getBlockMetadata()) &&
                this.counter % 20 == 0 && this.progress < cap && notFull(thisRuntime)) {
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

            if (this.progress >= cap && notFull(thisRuntime)) {
                b = true;
                this.progress -= cap;
                if (world.rand.nextInt(24) == 0)
                    tryAddItem(new ItemStack(ItemsTC.curio, 1, 3), thisRuntime);
                else
                    tryAddItem(new ItemStack(ItemsTC.voidSeed), thisRuntime);
            }
            if (b) {
                this.syncTile(false);
                this.markDirty();
            }
        }
        ci.cancel();
    }

    private void tryAddItem(ItemStack is, TileVoidSiphon thisRuntime) {
        if (thisRuntime.getStackInSlot(0).isEmpty())
            thisRuntime.setInventorySlotContents(0, is);
        else if (thisRuntime.getStackInSlot(0).getItem() == is.getItem())
            thisRuntime.getStackInSlot(0).setCount(thisRuntime.getStackInSlot(0).getCount() + 1);
    }

    private boolean notFull(TileVoidSiphon thisRuntime) {
        return thisRuntime.getStackInSlot(0).isEmpty() || thisRuntime.getStackInSlot(0).getItem() == ItemsTC.voidSeed && thisRuntime.getStackInSlot(0).getCount() < thisRuntime.getStackInSlot(0).getMaxStackSize();
    }


    @Shadow(remap = false)
    private List<EntityFluxRift> getValidRifts(){return new ArrayList<>();}
    @Shadow(remap = false)
    int counter;
    @Shadow(remap = false)
    public int progress;
}
