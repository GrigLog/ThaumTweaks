package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.research.theorycraft.ResearchTableData;
import thaumcraft.api.research.theorycraft.TheorycraftCard;
import thaumcraft.client.gui.GuiResearchTable;
import thaumcraft.common.container.ContainerResearchTable;
import thaumcraft.common.lib.utils.InventoryUtils;
import thaumcraft.common.tiles.crafting.TileResearchTable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Mixin(ContainerResearchTable.class)
public abstract class ResearchTableContainerMixin extends Container {
    public boolean enchantItem(EntityPlayer playerIn, int button) {
        if (button == 1) {
            if (tileEntity.data.lastDraw != null) {
                tileEntity.data.savedCards.add(tileEntity.data.lastDraw.card.getSeed());
            }
            for (ResearchTableData.CardChoice cc : tileEntity.data.cardChoices) {
                if (cc.selected) {
                    tileEntity.data.lastDraw = cc;
                    break;
                }
            }
            tileEntity.data.cardChoices.clear();
            tileEntity.syncTile(false);
            return true;
        } else {
            if (button == 4 || button == 5 || button == 6) {
                long tn = System.currentTimeMillis();
                long to = 0L;
                if (antiSpam.containsKey(playerIn.getEntityId())) {
                    to = antiSpam.get(playerIn.getEntityId());
                }

                if (tn - to < 333L) {
                    return false;
                }
                antiSpam.put(playerIn.getEntityId(), tn);
                List<IItemHandler> invs = new ArrayList<>();
                try {
                    TheorycraftCard card = (tileEntity.data.cardChoices.get(button - 4)).card;
                    if (TTConfig.general.researchTable) {
                        for (EnumFacing face : EnumFacing.VALUES) {
                            BlockPos check = tileEntity.getPos().add(face.getDirectionVec());
                            TileEntity checkInv = tileEntity.getWorld().getTileEntity(check);
                            if (checkInv != null) {
                                IItemHandler inv = checkInv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite());
                                if (inv != null)
                                    invs.add(inv);
                            }
                        }
                    }
                    int[] invContains = new int[card.getRequiredItems() != null ? card.getRequiredItems().length : 0];
                    if (card.getRequiredItems() != null) {
                        ItemStack[] reqItems = card.getRequiredItems();
                        for (int i = 0; i < reqItems.length; i++) {
                            ItemStack required = reqItems[i];
                            int invCount = 0;
                            for (IItemHandler inv: invs)
                                invCount += SF.getCount(inv, required);
                            invContains[i] = Math.min(invCount, required.getCount());
                            ItemStack playerStack = required.copy();
                            playerStack.setCount(required.getCount() - invCount);
                            if (!playerStack.isEmpty() && !InventoryUtils.isPlayerCarryingAmount(player, playerStack, true)) {
                                return false;
                            }
                        }
                        if (card.getRequiredItemsConsumed() != null && card.getRequiredItemsConsumed().length == card.getRequiredItems().length) {
                            for(int i = 0; i < card.getRequiredItems().length; ++i) {
                                if (card.getRequiredItemsConsumed()[i] && card.getRequiredItems()[i] != null && !card.getRequiredItems()[i].isEmpty()) {
                                    ItemStack stack = card.getRequiredItems()[i];
                                    int invCount = invContains[i];
                                    ItemStack copy = stack.copy();
                                    copy.setCount(invCount);
                                    for (IItemHandler inv : invs)
                                        copy.setCount(SF.extract(inv, copy));
                                    InventoryUtils.consumePlayerItem(player, copy, true, true);
                                }
                            }
                        }
                    }
                    if (card.activate(playerIn, tileEntity.data)) {
                        tileEntity.consumeInkFromTable();
                        tileEntity.data.cardChoices.get(button - 4).selected = true;
                        tileEntity.data.addInspiration(-card.getInspirationCost());
                        tileEntity.syncTile(false);
                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (button == 7 && tileEntity.data.isComplete()) {
                tileEntity.finishTheory(playerIn);
                tileEntity.syncTile(false);
                return true;
            } else if (button == 9 && !tileEntity.data.isComplete()) {
                tileEntity.data = null;
                tileEntity.syncTile(false);
                return true;
            } else if (button != 2 && button != 3) {
                return false;
            } else {
                if (tileEntity.data != null && !tileEntity.data.isComplete() && tileEntity.consumepaperFromTable()) {
                    tileEntity.data.drawCards(button, playerIn);
                    tileEntity.syncTile(false);
                }

                return true;
            }
        }
    }
    @Shadow
    public TileResearchTable tileEntity;
    @Shadow
    EntityPlayer player;
    @Shadow
    static HashMap<Integer, Long> antiSpam;
}
