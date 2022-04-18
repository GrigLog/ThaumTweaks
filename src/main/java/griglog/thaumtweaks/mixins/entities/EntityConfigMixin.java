package griglog.thaumtweaks.mixins.entities;

import griglog.thaumtweaks.SF;
import griglog.thaumtweaks.TTConfig;
import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import thaumcraft.Thaumcraft;

import thaumcraft.common.config.ConfigEntities;
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

import java.util.*;

@Mixin(ConfigEntities.class)
public abstract class EntityConfigMixin {

    @Inject(method = "initEntities", at = @At("HEAD"), cancellable = true, remap=false)
    private static void initEntitiesHandler(IForgeRegistry<EntityEntry> iForgeRegistry, CallbackInfo ci) {
        if (!TTConfig.general.pechs)
            return;
        otherEntitiesStuff();
        EntityPech.valuedItems.put(Item.getIdFromItem(Items.ENDER_PEARL), 15);
        List<String> file = SF.readFile("config/thaumtweaks_pech_trades.txt", TWEAKED_TRADES);
        HashMap<String, ArrayList<List>> trades = new HashMap<>();
        trades.put("MINER", new ArrayList<>());
        trades.put("MAGE", new ArrayList<>());
        trades.put("ARCHER", new ArrayList<>());
        String pechType = null;
        int tier = -1;
        for (String line : file){
            if (line.trim().equals(""))
                continue;
            if (line.startsWith("#"))
                break;
            if (line.equals("MINER:") || line.equals("MAGE:") || line.equals("ARCHER:") || line.equals("COMMON:"))
                pechType = line.substring(0, line.length()-1);
            else if (line.endsWith(":"))
                tier = Integer.parseInt((line.substring(line.length()-2, line.length()-1)));
            else {
                line = line.split("//")[0].trim();
                String[] parts = line.split(" ");
                String id = parts[0];
                String tag = "";
                int amount = 1;
                if (parts.length >= 2) {
                    try {
                        amount = Integer.parseInt(parts[parts.length - 1]);
                        tag = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length - 1));
                    } catch (NumberFormatException e) {
                        tag = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length));
                    }
                }
                String[] idParts = id.split(":");
                int meta = -1;
                if (idParts.length == 3) {
                    meta = Integer.parseInt(idParts[2]);
                    id = String.join(":", Arrays.copyOfRange(idParts, 0, 2));
                }
                Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(id));
                if (item == null) {
                    ThaumTweaks.LOGGER.error(String.format("Item \"%s\" does not exist!", id));
                    throw new NullPointerException();
                }
                ItemStack is = (meta == -1 ? new ItemStack(item) : new ItemStack(item, 1, meta));
                if (!tag.equals("")) {
                    try {
                        is.setTagCompound(JsonToNBT.getTagFromJson(tag));
                    } catch (NBTException e) {
                        e.printStackTrace();
                    }
                }
                //ThaumTweaks.LOGGER.info("type:" + pechType + ",tier:" + tier + ",item:" + item.getRegistryName() + ", amount:" + amount + ", tag:" + tag);
                for (int i = 0; i < amount; i++) {
                    if (pechType.equals("COMMON")) {
                        trades.get("MINER").add(Arrays.asList(tier, is));
                        trades.get("MAGE").add(Arrays.asList(tier, is));
                        trades.get("ARCHER").add(Arrays.asList(tier, is));
                    } else
                        trades.get(pechType).add(Arrays.asList(tier, is));
                }
            }
        }
        EntityPech.tradeInventory.put(0, trades.get("MINER"));
        EntityPech.tradeInventory.put(1, trades.get("MAGE"));
        EntityPech.tradeInventory.put(2, trades.get("ARCHER"));

        ci.cancel();
    }

    private static final String TWEAKED_TRADES = "MINER:\n" +
        "\t1:\n" +
        "\tthaumcraft:cluster:0 //iron\n" +
        "\tthaumcraft:cluster:1 //gold\n" +
        "\tthaumcraft:cluster:2 //copper\n" +
        "\tthaumcraft:cluster:3 //tin\n" +
        "\tthaumcraft:cluster:4 //silver\n" +
        "\tthaumcraft:cluster:5 //lead\n" +
        "\tthaumcraft:cluster:6 //cinnabar\n" +
        "\tthaumcraft:cluster:7 //quartz\n" +
        "\t2:\n" +
        "\tthaumcraft:sapling_greatwood\n" +
        "\tthaumcraft:thaumium_pick\n" +
        "\tthaumcraft:thaumium_axe\n" +
        "\tthaumcraft:thaumium_hoe\n" +
        "\tminecraft:compass\n" +
        "\t3:\n" +
        "\tminecraft:blaze_rod\n" +
        "\t4:\n" +
        "\tthaumcraft:sapling_silverwood\n" +
        "\t5:\n" +
        "\tminecraft:totem_of_undying\n" +
        "MAGE:\n" +
        "\t1:\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"aer\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"terra\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"ignis\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"aqua\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"perditio\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"ordo\"}]}\n" +
        "\t2:\n" +
        "\tminecraft:potion {Potion:\"minecraft:regeneration\"} //vanilla TC uses 1.8 metadata numbers, resulting in \"uncraftable potions\"\n" +
        "\tminecraft:potion {Potion:\"minecraft:healing\"}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"vitium\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"auram\"}]}\n" +
        "\tthaumcraft:cloth_chest\n" +
        "\tthaumcraft:cloth_legs\n" +
        "\tthaumcraft:cloth_boots\n" +
        "\t3:\n" +
        "\t4:\n" +
        "\tthaumcraft:amulet_vis:0\n" +
        "\t5:\n" +
        "\tthaumcraft:pech_wand\n" +
        "ARCHER:\n" +
        "\t1:\n" +
        "\tthaumcraft:candle_white\n" +
        "\tthaumcraft:candle_orange\n" +
        "\tthaumcraft:candle_magenta\n" +
        "\tthaumcraft:candle_lightblue\n" +
        "\tthaumcraft:candle_yellow\n" +
        "\tthaumcraft:candle_lime\n" +
        "\tthaumcraft:candle_pink\n" +
        "\tthaumcraft:candle_gray\n" +
        "\tthaumcraft:candle_silver\n" +
        "\tthaumcraft:candle_cyan\n" +
        "\tthaumcraft:candle_purple\n" +
        "\tthaumcraft:candle_blue\n" +
        "\tthaumcraft:candle_brown\n" +
        "\tthaumcraft:candle_green\n" +
        "\tthaumcraft:candle_red\n" +
        "\tthaumcraft:candle_black\n" +
        "\t2:\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 1s, id: 50s}]} //Flame\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 3s, id: 48s}]} //Powers\n" +
        "\t3:\n" +
        "\tminecraft:dragon_breath\n" +
        "\t4:\n" +
        "\tminecraft:ghast_tear\n" +
        "\tthaumcraft:baubles:3 //Apprentice Ring\n" +
        "\t5:\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 1s, id: 51s}]} //Infinity\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 5s, id: 48s}]} //Power\n" +
        "COMMON:\n" +
        "\t3:\n" +
        "\tminecraft:experience_bottle 5\n" +
        "\tminecraft:golden_apple:0 2\n" +
        "\tthaumcraft:curio:4\n" +
        "\t4:\n" +
        "\tthaumcraft:curio:4 3\n" +
        "\t5:\n" +
        "\tminecraft:golden_apple:1\n" +
        "\tthaumcraft:curio:4 5\n" +
        "\n" +
        "#parsing ends with a line starting with #, everything below is a comment. \n" +
        "The format of this file is quite obvious, <item> [tag] [weight]. I just want to specify that \n" +
        "1) its not recommended to make weight too high as it just multiplies the same entry several times\n" +
        "2) rarity can be from 1 to 5 and nothing else.\n" +
        "I have placed vanilla TC pech trades here, for the reference.\n" +
        "\n" +
        "\n" +
        "\n" +
        "\n" +
        "MINER:\n" +
        "\t1:\n" +
        "\tthaumcraft:cluster:0 //iron\n" +
        "\tthaumcraft:cluster:1 //gold\n" +
        "\tthaumcraft:cluster:2 //copper\n" +
        "\tthaumcraft:cluster:3 //tin\n" +
        "\tthaumcraft:cluster:4 //silver\n" +
        "\tthaumcraft:cluster:5 //lead\n" +
        "\tthaumcraft:cluster:6 //cinnabar\n" +
        "\tthaumcraft:cluster:7 //quartz\n" +
        "\t2:\n" +
        "\tminecraft:blaze_rod\n" +
        "\tminecraft:dragon_breath\n" +
        "\tminecraft:compass\n" +
        "\tthaumcraft:sapling_greatwood\t\n" +
        "\t3:\n" +
        "\tminecraft:experience_bottle 2\n" +
        "\tminecraft:golden_apple:0\n" +
        "\t4:\n" +
        "\tminecraft:spectral_arrow\n" +
        "\tthaumcraft:thaumium_pick\n" +
        "\tthaumcraft:thaumium_axe\n" +
        "\tthaumcraft:thaumium_hoe\n" +
        "\t5:\n" +
        "\tminecraft:golden_apple:1\n" +
        "\tthaumcraft:curio:4\t\n" +
        "\tminecraft:totem_of_undying\n" +
        "MAGE:\n" +
        "\t1:\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"aer\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"terra\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"ignis\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"aqua\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"perditio\"}]}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"ordo\"}]}\n" +
        "\t2:\n" +
        "\tminecraft:potion {Potion:\"minecraft:regeneration\"} //vanilla TC uses 1.8 metadata numbers, resulting in \"uncraftable potions\"\n" +
        "\tminecraft:potion {Potion:\"minecraft:healing\"}\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"vitium\"}]}\n" +
        "\t3:\n" +
        "\tminecraft:experience_bottle 2\n" +
        "\tminecraft:golden_apple:0\n" +
        "\tthaumcraft:crystal_essence {Aspects:[{amount:1,key:\"auram\"}]}\n" +
        "\t4:\n" +
        "\tthaumcraft:cloth_chest\n" +
        "\tthaumcraft:cloth_legs\n" +
        "\tthaumcraft:cloth_boots\n" +
        "\t5:\n" +
        "\tminecraft:golden_apple:1\n" +
        "\tthaumcraft:curio:4\n" +
        "\tthaumcraft:pech_wand\n" +
        "\tthaumcraft:amulet_vis:0\n" +
        "ARCHER:\n" +
        "\t1:\n" +
        "\tthaumcraft:candle_white\n" +
        "\tthaumcraft:candle_orange\n" +
        "\tthaumcraft:candle_magenta\n" +
        "\tthaumcraft:candle_lightblue\n" +
        "\tthaumcraft:candle_yellow\n" +
        "\tthaumcraft:candle_lime\n" +
        "\tthaumcraft:candle_ping\n" +
        "\tthaumcraft:candle_gray\n" +
        "\tthaumcraft:candle_silver\n" +
        "\tthaumcraft:candle_cyan\n" +
        "\tthaumcraft:candle_purple\n" +
        "\tthaumcraft:candle_blue\n" +
        "\tthaumcraft:candle_brown\n" +
        "\tthaumcraft:candle_green\n" +
        "\tthaumcraft:candle_red\n" +
        "\tthaumcraft:candle_black\n" +
        "\t2:\n" +
        "\tminecraft:ghast_tear\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 1s, id: 48s}]} //Power\n" +
        "\t3:\n" +
        "\tminecraft:experience_bottle 2\n" +
        "\tminecraft:golden_apple:0\n" +
        "\t4:\n" +
        "\tthaumcraft:golden_apple:1\n" +
        "\t5:\n" +
        "\tthaumcraft:curio:4\n" +
        "\tthaumcraft:baubles:3 //Apprentice Ring\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 1s, id: 50s}]} //Flame\n" +
        "\tminecraft:enchanted_book {StoredEnchantments:[{lvl: 1s, id: 51s}]} //Infinity\n";

    private static void otherEntitiesStuff(){
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

    }
}
