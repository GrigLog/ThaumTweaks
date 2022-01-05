package griglog.thaumtweaks.mixins.blocks.workbench;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.blocks.workbench.InventoryArcaneResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

@Mixin(ContainerArcaneWorkbench.class)
public abstract class ContainerArcaneMixin extends Container {

    @Inject(method = "<init>", at=@At("RETURN"), remap = false)
    void adjustConstructor(InventoryPlayer invPlayer, TileArcaneWorkbench e, CallbackInfo ci) throws IllegalAccessException, NoSuchFieldException {
        craftResult = (InventoryArcaneResult)(tileEntity.getClass().getDeclaredField("inventoryResult").get(tileEntity));
        slotInventory.set(inventorySlots.get(0), craftResult);

        SF.copyKnowledge(invPlayer.player, tileEntity);
    }

    public void onCraftMatrixChanged(IInventory inventory) {  //called when something is added to the grid
        try {
            checkCrafting.invoke(tileEntity, tileEntity.getWorld(), ip.player);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        super.detectAndSendChanges();
    }


    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMat, InventoryCraftResult craftRes) {
    }


    private static Field slotInventory;
    private static Method checkCrafting;
    static {
        try {
            slotInventory = Slot.class.getDeclaredField((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ?
                    "inventory": "field_75224_c");
            Field mods = slotInventory.getClass().getDeclaredField("modifiers");
            mods.setAccessible(true);
            mods.setInt(slotInventory, slotInventory.getModifiers() & ~Modifier.FINAL);

            checkCrafting = TileArcaneWorkbench.class.getDeclaredMethod("checkCrafting", World.class, EntityPlayer.class);
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Shadow
    public InventoryCraftResult craftResult;
    @Shadow
    private TileArcaneWorkbench tileEntity;
    @Shadow
    private InventoryPlayer ip;
}
