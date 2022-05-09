package griglog.thaumtweaks.entity;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import thaumcraft.common.entities.monster.EntityPech;

import java.util.ArrayList;
import java.util.Arrays;

public class PechTradesHelper {
    public static class TradeEntry{
        int meta, tier;
        PechType type;
        String id, tag;
        public TradeEntry(PechType type, int tier, String id, int meta, String tag) {
            this.meta = meta;
            this.tier = tier;
            this.type = type;
            this.id = id;
            this.tag = tag;
        }
    }
    public static ArrayList<TradeEntry> uninitializedTrades = new ArrayList<>();
    public static void tryAddEntries(TradeEntry e, int amount){
        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(e.id));
        if (item == null) {
            for (int i = 0; i < amount; i++)
                uninitializedTrades.add(e);
            return;
        }
        for (int i = 0; i < amount; i++)
            addEntry(e, item);
    }

    static void addEntry(TradeEntry e, Item parsedItem){
        ItemStack is = (e.meta == -1 ? new ItemStack(parsedItem) : new ItemStack(parsedItem, 1, e.meta));
        if (!e.tag.equals("")) {
            try {
                is.setTagCompound(JsonToNBT.getTagFromJson(e.tag));
            } catch (NBTException error) {
                error.printStackTrace();
            }
        }
        if (e.type.equals(PechType.COMMON)) {
            EntityPech.tradeInventory.get(PechType.MINER.ordinal()).add(Arrays.asList(e.tier, is));
            EntityPech.tradeInventory.get(PechType.MAGE.ordinal()).add(Arrays.asList(e.tier, is));
            EntityPech.tradeInventory.get(PechType.ARCHER.ordinal()).add(Arrays.asList(e.tier, is));
        } else
            EntityPech.tradeInventory.get(e.type.ordinal()).add(Arrays.asList(e.tier, is));
    }


    public static void addUninitialized(){
        for (TradeEntry e : uninitializedTrades){
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(e.id));
            if (item == null) {
                ThaumTweaks.LOGGER.error(String.format("Item \"%s\" does not exist!", e.id));
                return;
            }
            addEntry(e, item);
        }
        uninitializedTrades.clear();
    }
}
