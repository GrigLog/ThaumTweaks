package griglog.thaumtweaks.mixins.blocks.workbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import java.lang.reflect.Field;


@Mixin(SlotCraftingArcaneWorkbench.class)
public abstract class SlotArcaneResultMixin extends Slot {
    public SlotArcaneResultMixin(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Inject(method = "onTake", at=@At("HEAD"), remap=false, cancellable = true)
    void checkPreview(EntityPlayer thePlayer, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) throws IllegalAccessException {
        boolean prev = (boolean)preview.get(tile);
        if (!prev){
            preview.set(tile, true);
            ci.setReturnValue(stack);
        }
    }


    private static Field preview;
    static {
        try {
            preview = TileArcaneWorkbench.class.getField("preview");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }
    @Shadow private TileArcaneWorkbench tile;
}
