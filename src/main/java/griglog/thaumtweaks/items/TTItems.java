package griglog.thaumtweaks.items;

import griglog.thaumtweaks.ThaumTweaks;
import griglog.thaumtweaks.blocks.TTBlocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;


public class TTItems {
    public static final Item filler = new ItemFiller();

    public static final Item arcaneCrafterItem = new ItemBlock(TTBlocks.arcaneCrafter)
            .setRegistryName(TTBlocks.arcaneCrafter.getRegistryName())
            .setUnlocalizedName(TTBlocks.arcaneCrafter.getUnlocalizedName());
}
