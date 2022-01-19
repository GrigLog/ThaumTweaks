package griglog.thaumtweaks.core;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.launchwrapper.LaunchClassLoader;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Map;


@IFMLLoadingPlugin.MCVersion("1.12.2")
public class TTPlugin implements IFMLLoadingPlugin {
    public TTPlugin() {
        try {
            File thisFile = new File(TTPlugin.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            File thaumFile = new File(thisFile.getParent(), "Thaumcraft-1.12.2-6.1.BETA26.jar");
            ThaumTweaks.LOGGER.info("Supposed jar location: " + thaumFile.getAbsolutePath());
            ThaumTweaks.LOGGER.info("Jar exists: " + thaumFile.exists());
            loadModJar(thaumFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.thaumtweaks.json");
    }

    private void loadModJar(File jar) throws Exception{
        ((LaunchClassLoader) getClass().getClassLoader()).addURL(jar.toURI().toURL());
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
