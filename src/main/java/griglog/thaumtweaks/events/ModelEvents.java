package griglog.thaumtweaks.events;

import griglog.thaumtweaks.items.TTItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT)
public class ModelEvents {
    @SubscribeEvent
    public static void regModels(ModelRegistryEvent event) {
        defaultModel(TTItems.filler);
        defaultModel(TTItems.arcaneCrafterItem);
    }

    static void defaultModel(Item item) {
        ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
    }
}
