package dimasik.modules.render;

import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.events.api.EventListener;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;
import org.lwjgl.opengl.GL11;

public class FireworkEsp
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("On spawn"), new SelectOptionValue("\u0421\u043b\u0435\u0434\u043e\u0432\u0430\u043d\u0438\u0435"));
    private final Map<Integer, org.joml.Vector3d> fireworkPositions = new HashMap<Integer, org.joml.Vector3d>();
    private final Map<Integer, Long> fireworkSpawnTimes = new HashMap<Integer, Long>();
    private final Map<Integer, Long> fireworkDespawnTimes = new HashMap<Integer, Long>();
    private final long animationTime = 180L;
    private final float maxSize = 40.0f;
    private final float minSize = 40.0f;
    private final EventListener<EventRender2D> render = this::render;

    public FireworkEsp() {
        super("FireworkESP", Category.RENDER);
        this.settings(this.mode);
    }

    public void render(EventRender2D e) {
        long currentTime = System.currentTimeMillis();
        for (Entity entity : FireworkEsp.mc.world.getAllEntities()) {
            if (!(entity instanceof FireworkRocketEntity)) continue;
            FireworkRocketEntity f = (FireworkRocketEntity)entity;
            int entityId = entity.getEntityId();
            this.fireworkPositions.putIfAbsent(entityId, new org.joml.Vector3d(entity.getPosX(), entity.getPosY(), entity.getPosZ()));
            this.fireworkSpawnTimes.putIfAbsent(entityId, currentTime);
            if (!this.mode.getSelected("\u0421\u043b\u0435\u0434\u043e\u0432\u0430\u043d\u0438\u0435")) continue;
            float size = 40.0f;
            Vector2f vec2f = ScreenHelpers.worldToScreen(f.getPosX(), f.getPosY(), f.getPosZ());
            GL11.glPushMatrix();
            VisualHelpers.drawRoundedRect(vec2f.x - size / 2.0f, vec2f.y - size / 2.0f, size, size, 20.0f, new Color(0, 0, 0, 128).getRGB());
            this.drawItemStack(new ItemStack(Items.FIREWORK_ROCKET), vec2f.x - size / 1.9f, vec2f.y - size / 2.0f, size, size);
            GL11.glPopMatrix();
        }
        Iterator<Map.Entry<Integer, org.joml.Vector3d>> iterator = this.fireworkPositions.entrySet().iterator();
        while (iterator.hasNext() && this.mode.getSelected("On spawn")) {
            float scale;
            Map.Entry<Integer, org.joml.Vector3d> entry = iterator.next();
            int entityId = entry.getKey();
            org.joml.Vector3d savedPos = entry.getValue();
            Vector2d vector2d = ScreenHelpers.project(savedPos.x, savedPos.y + 0.6, savedPos.z);
            if (vector2d == null) continue;
            long spawnTime = this.fireworkSpawnTimes.getOrDefault(entityId, currentTime);
            long despawnTime = this.fireworkDespawnTimes.getOrDefault(entityId, -1L);
            long lifeTime = currentTime - spawnTime;
            if (despawnTime == -1L) {
                scale = Math.min(1.0f, (float)lifeTime / 180.0f);
            } else {
                long fadeTime = currentTime - despawnTime;
                scale = 1.0f - Math.min(1.0f, (float)fadeTime / 180.0f);
                if (scale <= 0.0f) {
                    iterator.remove();
                    this.fireworkSpawnTimes.remove(entityId);
                    this.fireworkDespawnTimes.remove(entityId);
                    continue;
                }
            }
            float size = 40.0f + 0.0f * scale;
            GL11.glPushMatrix();
            VisualHelpers.drawRoundedRect((float)vector2d.x - size / 2.0f, (float)vector2d.y - size / 2.0f, size, size, 20.0f, new Color(0, 0, 0, 128).getRGB());
            this.drawItemStack(new ItemStack(Items.FIREWORK_ROCKET), (float)vector2d.x - size / 1.9f, (float)vector2d.y - size / 2.0f, size, size);
            GL11.glPopMatrix();
        }
        for (int entityId : this.fireworkPositions.keySet()) {
            Entity entity = FireworkEsp.mc.world.getEntityByID(entityId);
            if (entity != null && entity.isAlive()) continue;
            this.fireworkDespawnTimes.putIfAbsent(entityId, currentTime);
        }
    }

    public void drawItemStack(ItemStack stack, double x, double y, double width, double height) {
        RenderSystem.translated(x, y, 0.0);
        double scaleX = width / 16.0;
        double scaleY = height / 16.0;
        RenderSystem.scaled(scaleX, scaleY, 1.0);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        RenderSystem.scaled(1.0 / scaleX, 1.0 / scaleY, 1.0);
        RenderSystem.translated(-x, -y, 0.0);
    }

    private class FireWork {
        private Vector3d pos;
        private ItemStack item;
        private final long time;
        private Entity entity;

        public FireWork(Vector3d pos, ItemStack item, Entity entity) {
            this.pos = pos;
            this.item = item;
            this.entity = entity;
            this.time = System.currentTimeMillis();
        }
    }
}
