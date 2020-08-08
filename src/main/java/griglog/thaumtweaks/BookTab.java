package griglog.thaumtweaks;

import net.minecraft.util.ResourceLocation;
import thaumcraft.api.research.ResearchCategories;

public class BookTab {
    public static final String catName = "ThaumTweaks";
    public static final ResourceLocation[] backgrounds = new ResourceLocation[]{new ResourceLocation("thaumtweaks", "textures/research/particlefield.png")};

    public static void setup() {
        ResearchCategories.registerCategory(catName, null, null, new ResourceLocation("thaumtweaks", "textures/items/void_hoe.png"), (ResourceLocation) backgrounds[0]);
    }
}