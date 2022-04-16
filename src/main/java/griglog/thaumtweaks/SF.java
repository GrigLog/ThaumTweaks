package griglog.thaumtweaks;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;
import thaumcraft.api.capabilities.IPlayerKnowledge;
import thaumcraft.api.capabilities.ThaumcraftCapabilities;

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



}
