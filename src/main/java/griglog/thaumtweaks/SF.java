package griglog.thaumtweaks;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.items.IItemHandler;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;
import thaumcraft.api.items.IRechargable;
import thaumcraft.common.items.armor.ItemFortressArmor;
import thaumcraft.common.items.armor.ItemVoidRobeArmor;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SF {  //SomeFuncs
    public static void print(String prefix, String text) {
        ThaumTweaks.LOGGER.info("!!!" + prefix + " " + text);
    }

    public static void print(String prefix) {
        print(prefix, "");
    }

    public static void printChat(String s) {
        GuiNewChat chat = Minecraft.getMinecraft().ingameGUI.getChatGUI();
        TextComponentString msg = new TextComponentString(s);
        chat.printChatMessage(msg);
    }

    public static FakePlayer getFake(WorldServer w) {
        return new FakePlayer(w, new GameProfile(new UUID(0, 0), ""));
    }

    public static void copyKnowledge(Object a, Object b) {
        IPlayerKnowledge aBrains = null, bBrains = null;
        if (a instanceof EntityLivingBase)
            aBrains = ((EntityLivingBase) a).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        else if (a instanceof TileEntity)
            aBrains = ((TileEntity) a).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);

        if (b instanceof EntityLivingBase)
            bBrains = ((EntityLivingBase) b).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);
        else if (b instanceof TileEntity)
            bBrains = ((TileEntity) b).getCapability(ThaumcraftCapabilities.KNOWLEDGE, null);

        if (aBrains != null && bBrains != null) {
            for (String k : aBrains.getResearchList()) {
                bBrains.addResearch(k);
                bBrains.setResearchStage(k, aBrains.getResearchStage(k));
            }
        }
    }

    public static void spawnItem(World world, ItemStack is, BlockPos pos) {
        world.spawnEntity(new EntityItem(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, is));
    }

    public static List<String> readFile(String filename, String defaultContents){
        try {
            Path path = Paths.get(filename);
            if (!Files.exists(path)) {
                FileWriter w = new FileWriter(filename);
                w.write(defaultContents);
                w.close();
            }
            return Files.readAllLines(path);
        } catch (IOException e){
            return Arrays.asList(defaultContents.split("\n"));
        }
    }

    public static int getCount(IItemHandler handler, ItemStack checked){
        if (handler == null)
            return 0;
        int slots = handler.getSlots();
        int count = 0;
        for (int i = 0; i < slots; i++){
            ItemStack is = handler.getStackInSlot(i);
            if (is.getItem() == checked.getItem() && (is.hasTagCompound() == checked.hasTagCompound())
                && (!is.hasTagCompound() || is.getTagCompound().equals(checked.getTagCompound()))){
                count += is.getCount();
            }
        }
        return count;
    }

    public static boolean canExtract(IItemHandler handler, ItemStack checked){
        if (handler == null)
            return false;
        int slots = handler.getSlots();
        int count = 0;
        for (int i = 0; i < slots; i++){
            ItemStack is = handler.getStackInSlot(i);
            if (is.getItem() == checked.getItem() && (is.hasTagCompound() == checked.hasTagCompound())
                && (!is.hasTagCompound() || is.getTagCompound().equals(checked.getTagCompound()))){
                count += is.getCount();
                if (count >= checked.getCount())
                    return true;
            }
        }
        return false;
    }

    public static int extract(IItemHandler handler, ItemStack extract){ //returns how many could not be extracted
        if (handler == null)
            return extract.getCount();
        int slots = handler.getSlots();
        int toExtract = extract.getCount();
        for (int i = 0; i < slots; i++){
            ItemStack is = handler.getStackInSlot(i);
            if (is.getItem() == extract.getItem() && (is.hasTagCompound() == extract.hasTagCompound())
                && (!is.hasTagCompound() || is.getTagCompound().equals(extract.getTagCompound()))){
                int count = is.getCount();
                if (count >= toExtract){
                    handler.extractItem(i, toExtract, false);
                    return 0;
                }
                toExtract -= count;
                handler.extractItem(i, count, false);
            }
        }
        return toExtract;
    }

    public static boolean isRechargeable(Item item){
        if (!(item instanceof IRechargable))
            return false;
        if (TTConfig.general.armor == false)
            return false;
        if (TTConfig.fortArmor.vis == 0 && item instanceof ItemFortressArmor)
            return false;
        if (TTConfig.voidRobe.vis == 0 && item instanceof ItemVoidRobeArmor)
            return false;
        return true;
    }
}
