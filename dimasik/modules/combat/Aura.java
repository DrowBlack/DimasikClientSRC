package dimasik.modules.combat;

import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.input.EventMoveInput;
import dimasik.events.main.movement.EventJump;
import dimasik.events.main.movement.EventNoSlow;
import dimasik.events.main.movement.EventStrafe;
import dimasik.events.main.movement.MovingEvent;
import dimasik.events.main.player.EventElytra;
import dimasik.events.main.player.EventSwimming;
import dimasik.events.main.player.EventSync;
import dimasik.events.main.render.EventRender3D;
import dimasik.helpers.module.aura.AuraHelpers;
import dimasik.helpers.module.aura.RayTrace;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.CheckboxOption;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.managers.module.option.main.SliderOption;
import dimasik.modules.combat.ElytraTarget;
import dimasik.modules.movement.AutoSprint;
import dimasik.utils.math.GCDUtils;
import dimasik.utils.math.MathUtils;
import dimasik.utils.time.TimerUtils;
import lombok.Generated;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShieldItem;
import net.minecraft.item.SwordItem;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Random;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtection;
import ru.dreamix.class2native.experimental.AdditionalReferenceProtectionType;

@AdditionalReferenceProtection(value=AdditionalReferenceProtectionType.GENERATIVE_CONFUSION)
public class Aura
extends Module {
    public Vector2f selfRotation;
    public Vector2f fakeRotation;
    public Vector2f targetRotation;
    public Vector2f fakeTargetRotation;
    private final SelectOption mode = new SelectOption("Mode Rotation", 0, new SelectOptionValue("ReallyWorld"), new SelectOptionValue("Snap"), new SelectOptionValue("AI"), new SelectOptionValue("HollyWorld"));
    public final MultiOption options = new MultiOption("Options", new MultiOptionValue("Only Crits", true), new MultiOptionValue("Shield Breaker", true), new MultiOptionValue("Dont Hit Eating", false), new MultiOptionValue("Unpress Shield", false), new MultiOptionValue("Dont Hit Walls", false), new MultiOptionValue("Random Hits", true), new MultiOptionValue("Only Jump", true));
    private final SelectOption wallsBypass = new SelectOption("Walls Bypass", 0, new SelectOptionValue("None"), new SelectOptionValue("V1"));
    private final SliderOption distance = new SliderOption("Distance", 3.0f, 1.0f, 5.0f).increment(0.05f);
    private final SliderOption preDistance = new SliderOption("Pre Distance", 0.5f, 0.0f, 30.0f).increment(0.5f).visible(() -> !this.mode.getSelected("Snap"));
    private final SliderOption snapTicks = new SliderOption("Snap Ticks", 1.0f, 1.0f, 8.0f).increment(1.0f).visible(() -> this.mode.getSelected("Snap"));
    private final SliderOption speedYaw = new SliderOption("Speed Yaw", 0.5f, 0.1f, 1.0f).increment(0.05f).visible(() -> this.mode.getSelected("Advanced Rotation"));
    private final SliderOption speedPicth = new SliderOption("Speed Pitch", 0.05f, 0.01f, 0.2f).increment(0.01f).visible(() -> this.mode.getSelected("Advanced Rotation"));
    private final SliderOption randomizationSpeed = new SliderOption("Randomization Speed", 10.0f, 0.0f, 20.0f).increment(1.0f).visible(() -> this.mode.getSelected("Advanced Rotation"));
    private final CheckboxOption moveFix = new CheckboxOption("Movement Fix", true);
    private final CheckboxOption paster = new CheckboxOption("Hit The Shield", false);
    private final CheckboxOption clientLook = new CheckboxOption("Client Look", false);
    private final SelectOption correctionType = new SelectOption("Correction Type", 0, new SelectOptionValue("Silent"), new SelectOptionValue("Focus")).visible(this.moveFix::getValue);
    private final MultiOption targets = new MultiOption("Targets", new MultiOptionValue("Players", true), new MultiOptionValue("Creative", true), new MultiOptionValue("Mobs", false), new MultiOptionValue("Naked", true), new MultiOptionValue("Friends", false));
    private final AuraHelpers auraHelpers = new AuraHelpers();
    private final TimerUtils timer = new TimerUtils();
    private final TimerUtils stopWatch = new TimerUtils();
    private LivingEntity target = null;
    private long cps = 0L;
    private long spin = 0L;
    int ticks = 0;
    private final Random random = new Random();
    boolean axyeenn;
    public boolean attacksave;
    public boolean targetAir;
    private boolean testABoolean;
    private boolean aboba;
    public float speed;
    private double prevSpeed = 0.0;
    public double lastSpeed = 0.0;
    private final SliderOption legitAISmoothness = new SliderOption("\u041f\u043b\u0430\u0432\u043d\u043e\u0441\u0442\u044c X", 0.3f, 0.1f, 0.9f).increment(0.05f);
    private final SliderOption legitAISmoothnessX = new SliderOption("\u041f\u043b\u0430\u0432\u043d\u043e\u0441\u0442\u044c Y", 0.3f, 0.1f, 0.9f).increment(0.05f);
    private final SliderOption legitAIJitter = new SliderOption("\u0414\u0436\u0438\u0442\u0442\u0435\u0440", 1.5f, 0.0f, 5.0f).increment(0.1f);
    private final SliderOption legitAIFOV = new SliderOption("Fov", 90.0f, 30.0f, 360.0f).increment(5.0f);
    TimerUtils axyenno;
    private long lastRotationTime = 0L;
    private float reactionDelay = 0.0f;
    private int identyTick = 0;
    private boolean snapBypass;
    private final EventListener<EventUpdate> update = this::update;
    private final EventListener<EventNoSlow> noslow = this::noslow;
    private final EventListener<EventRender3D.Post> renderBox = this::renderBox;
    private final EventListener<MovingEvent> move = this::move;
    private final EventListener<EventSync> sync = this::sync;
    private final EventListener<EventElytra> elytra = this::elytra;
    private final EventListener<EventMoveInput> input = this::input;
    private final EventListener<EventStrafe> strafe = this::strafe;
    private final EventListener<EventJump> jump = this::jump;
    private final EventListener<EventSwimming> swim = this::swimming;

    public Aura() {
        super("Aura", Category.COMBAT);
        this.setCurrentKey(82);
        this.settings(this.mode, this.options, this.wallsBypass, this.distance, this.preDistance, this.snapTicks, this.speedYaw, this.speedPicth, this.randomizationSpeed, this.legitAIFOV, this.legitAIJitter, this.legitAISmoothness, this.legitAISmoothnessX, this.moveFix, this.correctionType, this.targets, this.paster, this.clientLook);
        this.lastSpeed = 0.0;
        this.axyenno = new TimerUtils();
        this.attacksave = false;
        this.testABoolean = false;
        this.aboba = false;
        this.legitAISmoothness.setVisible(() -> this.mode.getSelected("AI"));
        this.legitAISmoothnessX.setVisible(() -> this.mode.getSelected("AI"));
        this.legitAIJitter.setVisible(() -> this.mode.getSelected("AI"));
        this.legitAIFOV.setVisible(() -> this.mode.getSelected("AI"));
    }

    @Override
    public void onEnabled() {
        if (Aura.mc.player != null) {
            this.reset();
            this.target = null;
        }
    }

    @Override
    public void onDisabled() {
        if (!this.mode.getSelected("AI")) {
            this.reset();
            this.stopWatch.setLastMS(0L);
            this.target = null;
            if (Aura.mc.player != null) {
                this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                this.fakeRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                this.targetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                this.fakeTargetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            }
            return;
        }
        this.target = null;
    }

    public void noslow(EventNoSlow noSlow) {
        this.fix();
    }

    private void fix() {
        boolean offHandActive;
        boolean bl = offHandActive = (Boolean)this.paster.getValue() != false && Aura.mc.player.isHandActive() && Aura.mc.player.getActiveHand() == Hand.OFF_HAND;
        if (Aura.mc.player.isHandActive() && !Aura.mc.player.isPassenger() && offHandActive && !Aura.mc.player.getCooldownTracker().hasCooldown(Aura.mc.player.getHeldItemOffhand().getItem())) {
            int old = Aura.mc.player.inventory.currentItem;
            Aura.mc.player.connection.sendPacket(new CHeldItemChangePacket(old + 1 > 8 ? old - 1 : old + 1));
            Aura.mc.player.connection.sendPacket(new CHeldItemChangePacket(Aura.mc.player.inventory.currentItem));
        }
    }

    public void renderBox(EventRender3D.Post e) {
        if (this.target != null && this.target.isElytraFlying() && ((Boolean)Load.getInstance().getHooks().getModuleManagers().getElytraTarget().target.getValue()).booleanValue()) {
            Vector3d targetPos = this.target.getPositionVec();
            Vector3d targetDirection = this.target.getLookVec().normalize();
            Vector3d espPosition = targetPos.add(targetDirection.scale(3.0f + ((Float)Load.getInstance().getHooks().getModuleManagers().getElytraTarget().distance.getValue()).floatValue()));
            VisualHelpers.drawBlockBox(new BlockPos(espPosition), -1);
        }
    }

    private void updateLegitAIRotation(LivingEntity targetEntity) {
        if (targetEntity != null) {
            Vector3d targetPos = targetEntity.getPositionVec().add(0.0, targetEntity.getEyeHeight(), 0.0);
            double deltaX = targetPos.x - Aura.mc.player.getPosX();
            double deltaY = targetPos.y - (Aura.mc.player.getPosY() + (double)Aura.mc.player.getEyeHeight());
            double deltaZ = targetPos.z - Aura.mc.player.getPosZ();
            double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            float yawToTarget = (float)Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0f;
            float pitchToTarget = (float)(-Math.toDegrees(Math.atan2(deltaY, distance)));
            float deltaYaw = MathHelper.wrapDegrees(yawToTarget - this.selfRotation.x);
            float deltaPitch = MathHelper.wrapDegrees(pitchToTarget - this.selfRotation.y);
            if (Math.abs(deltaYaw) > ((Float)this.legitAIFOV.getValue()).floatValue() / 2.0f) {
                return;
            }
            float smoothFactorX = ((Float)this.legitAISmoothness.getValue()).floatValue();
            float smoothFactorY = ((Float)this.legitAISmoothnessX.getValue()).floatValue();
            float jitter = ((Float)this.legitAIJitter.getValue()).floatValue();
            float jitterYaw = (this.random.nextFloat() - 0.5f) * jitter;
            float jitterPitch = (this.random.nextFloat() - 0.5f) * jitter;
            this.selfRotation.x += deltaYaw * smoothFactorX + jitterYaw;
            this.selfRotation.y += deltaPitch * smoothFactorY + jitterPitch;
            this.selfRotation.y = MathHelper.clamp(this.selfRotation.y, -89.0f, 89.0f);
            float gcd = this.getGCDValue();
            this.selfRotation.x -= (this.selfRotation.x - Aura.mc.player.rotationYaw) % gcd;
            this.selfRotation.y -= (this.selfRotation.y - Aura.mc.player.rotationPitch) % gcd;
        } else {
            float deltaYaw = MathHelper.wrapDegrees(Aura.mc.player.rotationYaw - this.selfRotation.x);
            float deltaPitch = MathHelper.wrapDegrees(Aura.mc.player.rotationPitch - this.selfRotation.y);
            if (Math.abs(deltaYaw) > ((Float)this.legitAIFOV.getValue()).floatValue() / 2.0f) {
                return;
            }
            float smoothFactor = 0.2f;
            float jitter = ((Float)this.legitAIJitter.getValue()).floatValue();
            float jitterYaw = (this.random.nextFloat() - 0.5f) * jitter;
            float jitterPitch = (this.random.nextFloat() - 0.5f) * jitter;
            this.selfRotation.x += deltaYaw * smoothFactor;
            this.selfRotation.y += deltaPitch * smoothFactor;
            this.selfRotation.y = MathHelper.clamp(this.selfRotation.y, -89.0f, 89.0f);
            float gcd = this.getGCDValue();
            this.selfRotation.x -= (this.selfRotation.x - Aura.mc.player.rotationYaw) % gcd;
            this.selfRotation.y -= (this.selfRotation.y - Aura.mc.player.rotationPitch) % gcd;
        }
    }

    private float getGCDValue() {
        float f = (float)(Aura.mc.gameSettings.mouseSensitivity * (double)0.6f + (double)0.2f);
        return f * f * f * 8.0f * 0.15f;
    }

    public void update(EventUpdate eventUpdate) {
        this.identyTick = MathUtils.isRangeVector(this.selfRotation, 2.0f) ? ++this.identyTick : 0;
        if (this.mode.getSelected("AI") && this.identyTick > 1 && this.testABoolean) {
            this.reset();
            this.stopWatch.setLastMS(0L);
            this.target = null;
            if (Aura.mc.player != null) {
                this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                this.fakeRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                this.targetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                this.fakeTargetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            }
            this.testABoolean = false;
            Load.getInstance().getEvents().unregister(this);
            return;
        }
        if (!this.testABoolean || !this.mode.getSelected("AI")) {
            this.updateTarget();
            this.target = this.auraHelpers.sortEntities(this.target, ((Float)this.distance.getValue()).floatValue(), Aura.mc.player.isElytraFlying() ? 30.0f : ((Float)this.preDistance.getValue()).floatValue(), this.targets);
        }
        for (PlayerEntity playerEntity : Aura.mc.world.getPlayers()) {
            if (!(Aura.mc.player.getDistance(playerEntity) < 6.0f) || !playerEntity.lastSwing.hasTimeElapsed(200L) || playerEntity.lastSwing.hasTimeElapsed(400L) || !playerEntity.tryAttack || Aura.mc.player.hurtTime <= 0) continue;
            this.target = playerEntity;
            playerEntity.tryAttack = false;
        }
        if (Aura.mc.player.isElytraFlying()) {
            if (this.target != null && this.timer.isReached(200L)) {
                double dx = this.getTarget().getPosX() - this.getTarget().prevPosX;
                double dy = this.getTarget().getPosY() - this.getTarget().prevPosY;
                double dz = this.getTarget().getPosZ() - this.getTarget().prevPosZ;
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
                double speed = distance * 20.0;
                this.prevSpeed = this.lastSpeed;
                this.lastSpeed = speed;
                this.timer.reset();
            }
            if (this.target == null) {
                if (this.mode.getSelected("AI")) {
                    if (this.selfRotation != null && (float)(System.currentTimeMillis() - this.lastRotationTime) >= this.reactionDelay) {
                        this.updateLegitAIRotation(null);
                        this.lastRotationTime = System.currentTimeMillis();
                        this.reactionDelay = 50 + this.random.nextInt(50);
                    }
                } else {
                    this.reset();
                }
            } else {
                if (this.shouldPlayerFalling() && this.stopWatch.hasTimeElapsed()) {
                    if (this.mode.getSelected("HollyWorld")) {
                        this.updateAttack();
                        this.ticks = 1;
                    }
                    if (this.mode.getSelected("Snap") || this.mode.getSelected("Advanced Rotation") || this.mode.getSelected("ReallyWorld") || Aura.mc.player.isElytraFlying()) {
                        this.updateAttack();
                        this.ticks = 2;
                    }
                }
                if (this.mode.getSelected("ReallyWorld") || Aura.mc.player.isElytraFlying() && !this.mode.getSelected("Advanced Rotation")) {
                    this.updateRotation(1.0f);
                } else if (this.mode.getSelected("AI")) {
                    if ((float)(System.currentTimeMillis() - this.lastRotationTime) >= this.reactionDelay) {
                        this.updateLegitAIRotation(this.target);
                        this.lastRotationTime = System.currentTimeMillis();
                        this.reactionDelay = 50 + this.random.nextInt(50);
                    }
                    this.fakeUpdateRotation();
                } else if (this.mode.getSelected("Advanced Rotation")) {
                    this.advancedRotation(((Float)this.speedYaw.getValue()).floatValue() + (float)Math.random() * ((Float)this.randomizationSpeed.getValue()).floatValue() / 100.0f, ((Float)this.speedPicth.getValue()).floatValue() + (float)Math.random() * ((Float)this.randomizationSpeed.getValue()).floatValue() / 100.0f);
                } else if (this.ticks > 0) {
                    this.updateRotation(1.0f);
                    --this.ticks;
                } else {
                    this.reset();
                }
                this.fakeUpdateRotation();
                this.snapBypass = false;
            }
        } else {
            if (this.target == null) {
                if (this.selfRotation != null && this.identyTick < 1 && this.mode.getSelected("AI")) {
                    this.updateLegitAIRotation(null);
                } else {
                    this.reset();
                }
                return;
            }
            this.updateRotation();
            this.attackTarget();
            this.fakeRotation();
            if (this.mode.getSelected("ReallyWorld")) {
                if ((RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() != RayTraceResult.Type.BLOCK || Aura.mc.player.isElytraFlying() || this.wallsBypass.getSelected("None")) && !Load.getInstance().getHooks().getModuleManagers().getWallsBypass().isToggled()) {
                    this.fastRotation();
                } else if (this.ticks > 0) {
                    this.fastRotation();
                    --this.ticks;
                } else {
                    if (Aura.mc.player != null) {
                        Aura.mc.player.rotationYawOffset = -2.1474836E9f;
                    }
                    this.selfRotation = new Vector2f(Aura.mc.player != null ? Aura.mc.player.rotationYaw : 0.0f, Aura.mc.player != null ? Aura.mc.player.rotationPitch : 0.0f);
                }
            }
            if (this.mode.getSelected("AI")) {
                if ((float)(System.currentTimeMillis() - this.lastRotationTime) >= this.reactionDelay) {
                    this.updateLegitAIRotation(this.target);
                    this.lastRotationTime = System.currentTimeMillis();
                    this.reactionDelay = 50 + this.random.nextInt(50);
                }
                this.fakeUpdateRotation();
            }
            if (this.mode.getSelected("Advanced Rotation")) {
                if (RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() != RayTraceResult.Type.BLOCK || Aura.mc.player.isElytraFlying() || this.wallsBypass.getSelected("None")) {
                    this.advancedRotation(((Float)this.speedYaw.getValue()).floatValue() + (float)Math.random() * ((Float)this.randomizationSpeed.getValue()).floatValue() / 100.0f, ((Float)this.speedPicth.getValue()).floatValue() + (float)Math.random() * ((Float)this.randomizationSpeed.getValue()).floatValue() / 100.0f);
                } else if (this.ticks > 0) {
                    this.advancedRotation(((Float)this.speedYaw.getValue()).floatValue() + (float)Math.random() * ((Float)this.randomizationSpeed.getValue()).floatValue() / 100.0f, ((Float)this.speedPicth.getValue()).floatValue() + (float)Math.random() * ((Float)this.randomizationSpeed.getValue()).floatValue() / 100.0f);
                    --this.ticks;
                } else {
                    this.selfRotation = this.auraHelpers.smoothRotation(this.selfRotation, new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch), 2.5f);
                }
            }
            if (this.mode.getSelected("HollyWorld")) {
                if (Aura.mc.player.isInWater()) {
                    Aura.mc.player.setSwimming(false);
                }
                if (this.ticks > 0) {
                    this.applyGcdRotation(this.selfRotation, this.target);
                    --this.ticks;
                } else {
                    this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                }
            }
            if (this.mode.getSelected("Snap")) {
                if (Aura.mc.player.isElytraFlying()) {
                    this.fastRotation();
                } else if (this.ticks > 0) {
                    this.fastRotation();
                    --this.ticks;
                } else {
                    this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
                }
            }
        }
    }

    public void move(MovingEvent eventMove) {
        if (this.target != null) {
            this.aboba = !this.collisionPredict(this.target.getPositionVec(), this.target);
        }
    }

    public boolean collisionPredict(Vector3d to, LivingEntity target) {
        boolean prevCollision = Aura.mc.world.getCollisionShapes(target, target.getBoundingBox().shrink(0.0625)).toList().isEmpty();
        Vector3d backUp = new Vector3d(target.getPosX(), target.getPosY(), target.getPosZ());
        target.setPosition(to.x, to.y, to.z);
        boolean collision = Aura.mc.world.getCollisionShapes(target, Aura.mc.player.getBoundingBox().shrink(0.0625)).toList().isEmpty() && prevCollision;
        target.setPosition(backUp.x, backUp.y, backUp.z);
        return collision;
    }

    private boolean shouldPlayerFalling() {
        boolean bypass;
        boolean water = !Aura.mc.gameSettings.keyBindJump.isKeyDown() && Aura.mc.player.isInWater() || Aura.mc.player.isInWater() && Aura.mc.player.areEyesInFluid(FluidTags.WATER);
        boolean reasonForAttack = Aura.mc.player.isPotionActive(Effects.BLINDNESS) || Aura.mc.player.isOnLadder() || Aura.mc.player.getBlockState().isIn(Blocks.COBWEB) || water || Aura.mc.player.isRidingHorse() || Aura.mc.player.abilities.isFlying || Aura.mc.player.isElytraFlying() || Aura.mc.player.isInLava() && Aura.mc.player.areEyesInFluid(FluidTags.LAVA) || Aura.mc.player.isPotionActive(Effects.LEVITATION) || Aura.mc.player.isPassenger();
        boolean checkWalls = !this.options.getSelected("Dont Hit Walls") || RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() != RayTraceResult.Type.BLOCK;
        boolean checkEat = !this.options.getSelected("Dont Hit Eating") || !Aura.mc.player.isHandActive();
        boolean checkRayTrace = !this.mode.getSelected("Advanced Rotation") || RayTrace.getMouseOver(this.target, this.selfRotation.x, this.selfRotation.y, ((Float)this.distance.getValue()).floatValue()) == this.target;
        double dist = Aura.mc.player.getDistanceEyePos(this.target);
        boolean bl = bypass = dist < (double)((Float)this.distance.getValue()).floatValue();
        if (bypass && Aura.mc.player.getCooledAttackStrength(1.5f) >= 0.93f && checkWalls && checkEat && checkRayTrace) {
            if (Load.getInstance().getHooks().getModuleManagers().getFreeCam().isToggled()) {
                return true;
            }
            if (!reasonForAttack && this.options.getSelected("Only Crits")) {
                return !Aura.mc.player.isOnGround() && Aura.mc.player.fallDistance > 0.0f;
            }
            return true;
        }
        return false;
    }

    public void sync(EventSync event) {
        if (this.selfRotation != null) {
            event.setYaw(this.selfRotation.x);
            event.setPitch(this.selfRotation.y);
            if (((Boolean)this.clientLook.getValue()).booleanValue()) {
                if (this.mode.getSelected("AI") && !Aura.mc.player.isElytraFlying()) {
                    Aura.mc.player.rotationYawHead = this.selfRotation.x;
                    Aura.mc.player.renderYawOffset = this.selfRotation.x;
                    Aura.mc.player.rotationPitchHead = this.selfRotation.y;
                } else if (this.mode.getSelected("AI") && Aura.mc.player.isElytraFlying()) {
                    Aura.mc.player.rotationYaw = this.selfRotation.x;
                    Aura.mc.player.rotationPitch = this.selfRotation.y;
                } else {
                    Aura.mc.player.rotationYaw = this.selfRotation.x;
                    Aura.mc.player.rotationPitch = this.selfRotation.y;
                }
            } else {
                Aura.mc.player.rotationYawHead = this.selfRotation.x;
                Aura.mc.player.renderYawOffset = this.selfRotation.x;
                Aura.mc.player.rotationPitchHead = this.selfRotation.y;
            }
        }
    }

    public void elytra(EventElytra eventElytra) {
        if (this.selfRotation != null) {
            eventElytra.setYaw(this.selfRotation.x);
            eventElytra.setPitch(this.selfRotation.y);
            eventElytra.setVisualPitch(this.selfRotation.y);
        }
    }

    public void input(EventMoveInput eventMoveInput) {
        if (((Boolean)this.moveFix.getValue()).booleanValue() && this.selfRotation != null && this.correctionType.getSelected("Silent")) {
            this.auraHelpers.fixMovement(eventMoveInput, this.selfRotation.x);
        }
    }

    public void strafe(EventStrafe eventStrafe) {
        if (((Boolean)this.moveFix.getValue()).booleanValue() && this.selfRotation != null) {
            eventStrafe.setYaw(this.selfRotation.x);
        }
    }

    public void jump(EventJump eventJump) {
        if (((Boolean)this.moveFix.getValue()).booleanValue() && this.selfRotation != null) {
            eventJump.setYaw(this.selfRotation.x);
        }
    }

    public void swimming(EventSwimming eventSwimming) {
        if (((Boolean)this.moveFix.getValue()).booleanValue() && this.selfRotation != null) {
            eventSwimming.setYaw(this.selfRotation.x);
            eventSwimming.setPitch(this.selfRotation.y);
        }
    }

    private void updateAttack() {
        LivingEntity livingEntity;
        this.stopWatch.setLastMS(460L);
        AutoSprint autoSprint = Load.getInstance().getHooks().getModuleManagers().getAutoSprint();
        if (autoSprint.mode.getSelected("Rage") && CEntityActionPacket.lastUpdatedSprint && autoSprint.canSprint() && !Aura.mc.player.isInWater()) {
            Aura.mc.player.connection.sendPacket(new CEntityActionPacket(Aura.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
        }
        Aura.mc.playerController.attackEntity(Aura.mc.player, this.target);
        Aura.mc.player.swingArm(Hand.MAIN_HAND);
        if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint() && !Aura.mc.player.isInWater()) {
            Aura.mc.player.connection.sendPacket(new CEntityActionPacket(Aura.mc.player, CEntityActionPacket.Action.START_SPRINTING));
        }
        if ((livingEntity = this.target) instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)livingEntity;
            if (this.options.getSelected("Shield Breaker")) {
                this.shieldBreaker(player);
            }
        }
        this.auraHelpers.randomX.generate();
        this.auraHelpers.randomY.generate();
    }

    private void shieldBreaker(PlayerEntity entity) {
        if (entity.isBlocking()) {
            int invSlot = this.getAxe(false);
            int hotBarSlot = this.getAxe(true);
            if (hotBarSlot == -1 && invSlot != -1) {
                int bestSlot = this.findBestSlotInHotBar();
                Aura.mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, Aura.mc.player);
                Aura.mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, Aura.mc.player);
                Aura.mc.player.connection.sendPacket(new CHeldItemChangePacket(bestSlot));
                Aura.mc.playerController.attackEntity(Aura.mc.player, entity);
                Aura.mc.player.swingArm(Hand.MAIN_HAND);
                Aura.mc.player.connection.sendPacket(new CHeldItemChangePacket(Aura.mc.player.inventory.currentItem));
                Aura.mc.playerController.windowClick(0, bestSlot + 36, 0, ClickType.PICKUP, Aura.mc.player);
                Aura.mc.playerController.windowClick(0, invSlot, 0, ClickType.PICKUP, Aura.mc.player);
            }
            if (hotBarSlot != -1) {
                Aura.mc.player.connection.sendPacket(new CHeldItemChangePacket(hotBarSlot));
                Aura.mc.playerController.attackEntity(Aura.mc.player, entity);
                Aura.mc.player.swingArm(Hand.MAIN_HAND);
                Aura.mc.player.connection.sendPacket(new CHeldItemChangePacket(Aura.mc.player.inventory.currentItem));
            }
        }
    }

    public int findBestSlotInHotBar() {
        int emptySlot = this.findEmptySlot();
        return emptySlot != -1 ? emptySlot : this.findNonSwordSlot();
    }

    private int findEmptySlot() {
        for (int i = 0; i < 9; ++i) {
            if (!Aura.mc.player.inventory.getStackInSlot(i).isEmpty() || Aura.mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    private int findNonSwordSlot() {
        for (int i = 0; i < 9; ++i) {
            if (Aura.mc.player.inventory.getStackInSlot(i).getItem() instanceof SwordItem || Aura.mc.player.inventory.getStackInSlot(i).getItem() instanceof ElytraItem || Aura.mc.player.inventory.currentItem == i) continue;
            return i;
        }
        return -1;
    }

    private int getAxe(boolean hotBar) {
        int startSlot = hotBar ? 0 : 9;
        int endSlot = hotBar ? 9 : 36;
        for (int i = startSlot; i < endSlot; ++i) {
            ItemStack itemStack = Aura.mc.player.inventory.getStackInSlot(i);
            if (!(itemStack.getItem() instanceof AxeItem)) continue;
            return i;
        }
        return -1;
    }

    public Vector2f calculateRotation(Vector2f current, LivingEntity target) {
        Vector2f angleDelta = AngleUtil.calculateDelta(current, this.targetRotation);
        float yawDelta = angleDelta.x;
        float pitchDelta = angleDelta.y;
        float rotationDifference = (float)Math.hypot(Math.abs(yawDelta), Math.abs(pitchDelta));
        if (target != null) {
            float straightLineYaw = Math.abs(yawDelta / rotationDifference) * 265.0f;
            float straightLinePitch = Math.abs(pitchDelta / rotationDifference) * 265.0f;
            return new Vector2f(current.x + Math.min(Math.max(yawDelta, -straightLineYaw), straightLineYaw), current.y + Math.min(Math.max(pitchDelta, -straightLinePitch), straightLinePitch));
        }
        float lineYaw = Math.abs(yawDelta / rotationDifference) * 90.0f;
        float linePitch = Math.abs(pitchDelta / rotationDifference) * 90.0f;
        float moveYaw = MathHelper.clamp(yawDelta, -lineYaw, lineYaw);
        float movePitch = MathHelper.clamp(pitchDelta, -linePitch, linePitch);
        Vector2f moveAngle = new Vector2f(current.x, current.y);
        moveAngle.setX(MathHelper.lerp(MathUtils.random1(0.1f, 0.3f), current.x, current.x + moveYaw));
        moveAngle.setY(MathHelper.lerp(MathUtils.random1(0.1f, 0.3f), current.y, current.y + movePitch));
        return new Vector2f(moveAngle.x, moveAngle.y);
    }

    public void applyGcdRotation(Vector2f current, LivingEntity target) {
        this.selfRotation = this.auraHelpers.applyRotation(this.calculateRotation(current, target));
    }

    private void fakeUpdateRotation() {
        Vector3d Vector3d2 = this.target.getPositionVec().add(0.0, MathHelper.clamp(Aura.mc.player.getEyePosition((float)Aura.mc.getRenderPartialTicks()).y - this.target.getPosY(), 0.0, (double)this.target.getHeight() * (MathHelper.getStrictDistance(this.target) / (double)Math.max(Aura.mc.playerController.extendedReach() ? 6.0f : 3.0f, ((Float)this.distance.getValue()).floatValue()))), 0.0).subtract(Aura.mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
        float rawYaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(Vector3d2.z, Vector3d2.x)) - 90.0);
        float rawPitch = (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(Vector3d2.y, Math.hypot(Vector3d2.x, Vector3d2.z))));
        float yawDelta = MathHelper.wrapDegrees(rawYaw - this.fakeRotation.x);
        float pitchDelta = MathHelper.wrapDegrees(rawPitch - this.fakeRotation.y);
        if (Math.abs(yawDelta) > 180.0f) {
            yawDelta -= Math.signum(yawDelta) * 360.0f;
        }
        float additionYaw = MathHelper.clamp(yawDelta, -180.0f, 180.0f);
        float additionPitch = MathHelper.clamp(pitchDelta, -90.0f, 90.0f);
        float yaw = this.fakeRotation.x + additionYaw;
        float pitch = this.fakeRotation.y + additionPitch;
        float x = GCDUtils.getFixedRotation(yaw);
        float y = GCDUtils.getFixedRotation(MathHelper.clamp(pitch, -89.0f, 89.0f));
        this.fakeRotation.x = x;
        this.fakeRotation.y = y;
    }

    private void updateRotation(float speed) {
        if (this.selfRotation == null) {
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            return;
        }
        Vector3d Vector3d2 = this.getVector3d(Aura.mc.player, this.target, true);
        float rawYaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(Vector3d2.z, Vector3d2.x)) - 90.0);
        float rawPitch = (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(Vector3d2.y, Math.hypot(Vector3d2.x, Vector3d2.z))));
        float yawDelta = MathHelper.wrapDegrees(rawYaw - this.selfRotation.x);
        float pitchDelta = MathHelper.wrapDegrees(rawPitch - this.selfRotation.y);
        if (Math.abs(yawDelta) > 180.0f && Aura.mc.player.isSwimming()) {
            yawDelta -= Math.signum(yawDelta) * 360.0f;
        }
        float additionYaw = MathHelper.clamp(yawDelta, -180.0f * speed, 180.0f * speed);
        float additionPitch = MathHelper.clamp(pitchDelta, -90.0f * speed, 90.0f * speed);
        float yaw = this.selfRotation.x + additionYaw;
        float pitch = this.selfRotation.y + additionPitch;
        if (!Aura.mc.player.isElytraFlying()) {
            float randomFactor = this.auraHelpers.randomX.getCurrent();
            yaw += (float)(Math.random() * (double)randomFactor - (double)(randomFactor / 2.0f));
            randomFactor = this.auraHelpers.randomY.getCurrent();
            pitch += (float)(Math.random() * (double)randomFactor - (double)(randomFactor / 2.0f));
        }
        float x = GCDUtils.getFixedRotation(yaw);
        float y = GCDUtils.getFixedRotation(MathHelper.clamp(pitch, -89.0f, 89.0f));
        this.selfRotation.x = x;
        this.selfRotation.y = y;
    }

    private void advancedRotation(float speedX, float speedY) {
        if (this.selfRotation == null) {
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            return;
        }
        Vector3d Vector3d2 = this.getVector3d(Aura.mc.player, this.target, true);
        float rawYaw = (float)MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(Vector3d2.z, Vector3d2.x)) - 90.0);
        float rawPitch = (float)MathHelper.wrapDegrees(Math.toDegrees(-Math.atan2(Vector3d2.y, Math.hypot(Vector3d2.x, Vector3d2.z))));
        float yawDelta = MathHelper.wrapDegrees(rawYaw - this.selfRotation.x);
        float pitchDelta = MathHelper.wrapDegrees(rawPitch - this.selfRotation.y);
        if (Math.abs(yawDelta) > 180.0f) {
            yawDelta -= Math.signum(yawDelta) * 360.0f;
        }
        float additionYaw = MathHelper.clamp(yawDelta, -180.0f * speedX, 180.0f * speedX);
        float additionPitch = MathHelper.clamp(pitchDelta, -90.0f * speedY, 90.0f * speedY);
        float yaw = this.selfRotation.x + additionYaw;
        float pitch = this.selfRotation.y + additionPitch;
        if (!Aura.mc.player.isElytraFlying()) {
            float randomFactor = this.auraHelpers.randomX.getCurrent();
            yaw += (float)(Math.random() * (double)randomFactor - (double)(randomFactor / 2.0f));
            randomFactor = this.auraHelpers.randomY.getCurrent();
            pitch += (float)(Math.random() * (double)randomFactor - (double)(randomFactor / 2.0f));
        }
        float x = GCDUtils.getFixedRotation(yaw);
        float y = GCDUtils.getFixedRotation(MathHelper.clamp(pitch, -89.0f, 89.0f));
        this.selfRotation.x = x;
        this.selfRotation.y = y;
    }

    private Vector3d getVector3d(LivingEntity me, LivingEntity to, boolean targetAir) {
        float dist = 0.0f;
        if (this.target != null) {
            double dx = this.getTarget().getPosX() - this.getTarget().prevPosX;
            double dy = this.getTarget().getPosY() - this.getTarget().prevPosY;
            double dz = this.getTarget().getPosZ() - this.getTarget().prevPosZ;
            double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
            dist = (float)distance;
        }
        if (this.axyenno.isReached(1000L) && Aura.mc.player.isElytraFlying()) {
            this.axyeenn = true;
            this.axyenno.reset();
        }
        if (this.stopWatch.getTime() > 100L) {
            this.attacksave = true;
        } else if (this.stopWatch.getTime() < 100L) {
            this.attacksave = false;
        }
        ElytraTarget elytraTarget = Load.getInstance().getHooks().getModuleManagers().getElytraTarget();
        this.speed = dist;
        boolean antiAir = this.lastSpeed >= 20.0 || this.lastSpeed != this.prevSpeed && this.lastSpeed == 0.0;
        boolean xuy = this.target != null && (Boolean)elytraTarget.getTarget().getValue() != false && targetAir && antiAir && Aura.mc.player.isElytraFlying() && !Load.getInstance().getHooks().getModuleManagers().getFreeCam().isToggled();
        float SpeedBypass = xuy ? 3.0f + ((Float)elytraTarget.getDistance().getValue()).floatValue() : 0.0f;
        Vector3d targetPos = to.getPositionVec().add(to.getForward().normalize().scale(SpeedBypass));
        Vector3d vec = this.target.getPositionVec().add(0.0, MathHelper.clamp(Aura.mc.player.getEyePosition((float)Aura.mc.getRenderPartialTicks()).y - this.target.getPosY(), 0.0, (double)this.target.getHeight() * (MathHelper.getStrictDistance(this.target) / (double)Math.max(Aura.mc.playerController.extendedReach() ? 6.0f : 3.0f, ((Float)this.distance.getValue()).floatValue()))), 0.0).subtract(Aura.mc.player.getEyePosition(mc.getRenderPartialTicks())).normalize();
        this.targetAir = xuy;
        return targetAir && xuy && !this.snapBypass ? new Vector3d(targetPos.getX() - me.getPosX(), targetPos.getY() - me.getPosY(), targetPos.getZ() - me.getPosZ()) : vec;
    }

    private void reset() {
        if (Aura.mc.player != null) {
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.fakeRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.targetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.fakeTargetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
    }

    private void updateTarget() {
        this.target = this.auraHelpers.sortEntities(this.target, ((Float)this.distance.getValue()).floatValue(), this.mode.getSelected("Snap") ? 0.0f : ((Float)this.preDistance.getValue()).floatValue(), this.targets);
    }

    private void updateRotation() {
        if (this.target != null) {
            this.fakeTargetRotation = this.auraHelpers.fakeRotationAngles(this.target);
            this.targetRotation = this.auraHelpers.rotationAngles(this.target, false, Load.getInstance().getHooks().getModuleManagers().getElytraTarget());
        } else {
            this.fakeTargetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.targetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
    }

    private void fastRotation() {
        if (this.selfRotation == null) {
            this.selfRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
        if (this.targetRotation == null) {
            this.targetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
        this.selfRotation = this.auraHelpers.applyRotation(this.auraHelpers.fastRotation(this.selfRotation, this.targetRotation));
    }

    private void fakeRotation() {
        if (this.fakeRotation == null) {
            this.fakeRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
        if (this.fakeTargetRotation == null) {
            this.fakeTargetRotation = new Vector2f(Aura.mc.player.rotationYaw, Aura.mc.player.rotationPitch);
        }
        this.fakeRotation = this.auraHelpers.applyRotation(this.auraHelpers.fakeRotation(this.fakeRotation, this.fakeTargetRotation));
    }

    private void attackTarget() {
        AutoSprint autoSprint = Load.getInstance().getHooks().getModuleManagers().getAutoSprint();
        if (this.auraHelpers.attack(this.target, this.options, ((Float)this.distance.getValue()).floatValue(), this.options.getSelected("Only Crits"), this.options.getSelected("Random Hits")) && this.cps <= System.currentTimeMillis()) {
            int n = this.ticks = this.wallsBypass.getSelected("V1") && RayTrace.blockResult(((Float)this.distance.getValue()).floatValue(), this.selfRotation.x, this.selfRotation.y, Aura.mc.player).getType() == RayTraceResult.Type.BLOCK || Load.getInstance().getHooks().getModuleManagers().getWallsBypass().isToggled() ? 1 : ((Float)this.snapTicks.getValue()).intValue();
            if (Aura.mc.player.isHandActive() && Aura.mc.player.getActiveHand() == Hand.OFF_HAND && Aura.mc.player.getHeldItemOffhand().getItem() instanceof ShieldItem && this.options.getSelected("Unpress Shield")) {
                Aura.mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.RELEASE_USE_ITEM, new BlockPos(0, 0, 0), Direction.DOWN));
            }
            if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint() && CEntityActionPacket.lastUpdatedSprint) {
                Aura.mc.player.connection.sendPacket(new CEntityActionPacket(Aura.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            }
            Aura.mc.playerController.attackEntity(Aura.mc.player, this.target);
            Aura.mc.player.swingArm(Hand.MAIN_HAND);
            if (this.options.getSelected("Shield Breaker")) {
                this.auraHelpers.shieldBreaker(this.target);
            }
            if (autoSprint.mode.getSelected("Rage") && autoSprint.canSprint()) {
                Aura.mc.player.connection.sendPacket(new CEntityActionPacket(Aura.mc.player, CEntityActionPacket.Action.START_SPRINTING));
            }
            this.cps = System.currentTimeMillis() + 460L + (Load.getInstance().getHooks().getModuleManagers().getElytraTarget().mode.getSelected("CakeWorld") && Aura.mc.player.isElytraFlying() && this.target.isElytraFlying() ? 100L : 0L);
            this.spin = (long)((double)System.currentTimeMillis() + 432.0);
        }
    }

    @Generated
    public SelectOption getMode() {
        return this.mode;
    }

    @Generated
    public SliderOption getDistance() {
        return this.distance;
    }

    @Generated
    public LivingEntity getTarget() {
        return this.target;
    }

    @Generated
    public long getCps() {
        return this.cps;
    }

    @Generated
    public long getSpin() {
        return this.spin;
    }

    @Generated
    public boolean isSnapBypass() {
        return this.snapBypass;
    }

    private static class AngleUtil {
        private AngleUtil() {
        }

        public static Vector2f calculateDelta(Vector2f current, Vector2f target) {
            float yawDelta = MathHelper.wrapDegrees(target.x - current.x);
            float pitchDelta = MathHelper.wrapDegrees(target.y - current.y);
            return new Vector2f(yawDelta, pitchDelta);
        }
    }
}
