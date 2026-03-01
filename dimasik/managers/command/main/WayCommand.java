package dimasik.managers.command.main;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.Load;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.managers.command.api.Command;
import dimasik.managers.config.main.WayConfig;
import dimasik.utils.client.ChatUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.Generated;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3i;

public class WayCommand
extends Command {
    private final Map<String, Vector3i> points = new LinkedHashMap<String, Vector3i>();

    public WayCommand() {
        super("\u0423\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u0438\u0435 \u043c\u0435\u0442\u043a\u0430\u043c\u0438", "way", "waypoint");
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            if (args.length == 5 && args[1].equalsIgnoreCase("add")) {
                this.newPoint(args);
            }
            if (args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("delete")) {
                this.removePoint(args);
            }
            if (args[1].equalsIgnoreCase("clear")) {
                this.clearPoint(args);
            }
        } else {
            this.error();
        }
    }

    private void newPoint(String[] args) {
        String name = args[2];
        int x = Integer.parseInt(args[3]);
        int z = Integer.parseInt(args[4]);
        Vector3i vec = new Vector3i(x, 60, z);
        this.points.put(name, vec);
        ((WayConfig)Load.getInstance().getHooks().getConfigManagers().findClass(WayConfig.class)).fastSave();
        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0441\u043e\u0437\u0434\u0430\u043b \u043c\u0435\u0442\u043a\u0443 \u0441 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u043c: " + name + "!");
    }

    private void removePoint(String[] args) {
        String name = args[2];
        this.points.remove(name);
        ((WayConfig)Load.getInstance().getHooks().getConfigManagers().findClass(WayConfig.class)).fastSave();
        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u0443\u0434\u0430\u043b\u0438\u043b \u043c\u0435\u0442\u043a\u0443 \u0441 \u043d\u0430\u0437\u0432\u0430\u043d\u0438\u0435\u043c: " + name + "!");
    }

    private void clearPoint(String[] args) {
        this.points.clear();
        ((WayConfig)Load.getInstance().getHooks().getConfigManagers().findClass(WayConfig.class)).fastSave();
        ChatUtils.addClientMessage("\u0423\u0441\u043f\u0435\u0448\u043d\u043e \u043e\u0447\u0438\u0441\u0442\u0438\u043b \u043c\u0435\u0442\u043a\u0438!");
    }

    public void drawTag(MatrixStack stack) {
        if (!this.points.isEmpty()) {
            for (String name : this.points.keySet()) {
                Vector3i position = this.points.get(name);
                Vector2f pos = ScreenHelpers.worldToScreen(position.getX(), position.getY(), position.getZ());
                float x = pos.x;
                float y = pos.y;
                String finalName = name + " - " + String.format("%.0f", WayCommand.mc.player.getPositionVec().distanceTo(Vector3d.copyCentered(position))) + "\u043c";
                sf_semibold.drawText(stack, finalName, x, y, ColorHelpers.rgba(255, 255, 255, 255), 16.0f);
            }
        }
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".way add <name> <x> <z>");
        ChatUtils.addClientMessage(".way remove <name>");
        ChatUtils.addClientMessage(".way clear");
    }

    @Generated
    public Map<String, Vector3i> getPoints() {
        return this.points;
    }
}
