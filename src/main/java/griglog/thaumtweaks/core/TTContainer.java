package griglog.thaumtweaks.core;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import griglog.thaumtweaks.ThaumTweaks;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class TTContainer extends DummyModContainer {
    public TTContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();

        meta.modId = "thaumtweaks_core";
        meta.name = "ThaumTweaks' Core ";
        meta.version = ThaumTweaks.VERSION;
        meta.credits = "GrigLog";
        meta.authorList = ImmutableList.of("GrigLog");
        meta.description = "Coremod to change what should be changed.";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
