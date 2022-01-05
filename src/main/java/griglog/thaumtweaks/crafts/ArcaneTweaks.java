package griglog.thaumtweaks.crafts;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.ShapedArcaneRecipe;
import thaumcraft.api.items.ItemsTC;

public class ArcaneTweaks{
    static ResourceLocation defaultGroup = new ResourceLocation("");
    public static void override() {
        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:AlchemicalConstruct"),
                new ShapedArcaneRecipe(defaultGroup, "TUBES",
                        75,
                        (new AspectList())
                                .add(Aspect.ORDER, 1)
                                .add(Aspect.ENTROPY, 1),
                        new ItemStack(BlocksTC.metalAlchemical, 2),
                        "ITI", "TWT", "ITI",
                        'W', new ItemStack(BlocksTC.plankGreatwood),
                        'T', new ItemStack(BlocksTC.tube),
                        'I', "plateIron"));

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:workbenchcharger"),
                new ShapedArcaneRecipe(defaultGroup, "WORKBENCHCHARGER", 400,
                        (new AspectList()).add(Aspect.AIR, 2)
                                .add(Aspect.ORDER, 2),
                        new ItemStack(BlocksTC.arcaneWorkbenchCharger),
                        " R ", "W W", "I I",
                        'I', "ingotIron",
                        'R', new ItemStack(ItemsTC.visResonator),
                        'W', new ItemStack(BlocksTC.plankGreatwood)));


        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:ApprenticesRing"),
                new ShapedArcaneRecipe(defaultGroup, "UNLOCKINFUSION", 200,
                        (AspectList)null,
                        new ItemStack(ItemsTC.baubles, 1, 3),
                        " BR", "B B", " B ",
                        'R', new ItemStack(ItemsTC.morphicResonator),
                        'B', "ingotBrass"));

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MatrixMotion"),
                new ShapedArcaneRecipe(defaultGroup, "INFUSIONBOOST", 3000,
                        (new AspectList()).add(Aspect.AIR, 1).add(Aspect.ORDER, 1),
                        new ItemStack(BlocksTC.matrixSpeed), "SNS", "NGN", "SNS", 'S', new ItemStack(BlocksTC.stoneArcane), 'N', "nitor", 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));

        ThaumcraftApi.addArcaneCraftingRecipe(new ResourceLocation("thaumcraft:MatrixCost"),
                new ShapedArcaneRecipe(defaultGroup, "INFUSIONBOOST", 1500,
                        (new AspectList()).add(Aspect.AIR, 1).add(Aspect.WATER, 1).add(Aspect.ENTROPY, 1),
                        new ItemStack(BlocksTC.matrixCost), "SAS", "AGA", "SAS", 'S', new ItemStack(BlocksTC.stoneArcane), 'A', new ItemStack(ItemsTC.alumentum), 'G', new ItemStack(Blocks.DIAMOND_BLOCK)));


    }
}
