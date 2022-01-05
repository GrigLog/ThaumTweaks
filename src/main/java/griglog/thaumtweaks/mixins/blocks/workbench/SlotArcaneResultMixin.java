package griglog.thaumtweaks.mixins.blocks.workbench;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.container.slot.SlotCraftingArcaneWorkbench;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import java.lang.reflect.Field;


@Mixin(SlotCraftingArcaneWorkbench.class)
public abstract class SlotArcaneResultMixin extends Slot {
    @Shadow @Final private InventoryCrafting craftMatrix;
    @Shadow private int amountCrafted;

    public SlotArcaneResultMixin(IInventory inventoryIn, int index, int xPosition, int yPosition) {
        super(inventoryIn, index, xPosition, yPosition);
    }

    @Inject(method = "onTake", at=@At("HEAD"), remap=false, cancellable = true)
    void take(EntityPlayer thePlayer, ItemStack stack, CallbackInfoReturnable<ItemStack> ci) throws IllegalAccessException {
        boolean prev = (boolean)preview.get(tile);
        ThaumTweaks.LOGGER.info(stack.getUnlocalizedName());
        if (!prev){
            preview.set(tile, true);
            ci.setReturnValue(stack);
        }
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMatrix, thePlayer);
        ForgeHooks.setCraftingPlayer(null);
        if (recipe != null) {
            int vis = recipe.getVis();
            vis = (int)((float)vis * (1.0F - CasterManager.getTotalVisDiscount(thePlayer)));
            tile.getAura();
            if (tile.auraVisServer < 2 * recipe.getVis()) {
                stack.setCount(0);
                ci.setReturnValue(stack);
            }
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
