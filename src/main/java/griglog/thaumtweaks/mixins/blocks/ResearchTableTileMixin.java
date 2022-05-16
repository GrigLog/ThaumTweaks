package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.tiles.TileThaumcraftInventory;
import thaumcraft.common.tiles.crafting.TileResearchTable;

@Mixin(TileResearchTable.class)
public class ResearchTableTileMixin extends TileThaumcraftInventory {
    public ResearchTableTileMixin(int size) {
        super(size);
    }
    public BlockPos invPos = BlockPos.ORIGIN;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        invPos = BlockPos.fromLong(tag.getLong("invPos"));
        super.readFromNBT(tag);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag.setLong("invPos", invPos.toLong());
        return super.writeToNBT(tag);
    }

    public void checkInventories(){
        for(EnumFacing face : EnumFacing.VALUES){
            BlockPos check = getPos().add(face.getDirectionVec());
            TileEntity checkInv = world.getTileEntity(check);
            if (checkInv != null && checkInv.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, face.getOpposite()) != null){
                invPos = check;
                return;
            }
        }
    }
}
