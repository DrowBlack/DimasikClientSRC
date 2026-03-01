package dimasik.managers.command.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.command.api.Command;
import dimasik.utils.client.ChatUtils;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class GpsCommand
extends Command {
    public static boolean enabled;
    public static Vector3d vector3d;
    protected int age;

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    public GpsCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043c\u0435\u0442\u043a\u0430\u043c\u0438", "gps");
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("off")) {
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u041d\u0430\u0432\u0438\u0433\u0430\u0442\u043e\u0440 \u0432\u044b\u043a\u043b\u044e\u0447\u0435\u043d!");
                enabled = false;
                vector3d = null;
                return;
            }
            if (args[1].equalsIgnoreCase("okey")) {
                double var10000 = Math.pow(GpsCommand.vector3d.x - GpsCommand.mc.player.getPosX(), 2.0);
                double dst = Math.sqrt(var10000 + Math.pow(GpsCommand.vector3d.z - GpsCommand.mc.player.getPosZ(), 2.0));
                if (dst < 135.0) {
                    GpsCommand.mc.player.sendChatMessage("air");
                }
                return;
            }
            if (args[1].equalsIgnoreCase("info")) {
                if (vector3d != null) {
                    ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u0418\u043d\u0444\u043e\u0440\u043c\u0430\u0446\u0438\u044f \u043e \u0442\u0435\u043a\u0443\u0449\u0443\u043c GPS - x: " + String.valueOf((Object)TextFormatting.WHITE) + GpsCommand.vector3d.x + String.valueOf((Object)TextFormatting.GRAY) + " y: " + String.valueOf((Object)TextFormatting.WHITE) + GpsCommand.vector3d.z + String.valueOf((Object)TextFormatting.RESET));
                    return;
                }
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u0421\u0435\u0439\u0447\u0430\u0441 GPS \u043e\u0442\u043a\u043b\u044e\u0447\u0435\u043d!");
            }
            if (args.length == 3) {
                int x = Integer.parseInt(args[1]);
                int y = Integer.parseInt(args[2]);
                enabled = true;
                vector3d = new Vector3d(x, 0.0, y);
                ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u041d\u0430\u0432\u0438\u0433\u0430\u0442\u043e\u0440 \u0432\u043a\u043b\u044e\u0447\u0435\u043d! \u041a\u043e\u043e\u0440\u0434\u0438\u043d\u0430\u0442\u044b " + x + ";" + y);
            }
        } else {
            this.error();
        }
    }

    public static void drawArrow2(MatrixStack stack) {
        if (enabled) {
            double timeToReach;
            double x = GpsCommand.vector3d.x - IFastAccess.mc.getRenderManager().info.getProjectedView().getX();
            double z = GpsCommand.vector3d.z - IFastAccess.mc.getRenderManager().info.getProjectedView().getZ();
            double cos = MathHelper.cos((float)((double)GpsCommand.mc.player.rotationYaw * (Math.PI / 180)));
            double sin = MathHelper.sin((float)((double)GpsCommand.mc.player.rotationYaw * (Math.PI / 180)));
            double rotY = -(z * cos - x * sin);
            double rotX = -(x * cos + z * sin);
            double dst = Math.sqrt(Math.pow(GpsCommand.vector3d.x - GpsCommand.mc.player.getPosX(), 2.0) + Math.pow(GpsCommand.vector3d.z - GpsCommand.mc.player.getPosZ(), 2.0));
            float angle = (float)(Math.atan2(rotY, rotX) * 180.0 / Math.PI);
            Object[] var10001 = new Object[]{Math.hypot(GpsCommand.mc.player.getPosX() - GpsCommand.mc.player.prevPosX, GpsCommand.mc.player.getPosZ() - GpsCommand.mc.player.prevPosZ) * 20.0};
            double speed = (Double)var10001[0];
            double d = timeToReach = speed > 0.0 ? dst / speed : -1.0;
            String formattedTime = timeToReach < 0.0 ? "--" : (timeToReach < 60.0 ? String.format("%d s", (int)timeToReach) : (timeToReach < 3600.0 ? String.format("%d m %d s", (int)(timeToReach / 60.0), (int)(timeToReach % 60.0)) : (timeToReach < 86400.0 ? String.format("%d h %d m", (int)(timeToReach / 3600.0), (int)(timeToReach % 3600.0 / 60.0)) : String.format("%d d %d h", (int)(timeToReach / 86400.0), (int)(timeToReach % 86400.0 / 3600.0)))));
            GL11.glPushMatrix();
            GL11.glTranslated((double)mc.getMainWindow().getScaledWidth() / 2.0, (double)mc.getMainWindow().getScaledHeight() / 2.0 - 95.0, 0.0);
            GL11.glRotated(angle, 0.0, 0.0, 1.0);
            VisualHelpers.drawImage2(new ResourceLocation("main/textures/images/triangle.png"), -9.0f, -10.0f, 24.0f, 25.0f, -1);
            GL11.glPopMatrix();
            GL11.glPushMatrix();
            GL11.glTranslated((double)mc.getMainWindow().getScaledWidth() / 2.0, (double)mc.getMainWindow().getScaledHeight() / 2.0 - 85.0, 0.0);
            sf_medium.drawCenteredText(stack, (int)dst + "m", 0.0f, 0.0f, -1, 12.0f);
            GL11.glPopMatrix();
            if (timeToReach >= 0.0) {
                GL11.glPushMatrix();
                GL11.glTranslated((double)mc.getMainWindow().getScaledWidth() / 2.0, (double)mc.getMainWindow().getScaledHeight() / 2.0 - 75.0, 0.0);
                sf_medium.drawCenteredText(stack, formattedTime, 0.0f, 0.0f, -1, 12.0f);
                GL11.glPopMatrix();
            }
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.GRAY) + "\u041e\u0448\u0438\u0431\u043a\u0430 \u0432 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0438" + String.valueOf((Object)TextFormatting.WHITE) + ":");
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.WHITE) + ".gps " + String.valueOf((Object)TextFormatting.GRAY) + "(" + String.valueOf((Object)TextFormatting.RED) + "x, z" + String.valueOf((Object)TextFormatting.GRAY) + ")");
        ChatUtils.addClientMessage(String.valueOf((Object)TextFormatting.WHITE) + ".gps " + String.valueOf((Object)TextFormatting.GRAY) + "(" + String.valueOf((Object)TextFormatting.RED) + "off" + String.valueOf((Object)TextFormatting.GRAY) + ")");
    }
}
