package griglog.thaumtweaks.items;

import griglog.thaumtweaks.ThaumTweaks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import scala.Predef;
import thaumcraft.api.ThaumcraftMaterials;
import thaumcraft.api.items.ItemsTC;

public class TTMaterials {
    public static void overrideMaterials()
    {
        ThaumcraftMaterials.ARMORMAT_THAUMIUM = EnumHelper.addArmorMaterial("THAUMIUM", "THAUMIUM", 50, new int[]{2, 5, 6, 3}, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 2.0F);
        //+1 defense, +4 toughness, 2x durability

        ThaumcraftMaterials.ARMORMAT_FORTRESS = EnumHelper.addArmorMaterial("FORTRESS","FORTRESS", 120, new int[] { 3, 5, 7, 4 }, 25, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 4f);
        //+1 defense, +3 toughness, strength 2 effect, 100% kb resist, recalculated ratio (slightly more), 3x durability

        ThaumcraftMaterials.ARMORMAT_VOID = EnumHelper.addArmorMaterial("VOID","VOID", 30, new int[] { 4, 7, 9, 4 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_CHAIN, 1F);
        //+4 defense, regen effect, 3x durability, 3x repairing

        ThaumcraftMaterials.ARMORMAT_VOIDROBE = EnumHelper.addArmorMaterial("VOIDROBE","VOIDROBE", 72, new int[] { 4, 7, 9, 4 }, 10, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 2f);
        //regen 2 effect, debuff clearing, 75% kb resist, 4x durability, 4x repairing, recalculated ratio (more), consumes vis



        ThaumcraftMaterials.TOOLMAT_VOID = EnumHelper.addToolMaterial("VOID", 4, 150, 8F, 7, 10).setRepairItem(new ItemStack(ItemsTC.ingots,1,1));
    }
}
