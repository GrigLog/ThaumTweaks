package griglog.thaumtweaks.blocks.crafter.helpers;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.ThaumTweaks;
import griglog.thaumtweaks.blocks.crafter.TileArcaneCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.ContainerDummy;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.slot.SlotCrystal;

public class ContainerArcaneCrafter extends Container{
    private TileArcaneCrafter crafter;
    private InventoryPlayer inv;
    //public InventoryCraftResult craftResult = new InventoryCraftResult();
    public static int[] xx = new int[]{64, 17, 112, 17, 112, 64};
    public static int[] yy = new int[]{13, 35, 35, 93, 93, 115};
    private int lastVis = -1;
    private long lastCheck = 0L;

    public ContainerArcaneCrafter(InventoryPlayer inventory, TileArcaneCrafter tile) {
        crafter = tile;
        crafter.inventoryCraft.eventHandler = this;
        inv = inventory;
        tile.updateAura();
        addSlotToContainer(new LockedSlot(crafter.inventoryResult, 15, 160, 64));

        //show crafter inventory
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                addSlotToContainer(new LockedSlot(crafter.inventoryCraft, i * 3 + j, 40 + j * 24, 40 + i * 24));
            }
        }
        ShardType[] shardTypes = ShardType.values();
        int shardTypesLen = shardTypes.length;
        for(int i = 0; i < shardTypesLen; ++i) {
            ShardType st = shardTypes[i];
            if (st.getMetadata() < 6) {
                addSlotToContainer(new LockedSlotCrystal(st.getAspect(), crafter.inventoryCraft, st.getMetadata() + 9, xx[st.getMetadata()], yy[st.getMetadata()]));
            }
        }

        //show player inventory
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventory, i * 9 + j + 9, 16 + j * 18, 151 + i * 18));
            }
        }
        for(int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventory, i, 16 + i * 18, 209));
        }

        SF.copyKnowledge(inv.player, crafter);
        onCraftMatrixChanged(crafter.inventoryCraft);
    }

    /*public void addListener(IContainerListener par1ICrafting) {
        super.addListener(par1ICrafting);
        crafter.updateAura();
        par1ICrafting.sendWindowProperty(this, 0, crafter.auraVisServer);
    }*/

    //called every tick when tha player looks into gui
    public void detectAndSendChanges() {
        //ThaumTweaks.LOGGER.info("detectAndSendChanges");
        super.detectAndSendChanges();
        long t = System.currentTimeMillis();
        if (t > lastCheck) {
            lastCheck = t + 500L;
            crafter.updateAura();
        }
        if (lastVis != crafter.auraVisServer) {
            IArcaneRecipe recipe = crafter.findRecipe(inv.player, true);
            boolean hasVis, hasCrystals=true;
            if (recipe != null) {
                int vis = recipe.getVis();
                AspectList crystals = recipe.getCrystals();
                crafter.updateAura();
                hasVis = crafter.getWorld().isRemote ? crafter.auraVisClient >= vis : crafter.auraVisServer >= vis;
                if (crystals != null && crystals.size() > 0) {
                    Aspect[] aspects = crystals.getAspects();
                    for(int i = 0; i < aspects.length; ++i) {
                        Aspect aspect = aspects[i];
                        if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(crafter.inventoryCraft, EnumFacing.UP), ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), ThaumcraftInvHelper.InvFilter.STRICT) < crystals.getAmount(aspect)) {
                            hasCrystals = false;
                            break;
                        }
                    }
                }
            }
        }
        for(int i = 0; i < listeners.size(); ++i) {
            IContainerListener icrafting = (IContainerListener)listeners.get(i);
            if (lastVis != crafter.auraVisServer) {
                icrafting.sendWindowProperty(this, 0, crafter.auraVisServer);
            }
        }
        lastVis = crafter.auraVisServer;
    }

    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int par1, int par2) {
        if (par1 == 0) {
            crafter.auraVisClient = par2;
        }
    }

    public void onCraftMatrixChanged(IInventory par1IInventory) {
        //ThaumTweaks.LOGGER.info("onMatrixChanged");

        /*IArcaneRecipe recipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(crafter.inventoryCraft, inv.player);
        boolean hasVis = true;
        boolean hasCrystals = true;
        if (recipe != null) {
            int vis = recipe.getVis();
            vis = (int)((float)vis * (1.0F - CasterManager.getTotalVisDiscount(inv.player)));
            AspectList crystals = recipe.getCrystals();
            crafter.updateAura();
            hasVis = crafter.getWorld().isRemote ? crafter.auraVisClient >= vis : crafter.auraVisServer >= vis;
            if (crystals != null && crystals.size() > 0) {
                Aspect[] aspects = crystals.getAspects();
                for(int i = 0; i < aspects.length; ++i) {
                    Aspect aspect = aspects[i];
                    if (ThaumcraftInvHelper.countTotalItemsIn(ThaumcraftInvHelper.wrapInventory(crafter.inventoryCraft, EnumFacing.UP), ThaumcraftApiHelper.makeCrystal(aspect, crystals.getAmount(aspect)), ThaumcraftInvHelper.InvFilter.STRICT) < crystals.getAmount(aspect)) {
                        hasCrystals = false;
                        break;
                    }
                }
            }
        }

        if (hasVis && hasCrystals) {
            slotChangedCraftingGrid(crafter.getWorld(), inv.player, crafter.inventoryCraft, crafter.inventoryResult);
        }

        super.detectAndSendChanges();*/
    }

    protected void slotChangedCraftingGrid(World world, EntityPlayer player, InventoryCrafting craftMat, InventoryCraftResult craftRes) {
        ThaumTweaks.LOGGER.info("slotChangedCraftingGrid");
        /*if (!world.isRemote) {
            EntityPlayerMP entityplayermp = (EntityPlayerMP)player;
            ItemStack itemstack = ItemStack.EMPTY;
            IArcaneRecipe arecipe = ThaumcraftCraftingManager.findMatchingArcaneRecipe(craftMat, entityplayermp);
            if (arecipe != null && (arecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(arecipe)) && ThaumcraftCapabilities.getKnowledge(player).isResearchKnown(arecipe.getResearch())) {
                craftRes.setRecipeUsed(arecipe);
                itemstack = arecipe.getCraftingResult(craftMat);
            } else {
                InventoryCrafting craftInv = new InventoryCrafting(new ContainerDummy(), 3, 3);

                for(int a = 0; a < 9; ++a) {
                    craftInv.setInventorySlotContents(a, craftMat.getStackInSlot(a));
                }

                IRecipe irecipe = CraftingManager.findMatchingRecipe(craftInv, world);
                if (irecipe != null && (irecipe.isDynamic() || !world.getGameRules().getBoolean("doLimitedCrafting") || entityplayermp.getRecipeBook().isUnlocked(irecipe))) {
                    craftRes.setRecipeUsed(irecipe);
                    itemstack = irecipe.getCraftingResult(craftMat);
                }
            }

            craftRes.setInventorySlotContents(0, itemstack);
            entityplayermp.connection.sendPacket(new SPacketSetSlot(windowId, 0, itemstack));
        }*/
    }

    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        if (!crafter.getWorld().isRemote) {
            crafter.inventoryCraft.eventHandler = new ContainerDummy();
        }
    }

    public boolean canInteractWith(EntityPlayer player) {
        return crafter.getWorld().getTileEntity(crafter.getPos()) != crafter ? false : player.getDistanceSqToCenter(crafter.getPos()) <= 64.0D;
    }

    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index) {
        ItemStack res = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            res = stack.copy();
            if (index == 0) {
                if (!mergeItemStack(stack, 16, 52, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onSlotChange(stack, res);
            } else if (index >= 16 && index < 52) {
                ShardType[] var6 = ShardType.values();
                int var7 = var6.length;

                for(int var8 = 0; var8 < var7; ++var8) {
                    ShardType st = var6[var8];
                    if (st.getMetadata() < 6 && SlotCrystal.isValidCrystal(stack, st.getAspect())) {
                        if (!mergeItemStack(stack, 10 + st.getMetadata(), 11 + st.getMetadata(), false)) {
                            return ItemStack.EMPTY;
                        }

                        if (stack.getCount() == 0) {
                            break;
                        }
                    }
                }

                if (stack.getCount() != 0) {
                    if (index >= 16 && index < 43) {
                        if (!mergeItemStack(stack, 43, 52, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (index >= 43 && index < 52 && !mergeItemStack(stack, 16, 43, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!mergeItemStack(stack, 16, 52, false)) {
                return ItemStack.EMPTY;
            }

            if (stack.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == res.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(inv.player, stack);
        }

        return res;
    }

    public boolean canMergeSlot(ItemStack stack, Slot slot) {
        //return slot.inventory != crafter.inventoryResult && super.canMergeSlot(stack, slot);
        return true;
    }
}
