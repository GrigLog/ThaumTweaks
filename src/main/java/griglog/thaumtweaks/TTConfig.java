package griglog.thaumtweaks;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = ThaumTweaks.MODID)
@Config(modid = ThaumTweaks.MODID, category = "")
public class TTConfig {
    @Config.Name("General")
    public static General general = new General();
    @Config.Name("Curiosity band")
    public static CurBand curBand = new CurBand();
    @Config.Name("Fortress masks")
    public static FortressMask fortressMask = new FortressMask();
    @Config.Name("Void siphon")
    public static VoidSiphon voidSiphon = new VoidSiphon();
    @Config.Name("Runic shield")
    public static RunShield runShield = new RunShield();
    @Config.Name("Void robe")
    public static VoidRobe voidRobe = new VoidRobe();
    @Config.Name("Fortress armor")
    public static FortArmor fortArmor = new FortArmor();
    @Config.Name("Tools")
    public static Tools tools = new Tools();
    @Config.Name("Golem stats")
    public static GolemStats golemStats = new GolemStats();
    @Config.Name("Curiosities")
    public static Curiosities curiosities = new Curiosities();
    @Config.Name("Blocks")
    public static Blocks blocks = new Blocks();
    @Config.Name("Infusion matrix")
    public static Matrix matrix = new Matrix();

    public static class General {
        @Config.Name("Tweak crafts")
        public boolean craftOverride = true;
        @Config.Name("Tweak materials (tools' and armors' stats)")
        public boolean materialOverride = true;
        @Config.Name("Golems can start infusions")
        public boolean autoInfusion = true;
        @Config.Name("Tweaked pech trades")
        public boolean pechs = true;
        @Config.Name("Rare earth drops are affected by Fortune and Refining")
        public boolean earths = true;
        @Config.Name("Buffed armor")
        public boolean armor = true;
        @Config.Name("Research table can fetch items from adjacent containers")
        public boolean researchTable = true;
    }

    public static class CurBand {
        @Config.Name("Allow tweaking")
        public boolean allow = true;
        @Config.Name("Efficiency of xp absorption")
        public double xpMult = 2;
        @Config.Name("Amount of gained research points")
        public int theorMult = 4;
    }

    public static class FortressMask {
        @Config.Name("Allow tweaking")
        public boolean allow = true;
        @Config.Name("Healing ratio")
        public double healCoeff = 0.1D;

        @Config.Name("Wither duration (in secs)")
        public int witherDuration = 20;
        @Config.Name("Wither level")
        @Config.RangeInt(min = 1, max = 10)
        public int witherLevel = 2;
    }

    public static class VoidSiphon {
        @Config.Name("Allow tweaking")
        public boolean allow = true;
        @Config.Name("Speed boost")
        public double speed = 10;
        @Config.Name("Decrease of chance to damage a rift")
        public double endurance = 2;
    }

    public static class RunShield {
        @Config.Name("Allow tweaking")
        public boolean allow = true;
        @Config.Name("Recharge speed boost from inventory")
        public double invBoost = 4;
    }

    public static class FortArmor {
        @Config.Name("Vis consumption's logarithm's base")
        public double logBase = 2;
        @Config.Name("Vis charge. Can be set to 0.")
        public int vis = 320;

        @Config.Name("Vis-based protection ratio (per item)")
        public double protec = 0.02D;
        @Config.Name("Damage reduction")
        public double ratio = 0.7D;
        @Config.Name("Magic damage reduction")
        public double magicRatio = 0.5D;

        @Config.Name("Strength level")
        @Config.RangeInt(min=0, max=10)
        public int str = 2;
    }

    public static class VoidRobe {
        @Config.Name("Vis consumption's logarithm's base")
        public double logBase = 2;
        @Config.Name("Vis charge. Can be set to 0.")
        public int vis = 480;

        @Config.Name("Vis-based protection ratio (per item)")
        public double protec = 0.0333D;
        @Config.Name("Damage reduction")
        public double ratio = 0.8D;
        @Config.Name("Magic damage reduction")
        public double magicRatio = 0.65D;

        @Config.Name("Heal per second")
        public double heal = 1.0D;
        @Config.Name("Vis per 1 hp healed")
        public int healVis = 1;

        @Config.Name("Allow feeding")
        public boolean canFeed = true;
        @Config.Name("Vis per feeding")
        public int feedVis = 5;

        @Config.Name("Allow debuff clearing")
        public boolean canClear = true;
        @Config.Name("Vis per debuff")
        public int clearVis = 5;
    }

    public static class GolemStats {
        @Config.Name("Allow modification")
        public boolean allowed = true;
        @Config.Name("Health multiplier")
        public double hp = 1.5;
        @Config.Name("Armor modifier")
        public double armor = 2;
    }

    public static class Tools {
        @Config.Name("Primordial crusher has 5*5 mining zone")
        public boolean zone_5 = true;
    }

    public static class Curiosities {
        @Config.Name("Allow tweaking")
        public boolean allow = true;
    }

    public static class Blocks {
        @Config.Name("Pedestals output redstone signal")
        public boolean pedestal = true;
        @Config.Name("Smelteries can be accessed by hoppers")
        public boolean smeltery = true;
    }

    public static class Matrix {
        @Config.Name("Allow tweaking")
        public boolean allow = true;
        @Config.Comment("Resulting time (in ticks) between matrix eating 1 essentia point = 1/2 * (10 - (int)(all speed bonuses)). Its never less than 1 tick, obviously.")
        @Config.Name("4 Ancient pillars cost discount (base TC: 0.1)")
        public double ancientDiscount = 0.2;
        @Config.Name("4 Ancient pillars speed bonus (base TC: 1)")
        public int ancientSpeedUp = 0;

        @Config.Name("4 Eldritch pillars cost increase (base TC: 0.05)")
        public double eldritchIncrease = 0.2;
        @Config.Name("4 Eldritch pillars speed bonus (base TC: 3)")
        public int eldritchSpeedUp = 2;

        @Config.Name("Each Speed stone speed bonus (base TC: 1)")
        public double stoneSpeedSpeed = 1.5;
        @Config.Name("Each Speed stone cost increase (base TC: 0.01)")
        public double stoneSpeedCost = 0.1;

        @Config.Name("Each Cost stone speed decrease (base TC: 1)")
        public double stoneSlowSpeed = 1;
        @Config.Name("Each Cost stone cost discount (base TC: 0.02)")
        public double stoneSlowCost = 0.1;

        @Config.Name("Each Eldricth pedestal cost discount (base TC: 0.0025")
        public double pedestalEldritchCost = 0.002;
        @Config.Name("Each Ancient pedestal cost discount (base TC: 0.01")
        public double pedestalAncientCost = 0.003;
    }



    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(ThaumTweaks.MODID))
            ConfigManager.sync(event.getModID(), Config.Type.INSTANCE);
    }
}
