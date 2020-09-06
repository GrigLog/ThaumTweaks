package griglog.thaumtweaks.mixins.blocks.workbench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;
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

        IPlayerKnowledge tileBrains = tileEntity.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        IPlayerKnowledge playerBrains = invPlayer.player.getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        if (tileBrains != null && playerBrains != null) {
            for (String k : playerBrains.getResearchList()) {
                if (!tileBrains.isResearchKnown(k))
                    tileBrains.addResearch(k);
            }
        }
    }

    public void onCraftMatrixChanged(IInventory par1IInventory) {  //called when something is added to the grid
        IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(tileEntity.inventoryCraft, ip.player);
        boolean hasVis = true;
        boolean hasCrystals = true;
        if (recipe != null) {
            int vis = (int)(recipe.getVis() * (1.0F - CasterManager.getTotalVisDiscount(ip.player)));
            AspectList crystals = recipe.getCrystals();
            tileEntity.getAura();
            hasVis = tileEntity.getWorld().isRemote ? tileEntity.auraVisClient >= vis : tileEntity.auraVisServer >= vis;
            if (crystals != null && crystals.size() > 0) {
                Aspect[] aspects = crystals.getAspects();
                for (Aspect aspect: aspects) {
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(tileEntity.inventoryCraft, EnumFacing.UP),
                            ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)),
                            ThaumcraftInvHelper.InvFilter.STRICT)
                            < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }

        if (hasVis && hasCrystals) {
            this.slotChangedCraftingGrid(tileEntity.getWorld(), ip.player, tileEntity.inventoryCraft, craftResult);
        }

        super.detectAndSendChanges();
    }


    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMat, InventoryCraftResult craftRes) {  //called when correct crafting pattern was found
        if (!world.isRemote) {
            try {
                doCrafting.invoke(tileEntity, world, player, windowId);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }


    private static Field slotInventory;
    private static Method doCrafting;
    static {
        try {
            slotInventory = Slot.class.getDeclaredField((boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment") ?
                    "inventory": "field_75224_c");
            Field mods = slotInventory.getClass().getDeclaredField("modifiers");
            mods.setAccessible(true);
            mods.setInt(slotInventory, slotInventory.getModifiers() & ~Modifier.FINAL);

            doCrafting = TileArcaneWorkbench.class.getDeclaredMethod("doCrafting", World.class, EntityPlayer.class, Integer.class);
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
