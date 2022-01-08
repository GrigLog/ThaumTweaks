package griglog.thaumtweaks.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TTCreativeTab extends CreativeTabs {
    public TTCreativeTab(){
        super("thaumtweaks");
    }
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getTabIconItem() {
        return new ItemStack(TTItems.filler);
    }
}
