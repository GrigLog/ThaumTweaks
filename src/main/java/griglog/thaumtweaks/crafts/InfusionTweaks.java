package griglog.thaumtweaks.crafts;

import griglog.thaumtweaks.SF;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.ThaumcraftApiHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.blocks.BlocksTC;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.ConfigItems;
import thaumcraft.common.lib.enchantment.EnumInfusionEnchantment;

import java.util.HashMap;

public class InfusionTweaks {
    public static void override() {
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskAngryGhost"),
                new InfusionRecipe("FORTRESSMASK", new Object[]{"mask", new NBTTagInt(1)}, 8,
                        (new AspectList())
                                .add(Aspect.ENTROPY, 80)
                                .add(Aspect.DEATH, 80)
                                .add(Aspect.PROTECT, 20),
                        new ItemStack(ItemsTC.fortressHelm, 1, 32767),
                        "leather",
                        "plateIron",
                        new ItemStack(Items.SKULL, 1, 1),
                        "plateIron"));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskSippingFiend"),
                new InfusionRecipe("FORTRESSMASK", new Object[]{"mask", new NBTTagInt(2)}, 8,
                        (new AspectList())
                                .add(Aspect.UNDEAD, 80)
                                .add(Aspect.LIFE, 80)
                                .add(Aspect.PROTECT, 20),
                        new ItemStack(ItemsTC.fortressHelm, 1, 32767),
                        "leather",
                        "plateIron",
                        new ItemStack(Items.GHAST_TEAR),
                        "plateIron"));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MaskGrinningDevil"),
                new InfusionRecipe("FORTRESSMASK", new Object[]{"mask", new NBTTagInt(0)}, 8,
                        (new AspectList())
                                .add(Aspect.MIND, 80)
                                .add(Aspect.LIFE, 80)
                                .add(Aspect.PROTECT, 20),
                        new ItemStack(ItemsTC.fortressHelm, 1, 32767),
                        "leather",
                        "plateIron",
                        new ItemStack(BlocksTC.shimmerleaf),
                        "plateIron"));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:PrimalCrusher"),
                new InfusionRecipe("PRIMALCRUSHER", new ItemStack(ItemsTC.primalCrusher), 6,
                        (new AspectList())
                                .add(Aspect.TOOL, 100)
                                .add(Aspect.ENTROPY, 150)
                                .add(Aspect.VOID, 150)
                                .add(Aspect.ELDRITCH, 200),
                        Ingredient.fromItem(ItemsTC.primordialPearl),
                        Ingredient.fromItem(ItemsTC.voidPick),
                        Ingredient.fromItem(ItemsTC.voidShovel),
                        Ingredient.fromItem(ItemsTC.elementalPick),
                        Ingredient.fromItem(ItemsTC.elementalShovel)));


        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidSiphon"),
                new InfusionRecipe("VOIDSIPHON", new ItemStack(BlocksTC.voidSiphon), 7,
                        (new AspectList())
                                .add(Aspect.ELDRITCH, 90)
                                .add(Aspect.ENTROPY, 90)
                                .add(Aspect.VOID, 150),
                        new ItemStack(BlocksTC.metalBlockVoid),
                        new ItemStack(ItemsTC.mechanismComplex),
                        "plateBrass",
                        new ItemStack(Items.NETHER_STAR),
                        "plateBrass"));


        NBTTagCompound nbt2 = new NBTTagCompound();
        nbt2.setByte("type", (byte)2);
        ItemStack charm2 = new ItemStack(ItemsTC.charmVerdant).copy();
        charm2.setTagCompound(nbt2);

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeHelm"),
                new InfusionRecipe("VOIDROBEARMOR", new ItemStack(ItemsTC.voidRobeHelm), 6,
                        (new AspectList())
                                .add(Aspect.PROTECT, 75)
                                .add(Aspect.ENERGY, 50)
                                .add(Aspect.ELDRITCH, 100)
                                .add(Aspect.VOID, 100),
                        new ItemStack(ItemsTC.voidHelm),
                        new ItemStack(ItemsTC.goggles, 1, 32767),
                        "plateVoid",
                        "plateVoid",
                        new ItemStack(ItemsTC.charmVerdant),
                        new ItemStack(ItemsTC.fabric),
                        "leather"));


        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setByte("type", (byte)1);
        ItemStack charm = new ItemStack(ItemsTC.charmVerdant).copy();
        charm.setTagCompound(nbt);

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeChest"),
                new InfusionRecipe("VOIDROBEARMOR",
                        new ItemStack(ItemsTC.voidRobeChest), 6,
                        (new AspectList())
                            .add(Aspect.PROTECT, 90)
                            .add(Aspect.ENERGY, 60)
                            .add(Aspect.ELDRITCH, 120)
                            .add(Aspect.VOID, 120),
                        new ItemStack(ItemsTC.voidChest),
                        new ItemStack(ItemsTC.clothChest),
                        "plateVoid",
                        "plateVoid",
                        charm,
                        new ItemStack(ItemsTC.fabric),
                        "leather"));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VoidRobeLegs"),
                new InfusionRecipe("VOIDROBEARMOR",
                        new ItemStack(ItemsTC.voidRobeLegs), 6,
                        (new AspectList())
                                .add(Aspect.PROTECT, 75)
                                .add(Aspect.ENERGY, 50)
                                .add(Aspect.ELDRITCH, 100)
                                .add(Aspect.VOID, 100),
                        new ItemStack(ItemsTC.voidLegs),
                        new ItemStack(ItemsTC.clothLegs),
                        "plateVoid",
                        "plateVoid",
                        new ItemStack(ItemsTC.charmVerdant),
                        new ItemStack(ItemsTC.fabric),
                        "leather"));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeart"),
                new InfusionRecipe("VERDANTCHARMS", new ItemStack(ItemsTC.charmVerdant), 5,
                        (new AspectList())
                                .add(Aspect.LIFE, 60)
                                .add(Aspect.PLANT, 60),
                        new ItemStack(ItemsTC.baubles, 1, 4),
                        new ItemStack(ItemsTC.nuggets, 1, 10),
                        ThaumcraftApiHelper.makeCrystal(Aspect.LIFE),
                        new ItemStack(Items.MILK_BUCKET),
                        ThaumcraftApiHelper.makeCrystal(Aspect.PLANT)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartLife"),
                new InfusionRecipe("VERDANTCHARMS", new Object[]{"type", new NBTTagByte((byte)1)}, 5,
                        (new AspectList())
                                .add(Aspect.LIFE, 80)
                                .add(Aspect.MAN, 80),
                        new ItemStack(ItemsTC.charmVerdant),
                        new ItemStack(Items.GOLDEN_APPLE),
                        ThaumcraftApiHelper.makeCrystal(Aspect.LIFE),
                        ThaumcraftApiHelper.makeCrystal(Aspect.MAN)));


        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VerdantHeartSustain"),
                new InfusionRecipe("VERDANTCHARMS", new Object[]{"type", new NBTTagByte((byte)2)}, 5,
                        (new AspectList())
                                .add(Aspect.DESIRE, 80)
                                .add(Aspect.MAN, 80),
                        new ItemStack(ItemsTC.charmVerdant),
                        new ItemStack(Items.GOLDEN_CARROT),
                        ThaumcraftApiHelper.makeCrystal(Aspect.DESIRE),
                        ThaumcraftApiHelper.makeCrystal(Aspect.MAN)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:BootsTraveller"),
                new InfusionRecipe("BOOTSTRAVELLER", new ItemStack(ItemsTC.travellerBoots), 1,
                        (new AspectList())
                                .add(Aspect.FLIGHT, 50)
                                .add(Aspect.MOTION, 50),
                        new ItemStack(Items.LEATHER_BOOTS, 1, 32767),
                        new ItemStack(ItemsTC.fabric),
                        new ItemStack(ItemsTC.fabric),
                        new ItemStack(Items.FEATHER),
                        new ItemStack(Items.FISH, 1, 32767)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampGrowth"),
                new InfusionRecipe("LAMPGROWTH", new ItemStack(BlocksTC.lampGrowth), 4,
                        (new AspectList())
                                .add(Aspect.PLANT, 20)
                                .add(Aspect.LIGHT, 15)
                                .add(Aspect.LIFE, 20),
                        new ItemStack(BlocksTC.lampArcane),
                        new ItemStack(Items.GOLD_INGOT),
                        ConfigItems.EARTH_CRYSTAL,
                        new ItemStack(Items.GOLD_INGOT),
                        ConfigItems.EARTH_CRYSTAL));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:LampFertility"),
                new InfusionRecipe("LAMPFERTILITY", new ItemStack(BlocksTC.lampFertility), 4,
                        (new AspectList())
                                .add(Aspect.BEAST, 20)
                                .add(Aspect.LIGHT, 15)
                                .add(Aspect.LIFE, 20),
                        new ItemStack(BlocksTC.lampArcane),
                        new ItemStack(Items.GOLD_INGOT),
                        ConfigItems.FIRE_CRYSTAL,
                        new ItemStack(Items.GOLD_INGOT),
                        ConfigItems.FIRE_CRYSTAL));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:Mirror"),
                new InfusionRecipe("MIRROR", new ItemStack(BlocksTC.mirror), 1,
                        (new AspectList())
                                .add(Aspect.MOTION, 25)
                                .add(Aspect.DARKNESS, 25),
                        new ItemStack(ItemsTC.mirroredGlass),
                        "ingotGold",
                        new ItemStack(Items.ENDER_PEARL),
                        new ItemStack(Items.ENDER_PEARL),
                        "ingotGold",
                        new ItemStack(Items.ENDER_PEARL),
                        new ItemStack(Items.ENDER_PEARL)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorHand"),
                new InfusionRecipe("MIRRORHAND", new ItemStack(ItemsTC.handMirror), 5,
                        (new AspectList())
                                .add(Aspect.TOOL, 40)
                                .add(Aspect.MOTION, 60),
                        new ItemStack(BlocksTC.mirror),
                        new ItemStack(Items.COMPASS),
                        "ingotBrass",
                        "stickWood",
                        "ingotBrass"
                       ));
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:MirrorEssentia"),
                new InfusionRecipe("MIRRORESSENTIA", new ItemStack(BlocksTC.mirrorEssentia), 2,
                        (new AspectList())
                                .add(Aspect.MOTION, 25)
                                .add(Aspect.WATER, 25),
                        new ItemStack(ItemsTC.mirroredGlass),
                        "ingotIron",
                        new ItemStack(Items.ENDER_PEARL),
                        new ItemStack(Items.ENDER_PEARL),
                        "ingotIron",
                        new ItemStack(Items.ENDER_PEARL),
                        new ItemStack(Items.ENDER_PEARL)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ArcaneBore"),
                new InfusionRecipe("ARCANEBORE", new ItemStack(ItemsTC.turretPlacer, 1, 2), 4,
                        (new AspectList())
                                .add(Aspect.ENERGY, 50)
                                .add(Aspect.MECHANISM, 100)
                                .add(Aspect.VOID, 40)
                                .add(Aspect.MOTION, 70),
                        new ItemStack(ItemsTC.turretPlacer),
                        new ItemStack(ItemsTC.mechanismComplex),
                        new ItemStack(BlocksTC.logGreatwood),
                        new ItemStack(ItemsTC.morphicResonator),
                        "plateBrass",
                        Ingredient.fromItem(Items.DIAMOND_PICKAXE),
                        "plateBrass",
                        new ItemStack(ItemsTC.morphicResonator),
                        new ItemStack(BlocksTC.logGreatwood)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:VisAmulet"),
                new InfusionRecipe("VISAMULET",
                        new ItemStack(ItemsTC.amuletVis, 1, 1), 6,
                        (new AspectList())
                                .add(Aspect.AURA, 150)
                                .add(Aspect.ENERGY, 150)
                                .add(Aspect.MAGIC, 150),
                        new ItemStack(ItemsTC.baubles, 1, 0),
                        new ItemStack(ItemsTC.visResonator),
                        ThaumcraftApiHelper.makeCrystal(Aspect.AIR),
                        ThaumcraftApiHelper.makeCrystal(Aspect.FIRE),
                        ThaumcraftApiHelper.makeCrystal(Aspect.WATER),
                        ThaumcraftApiHelper.makeCrystal(Aspect.EARTH),
                        ThaumcraftApiHelper.makeCrystal(Aspect.ORDER),
                        ThaumcraftApiHelper.makeCrystal(Aspect.ENTROPY)));

        ItemStack in = new ItemStack(ItemsTC.elementalPick);
        EnumInfusionEnchantment.addInfusionEnchantment(in, EnumInfusionEnchantment.REFINING, 1);
        EnumInfusionEnchantment.addInfusionEnchantment(in, EnumInfusionEnchantment.SOUNDING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalPick"),
                new InfusionRecipe("ELEMENTALTOOLS", in, 1,
                        (new AspectList())
                                .add(Aspect.FIRE, 60)
                                .add(Aspect.SENSES, 30),
                        new ItemStack(ItemsTC.thaumiumPick, 1, 32767),
                        ConfigItems.FIRE_CRYSTAL,
                        ConfigItems.FIRE_CRYSTAL,
                        new ItemStack(ItemsTC.nuggets, 1, 10),
                        new ItemStack(BlocksTC.plankGreatwood)));

        ItemStack isESW = new ItemStack(ItemsTC.elementalSword);
        EnumInfusionEnchantment.addInfusionEnchantment(isESW, EnumInfusionEnchantment.ARCING, 2);
        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalSword"),
                new InfusionRecipe("ELEMENTALTOOLS", isESW, 1,
                        (new AspectList())
                                .add(Aspect.AIR, 60)
                                .add(Aspect.AVERSION, 30),
                        new ItemStack(ItemsTC.thaumiumSword, 1, 32767),
                        ConfigItems.AIR_CRYSTAL,
                        ConfigItems.AIR_CRYSTAL,
                        new ItemStack(ItemsTC.nuggets, 1, 10),
                        new ItemStack(BlocksTC.plankGreatwood)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:ElementalHoe"),
                new InfusionRecipe("ELEMENTALTOOLS",
                        new ItemStack(ItemsTC.elementalHoe), 1,
                        (new AspectList())
                                .add(Aspect.ORDER, 30)
                                .add(Aspect.PLANT, 60),
                        new ItemStack(ItemsTC.thaumiumHoe, 1, 32767),
                        ConfigItems.ORDER_CRYSTAL,
                        ConfigItems.ORDER_CRYSTAL,
                        new ItemStack(ItemsTC.nuggets, 1, 10),
                        new ItemStack(BlocksTC.plankGreatwood)));

        ThaumcraftApi.addInfusionCraftingRecipe(new ResourceLocation("thaumcraft:voidingot"),
                new InfusionRecipe("BASEELDRITCH",
                        new ItemStack(ItemsTC.ingots, 1, 1), 4,
                        (new AspectList())
                                .add(Aspect.FLUX, 20)
                                .add(Aspect.DARKNESS, 40)
                                .add(Aspect.VOID, 20),
                        new ItemStack(Items.GOLD_INGOT),
                        new ItemStack(ItemsTC.salisMundus),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.morphicResonator),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.salisMundus),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.morphicResonator),
                        new ItemStack(ItemsTC.voidSeed),
                        new ItemStack(ItemsTC.voidSeed)
                ));


    }
}

