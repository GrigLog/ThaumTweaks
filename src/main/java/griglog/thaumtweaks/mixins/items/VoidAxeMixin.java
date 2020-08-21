package griglog.thaumtweaks.mixins.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.common.items.tools.ItemVoidAxe;

@Mixin(ItemVoidAxe.class)
public class VoidAxeMixin extends ItemAxe {
    protected VoidAxeMixin(ToolMaterial material) {
        super(material);
    }

    /*@ModifyConstant(method = "ItemVoidAxe")
    float retConst() { return 20; }*/

    /*@Inject(method="ItemVoidAxe", at=@At("HEAD"), remap = false, cancellable = true)
    void fixedCnstr(Item.ToolMaterial material, CallbackInfo ci) {
    }*/
}
