package griglog.thaumtweaks.core;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.util.Map;


@IFMLLoadingPlugin.TransformerExclusions({"griglog.thaumtweaks"})
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("ThaumTweaker's tweaks")
public class TTPlugin implements IFMLLoadingPlugin {
    public TTPlugin() {
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thaumtweaks.json");
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return "griglog.thaumtweaks.core.TTContainer";
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
