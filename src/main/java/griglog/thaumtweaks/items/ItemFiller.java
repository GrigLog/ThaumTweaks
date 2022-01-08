package griglog.thaumtweaks.items;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemFiller extends Item {
    public ItemFiller() {
        setUnlocalizedName(ThaumTweaks.MODID + ":" + "filler");
        setRegistryName("filler");
        setCreativeTab(ThaumTweaks.tab);
    }
}
