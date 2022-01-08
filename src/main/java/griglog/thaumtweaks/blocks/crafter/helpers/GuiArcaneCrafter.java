package griglog.thaumtweaks.blocks.crafter.helpers;

import griglog.thaumtweaks.blocks.crafter.TileArcaneCrafter;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.IArcaneRecipe;
import thaumcraft.common.blocks.world.ore.ShardType;
import thaumcraft.common.container.ContainerArcaneWorkbench;
import thaumcraft.common.items.casters.CasterManager;
import thaumcraft.common.lib.crafting.ThaumcraftCraftingManager;

import java.awt.*;

@SideOnly(Side.CLIENT)
public class GuiArcaneCrafter extends GuiContainer {
    private TileArcaneCrafter tileEntity;
    private InventoryPlayer inv;
    private int[][] aspectLocs = new int[][]{{72, 21}, {24, 43}, {24, 102}, {72, 124}, {120, 102}, {120, 43}};
    ResourceLocation tex = new ResourceLocation("thaumtweaks", "textures/gui/crafter.png");

    public GuiArcaneCrafter(InventoryPlayer inventory, TileArcaneCrafter tile) {
        super(new ContainerArcaneCrafter(inventory, tile));
        tileEntity = tile;
        inv = inventory;
        ySize = 234;
        xSize = 190;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    protected void drawGuiContainerBackgroundLayer(float par1, int par2, int par3) {
        mc.renderEngine.bindTexture(tex);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3042);
        int var5 = (width - xSize) / 2;
        int var6 = (height - ySize) / 2;
        drawTexturedModalRect(var5, var6, 0, 0, xSize, ySize);
        int cost = 0;
        int discount = 0;
        IArcaneRecipe result = ThaumcraftCraftingManager.findMatchingArcaneRecipe(tileEntity.inventoryCraft, inv.player);
        AspectList crystals = null;
        float df = CasterManager.getTotalVisDiscount(inv.player);
        if (result != null) {
            cost = result.getVis();
            cost = (int)((float)cost * (1.0F - df));
            discount = (int)(df * 100.0F);
            crystals = result.getCrystals();
        }

        if (crystals != null) {
            GlStateManager.blendFunc(770, 1);
            Aspect[] var12 = crystals.getAspects();
            int var13 = var12.length;

            for(int var14 = 0; var14 < var13; ++var14) {
                Aspect a = var12[var14];
                int id = ShardType.getMetaByAspect(a);
                Color col = new Color(a.getColor());
                GL11.glColor4f((float)col.getRed() / 255.0F, (float)col.getGreen() / 255.0F, (float)col.getBlue() / 255.0F, 0.33F);
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(var5 + ContainerArcaneWorkbench.xx[id]) + 7.5F, (float)(var6 + ContainerArcaneWorkbench.yy[id]) + 8.0F, 0.0F);
                GL11.glRotatef((float)(id * 60 + mc.getRenderViewEntity().ticksExisted % 360) + par1, 0.0F, 0.0F, 1.0F);
                GL11.glScalef(0.5F, 0.5F, 0.0F);
                drawTexturedModalRect(-32, -32, 192, 0, 64, 64);
                GL11.glScalef(1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.blendFunc(770, 771);
        }

        GL11.glDisable(3042);
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(var5 + 168), (float)(var6 + 46), 0.0F);
        GL11.glScalef(0.5F, 0.5F, 0.0F);
        String text = tileEntity.auraVisClient + " " + I18n.translateToLocal("workbench.available");
        int ll = fontRenderer.getStringWidth(text) / 2;
        fontRenderer.drawString(text, -ll, 0, tileEntity.auraVisClient < cost ? 15625838 : 7237358);
        GL11.glScalef(1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
        if (cost > 0) {
            if (tileEntity.auraVisClient < cost) {
                GL11.glPushMatrix();
                float var40 = 0.33F;
                GL11.glColor4f(var40, var40, var40, 0.66F);
                GL11.glEnable(2896);
                GL11.glEnable(2884);
                GL11.glEnable(3042);
                itemRender.renderItemAndEffectIntoGUI(result.getCraftingResult(tileEntity.inventoryCraft), var5 + 160, var6 + 64);
                itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, result.getCraftingResult(tileEntity.inventoryCraft), var5 + 160, var6 + 64, "");
                GlStateManager.enableLighting();
                GlStateManager.enableDepth();
                RenderHelper.enableStandardItemLighting();
                GL11.glPopMatrix();
                RenderHelper.disableStandardItemLighting();
            }

            GL11.glPushMatrix();
            GL11.glTranslatef((float)(var5 + 168), (float)(var6 + 38), 0.0F);
            GL11.glScalef(0.5F, 0.5F, 0.0F);
            text = cost + " " + I18n.translateToLocal("workbench.cost");
            if (discount > 0) {
                text = text + " (" + discount + "% " + I18n.translateToLocal("workbench.discount") + ")";
            }

            ll = fontRenderer.getStringWidth(text) / 2;
            fontRenderer.drawString(text, -ll, 0, 12648447);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }

    }
}
