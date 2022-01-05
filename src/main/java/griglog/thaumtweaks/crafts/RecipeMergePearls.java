package griglog.thaumtweaks.crafts;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.items.curios.ItemPrimordialPearl;

public class RecipeMergePearls implements IRecipe {
    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        int count = 0;
        for(int x = 0; x < inv.getWidth(); ++x) {
            for (int y = 0; y < inv.getHeight(); ++y) {
                ItemStack is = inv.getStackInRowAndColumn(x, y);
                if (!is.isEmpty()) {
                    if (is.getItem() instanceof ItemPrimordialPearl)
                        count++;
                    else
                        return false;
                }
            }
        }
        return (count > 1);
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
    int durability = 0;
        for(int x = 0; x < inv.getWidth(); ++x) {
            for (int y = 0; y < inv.getHeight(); ++y) {
                ItemStack is = inv.getStackInRowAndColumn(x, y);
                if (!is.isEmpty()) {
                    durability += is.getMaxDamage() - is.getItemDamage();
                }
            }
        }
        ItemStack pearl = new ItemStack(ItemsTC.primordialPearl);
        pearl.setItemDamage(Math.max(pearl.getMaxDamage()-durability, 0));
        return pearl;
    }

    @Override
    public boolean canFit(int width, int height) {
        return (width >= 2 || height >= 2);
    }

    @Override
    public ItemStack getRecipeOutput() {
        return new ItemStack(ItemsTC.primordialPearl);
    }

    public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
        return NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);
    }

    @Override
    public IRecipe setRegistryName(ResourceLocation name) {
        return null;
    }

    @Override
    public ResourceLocation getRegistryName() {
        return new ResourceLocation("primalpearlmerge");
    }

    @Override
    public Class<IRecipe> getRegistryType() {
        return null;
    }
}
