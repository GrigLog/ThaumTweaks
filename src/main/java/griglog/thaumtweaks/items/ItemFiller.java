package griglog.thaumtweaks.items;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import thaumcraft.common.config.ConfigItems;

public class ItemFiller extends Item {
    public ItemFiller() {
        setUnlocalizedName(ThaumTweaks.MODID + ":" + "filler");
        setRegistryName("filler");
        setCreativeTab(ConfigItems.TABTC);
    }
}
