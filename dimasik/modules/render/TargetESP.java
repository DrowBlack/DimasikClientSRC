package dimasik.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventRender3D;
import dimasik.events.main.render.EventTest;
import dimasik.helpers.animation.Animation;
import dimasik.helpers.animation.Easing;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.animation.GhostAnim;
import dimasik.helpers.animation.InfinityAnimation;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.GhostRenderer3D;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.modules.combat.Aura;
import dimasik.utils.client.AnimationTest;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class TargetESP
extends Module {
    private final SelectOption mode = new SelectOption("Mode", 0, new SelectOptionValue("Ghosts"), new SelectOptionValue("Cube"), new SelectOptionValue("Ghost Rider"), new SelectOptionValue("Smail"));
    public final SliderOption sizes = new SliderOption("Size", 1.0f, 0.5f, 1.5f).increment(0.1f).visible(() -> this.mode.getSelected("Ghosts"));
    private static final int MAX_PARTICLES = 3;
    private static final float BASE_SIZE = 0.5f;
    private static final float BASE_MUL = 0.05f;
    private static final float ALPHA_STEP = 0.005f;
    private final List<GhostRenderer3D> particles = new ArrayList<GhostRenderer3D>();
    private final GhostAnim targetEspAnim = new GhostAnim().setEasing(Easing.TARGETESP_EASE_OUT_BACK).setSpeed(300);
    private float x = 0.0f;
    private float y = 0.0f;
    private float z = 0.0f;
    private final InfinityAnimation moving = new InfinityAnimation();
    private float rotationAngle = 0.0f;
    private float rotationSpeed = 0.0f;
    private boolean isReversing = false;
    private LivingEntity prevTarget;
    private final List<Deque<GhostPoint>> ghostTrails = Arrays.asList(new ArrayDeque(), new ArrayDeque(), new ArrayDeque(), new ArrayDeque());
    private final AnimationTest animationtest;
    public static long startTime = System.currentTimeMillis();
    private final Animation animation = new Animation();
    private final Animation hurtAnimation = new Animation();
    private LivingEntity currentTarget;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventTest> ghost = this::onRender;
    private final EventListener<EventRender3D.PreHand> preHand = this::eee;
    private final EventListener<EventRender3D.Post> render3d = this::renderGhosts;
    private final EventListener<EventRender3D.Post> spawnGhosts = this::spawnGhostPoints;
    private final EventListener<EventRender3D.PreHand> gandon = this::pidoras;

    public TargetESP() {
        super("TargetESP", Category.RENDER);
        this.settings(this.mode, this.sizes);
        this.animationtest = new AnimationTest(dimasik.utils.client.Easing.EASE_OUT_CIRC, 400L);
    }

    public void update(EventUpdate event) {
        if (Load.getInstance().getHooks().getModuleManagers().getAura().getTarget() != null) {
            this.currentTarget = Load.getInstance().getHooks().getModuleManagers().getAura().getTarget();
        } else if (this.animation.getAnimationValue() <= 0.1f) {
            this.currentTarget = null;
        }
        this.animation.update(Load.getInstance().getHooks().getModuleManagers().getAura().getTarget() != null);
    }

    public void updateRotation() {
        if (!this.isReversing) {
            this.rotationSpeed += 0.01f;
            if ((double)this.rotationSpeed > (double)2.65f) {
                this.rotationSpeed = 2.65f;
                this.isReversing = true;
            }
        } else {
            this.rotationSpeed -= 0.01f;
            if ((double)this.rotationSpeed < (double)-2.65f) {
                this.rotationSpeed = -2.65f;
                this.isReversing = false;
            }
        }
        this.rotationAngle += this.rotationSpeed;
        this.rotationAngle = (this.rotationAngle + 360.0f) % 360.0f;
    }

    public void onRender(EventTest event) {
        Aura aura = Load.getInstance().getHooks().getModuleManagers().getAura();
        this.prevTarget = aura.getTarget();
        if (this.prevTarget != null && this.mode.getSelected("Ghosts")) {
            this.renderGhost(event);
        }
    }

    public void eee(EventRender3D.PreHand event) {
        if (this.mode.getSelected("Cube")) {
            this.renderCube(event);
        }
    }

    public void pidoras(EventRender3D.PreHand event) {
        if (this.mode.getSelected("Smail")) {
            this.gandon(event);
        }
    }

    public void renderCube(EventRender3D.PreHand event) {
        MatrixStack matrixStack = event.getMatrixStack();
        float size = 50.0f;
        int color2 = ColorHelpers.getThemeColor(2);
        int color1 = ColorHelpers.getThemeColor(1);
        color1 = ColorHelpers.interpolateColor(color1, ColorHelpers.rgba(211, 16, 16, 220), this.hurtAnimation.getAnimationValue());
        color2 = ColorHelpers.interpolateColor(color2, ColorHelpers.rgba(211, 16, 16, 220), this.hurtAnimation.getAnimationValue());
        if (this.currentTarget != null) {
            Vector3d vector3d = VisualHelpers.getEntityPosition(this.currentTarget, event.getPartialTicks());
            this.x = (float)vector3d.x;
            this.y = (float)vector3d.y + this.currentTarget.getHeight() / 2.0f;
            this.z = (float)vector3d.z;
        }
        this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.NONE, event.getPartialTicks());
        matrixStack.push();
        matrixStack.translate(this.x, this.y, this.z);
        matrixStack.scale(this.animation.getAnimationValue() * 0.025f);
        matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(this.rotationAngle));
        this.updateRotation();
        RenderSystem.disableDepthTest();
        VisualHelpers.drawTexture(matrixStack, new ResourceLocation("main/textures/images/target.png"), -size / 2.0f, -size / 2.0f, size, size, color1, color1, color2, color2);
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }

    public void gandon(EventRender3D.PreHand event) {
        MatrixStack matrixStack = event.getMatrixStack();
        float size = 50.0f;
        int color2 = ColorHelpers.getThemeColor(2);
        int color1 = ColorHelpers.getThemeColor(1);
        color1 = ColorHelpers.interpolateColor(color1, ColorHelpers.rgba(211, 16, 16, 220), this.hurtAnimation.getAnimationValue());
        color2 = ColorHelpers.interpolateColor(color2, ColorHelpers.rgba(211, 16, 16, 220), this.hurtAnimation.getAnimationValue());
        if (this.currentTarget != null) {
            Vector3d vector3d = VisualHelpers.getEntityPosition(this.currentTarget, event.getPartialTicks());
            this.x = (float)vector3d.x;
            this.y = (float)vector3d.y + this.currentTarget.getHeight() / 2.0f;
            this.z = (float)vector3d.z;
        }
        this.animation.animate(0.0f, 1.0f, 0.2f, EasingList.NONE, event.getPartialTicks());
        matrixStack.push();
        matrixStack.translate(this.x, this.y, this.z);
        matrixStack.scale(this.animation.getAnimationValue() * 0.025f);
        matrixStack.rotate(mc.getRenderManager().getCameraOrientation());
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(this.rotationAngle));
        this.updateRotation();
        RenderSystem.disableDepthTest();
        VisualHelpers.drawTexture(matrixStack, new ResourceLocation("main/textures/images/targetesp.png"), -size / 2.0f, -size / 2.0f, size, size, color1, color1, color2, color2);
        RenderSystem.enableDepthTest();
        matrixStack.pop();
    }

    private void renderGhosts(EventRender3D.Post e) {
        if (this.mode.getSelected("Ghost Rider") && this.currentTarget != null) {
            int maxAlpha = (int)(255.0 * this.animationtest.getValue());
            int alphaFalloff = (int)(4.0 * this.animationtest.getValue());
            int trailLength = 70;
            MatrixStack ms = new MatrixStack();
            ms.push();
            RenderSystem.pushMatrix();
            RenderSystem.disableLighting();
            RenderSystem.depthMask(false);
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.shadeModel(7425);
            RenderSystem.disableCull();
            RenderSystem.disableAlphaTest();
            RenderSystem.blendFuncSeparate(770, 1, 0, 1);
            mc.getTextureManager().bindTexture(new ResourceLocation("main/textures/images/glow.png"));
            ActiveRenderInfo camera = TargetESP.mc.getRenderManager().info;
            Vector3d camPos = camera.getProjectedView();
            buffer.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
            for (int i = 0; i < 4; ++i) {
                Deque<GhostPoint> trail = this.ghostTrails.get(i);
                int index = 0;
                for (GhostPoint point : trail) {
                    point.update();
                    float ghostSize = point.size * (0.7f - (float)index / (float)trailLength);
                    Quaternion rotation = camera.getRotation().copy();
                    double dx = point.position.x - camPos.x;
                    double dy = point.position.y - camPos.y;
                    double dz = point.position.z - camPos.z;
                    int alpha = MathHelper.clamp(maxAlpha - index * alphaFalloff, 0, maxAlpha);
                    ms.push();
                    ms.translate(dx, dy, dz);
                    ms.translate(-ghostSize / 2.0f, -ghostSize / 2.0f, 0.0);
                    ms.rotate(rotation);
                    ms.translate(ghostSize / 2.0f, ghostSize / 2.0f, 0.0);
                    int color = ColorHelpers.getTheme(index);
                    int argb = ColorHelpers.setAlpha(color, alpha);
                    Matrix4f mat = ms.getLast().getMatrix();
                    buffer.pos(mat, 0.0f, -ghostSize, 0.0f).color(argb).tex(0.0f, 0.0f).endVertex();
                    buffer.pos(mat, -ghostSize, -ghostSize, 0.0f).color(argb).tex(0.0f, 1.0f).endVertex();
                    buffer.pos(mat, -ghostSize, 0.0f, 0.0f).color(argb).tex(1.0f, 1.0f).endVertex();
                    buffer.pos(mat, 0.0f, 0.0f, 0.0f).color(argb).tex(1.0f, 0.0f).endVertex();
                    ms.pop();
                    ++index;
                }
            }
            Tessellator.getInstance().draw();
            ms.pop();
            RenderSystem.enableCull();
            RenderSystem.enableAlphaTest();
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.popMatrix();
        }
    }

    private void spawnGhostPoints(EventRender3D.Post e) {
        Aura aura = (Aura)Load.getInstance().getHooks().getModuleManagers().findClass(Aura.class);
        if (aura.getTarget() != null) {
            this.animationtest.run(1.0);
        } else {
            this.animationtest.run(0.0);
        }
        if (aura.getTarget() != null) {
            this.currentTarget = aura.getTarget();
        } else if (this.animationtest.getValue() == 0.0) {
            this.currentTarget = null;
        }
        if (this.mode.getSelected("Ghost Rider") && this.currentTarget != null) {
            LivingEntity target = this.currentTarget;
            int trailLength = 80;
            double rotationSpeed = 0.005;
            double orbitRadius = 0.5;
            long currentTime = System.currentTimeMillis() - startTime;
            for (int i = 0; i < 4; ++i) {
                Deque<GhostPoint> trail = this.ghostTrails.get(i);
                trail.clear();
                for (int j = 0; j < trailLength; ++j) {
                    double ageOffset = j * 10;
                    double angle = ((double)currentTime - ageOffset) * rotationSpeed + (double)(i * 2) * Math.PI / 4.0;
                    double dx = Math.cos(angle) * orbitRadius;
                    double dz = Math.sin(angle) * orbitRadius;
                    double verticalOscillation = Math.sin(((double)(currentTime + (long)(i * 759)) - ageOffset) * 0.003) * 0.5;
                    double x = target.getPosition((float)e.getPartialTicks()).x + dx + (double)0.1f;
                    double y = target.getPosition((float)e.getPartialTicks()).y + (double)(target.getHeight() / 2.0f) + verticalOscillation + 0.25;
                    double z = target.getPosition((float)e.getPartialTicks()).z + dz;
                    trail.addLast(new GhostPoint(new Vector3d(x, y, z)));
                }
            }
        }
    }

    private void renderGhost(EventTest event) {
        if (this.particles.size() < 3) {
            this.particles.add(new GhostRenderer3D(this.prevTarget.getPositionVec(), Vector3d.ZERO, 0.3f));
        }
        ArrayList<GhostRenderer3D> toRemove = new ArrayList<GhostRenderer3D>();
        float fpsFactor = 500.0f / (float)Math.max(Minecraft.getDebugFPS(), 5);
        VisualHelpers.startImageRendering(new ResourceLocation("main/textures/images/glow.png"));
        for (GhostRenderer3D particle : this.particles) {
            this.updateParticlePosition(particle, fpsFactor);
            particle.onRender(event);
            if (particle.getAlpha() < 1.0f) {
                particle.setAlpha(Math.max(0.0f, particle.getAlpha() - 0.005f * fpsFactor));
            }
            if (!(particle.getAlpha() <= 0.0f)) continue;
            toRemove.add(particle);
        }
        VisualHelpers.finishImageRendering();
        this.particles.removeAll(toRemove);
    }

    private void updateParticlePosition(GhostRenderer3D particle, float fpsFactor) {
        this.moving.animate(this.moving.get() + 20.0f, 55);
        this.targetEspAnim.setForward(this.prevTarget == null);
        int particleIndex = this.particles.indexOf(particle);
        float angleOffset = (float)particleIndex * 360.0f / 3.0f;
        float currentAngle = this.moving.get() + angleOffset;
        double radian = Math.toRadians(currentAngle);
        float x = (float)Math.sin(radian) * (0.3f - this.targetEspAnim.get() * 0.3f);
        float z = (float)Math.cos(radian) * (0.3f - this.targetEspAnim.get() * 0.3f);
        Vector3d targetPos = this.prevTarget.getPositionVec().add(x, 0.2 + (double)(this.prevTarget.getHeight() / 2.0f) * Math.sin(Math.toRadians(this.moving.get() / ((float)particleIndex + 1.0f))), z);
        float mul = 0.05f * fpsFactor;
        particle.setMotion(targetPos.subtract(particle.getPosition()).mul(mul, mul, mul));
    }

    public class GhostPoint {
        public Vector3d position;
        public float size;

        public GhostPoint(Vector3d pos) {
            this.position = pos;
            this.size = 0.5f;
        }

        public void update() {
            this.size = Math.max(this.size - 0.002f, 0.0f);
        }
    }
}
