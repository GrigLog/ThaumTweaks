package griglog.thaumtweaks.crafts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.common.config.ConfigRecipes;

import java.lang.reflect.Field;

public class ArcaneTweaks{
    static ResourceLocation defaultGroup = new ResourceLocation("");
    public static void override() {
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AlchemicalConstruct"),
                new ShapedArcaneRecipe(defaultGroup, "TUBES",
                        90,
                        (new AspectList())
                                .add(Aspect.ORDER, 1)
                                .add(Aspect.ENTROPY, 1),
                        new ItemStack(BlocksTC.metalAlchemical, 2),
                        "ITI", "TWT", "ITI",
                        'W', new ItemStack(BlocksTC.plankGreatwood),
                        'T', new ItemStack(BlocksTC.tube),
                        'I', "plateIron"));

    }

    /*static {
        try {
            Field f = ConfigRecipes.class.getField("defaultGroup");
            f.setAccessible(true);
            defaultGroup = (ResourceLocation) f.get(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }*/
}
