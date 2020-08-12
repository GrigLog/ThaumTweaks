package griglog.thaumtweaks.events;

import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import thaumcraft.api.blocks.BlocksTC;

public class EventHelper {

    public static class GlimmRunnable implements Runnable {
        BlockEvent.HarvestDropsEvent event;
        public GlimmRunnable(BlockEvent.HarvestDropsEvent event) {
            this.event = event;
        }
        public void run() {
            if (event.getWorld().isAirBlock(event.getPos()) && event.getWorld().getBlockState(event.getPos()) != BlocksTC.effectGlimmer.getDefaultState() && event.getWorld().getLight(event.getPos()) < 10) {
                event.getWorld().setBlockState(event.getPos(), BlocksTC.effectGlimmer.getDefaultState(), 3);
            }
        }
    }

}
