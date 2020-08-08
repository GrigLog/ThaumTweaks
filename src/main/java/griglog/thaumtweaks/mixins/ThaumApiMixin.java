package griglog.thaumtweaks.mixins;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.crafts.InfusionTweaks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(ThaumcraftApi.class)
public abstract class ThaumApiMixin {


    @Inject(method="addInfusionCraftingRecipe", at=@At("HEAD"), cancellable=true, remap=false)
    private static void addInfusion(ResourceLocation registry, InfusionRecipe recipe, CallbackInfo ci) {
        SF.print(registry.toString());
        if (InfusionTweaks.map.containsKey(registry)) {
            SF.print(registry.toString());
            ThaumcraftApi.getCraftingRecipes().put(registry, InfusionTweaks.map.get(registry));
            ci.cancel();
        }
    }

}
