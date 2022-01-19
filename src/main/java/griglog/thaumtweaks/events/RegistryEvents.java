package griglog.thaumtweaks.events;

import griglog.thaumtweaks.blocks.crafter.TileArcaneCrafter;
import griglog.thaumtweaks.blocks.TTBlocks;
import griglog.thaumtweaks.crafts.RecipeMergePearls;
import griglog.thaumtweaks.items.TTItems;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod.EventBusSubscriber
public class RegistryEvents {
    @SubscribeEvent
    public static void regItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().registerAll(TTItems.filler);
        event.getRegistry().registerAll(TTItems.arcaneCrafterItem);

    }

    @SubscribeEvent
    public static void regBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(TTBlocks.arcaneCrafter);
        GameRegistry.registerTileEntity(TileArcaneCrafter.class, TTBlocks.arcaneCrafter.getRegistryName());
    }

    @SubscribeEvent
    public static void registerCrafts(RegistryEvent.Register<IRecipe> event) {
        event.getRegistry().register(new RecipeMergePearls());
    }
}
