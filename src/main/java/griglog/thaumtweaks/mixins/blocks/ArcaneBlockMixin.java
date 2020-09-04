package griglog.thaumtweaks.mixins.blocks;

import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;
import org.spongepowered.asm.mixin.Mixin;
import thaumcraft.common.blocks.BlockTCDevice;
import thaumcraft.common.blocks.crafting.BlockArcaneWorkbench;
import thaumcraft.common.tiles.crafting.TileArcaneWorkbench;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

@Mixin(BlockArcaneWorkbench.class)
public class ArcaneBlockMixin extends BlockTCDevice {
    public ArcaneBlockMixin(Material mat, Class tc, String name) {
        super(mat, tc, name);
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        if (!world.isRemote && world.isBlockPowered(pos)) {
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileArcaneWorkbench) {
                FakePlayer fake = FakePlayerFactory.get((WorldServer) world, new GameProfile(new UUID(0, 0), ""));
                try {
                    tryCrafting.invoke(tile, world, fake, null);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static Method tryCrafting;
    static {
        try {
            tryCrafting = TileArcaneWorkbench.class.getDeclaredMethod("tryCrafting", World.class, EntityPlayer.class, Integer.class);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }
}
