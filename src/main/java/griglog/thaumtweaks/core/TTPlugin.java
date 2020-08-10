package griglog.thaumtweaks.core;

import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.struct.SourceMap;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;


@IFMLLoadingPlugin.TransformerExclusions({"griglog.thaumtweaks"})
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.Name("ThaumTweaks' tweaks")
public class TTPlugin implements IFMLLoadingPlugin {
    public TTPlugin() {
        try {
            loadModJar(new File("C:\\Users\\lenovo\\IdeaProjects\\ThaumTweaks\\libs\\Thaumcraft-1.12.2-6.1.BETA26-deobf.jar"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thaumtweaks.json");
    }

    private void loadModJar(File jar) throws Exception{
        ((LaunchClassLoader) this.getClass().getClassLoader()).addURL(jar.toURI().toURL());
        CoreModManager.getReparseableCoremods().add(jar.getName());
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
