package griglog.thaumtweaks.handlers;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@EventBusSubscriber(modid = ThaumTweaks.MODID)
public class RegistrationHandler {

    @SubscribeEvent
    public static void registerItems(Register<Item> event) {
        final Item[] items = {

        };
        final Item[] itemBlocks = {

        };
        event.getRegistry().registerAll(items);
        event.getRegistry().registerAll(itemBlocks);
    }

    @SubscribeEvent
    public static void registerBlocks(Register<Block> event) {
        final Block[] blocks = {

        };
        event.getRegistry().registerAll(blocks);
    }

    public static Item itemStuff(Item item, String id) {
        item.setRegistryName(ThaumTweaks.MODID, id);
        item.setUnlocalizedName(ThaumTweaks.MODID + "." + id);
        //item.setCreativeTab(ThaumTweaks.MOD_TAB);
        return item;
    }

    public static Block blockStuff(Block block, String id){
        block.setRegistryName(ThaumTweaks.MODID, id);
        block.setUnlocalizedName(ThaumTweaks.MODID + "." + id);
        //block.setCreativeTab(ThaumTweaks.MOD_TAB);
        return block;
    }

}