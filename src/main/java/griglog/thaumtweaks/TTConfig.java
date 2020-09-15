package griglog.thaumtweaks;

import net.minecraftforge.common.config.Config;

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
    @Config.Name("Armor")
    public static Armor armor = new Armor();

    public static class General {
        @Config.Name("Tweak crafts")
        public boolean craftOverride = true;
        @Config.Name("Tweak materials (tools' and armors' stats)")
        public boolean materialOverride = true;
    }

    public static class CurBand {
        @Config.Name("Efficiency of xp absorption")
        public double xpMult = 2;
        @Config.Name("Amount of gained research points")
        public int theorMult = 4;
    }

    public static class FortressMask {
        @Config.Name("Healing ratio")
        public double healCoeff = 0.1D;

        @Config.Name("Wither duration (in secs)")
        public int witherDuration = 20;
        @Config.Name("Wither level")
        @Config.RangeInt(min = 1, max = 10)
        public int witherLevel = 2;
    }

    public static class VoidSiphon {
        @Config.Name("Speed boost")
        public double speed = 10;
        @Config.Name("Decrease of chance to damage a rift")
        public double endurance = 2;
    }

    public static class RunShield {
        @Config.Name("Recharge speed boost from inventory")
        public double invBoost = 4;
    }

    public static class Armor {
        @Config.Name("Vis consumption's logarithm's base")
        public double logBase = 2;

        @Config.Name("Void robe's vis-based protection ratio (per item)")
        public double voidProtec = 0.0333D;
        @Config.Name("Void robe's damage reduction")
        public double voidRatio = 0.8D;
        @Config.Name("Void robe's magic damage reduction")
        public double voidMagicRatio = 0.65D;

        @Config.Name("Fortress armor's vis-based protection ratio (per item)")
        public double fortProtec = 0.02D;
        @Config.Name("Fortress armor's damage reduction")
        public double fortRatio = 0.7D;
        @Config.Name("Fortress armor's magic damage reduction")
        public double fortMagicRatio = 0.5D;
    }






}
