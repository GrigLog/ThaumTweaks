package griglog.thaumtweaks.mixins.entities;

import griglog.thaumtweaks.TTConfig;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.Thaumcraft;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigEntities;
import thaumcraft.common.config.ModConfig;
import thaumcraft.common.entities.EntityFallingTaint;
import thaumcraft.common.entities.EntityFluxRift;
import thaumcraft.common.entities.EntityFollowingItem;
import thaumcraft.common.entities.EntitySpecialItem;
import thaumcraft.common.entities.construct.EntityArcaneBore;
import thaumcraft.common.entities.construct.EntityTurretCrossbow;
import thaumcraft.common.entities.construct.EntityTurretCrossbowAdvanced;
import thaumcraft.common.entities.monster.*;
import thaumcraft.common.entities.monster.boss.*;
import thaumcraft.common.entities.monster.cult.EntityCultistCleric;
import thaumcraft.common.entities.monster.cult.EntityCultistKnight;
import thaumcraft.common.entities.monster.cult.EntityCultistPortalLesser;
import thaumcraft.common.entities.monster.tainted.*;
import thaumcraft.common.entities.projectile.*;
import thaumcraft.common.golems.EntityThaumcraftGolem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Mixin(ConfigEntities.class)
public abstract class EntityConfigMixin {

    @Inject(method = "initEntities", at = @At("HEAD"), cancellable = true, remap=false)
    // pech trades' re-prioritization
    private static void initEntitiesHandler(IForgeRegistry<EntityEntry> iForgeRegistry, CallbackInfo ci) {
        if (!TTConfig.general.pechs)
            return;
        int id = 0;
        ResourceLocation var10000 = new ResourceLocation("thaumcraft", "CultistPortalGreater");
        int var6 = id + 1;
        EntityRegistry.registerModEntity(var10000, EntityCultistPortalGreater.class, "CultistPortalGreater", id, Thaumcraft.instance, 64, 20, false, 6842578, 32896);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistPortalLesser"), EntityCultistPortalLesser.class, "CultistPortalLesser", var6++, Thaumcraft.instance, 64, 20, false, 9438728, 6316242);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FluxRift"), EntityFluxRift.class, "FluxRift", var6++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "SpecialItem"), EntitySpecialItem.class, "SpecialItem", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FollowItem"), EntityFollowingItem.class, "FollowItem", var6++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FallingTaint"), EntityFallingTaint.class, "FallingTaint", var6++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Alumentum"), EntityAlumentum.class, "Alumentum", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GolemDart"), EntityGolemDart.class, "GolemDart", var6++, Thaumcraft.instance, 64, 20, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchOrb"), EntityEldritchOrb.class, "EldritchOrb", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "BottleTaint"), EntityBottleTaint.class, "BottleTaint", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GolemOrb"), EntityGolemOrb.class, "GolemOrb", var6++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Grapple"), EntityGrapple.class, "Grapple", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CausalityCollapser"), EntityCausalityCollapser.class, "CausalityCollapser", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FocusProjectile"), EntityFocusProjectile.class, "FocusProjectile", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "FocusCloud"), EntityFocusCloud.class, "FocusCloud", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Focusmine"), EntityFocusMine.class, "Focusmine", var6++, Thaumcraft.instance, 64, 20, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TurretBasic"), EntityTurretCrossbow.class, "TurretBasic", var6++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TurretAdvanced"), EntityTurretCrossbowAdvanced.class, "TurretAdvanced", var6++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "ArcaneBore"), EntityArcaneBore.class, "ArcaneBore", var6++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Golem"), EntityThaumcraftGolem.class, "Golem", var6++, Thaumcraft.instance, 64, 3, true);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchWarden"), EntityEldritchWarden.class, "EldritchWarden", var6++, Thaumcraft.instance, 64, 3, true, 6842578, 8421504);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchGolem"), EntityEldritchGolem.class, "EldritchGolem", var6++, Thaumcraft.instance, 64, 3, true, 6842578, 8947848);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistLeader"), EntityCultistLeader.class, "CultistLeader", var6++, Thaumcraft.instance, 64, 3, true, 6842578, 9438728);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintacleGiant"), EntityTaintacleGiant.class, "TaintacleGiant", var6++, Thaumcraft.instance, 96, 3, false, 6842578, 10618530);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "BrainyZombie"), EntityBrainyZombie.class, "BrainyZombie", var6++, Thaumcraft.instance, 64, 3, true, -16129, -16744448);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "GiantBrainyZombie"), EntityGiantBrainyZombie.class, "GiantBrainyZombie", var6++, Thaumcraft.instance, 64, 3, true, -16129, -16760832);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Wisp"), EntityWisp.class, "Wisp", var6++, Thaumcraft.instance, 64, 3, false, -16129, -1);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Firebat"), EntityFireBat.class, "Firebat", var6++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Spellbat"), EntitySpellBat.class, "Spellbat", var6++, Thaumcraft.instance, 64, 3, false, -16129, -806354944);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Pech"), EntityPech.class, "Pech", var6++, Thaumcraft.instance, 64, 3, true, -16129, -12582848);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "MindSpider"), EntityMindSpider.class, "MindSpider", var6++, Thaumcraft.instance, 64, 3, true, 4996656, 4473924);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchGuardian"), EntityEldritchGuardian.class, "EldritchGuardian", var6++, Thaumcraft.instance, 64, 3, true, 8421504, 0);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistKnight"), EntityCultistKnight.class, "CultistKnight", var6++, Thaumcraft.instance, 64, 3, true, 9438728, 128);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "CultistCleric"), EntityCultistCleric.class, "CultistCleric", var6++, Thaumcraft.instance, 64, 3, true, 9438728, 8388608);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "EldritchCrab"), EntityEldritchCrab.class, "EldritchCrab", var6++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "InhabitedZombie"), EntityInhabitedZombie.class, "InhabitedZombie", var6++, Thaumcraft.instance, 64, 3, true, 8421504, 5570560);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "ThaumSlime"), EntityThaumicSlime.class, "ThaumSlime", var6++, Thaumcraft.instance, 64, 3, true, 10618530, -32513);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintCrawler"), EntityTaintCrawler.class, "TaintCrawler", var6++, Thaumcraft.instance, 64, 3, true, 10618530, 3158064);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "Taintacle"), EntityTaintacle.class, "Taintacle", var6++, Thaumcraft.instance, 64, 3, false, 10618530, 4469572);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintacleTiny"), EntityTaintacleSmall.class, "TaintacleTiny", var6++, Thaumcraft.instance, 64, 3, false);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSwarm"), EntityTaintSwarm.class, "TaintSwarm", var6++, Thaumcraft.instance, 64, 3, false, 10618530, 16744576);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSeed"), EntityTaintSeed.class, "TaintSeed", var6++, Thaumcraft.instance, 64, 20, false, 10618530, 4465237);
        EntityRegistry.registerModEntity(new ResourceLocation("thaumcraft", "TaintSeedPrime"), EntityTaintSeedPrime.class, "TaintSeedPrime", var6++, Thaumcraft.instance, 64, 20, false, 10618530, 5583718);
        EntityPech.valuedItems.put(Item.getIdFromItem(Items.ENDER_PEARL), 15);
        ArrayList<List> forInv = new ArrayList();
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 0)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 1)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 6)));
        forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 7)));
        if (ModConfig.foundCopperIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 2)));
        }

        if (ModConfig.foundTinIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 3)));
        }

        if (ModConfig.foundSilverIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 4)));
        }

        if (ModConfig.foundLeadIngot) {
            forInv.add(Arrays.asList(1, new ItemStack(ItemsTC.clusters, 1, 5)));
        }

        forInv.add(Arrays.asList(2, new ItemStack(BlocksTC.saplingGreatwood)));
        forInv.add(Arrays.asList(2, new ItemStack(ItemsTC.thaumiumPick)));
        forInv.add(Arrays.asList(2, new ItemStack(ItemsTC.thaumiumAxe)));
        forInv.add(Arrays.asList(2, new ItemStack(ItemsTC.thaumiumHoe)));
        forInv.add(Arrays.asList(2, new ItemStack(Items.DRAGON_BREATH)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.BLAZE_ROD)));
        forInv.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forInv.add(Arrays.asList(3, new ItemStack(ItemsTC.curio, 1, 4)));
        forInv.add(Arrays.asList(4, new ItemStack(BlocksTC.saplingSilverwood)));
        forInv.add(Arrays.asList(4, new ItemStack(ItemsTC.curio, 1, 4)));
        forInv.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        EntityPech.tradeInventory.put(0, forInv);
        ArrayList<List> forMag = new ArrayList();
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.AIR)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.EARTH)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.FIRE)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.WATER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ORDER)));
        forMag.add(Arrays.asList(1, ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTIONITEM, 1, 8193)));
        forMag.add(Arrays.asList(2, new ItemStack(Items.POTIONITEM, 1, 8261)));
        forMag.add(Arrays.asList(2, ThaumcraftApiHelper.makeCrystal(Aspect.FLUX)));
        forMag.add(Arrays.asList(2, ThaumcraftApiHelper.makeCrystal(Aspect.AURA)));
        forMag.add(Arrays.asList(2, new ItemStack(ItemsTC.clothBoots)));
        forMag.add(Arrays.asList(2, new ItemStack(ItemsTC.clothChest)));
        forMag.add(Arrays.asList(2, new ItemStack(ItemsTC.clothLegs)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forMag.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forMag.add(Arrays.asList(3, new ItemStack(ItemsTC.curio, 1, 4)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.amuletVis, 1, 0)));
        forMag.add(Arrays.asList(4, new ItemStack(ItemsTC.curio, 1, 4)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        forMag.add(Arrays.asList(5, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forMag.add(Arrays.asList(5, new ItemStack(ItemsTC.pechWand)));
        forInv.add(Arrays.asList(5, new ItemStack(Items.TOTEM_OF_UNDYING)));
        EntityPech.tradeInventory.put(1, forMag);
        ArrayList<List> forArc = new ArrayList();

        for(int a = 0; a < 15; ++a) {
            forArc.add(Arrays.asList(1, new ItemStack(BlocksTC.candles.get(EnumDyeColor.byDyeDamage(a)))));
        }

        forInv.add(Arrays.asList(2, new ItemStack(Items.COMPASS)));
        forArc.add(Arrays.asList(2, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.POWER, 1))));
        forInv.add(Arrays.asList(3, new ItemStack(Items.SPECTRAL_ARROW)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.EXPERIENCE_BOTTLE)));
        forArc.add(Arrays.asList(3, new ItemStack(Items.GOLDEN_APPLE, 1, 0)));
        forArc.add(Arrays.asList(3, new ItemStack(ItemsTC.curio, 1, 4)));
        forArc.add(Arrays.asList(4, new ItemStack(ItemsTC.eldritchEye)));
        forArc.add(Arrays.asList(4, new ItemStack(Items.GOLDEN_APPLE, 1, 1)));
        forArc.add(Arrays.asList(4, new ItemStack(Items.GHAST_TEAR)));
        forArc.add(Arrays.asList(4, new ItemStack(ItemsTC.baubles, 1, 3)));
        forArc.add(Arrays.asList(4, new ItemStack(ItemsTC.curio, 1, 4)));
        forArc.add(Arrays.asList(5, new ItemStack(ItemsTC.curio, 1, 4)));
        forArc.add(Arrays.asList(5, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.FLAME, 1))));
        forArc.add(Arrays.asList(5, ItemEnchantedBook.getEnchantedItemStack(new EnchantmentData(Enchantments.INFINITY, 1))));
        EntityPech.tradeInventory.put(2, forArc);


        ci.cancel();
    }
}
