package griglog.thaumtweaks.crafts;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.CrucibleRecipe;
import thaumcraft.api.items.ItemsTC;

public class CruicibleTweaks {
    public static void override() {
        ThaumcraftApi.addCrucibleRecipe(new ResourceLocation("thaumcraft:BathSalts"),
                new CrucibleRecipe("BATHSALTS", new ItemStack(ItemsTC.bathSalts),
                        new ItemStack(ItemsTC.salisMundus),
                        (new AspectList())
                                .add(Aspect.MIND, 30)
                                .add(Aspect.LIFE, 30)));


        ThaumcraftApi.addCrucibleRecipe(
                new ResourceLocation("thaumcraft:SaneSoap"),
                new CrucibleRecipe("SANESOAP",
                        new ItemStack(ItemsTC.sanitySoap),
                        new ItemStack(BlocksTC.fleshBlock),
                        (new AspectList())
                                .add(Aspect.MIND, 80)
                                .add(Aspect.ELDRITCH, 40)
                                .add(Aspect.ORDER, 30)));

    }
}
