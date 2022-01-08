package griglog.thaumtweaks.crafts;

import griglog.thaumtweaks.items.TTItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;

public class CustomCrafts {
    static ResourceLocation defaultGroup = new ResourceLocation("");
    public static void registerRecipes() {
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("ThaumTweaks:ArcaneFiller"),
                new ShapedArcaneRecipe(defaultGroup, "",
                        40,
                        (new AspectList()),
                        new ItemStack(TTItems.filler, 4),
                        " P ", "PWP", " P ",
                        'W', new ItemStack(BlocksTC.arcaneWorkbench),
                        'P', new ItemStack(Items.PAPER)));
    }
}
