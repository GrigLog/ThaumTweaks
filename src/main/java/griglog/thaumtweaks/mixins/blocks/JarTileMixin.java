package griglog.thaumtweaks.mixins.blocks;

import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import thaumcraft.common.tiles.essentia.TileJar;
import thaumcraft.common.tiles.essentia.TileJarFillable;

@Mixin(TileJarFillable.class)
public abstract class JarTileMixin extends TileJar {
    public String customName;

    @Inject(method="readSyncNBT", at=@At("RETURN"), remap=false)
    private void readNBT(NBTTagCompound nbt, CallbackInfo ci) {
        String name = nbt.getString("CustomName");
        if (name != null)
            customName = name;
    }

    @Inject(method="writeSyncNBT", at=@At("RETURN"), remap=false, cancellable = true)
    private void writeNBT(NBTTagCompound nbt, CallbackInfoReturnable<NBTTagCompound> ci) {
        if (customName != null)
            nbt.setString("CustomName", customName);
        ci.setReturnValue(nbt);
    }
}
