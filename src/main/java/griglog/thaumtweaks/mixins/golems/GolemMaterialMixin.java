package griglog.thaumtweaks.mixins.golems;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.api.golems.EnumGolemTrait;
import thaumcraft.api.golems.parts.GolemMaterial;

@Mixin(GolemMaterial.class)
public class GolemMaterialMixin {
    @Inject(method = "<init>", at=@At("RETURN"))
    void applyCoeffs(String key, String[] research, ResourceLocation texture, int itemColor, int hp, int armor, int damage, ItemStack compb, ItemStack compm, EnumGolemTrait[] tags, CallbackInfo ci) {
        if (TTConfig.golemStats.allowed) {
            this.healthMod = (int) (hp * TTConfig.golemStats.hp + 10 * (TTConfig.golemStats.hp - 1)) / 2 * 2;  //10 is a base hp value
            this.armor = (int) (armor * TTConfig.golemStats.armor);
        }
    }

    @Shadow
    int healthMod;
    @Shadow
    int armor;
}
