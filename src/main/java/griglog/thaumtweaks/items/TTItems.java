package griglog.thaumtweaks.items;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder(ThaumTweaks.MODID)
public class TTItems {
    public static final Item Filler = new ItemFiller();
}
