package griglog.thaumtweaks.events;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thaumcraft.common.golems.EntityThaumcraftGolem;
import thaumcraft.common.lib.capabilities.PlayerKnowledge;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;


@Mod.EventBusSubscriber
public class CapabilityEvents {
    @SubscribeEvent
    public static void attachCapabilitiesGolems(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof EntityThaumcraftGolem) {
            event.addCapability(PlayerKnowledge.Provider.NAME, new PlayerKnowledge.Provider());
        }
    }

    @SubscribeEvent
    public static void attachCapabilitiesTiles(AttachCapabilitiesEvent<TileEntity> event) {
        if (event.getObject() instanceof TileArcaneWorkbench) {
            event.addCapability(PlayerKnowledge.Provider.NAME, new PlayerKnowledge.Provider());
        }
    }

}
