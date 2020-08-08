package griglog.thaumtweaks;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thaumcraft.api.items.ItemsTC;


public class CreativeTab extends CreativeTabs {
    public CreativeTab() {
        super(ThaumTweaks.MODID);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(ItemsTC.voidHoe);
    }

    @SideOnly(Side.CLIENT)
    public ItemStack createIcon() {
        return getTabIconItem();
    }
}