package griglog.thaumtweaks.core;

import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class TTContainer extends DummyModContainer {
    public TTContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();

        meta.modId = "thaumtweaks_core";
        meta.name = "ThaumTweaks' Core ";
        meta.credits = "GrigLog";
        meta.authorList = ImmutableList.of("GrigLog");
        meta.description = "Coremod to make thaum more balanced";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
