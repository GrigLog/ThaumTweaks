package griglog.thaumtweaks;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.text.TextComponentString;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;

public class SF {  //SomeFuncs
    public static String getDescriptor(Class<?> cls, String name) {
        for (Method m : cls.getMethods())
            if (m.getName().equals(name))
                return Type.getMethodDescriptor(m);
        return "Not found";
    }

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
}
