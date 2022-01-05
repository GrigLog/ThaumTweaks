package griglog.thaumtweaks.mixins.blocks;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.IInfusionStabiliser;
import thaumcraft.api.crafting.IInfusionStabiliserExt;
import thaumcraft.common.blocks.basic.BlockPillar;
import thaumcraft.common.blocks.devices.BlockPedestal;
import thaumcraft.common.tiles.TileThaumcraft;
import thaumcraft.common.tiles.crafting.TileInfusionMatrix;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Mixin(TileInfusionMatrix.class)
public abstract class MatrixMixin extends TileThaumcraft {
    //this one decompiled incorrectly
    //unexpected behaviour possible
    private void getSurroundings() {
        this.pedestals.clear();
        this.tempBlockCount.clear();
        this.problemBlocks.clear();
        this.cycleTime = 10;
        this.stabilityReplenish = 0.0F;
        this.costMult = 1.0F;

        Set<Long> stuff = new HashSet();

        try {
            findAffectors(stuff);
            iterateFoundAffectors(stuff);
            checkPillarMaterials();
            checkBoostStones();
            checkPedestalMaterials();
            this.countDelay = Math.max(1, this.cycleTime / 2);

        } catch (Exception var17) {
        }
    }

    void findAffectors(Set<Long> stuff) {
        int x, y, z;
        for(int xx = -8; xx <= 8; ++xx) {
            for(int zz = -8; zz <= 8; ++zz) {
                for(int yy = -3; yy <= 7; ++yy) {
                    if (xx != 0 || zz != 0) {
                        x = this.pos.getX() + xx;
                        y = this.pos.getY() - yy;
                        z = this.pos.getZ() + zz;
                        BlockPos pos = new BlockPos(x, y, z);
                        Block block = this.world.getBlockState(pos).getBlock();
                        if (block instanceof BlockPedestal) {
                            this.pedestals.add(pos);
                        }

                        try {
                            if (block == Blocks.SKULL || block instanceof IInfusionStabiliser && ((IInfusionStabiliser)block).canStabaliseInfusion(this.getWorld(), pos)) {
                                stuff.add(pos.toLong());
                            }
                        } catch (Exception var15) {
                        }
                    }
                }
            }
        }
    }

    void iterateFoundAffectors(Set<Long> stuff) {
        for(long lpos; !stuff.isEmpty(); stuff.remove(lpos)) {
            Long[] stuffPoses = stuff.toArray(new Long[0]);
            if (stuffPoses[0] == null) {
                break;
            }

            lpos = stuffPoses[0];

            try {
                BlockPos pos = BlockPos.fromLong(lpos);
                //symmetry
                int x = 2 * this.pos.getX() - pos.getX();
                int z = 2 * this.pos.getZ() - pos.getZ();
                BlockPos posSym = new BlockPos(x, pos.getY(), z);
                Block b1 = this.world.getBlockState(pos).getBlock();
                Block b2 = this.world.getBlockState(posSym).getBlock();

                affectStability(pos, posSym, b1, b2);
                stuff.remove(posSym.toLong());
            } catch (Exception var16) {
            }
        }
    }

    void affectStability(BlockPos pos, BlockPos posSym, Block b1, Block b2) {
        float amt1 = 0.1F;
        float amt2 = 0.1F;
        if (b1 instanceof IInfusionStabiliserExt) {
            amt1 = ((IInfusionStabiliserExt)b1).getStabilizationAmount(this.getWorld(), pos);
        }

        if (b2 instanceof IInfusionStabiliserExt) {
            amt2 = ((IInfusionStabiliserExt)b2).getStabilizationAmount(this.getWorld(), posSym);
        }

        if (b1 == b2 && amt1 == amt2) {
            if (b1 instanceof IInfusionStabiliserExt && ((IInfusionStabiliserExt)b1).hasSymmetryPenalty(this.getWorld(), pos, posSym)) {
                this.stabilityReplenish -= ((IInfusionStabiliserExt)b1).getSymmetryPenalty(this.getWorld(), pos);
                this.problemBlocks.add(pos);
            } else {
                this.stabilityReplenish += this.calcDeminishingReturns(b1, amt1);
            }
        } else {
            this.stabilityReplenish -= Math.max(amt1, amt2);
            this.problemBlocks.add(pos);
        }
    }

    void checkPillarMaterials() {
        if (this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() instanceof BlockPillar && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() instanceof BlockPillar) {
            if (this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarAncient && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarAncient && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarAncient && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarAncient) {
                this.cycleTime -= TTConfig.matrix.allow ? TTConfig.matrix.ancientSpeedUp : 1;
                this.costMult -= TTConfig.matrix.allow ? TTConfig.matrix.ancientDiscount : 0.1;
                this.stabilityReplenish -= 0.1F;
            }

            if (this.world.getBlockState(this.pos.add(-1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && this.world.getBlockState(this.pos.add(1, -2, -1)).getBlock() == BlocksTC.pillarEldritch && this.world.getBlockState(this.pos.add(1, -2, 1)).getBlock() == BlocksTC.pillarEldritch && this.world.getBlockState(this.pos.add(-1, -2, 1)).getBlock() == BlocksTC.pillarEldritch) {
                this.cycleTime -= TTConfig.matrix.allow ? TTConfig.matrix.eldritchSpeedUp : 3;
                this.costMult += TTConfig.matrix.allow ? TTConfig.matrix.eldritchIncrease : 0.05;
                this.stabilityReplenish += 0.2F;
            }
        }
    }

    void checkBoostStones() {
        double dCycleTime = 0;
        int[] xm = new int[]{-1, 1, 1, -1};
        int[] zm = new int[]{-1, -1, 1, 1};
        for(int a = 0; a < 4; ++a) {
            Block b = this.world.getBlockState(this.pos.add(xm[a], -3, zm[a])).getBlock();
            if (b == BlocksTC.matrixSpeed) {
                dCycleTime -= TTConfig.matrix.allow ? TTConfig.matrix.stoneSpeedSpeed : 1;
                this.costMult += TTConfig.matrix.allow ? TTConfig.matrix.stoneSpeedCost : 0.01;
            }
            if (b == BlocksTC.matrixCost) {
                dCycleTime += TTConfig.matrix.allow ? TTConfig.matrix.stoneSlowSpeed : 1;
                this.costMult -= TTConfig.matrix.allow ? TTConfig.matrix.stoneSlowCost : 0.02;
            }
        }
        this.cycleTime += dCycleTime;
    }

    void checkPedestalMaterials() {
        for (BlockPos pedPos : this.pedestals) {
            int x = this.pos.getX() - pedPos.getX();
            int z = this.pos.getZ() - pedPos.getZ();
            Block block = this.world.getBlockState(pedPos).getBlock();
            if (block == BlocksTC.pedestalEldritch) {
                this.costMult -= TTConfig.matrix.allow ? TTConfig.matrix.pedestalEldritchCost : 0.0025;
            }

            if (block == BlocksTC.pedestalAncient) {
                this.costMult -= TTConfig.matrix.allow ? TTConfig.matrix.pedestalAncientCost : 0.01;
            }
        }
    }


    @Shadow
    private float calcDeminishingReturns(Block sb1, float amt1) { return 0; }
    @Shadow
    private ArrayList<BlockPos> pedestals;
    @Shadow
    HashMap<Block, Integer> tempBlockCount;
    @Shadow
    private ArrayList<BlockPos> problemBlocks;
    @Shadow
    private int cycleTime;
    @Shadow
    public float stabilityReplenish;
    @Shadow
    public float costMult;
    @Shadow
    private int countDelay;

}
