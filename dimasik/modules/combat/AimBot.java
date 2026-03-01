package dimasik.modules.combat;

import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;

public class AimBot
extends Module {
    private final SelectOption items = new SelectOption("\u041e\u0440\u0443\u0436\u0438\u0435", 0, new SelectOptionValue("\u041b\u0443\u043a"), new SelectOptionValue("\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446"), new SelectOptionValue("\u0410\u0440\u0431\u0430\u043b\u0435\u0442"));
    private final CheckboxOption prediction = new CheckboxOption("Prediction", true);
    private final SliderOption fov = new SliderOption("FOV", 90.0f, 1.0f, 180.0f).increment(1.0f);
    private final CheckboxOption fovCircle = new CheckboxOption("Circle FOV", true);
    private final CheckboxOption autoWall = new CheckboxOption("Auto wall", false);
    private float yaw;
    private float pitch;
    private LivingEntity target;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventRender2D.Pre> eventRender2dPre = this::render;

    public AimBot() {
        super("AimBot", Category.COMBAT);
        this.settings(this.items, this.prediction, this.fov, this.fovCircle, this.autoWall);
    }

    public void update(EventUpdate event) {
        boolean trident;
        Item main = AimBot.mc.player.getHeldItemMainhand().getItem();
        Item off = AimBot.mc.player.getHeldItemOffhand().getItem();
        boolean bow = (main == Items.BOW || off == Items.BOW) && this.items.getSelected("\u041b\u0443\u043a");
        boolean crossbow = (main == Items.CROSSBOW || off == Items.CROSSBOW) && this.items.getSelected("\u0410\u0440\u0431\u0430\u043b\u0435\u0442");
        boolean bl = trident = (main == Items.TRIDENT || off == Items.TRIDENT) && this.items.getSelected("\u0422\u0440\u0435\u0437\u0443\u0431\u0435\u0446");
        if (!(bow || crossbow || trident)) {
            this.target = null;
            return;
        }
        Item used = main;
        if (main != Items.BOW && main != Items.CROSSBOW && main != Items.TRIDENT) {
            used = off;
        }
        if (((Boolean)this.autoWall.getValue()).booleanValue() && AimBot.mc.player.isHandActive()) {
            this.target = AimBot.mc.world.getPlayers().stream().filter(e -> e != AimBot.mc.player && e.isAlive() && this.getFOV((LivingEntity)e) <= ((Float)this.fov.getValue()).floatValue()).filter(e -> this.isBehindWall((LivingEntity)e)).min((a, b) -> Double.compare(AimBot.mc.player.getDistance((Entity)a), AimBot.mc.player.getDistance((Entity)b))).orElse(null);
            if (this.target != null) {
                this.applyRotation(used);
                return;
            }
        }
        this.target = AimBot.mc.world.getPlayers().stream().filter(e -> e != AimBot.mc.player && e.isAlive() && this.getFOV((LivingEntity)e) <= ((Float)this.fov.getValue()).floatValue()).min((a, b) -> Double.compare(AimBot.mc.player.getDistance((Entity)a), AimBot.mc.player.getDistance((Entity)b))).orElse(null);
        if (this.target != null) {
            this.applyRotation(used);
        }
    }

    public void render(EventRender2D.Pre event) {
        if (((Boolean)this.fovCircle.getValue()).booleanValue() && ((Float)this.fov.getValue()).floatValue() <= 90.0f) {
            float radius = ((Float)this.fov.getValue()).floatValue() * 2.0f;
            VisualHelpers.drawCircle((float)mc.getMainWindow().getScaledWidth() / 2.0f, (float)mc.getMainWindow().getScaledHeight() / 2.0f, 0.0f, 360.0f, radius, -1, 1.0f);
        }
    }

    private void applyRotation(Item main) {
        float modif = 10.0f;
        Vector3d predicted = new Vector3d(this.target.getPosX() + (this.target.getPosX() - this.target.lastTickPosX) * (double)modif, this.target.getPosY() + (this.target.getPosY() - this.target.lastTickPosY) * (double)modif / 5.0 + (double)(this.target.getHeight() / 2.0f), this.target.getPosZ() + (this.target.getPosZ() - this.target.lastTickPosZ) * (double)modif);
        Vector2f rotation = this.getRotation((Boolean)this.prediction.getValue() != false ? predicted : this.target.getPositionVec().add(0.0, this.target.getHeight() / 2.0f, 0.0));
        float gravity = 0.0f;
        if (main == Items.BOW) {
            gravity = 0.0f;
        }
        if (main == Items.CROSSBOW) {
            gravity = 0.1f;
        }
        if (main == Items.TRIDENT) {
            gravity = 0.5f;
        }
        float dist = AimBot.mc.player.getDistance(this.target);
        float pitchCorr = (float)((double)(rotation.y - dist * gravity) + AimBot.mc.player.getMotion().y * (double)dist * (double)(AimBot.mc.player.getMotion().y > 0.0 ? 0.0f : 1.0f));
        Vector2f corrected = new Vector2f(rotation.x, pitchCorr);
        this.yaw = corrected.x;
        this.pitch = corrected.y;
        AimBot.mc.player.rotationYaw = this.yaw;
        AimBot.mc.player.rotationPitch = this.pitch;
    }

    private float getFOV(LivingEntity entity) {
        Vector2f rot = this.getRotation(entity.getPositionVec().add(0.0, entity.getHeight() / 2.0f, 0.0));
        float yawDiff = Math.abs(MathHelper.wrapDegrees(rot.x - AimBot.mc.player.rotationYaw));
        float pitchDiff = Math.abs(MathHelper.wrapDegrees(rot.y - AimBot.mc.player.rotationPitch));
        return MathHelper.sqrt(yawDiff * yawDiff + pitchDiff * pitchDiff);
    }

    private Vector2f getRotation(Vector3d vec) {
        double dx = vec.x - AimBot.mc.player.getPosX();
        double dy = vec.y - (AimBot.mc.player.getPosY() + (double)AimBot.mc.player.getEyeHeight());
        double dz = vec.z - AimBot.mc.player.getPosZ();
        double dist = MathHelper.sqrt(dx * dx + dz * dz);
        float yaw = (float)(MathHelper.atan2(dz, dx) * 57.29577951308232) - 90.0f;
        float pitch = (float)(-(MathHelper.atan2(dy, dist) * 57.29577951308232));
        return new Vector2f(yaw, pitch);
    }

    private boolean isBehindWall(LivingEntity entity) {
        Vector3d to;
        Vector3d from = AimBot.mc.player.getEyePosition(1.0f);
        BlockRayTraceResult result = AimBot.mc.world.rayTraceBlocks(new RayTraceContext(from, to = entity.getPositionVec().add(0.0, (double)entity.getHeight() / 2.0, 0.0), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, AimBot.mc.player));
        return ((RayTraceResult)result).getType() == RayTraceResult.Type.BLOCK;
    }

    @Override
    public void onDisabled() {
        this.target = null;
    }
}
