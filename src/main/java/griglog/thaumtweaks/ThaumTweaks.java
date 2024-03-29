package griglog.thaumtweaks;

import griglog.thaumtweaks.crafts.ArcaneTweaks;
import griglog.thaumtweaks.crafts.CruicibleTweaks;
import griglog.thaumtweaks.crafts.CustomCrafts;
import griglog.thaumtweaks.crafts.InfusionTweaks;
import griglog.thaumtweaks.entity.PechTradesHelper;
import griglog.thaumtweaks.items.TTMaterials;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = ThaumTweaks.MODID, name = ThaumTweaks.NAME, version = ThaumTweaks.VERSION, acceptedMinecraftVersions = ThaumTweaks.MC_VERSION,
dependencies="required-after:baubles@[1.5.2, ); required-after:thaumcraft@[6.1.BETA26]; required-after:mixinbooter@[4.2, )")
public class ThaumTweaks
{
    public static final String MODID = "thaumtweaks";
    public static final String NAME = "ThaumTweaks";
    public static final String VERSION = "0.3.5.3";
    public static final String MC_VERSION = "[1.12.2]";

    public static final Logger LOGGER = LogManager.getLogger(ThaumTweaks.MODID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if (TTConfig.general.materialOverride)
            TTMaterials.overrideMaterials();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        if (TTConfig.general.craftOverride) {
            InfusionTweaks.override();
            CruicibleTweaks.override();
            ArcaneTweaks.override();
        }
        CustomCrafts.registerRecipes();
        if (TTConfig.general.pechs)
            PechTradesHelper.addUninitialized();
    }
}
