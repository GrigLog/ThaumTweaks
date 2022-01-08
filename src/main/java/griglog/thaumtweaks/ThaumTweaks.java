package griglog.thaumtweaks;

import griglog.thaumtweaks.events.GuiHandler;
import griglog.thaumtweaks.crafts.ArcaneTweaks;
import griglog.thaumtweaks.crafts.CruicibleTweaks;
import griglog.thaumtweaks.crafts.CustomCrafts;
import griglog.thaumtweaks.crafts.InfusionTweaks;
import griglog.thaumtweaks.items.TTCreativeTab;
import griglog.thaumtweaks.items.TTMaterials;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(modid = ThaumTweaks.MODID, name = ThaumTweaks.NAME, version = ThaumTweaks.VERSION, acceptedMinecraftVersions = ThaumTweaks.MC_VERSION,
dependencies="required-after:baubles@[1.5.2, ); after:thaumcraft@[6.1.BETA26]")
public class ThaumTweaks
{
    public static final String MODID = "thaumtweaks";
    public static final String NAME = "ThaumTweaks";
    public static final String VERSION = "0.2.4.3";
    public static final String MC_VERSION = "[1.12.2]";

    public static final Logger LOGGER = LogManager.getLogger(ThaumTweaks.MODID);
    public static final boolean DEBUG = false;

    public static CreativeTabs tab;
    @Mod.Instance(MODID)  //why tf am I not allowed to initialize it myself?
    public static ThaumTweaks instance;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event){
        if (TTConfig.general.materialOverride)
            TTMaterials.overrideMaterials();
        tab = new TTCreativeTab();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        if (TTConfig.general.craftOverride) {
            InfusionTweaks.override();
            CruicibleTweaks.override();
            ArcaneTweaks.override();
        }
        CustomCrafts.registerRecipes();
    }
}
