package griglog.thaumtweaks.mixins.events;

import griglog.thaumtweaks.blocks.crafter.TileArcaneCrafter;
import griglog.thaumtweaks.blocks.crafter.helpers.ContainerArcaneCrafter;
import griglog.thaumtweaks.blocks.crafter.helpers.GuiArcaneCrafter;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.api.golems.seals.ISealEntity;
import thaumcraft.client.gui.*;
import thaumcraft.codechicken.lib.raytracer.RayTracer;
import thaumcraft.common.container.*;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.entities.monster.EntityPech;
import thaumcraft.common.golems.ItemGolemBell;
import thaumcraft.common.tiles.crafting.*;
import thaumcraft.common.tiles.devices.TilePotionSprayer;
import thaumcraft.common.tiles.devices.TileSpa;
import thaumcraft.common.tiles.essentia.TileSmelter;
import thaumcraft.proxies.ProxyGUI;


//You might wonder - GrigLog, why don't you just use your own event for this? Whats the point of abusing thaumcraft classes?
//Because fucking forge doesn't fucking load my fucking classes into the fucking game. I don't know why.
//Maybe its because of mixins, maybe because of broken forgegradle, maybe because 1.12 is not even supposed to work nowadays.
//At this point I just dream of having some normal errors. Like NullPointerException or ClassCastException.
//Not this damn "Ive lost some stuff before the game even started, so you'd better figure out what it is".
@Mixin(ProxyGUI.class)
public class GuiEventsMixin {
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (world instanceof WorldClient) {
            switch(ID) {
                case 1:
                    return new GuiPech(player.inventory, world, (EntityPech)((WorldClient)world).getEntityByID(x));
                case 2:
                case 8:
                case 11:
                case 15:
                default:
                    break;
                case 3:
                    return new GuiThaumatorium(player.inventory, (TileThaumatorium)world.getTileEntity(new BlockPos(x, y, z)));
                case 4:
                    return new GuiHandMirror(player.inventory, world, x, y, z);
                case 5:
                    return new GuiFocusPouch(player.inventory, world, x, y, z);
                case 6:
                    return new GuiSpa(player.inventory, (TileSpa)world.getTileEntity(new BlockPos(x, y, z)));
                case 7:
                    return new GuiFocalManipulator(player.inventory, (TileFocalManipulator)world.getTileEntity(new BlockPos(x, y, z)));
                case 9:
                    return new GuiSmelter(player.inventory, (TileSmelter)world.getTileEntity(new BlockPos(x, y, z)));
                case 10:
                    return new GuiResearchTable(player, (TileResearchTable)world.getTileEntity(new BlockPos(x, y, z)));
                case 12:
                    return new GuiResearchBrowser();
                case 13:
                    return new GuiArcaneWorkbench(player.inventory, (TileArcaneWorkbench)world.getTileEntity(new BlockPos(x, y, z)));
                case 14:
                    return new GuiArcaneBore(player.inventory, world, (EntityArcaneBore)((WorldClient)world).getEntityByID(x));
                case 16:
                    return new GuiTurretBasic(player.inventory, world, (EntityTurretCrossbow)((WorldClient)world).getEntityByID(x));
                case 17:
                    return new GuiTurretAdvanced(player.inventory, world, (EntityTurretCrossbowAdvanced)((WorldClient)world).getEntityByID(x));
                case 18:
                    ISealEntity se = ItemGolemBell.getSeal(player);
                    if (se != null) {
                        return se.getSeal().returnGui(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                    }
                    break;
                case 19:
                    return new GuiGolemBuilder(player.inventory, (TileGolemBuilder)world.getTileEntity(new BlockPos(x, y, z)));
                case 20:
                    RayTraceResult ray = RayTracer.retrace(player);
                    BlockPos target = null;
                    EnumFacing side = null;
                    if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                        target = ray.getBlockPos();
                        side = ray.sideHit;
                    }

                    return new GuiLogistics(player.inventory, world, target, side);
                case 21:
                    return new GuiPotionSprayer(player.inventory, (TilePotionSprayer)world.getTileEntity(new BlockPos(x, y, z)));
                case 22:
                    return new GuiVoidSiphon(player.inventory, (TileVoidSiphon)world.getTileEntity(new BlockPos(x, y, z)));
                case 23:
                    return new GuiArcaneCrafter(player.inventory, (TileArcaneCrafter) world.getTileEntity(new BlockPos(x, y, z)));
            }
        }

        return null;
    }

    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID) {
            case 1:
                return new ContainerPech(player.inventory, world, (EntityPech)((WorldServer)world).getEntityByID(x));
            case 3:
                return new ContainerThaumatorium(player.inventory, (TileThaumatorium)world.getTileEntity(new BlockPos(x, y, z)));
            case 4:
                return new ContainerHandMirror(player.inventory, world, x, y, z);
            case 5:
                return new ContainerFocusPouch(player.inventory, world, x, y, z);
            case 6:
                return new ContainerSpa(player.inventory, (TileSpa)world.getTileEntity(new BlockPos(x, y, z)));
            case 7:
                return new ContainerFocalManipulator(player.inventory, (TileFocalManipulator)world.getTileEntity(new BlockPos(x, y, z)));
            case 9:
                return new ContainerSmelter(player.inventory, (TileSmelter)world.getTileEntity(new BlockPos(x, y, z)));
            case 10:
                return new ContainerResearchTable(player.inventory, (TileResearchTable)world.getTileEntity(new BlockPos(x, y, z)));
            case 13:
                return new ContainerArcaneWorkbench(player.inventory, (TileArcaneWorkbench)world.getTileEntity(new BlockPos(x, y, z)));
            case 14:
                return new ContainerArcaneBore(player.inventory, world, (EntityArcaneBore)((WorldServer)world).getEntityByID(x));
            case 16:
                return new ContainerTurretBasic(player.inventory, world, (EntityTurretCrossbow)((WorldServer)world).getEntityByID(x));
            case 17:
                return new ContainerTurretAdvanced(player.inventory, world, (EntityTurretCrossbowAdvanced)((WorldServer)world).getEntityByID(x));
            case 18:
                ISealEntity se = ItemGolemBell.getSeal(player);
                if (se != null) {
                    return se.getSeal().returnContainer(world, player, new BlockPos(x, y, z), se.getSealPos().face, se);
                }
            case 2:
            case 8:
            case 11:
            case 12:
            case 15:
            default:
                return null;
            case 19:
                return new ContainerGolemBuilder(player.inventory, (TileGolemBuilder)world.getTileEntity(new BlockPos(x, y, z)));
            case 20:
                return new ContainerLogistics(player.inventory, world);
            case 21:
                return new ContainerPotionSprayer(player.inventory, (TilePotionSprayer)world.getTileEntity(new BlockPos(x, y, z)));
            case 22:
                return new ContainerVoidSiphon(player.inventory, (TileVoidSiphon)world.getTileEntity(new BlockPos(x, y, z)));
            case 23:
                return new ContainerArcaneCrafter(player.inventory, (TileArcaneCrafter) world.getTileEntity(new BlockPos(x, y, z)));
        }
    }
}
