package griglog.thaumtweaks.mixins.armor;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.api.items.IRechargable;
import thaumcraft.api.items.RechargeHelper;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;

@Mixin(RechargeHelper.class)
public class RechargeHelperMixin {
    @Inject(method="getCharge", at = @At("HEAD"), cancellable = true, remap = false)
    private static void getCharge(ItemStack is, CallbackInfoReturnable<Integer> ci) {
        if (is != null && !is.isEmpty() && SF.isRechargeable(is.getItem()))
            ci.setReturnValue(is.hasTagCompound() ? is.getTagCompound().getInteger("tc.charge") : 0);
        else
            ci.setReturnValue(-1);
        ci.cancel();
    }
}
