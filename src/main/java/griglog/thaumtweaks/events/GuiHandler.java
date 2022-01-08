package griglog.thaumtweaks.events;

import griglog.thaumtweaks.blocks.crafter.helpers.ContainerArcaneCrafter;
import griglog.thaumtweaks.blocks.crafter.helpers.GuiArcaneCrafter;
import griglog.thaumtweaks.blocks.crafter.TileArcaneCrafter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {
    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (id == 1)
            return new ContainerArcaneCrafter(player.inventory, (TileArcaneCrafter) world.getTileEntity(new BlockPos(x, y, z)));
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        if (world.isRemote && id == 1)
            return new GuiArcaneCrafter(player.inventory, (TileArcaneCrafter) world.getTileEntity(new BlockPos(x, y, z)));
        return null;
    }
}
