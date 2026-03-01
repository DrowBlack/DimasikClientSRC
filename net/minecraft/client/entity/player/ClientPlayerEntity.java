package net.minecraft.client.entity.player;

import com.google.common.collect.Lists;
import dimasik.Load;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.chat.EventSendMessage;
import dimasik.events.main.misc.EventPush;
import dimasik.events.main.movement.EventMove;
import dimasik.events.main.movement.EventNoSlow;
import dimasik.events.main.player.EventCloseScreen;
import dimasik.events.main.player.EventSync;
import dimasik.events.main.render.EventGameOverlay;
import dimasik.itemics.api.IItemics;
import dimasik.itemics.api.ItemicsAPI;
import dimasik.itemics.api.event.events.ChatEvent;
import dimasik.itemics.api.event.events.PlayerUpdateEvent;
import dimasik.itemics.api.event.events.type.EventState;
import dimasik.itemics.utils.PlayerMovementInput;
import dimasik.managers.client.ClientManagers;
import dimasik.utils.math.HookHelper;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.BiomeSoundHandler;
import net.minecraft.client.audio.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.audio.IAmbientSoundHandler;
import net.minecraft.client.audio.RidingMinecartTickableSound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.audio.UnderwaterAmbientSoundHandler;
import net.minecraft.client.audio.UnderwaterAmbientSounds;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.gui.screen.CommandBlockScreen;
import net.minecraft.client.gui.screen.EditBookScreen;
import net.minecraft.client.gui.screen.EditMinecartCommandBlockScreen;
import net.minecraft.client.gui.screen.EditSignScreen;
import net.minecraft.client.gui.screen.EditStructureScreen;
import net.minecraft.client.gui.screen.JigsawScreen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IJumpingMount;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.Pose;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.play.client.CAnimateHandPacket;
import net.minecraft.network.play.client.CChatMessagePacket;
import net.minecraft.network.play.client.CClientStatusPacket;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CInputPacket;
import net.minecraft.network.play.client.CMarkRecipeSeenPacket;
import net.minecraft.network.play.client.CMoveVehiclePacket;
import net.minecraft.network.play.client.CPlayerAbilitiesPacket;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.CommandBlockLogic;
import net.minecraft.tileentity.CommandBlockTileEntity;
import net.minecraft.tileentity.JigsawTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.MovementInput;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;

public class ClientPlayerEntity
extends AbstractClientPlayerEntity {
    public final ClientPlayNetHandler connection;
    private final StatisticsManager stats;
    private final ClientRecipeBook recipeBook;
    private final List<IAmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
    private int permissionLevel = 0;
    private double lastReportedPosX;
    private double lastReportedPosY;
    private double lastReportedPosZ;
    private float lastReportedYaw;
    private float lastReportedPitch;
    private boolean prevOnGround;
    private boolean isCrouching;
    private boolean clientSneakState;
    public boolean serverSprintState;
    private int positionUpdateTicks;
    private boolean hasValidHealth;
    private String serverBrand;
    public MovementInput movementInput;
    protected final Minecraft mc;
    protected int sprintToggleTimer;
    public int sprintingTicksLeft;
    public float renderArmYaw;
    public float renderArmPitch;
    public float prevRenderArmYaw;
    public float prevRenderArmPitch;
    private int horseJumpPowerCounter;
    private float horseJumpPower;
    public float timeInPortal;
    public float prevTimeInPortal;
    private boolean handActive;
    private Hand activeHand;
    private boolean rowingBoat;
    private boolean autoJumpEnabled = true;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int counterInWater;
    private boolean showDeathScreen = true;
    private static boolean changed = false;
    public float packetYaw;
    public float packetPitch;

    public ClientPlayerEntity(Minecraft mc, ClientWorld world, ClientPlayNetHandler connection, StatisticsManager stats, ClientRecipeBook recipeBook, boolean clientSneakState, boolean clientSprintState) {
        super(world, connection.getGameProfile());
        this.mc = mc;
        this.connection = connection;
        this.stats = stats;
        this.recipeBook = recipeBook;
        this.clientSneakState = clientSneakState;
        this.serverSprintState = clientSprintState;
        this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, mc.getSoundHandler()));
        this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
        this.ambientSoundHandlers.add(new BiomeSoundHandler(this, mc.getSoundHandler(), world.getBiomeManager()));
        if (!changed) {
            ClientManagers.changeLanguage(ClientManagers.getLanguage());
            changed = true;
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        return false;
    }

    @Override
    public void heal(float healAmount) {
    }

    public boolean isEating() {
        return this.mc.player.isHandActive() && this.mc.player.getActiveItemStack().getItem().isFood();
    }

    @Override
    public boolean startRiding(Entity entityIn, boolean force) {
        if (!super.startRiding(entityIn, force)) {
            return false;
        }
        if (entityIn instanceof AbstractMinecartEntity) {
            this.mc.getSoundHandler().play(new RidingMinecartTickableSound(this, (AbstractMinecartEntity)entityIn));
        }
        if (entityIn instanceof BoatEntity) {
            this.prevRotationYaw = entityIn.rotationYaw;
            this.rotationYaw = entityIn.rotationYaw;
            this.setRotationYawHead(entityIn.rotationYaw);
        }
        return true;
    }

    @Override
    public void dismount() {
        super.dismount();
        this.rowingBoat = false;
    }

    @Override
    public float getPitch(float partialTicks) {
        return this.rotationPitch;
    }

    @Override
    public float getYaw(float partialTicks) {
        return this.isPassenger() ? super.getYaw(partialTicks) : this.rotationYaw;
    }

    @Override
    public void tick() {
        if (this.world.isBlockLoaded(new BlockPos(this.getPosX(), 0.0, this.getPosZ()))) {
            EventUpdate eventUpdate = new EventUpdate();
            Load.getInstance().getEvents().call(eventUpdate);
            super.tick();
            if (this.isPassenger()) {
                this.connection.sendPacket(new CPlayerPacket.RotationPacket(this.rotationYaw, this.rotationPitch, this.onGround));
                this.connection.sendPacket(new CInputPacket(this.moveStrafing, this.moveForward, this.movementInput.jump, this.movementInput.sneaking));
                Entity entity = this.getLowestRidingEntity();
                if (entity != this && entity.canPassengerSteer()) {
                    this.connection.sendPacket(new CMoveVehiclePacket(entity));
                }
            } else {
                this.packetPitch = this.rotationPitch;
                this.packetYaw = this.rotationYaw;
                IItemics itemics = ItemicsAPI.getProvider().getItemicsForPlayer(this);
                if (itemics != null) {
                    itemics.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.PRE));
                }
                this.onUpdateWalkingPlayer();
                if (itemics != null) {
                    itemics.getGameEventHandler().onPlayerUpdate(new PlayerUpdateEvent(EventState.POST));
                }
            }
            for (IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
                iambientsoundhandler.tick();
            }
        }
    }

    public float getDarknessAmbience() {
        for (IAmbientSoundHandler iambientsoundhandler : this.ambientSoundHandlers) {
            if (!(iambientsoundhandler instanceof BiomeSoundHandler)) continue;
            return ((BiomeSoundHandler)iambientsoundhandler).getDarknessAmbienceChance();
        }
        return 0.0f;
    }

    private void onUpdateWalkingPlayer() {
        boolean flag = this.isSprinting();
        EventSync eventSync = new EventSync(this.rotationYaw, this.rotationPitch, this.getPosX(), this.getPosY(), this.getPosZ(), this.onGround, flag);
        IItemics itemics = ItemicsAPI.getProvider().getItemicsForPlayer(this);
        if (itemics != null && itemics.getMineProcess().isActive()) {
            eventSync = new EventSync(this.packetYaw, this.packetPitch, this.getPosX(), this.getPosY(), this.getPosZ(), this.onGround, flag);
            this.mc.player.rotationPitchHead = eventSync.getPitch();
            this.mc.player.renderYawOffset = eventSync.getYaw();
            this.mc.player.rotationYawHead = eventSync.getYaw();
        }
        Load.getInstance().getEvents().call(eventSync);
        if (!eventSync.isCancelled()) {
            boolean flag3;
            if (eventSync.isSprint() != this.serverSprintState) {
                CEntityActionPacket.Action centityactionpacket$action = eventSync.isSprint() ? CEntityActionPacket.Action.START_SPRINTING : CEntityActionPacket.Action.STOP_SPRINTING;
                this.connection.sendPacket(new CEntityActionPacket(this, centityactionpacket$action));
                this.serverSprintState = eventSync.isSprint();
            }
            if ((flag3 = this.isSneaking()) != this.clientSneakState) {
                CEntityActionPacket.Action centityactionpacket$action1 = flag3 ? CEntityActionPacket.Action.PRESS_SHIFT_KEY : CEntityActionPacket.Action.RELEASE_SHIFT_KEY;
                this.connection.sendPacket(new CEntityActionPacket(this, centityactionpacket$action1));
                this.clientSneakState = flag3;
            }
            if (this.isCurrentViewEntity()) {
                boolean flag2;
                double d4 = eventSync.getPosX() - this.lastReportedPosX;
                double d0 = eventSync.getPosY() - this.lastReportedPosY;
                double d1 = eventSync.getPosZ() - this.lastReportedPosZ;
                double d2 = eventSync.getYaw() - this.lastReportedYaw;
                double d3 = eventSync.getPitch() - this.lastReportedPitch;
                ++this.positionUpdateTicks;
                boolean flag1 = d4 * d4 + d0 * d0 + d1 * d1 > 9.0E-4 || this.positionUpdateTicks >= 20;
                boolean bl = flag2 = d2 != 0.0 || d3 != 0.0;
                if (this.isPassenger()) {
                    Vector3d vector3d = this.getMotion();
                    this.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(vector3d.x, -999.0, vector3d.z, eventSync.getYaw(), eventSync.getPitch(), eventSync.isOnGround()));
                    flag1 = false;
                } else if (flag1 && flag2) {
                    this.connection.sendPacket(new CPlayerPacket.PositionRotationPacket(eventSync.getPosX(), eventSync.getPosY(), eventSync.getPosZ(), eventSync.getYaw(), eventSync.getPitch(), eventSync.isOnGround()));
                } else if (flag1) {
                    this.connection.sendPacket(new CPlayerPacket.PositionPacket(eventSync.getPosX(), eventSync.getPosY(), eventSync.getPosZ(), eventSync.isOnGround()));
                } else if (flag2) {
                    this.connection.sendPacket(new CPlayerPacket.RotationPacket(eventSync.getYaw(), eventSync.getPitch(), eventSync.isOnGround()));
                } else if (this.prevOnGround != eventSync.isOnGround()) {
                    this.connection.sendPacket(new CPlayerPacket(eventSync.isOnGround()));
                }
                if (flag1) {
                    this.lastReportedPosX = eventSync.getPosX();
                    this.lastReportedPosY = eventSync.getPosY();
                    this.lastReportedPosZ = eventSync.getPosZ();
                    this.positionUpdateTicks = 0;
                }
                if (flag2) {
                    this.lastReportedYaw = eventSync.getYaw();
                    this.lastReportedPitch = eventSync.getPitch();
                }
                this.prevOnGround = eventSync.isOnGround();
                this.autoJumpEnabled = this.mc.gameSettings.autoJump;
            }
        }
    }

    @Override
    public boolean drop(boolean p_225609_1_) {
        CPlayerDiggingPacket.Action cplayerdiggingpacket$action = p_225609_1_ ? CPlayerDiggingPacket.Action.DROP_ALL_ITEMS : CPlayerDiggingPacket.Action.DROP_ITEM;
        this.connection.sendPacket(new CPlayerDiggingPacket(cplayerdiggingpacket$action, BlockPos.ZERO, Direction.DOWN));
        return this.inventory.decrStackSize(this.inventory.currentItem, p_225609_1_ && !this.inventory.getCurrentItem().isEmpty() ? this.inventory.getCurrentItem().getCount() : 1) != ItemStack.EMPTY;
    }

    public void sendChatMessage(String message) {
        EventSendMessage eventSendMessage = new EventSendMessage(message);
        Load.getInstance().getEvents().call(eventSendMessage);
        if (message.contains(ClientManagers.getRandom()) && ClientManagers.isUnHook()) {
            ClientManagers.unhook();
            return;
        }
        if (!ClientManagers.isUnHook()) {
            IItemics itemics = ItemicsAPI.getProvider().getItemicsForPlayer(this);
            if (itemics == null) {
                return;
            }
            ChatEvent event = new ChatEvent(message);
            itemics.getGameEventHandler().onSendChatMessage(event);
            if (event.isCancelled()) {
                return;
            }
            Load.getInstance().getHooks().getCommandManagers().run(message);
        }
        if (!eventSendMessage.isCancelled() && !Load.getInstance().getHooks().getCommandManagers().isMessage()) {
            this.connection.sendPacket(new CChatMessagePacket(message));
        }
    }

    @Override
    public void swingArm(Hand hand) {
        super.swingArm(hand);
        this.connection.sendPacket(new CAnimateHandPacket(hand));
    }

    @Override
    public void respawnPlayer() {
        this.connection.sendPacket(new CClientStatusPacket(CClientStatusPacket.State.PERFORM_RESPAWN));
    }

    @Override
    protected void damageEntity(DamageSource damageSrc, float damageAmount) {
        if (!this.isInvulnerableTo(damageSrc)) {
            this.setHealth(this.getHealth() - damageAmount);
        }
    }

    @Override
    public void closeScreen() {
        this.connection.sendPacket(new CCloseWindowPacket(this.openContainer.windowId));
        Load.getInstance().getEvents().call(new EventCloseScreen());
        this.closeScreenAndDropStack();
    }

    public void closeScreenAndDropStack() {
        this.inventory.setItemStack(ItemStack.EMPTY);
        super.closeScreen();
        this.mc.displayGuiScreen(null);
    }

    public void setPlayerSPHealth(float health) {
        if (this.hasValidHealth) {
            float f = this.getHealth() - health;
            if (f <= 0.0f) {
                this.setHealth(health);
                if (f < 0.0f) {
                    this.hurtResistantTime = 10;
                }
            } else {
                this.lastDamage = f;
                this.setHealth(this.getHealth());
                this.hurtResistantTime = 20;
                this.damageEntity(DamageSource.GENERIC, f);
                this.hurtTime = this.maxHurtTime = 10;
            }
        } else {
            this.setHealth(health);
            this.hasValidHealth = true;
        }
    }

    @Override
    public void sendPlayerAbilities() {
        this.connection.sendPacket(new CPlayerAbilitiesPacket(this.abilities));
    }

    @Override
    public boolean isUser() {
        return true;
    }

    @Override
    public boolean hasStoppedClimbing() {
        return !this.abilities.isFlying && super.hasStoppedClimbing();
    }

    @Override
    public boolean func_230269_aK_() {
        return !this.abilities.isFlying && super.func_230269_aK_();
    }

    @Override
    public boolean getMovementSpeed() {
        return !this.abilities.isFlying && super.getMovementSpeed();
    }

    protected void sendHorseJump() {
        this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.START_RIDING_JUMP, MathHelper.floor(this.getHorseJumpPower() * 100.0f)));
    }

    public void sendHorseInventory() {
        this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.OPEN_INVENTORY));
    }

    public void setServerBrand(String brand) {
        this.serverBrand = brand;
    }

    public String getServerBrand() {
        return this.serverBrand;
    }

    public StatisticsManager getStats() {
        return this.stats;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void removeRecipeHighlight(IRecipe<?> recipe) {
        if (this.recipeBook.isNew(recipe)) {
            this.recipeBook.markSeen(recipe);
            this.connection.sendPacket(new CMarkRecipeSeenPacket(recipe));
        }
    }

    @Override
    protected int getPermissionLevel() {
        return this.permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    @Override
    public void sendStatusMessage(ITextComponent chatComponent, boolean actionBar) {
        if (actionBar) {
            this.mc.ingameGUI.setOverlayMessage(chatComponent, false);
        } else {
            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponent);
        }
    }

    private void setPlayerOffsetMotion(double x, double z) {
        BlockPos blockpos = new BlockPos(x, this.getPosY(), z);
        if (this.shouldBlockPushPlayer(blockpos)) {
            Direction[] adirection;
            double d0 = x - (double)blockpos.getX();
            double d1 = z - (double)blockpos.getZ();
            Direction direction = null;
            double d2 = Double.MAX_VALUE;
            for (Direction direction1 : adirection = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}) {
                double d4;
                double d3 = direction1.getAxis().getCoordinate(d0, 0.0, d1);
                double d = d4 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d3 : d3;
                if (!(d4 < d2) || this.shouldBlockPushPlayer(blockpos.offset(direction1))) continue;
                d2 = d4;
                direction = direction1;
            }
            if (direction != null) {
                Vector3d vector3d = this.getMotion();
                if (direction.getAxis() == Direction.Axis.X) {
                    this.setMotion(0.1 * (double)direction.getXOffset(), vector3d.y, vector3d.z);
                } else {
                    this.setMotion(vector3d.x, vector3d.y, 0.1 * (double)direction.getZOffset());
                }
            }
        }
    }

    private boolean shouldBlockPushPlayer(BlockPos pos) {
        EventPush eventPush = new EventPush(EventPush.PushType.Blocks);
        Load.getInstance().getEvents().call(eventPush);
        if (eventPush.isCancelled()) {
            return false;
        }
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(pos.getX(), axisalignedbb.minY, pos.getZ(), (double)pos.getX() + 1.0, axisalignedbb.maxY, (double)pos.getZ() + 1.0).shrink(1.0E-7);
        return !this.world.func_242405_a(this, axisalignedbb1, (state, pos2) -> state.isSuffocating(this.world, (BlockPos)pos2));
    }

    @Override
    public void setSprinting(boolean sprinting) {
        super.setSprinting(sprinting);
        this.sprintingTicksLeft = 0;
    }

    public void setXPStats(float currentXP, int maxXP, int level) {
        this.experience = currentXP;
        this.experienceTotal = maxXP;
        this.experienceLevel = level;
    }

    @Override
    public void sendMessage(ITextComponent component, UUID senderUUID) {
        this.mc.ingameGUI.getChatGUI().printChatMessage(component);
    }

    @Override
    public void handleStatusUpdate(byte id) {
        if (id >= 24 && id <= 28) {
            this.setPermissionLevel(id - 24);
        } else {
            super.handleStatusUpdate(id);
        }
    }

    public void setShowDeathScreen(boolean show) {
        this.showDeathScreen = show;
    }

    public boolean isShowDeathScreen() {
        return this.showDeathScreen;
    }

    @Override
    public void playSound(SoundEvent soundIn, float volume, float pitch) {
        this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), soundIn, this.getSoundCategory(), volume, pitch, false);
    }

    @Override
    public void playSound(SoundEvent p_213823_1_, SoundCategory p_213823_2_, float p_213823_3_, float p_213823_4_) {
        this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), p_213823_1_, p_213823_2_, p_213823_3_, p_213823_4_, false);
    }

    @Override
    public boolean isServerWorld() {
        return true;
    }

    @Override
    public void setActiveHand(Hand hand) {
        ItemStack itemstack = this.getHeldItem(hand);
        if (!itemstack.isEmpty() && !this.isHandActive()) {
            super.setActiveHand(hand);
            this.handActive = true;
            this.activeHand = hand;
        }
    }

    @Override
    public boolean isHandActive() {
        return this.handActive;
    }

    @Override
    public void resetActiveHand() {
        super.resetActiveHand();
        this.handActive = false;
    }

    @Override
    public Hand getActiveHand() {
        return this.activeHand;
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (LIVING_FLAGS.equals(key)) {
            Hand hand;
            boolean flag = ((Byte)this.dataManager.get(LIVING_FLAGS) & 1) > 0;
            Hand hand2 = hand = ((Byte)this.dataManager.get(LIVING_FLAGS) & 2) > 0 ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if (flag && !this.handActive) {
                this.setActiveHand(hand);
            } else if (!flag && this.handActive) {
                this.resetActiveHand();
            }
        }
        if (FLAGS.equals(key) && this.isElytraFlying() && !this.wasFallFlying) {
            this.mc.getSoundHandler().play(new ElytraSound(this));
        }
    }

    public boolean isRidingHorse() {
        Entity entity = this.getRidingEntity();
        return this.isPassenger() && entity instanceof IJumpingMount && ((IJumpingMount)((Object)entity)).canJump();
    }

    public float getHorseJumpPower() {
        return this.horseJumpPower;
    }

    @Override
    public void openSignEditor(SignTileEntity signTile) {
        this.mc.displayGuiScreen(new EditSignScreen(signTile));
    }

    @Override
    public void openMinecartCommandBlock(CommandBlockLogic commandBlock) {
        this.mc.displayGuiScreen(new EditMinecartCommandBlockScreen(commandBlock));
    }

    @Override
    public void openCommandBlock(CommandBlockTileEntity commandBlock) {
        this.mc.displayGuiScreen(new CommandBlockScreen(commandBlock));
    }

    @Override
    public void openStructureBlock(StructureBlockTileEntity structure) {
        this.mc.displayGuiScreen(new EditStructureScreen(structure));
    }

    @Override
    public void openJigsaw(JigsawTileEntity p_213826_1_) {
        this.mc.displayGuiScreen(new JigsawScreen(p_213826_1_));
    }

    @Override
    public void openBook(ItemStack stack, Hand hand) {
        Item item = stack.getItem();
        if (item == Items.WRITABLE_BOOK) {
            this.mc.displayGuiScreen(new EditBookScreen(this, stack, hand));
        }
    }

    @Override
    public void onCriticalHit(Entity entityHit) {
        this.mc.particles.addParticleEmitter(entityHit, ParticleTypes.CRIT);
    }

    @Override
    public void onEnchantmentCritical(Entity entityHit) {
        this.mc.particles.addParticleEmitter(entityHit, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isSneaking() {
        return this.movementInput != null && this.movementInput.sneaking;
    }

    @Override
    public boolean isCrouching() {
        return this.isCrouching;
    }

    public boolean isForcedDown() {
        return this.isCrouching() || this.isVisuallySwimming();
    }

    @Override
    public void updateEntityActionState() {
        super.updateEntityActionState();
        if (this.isCurrentViewEntity() || this.movementInput instanceof PlayerMovementInput) {
            this.moveStrafing = this.movementInput.moveStrafe;
            this.moveForward = this.movementInput.moveForward;
            this.isJumping = this.movementInput.jump;
            this.prevRenderArmYaw = this.renderArmYaw;
            this.prevRenderArmPitch = this.renderArmPitch;
            if (!HookHelper.isActive() && !Load.getInstance().getHooks().getModuleManagers().getFreeCam().isToggled()) {
                this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.rotationPitch - this.renderArmPitch) * 0.5);
                this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.rotationYaw - this.renderArmYaw) * 0.5);
            } else {
                this.renderArmPitch = (float)((double)this.renderArmPitch + (double)(this.mc.gameRenderer.getActiveRenderInfo().getPitch() - this.renderArmPitch) * 0.5);
                this.renderArmYaw = (float)((double)this.renderArmYaw + (double)(this.mc.gameRenderer.getActiveRenderInfo().getYaw() - this.renderArmYaw) * 0.5);
            }
        }
    }

    protected boolean isCurrentViewEntity() {
        return this.mc.getRenderViewEntity() == this;
    }

    @Override
    public void livingTick() {
        ItemStack itemstack;
        boolean flag4;
        ++this.sprintingTicksLeft;
        if (this.sprintToggleTimer > 0) {
            --this.sprintToggleTimer;
        }
        this.handlePortalTeleportation();
        boolean flag = this.movementInput.jump;
        boolean flag1 = this.movementInput.sneaking;
        boolean flag2 = this.isUsingSwimmingAnimation();
        this.isCrouching = !this.abilities.isFlying && !this.isSwimming() && this.isPoseClear(Pose.CROUCHING) && (this.isSneaking() || !this.isSleeping() && !this.isPoseClear(Pose.STANDING));
        this.movementInput.tickMovement(this.isForcedDown());
        this.mc.getTutorial().handleMovement(this.movementInput);
        if (this.isHandActive() && !this.isPassenger()) {
            EventNoSlow eventNoSlow = new EventNoSlow(0.2f);
            Load.getInstance().getEvents().call(eventNoSlow);
            if (!eventNoSlow.isCancelled()) {
                this.movementInput.moveStrafe *= eventNoSlow.getSpeed();
                this.movementInput.moveForward *= eventNoSlow.getSpeed();
                this.sprintToggleTimer = 0;
            }
        }
        boolean flag3 = false;
        if (this.autoJumpTime > 0) {
            --this.autoJumpTime;
            flag3 = true;
            this.movementInput.jump = true;
        }
        if (!this.noClip) {
            this.setPlayerOffsetMotion(this.getPosX() - (double)this.getWidth() * 0.35, this.getPosZ() + (double)this.getWidth() * 0.35);
            this.setPlayerOffsetMotion(this.getPosX() - (double)this.getWidth() * 0.35, this.getPosZ() - (double)this.getWidth() * 0.35);
            this.setPlayerOffsetMotion(this.getPosX() + (double)this.getWidth() * 0.35, this.getPosZ() - (double)this.getWidth() * 0.35);
            this.setPlayerOffsetMotion(this.getPosX() + (double)this.getWidth() * 0.35, this.getPosZ() + (double)this.getWidth() * 0.35);
        }
        if (flag1) {
            this.sprintToggleTimer = 0;
        }
        boolean bl = flag4 = (float)this.getFoodStats().getFoodLevel() > 6.0f || this.abilities.allowFlying;
        if (!(!this.onGround && !this.canSwim() || flag1 || flag2 || !this.isUsingSwimmingAnimation() || this.isSprinting() || !flag4 || this.isHandActive() || this.isPotionActive(Effects.BLINDNESS))) {
            if (this.sprintToggleTimer <= 0 && !this.mc.gameSettings.keyBindSprint.isKeyDown()) {
                this.sprintToggleTimer = 7;
            } else {
                this.setSprinting(true);
            }
        }
        if (!this.isSprinting() && (!this.isInWater() || this.canSwim()) && this.isUsingSwimmingAnimation() && flag4 && !this.isHandActive() && !this.isPotionActive(Effects.BLINDNESS) && this.mc.gameSettings.keyBindSprint.isKeyDown()) {
            this.setSprinting(true);
        }
        if (this.isSprinting()) {
            boolean flag6;
            boolean flag5 = !this.movementInput.isMovingForward() || !flag4;
            boolean bl2 = flag6 = flag5 || this.collidedHorizontally || this.isInWater() && !this.canSwim();
            if (this.isSwimming()) {
                if (!this.onGround && !this.movementInput.sneaking && flag5 || !this.isInWater()) {
                    this.setSprinting(false);
                }
            } else if (flag6) {
                this.setSprinting(false);
            }
        }
        boolean flag7 = false;
        if (this.abilities.allowFlying) {
            if (this.mc.playerController.isSpectatorMode()) {
                if (!this.abilities.isFlying) {
                    this.abilities.isFlying = true;
                    flag7 = true;
                    this.sendPlayerAbilities();
                }
            } else if (!flag && this.movementInput.jump && !flag3) {
                if (this.flyToggleTimer == 0) {
                    this.flyToggleTimer = 7;
                } else if (!this.isSwimming()) {
                    this.abilities.isFlying = !this.abilities.isFlying;
                    flag7 = true;
                    this.sendPlayerAbilities();
                    this.flyToggleTimer = 0;
                }
            }
        }
        if (this.movementInput.jump && !flag7 && !flag && !this.abilities.isFlying && !this.isPassenger() && !this.isOnLadder() && (itemstack = this.getItemStackFromSlot(EquipmentSlotType.CHEST)).getItem() == Items.ELYTRA && ElytraItem.isUsable(itemstack) && this.tryToStartFallFlying()) {
            this.connection.sendPacket(new CEntityActionPacket(this, CEntityActionPacket.Action.START_FALL_FLYING));
        }
        this.wasFallFlying = this.isElytraFlying();
        if (this.isInWater() && this.movementInput.sneaking && this.func_241208_cS_()) {
            this.handleFluidSneak();
        }
        if (this.areEyesInFluid(FluidTags.WATER)) {
            int i = this.isSpectator() ? 10 : 1;
            this.counterInWater = MathHelper.clamp(this.counterInWater + i, 0, 600);
        } else if (this.counterInWater > 0) {
            this.areEyesInFluid(FluidTags.WATER);
            this.counterInWater = MathHelper.clamp(this.counterInWater - 10, 0, 600);
        }
        if (this.abilities.isFlying && this.isCurrentViewEntity()) {
            int j = 0;
            if (this.movementInput.sneaking) {
                --j;
            }
            if (this.movementInput.jump) {
                ++j;
            }
            if (j != 0) {
                this.setMotion(this.getMotion().add(0.0, (float)j * this.abilities.getFlySpeed() * 3.0f, 0.0));
            }
        }
        if (this.isRidingHorse()) {
            IJumpingMount ijumpingmount = (IJumpingMount)((Object)this.getRidingEntity());
            if (this.horseJumpPowerCounter < 0) {
                ++this.horseJumpPowerCounter;
                if (this.horseJumpPowerCounter == 0) {
                    this.horseJumpPower = 0.0f;
                }
            }
            if (flag && !this.movementInput.jump) {
                this.horseJumpPowerCounter = -10;
                ijumpingmount.setJumpPower(MathHelper.floor(this.getHorseJumpPower() * 100.0f));
                this.sendHorseJump();
            } else if (!flag && this.movementInput.jump) {
                this.horseJumpPowerCounter = 0;
                this.horseJumpPower = 0.0f;
            } else if (flag) {
                ++this.horseJumpPowerCounter;
                this.horseJumpPower = this.horseJumpPowerCounter < 10 ? (float)this.horseJumpPowerCounter * 0.1f : 0.8f + 2.0f / (float)(this.horseJumpPowerCounter - 9) * 0.1f;
            }
        } else {
            this.horseJumpPower = 0.0f;
        }
        super.livingTick();
        if (this.onGround && this.abilities.isFlying && !this.mc.playerController.isSpectatorMode()) {
            this.abilities.isFlying = false;
            this.sendPlayerAbilities();
        }
    }

    private void handlePortalTeleportation() {
        this.prevTimeInPortal = this.timeInPortal;
        if (this.inPortal) {
            if (this.mc.currentScreen != null && !this.mc.currentScreen.isPauseScreen()) {
                if (this.mc.currentScreen instanceof ContainerScreen) {
                    this.closeScreen();
                }
                this.mc.displayGuiScreen(null);
            }
            if (this.timeInPortal == 0.0f) {
                this.mc.getSoundHandler().play(SimpleSound.ambientWithoutAttenuation(SoundEvents.BLOCK_PORTAL_TRIGGER, this.rand.nextFloat() * 0.4f + 0.8f, 0.25f));
            }
            this.timeInPortal += 0.0125f;
            if (this.timeInPortal >= 1.0f) {
                this.timeInPortal = 1.0f;
            }
            this.inPortal = false;
        } else if (this.isPotionActive(Effects.NAUSEA) && this.getActivePotionEffect(Effects.NAUSEA).getDuration() > 60) {
            EventGameOverlay eventGameOverlay = new EventGameOverlay(EventGameOverlay.OverlayType.Nausea);
            Load.getInstance().getEvents().call(eventGameOverlay);
            if (!eventGameOverlay.isCancelled()) {
                this.timeInPortal += 0.006666667f;
                if (this.timeInPortal > 1.0f) {
                    this.timeInPortal = 1.0f;
                }
            }
        } else {
            if (this.timeInPortal > 0.0f) {
                this.timeInPortal -= 0.05f;
            }
            if (this.timeInPortal < 0.0f) {
                this.timeInPortal = 0.0f;
            }
        }
        this.decrementTimeUntilPortal();
    }

    @Override
    public void updateRidden() {
        super.updateRidden();
        this.rowingBoat = false;
        if (this.getRidingEntity() instanceof BoatEntity) {
            BoatEntity boatentity = (BoatEntity)this.getRidingEntity();
            boatentity.updateInputs(this.movementInput.leftKeyDown, this.movementInput.rightKeyDown, this.movementInput.forwardKeyDown, this.movementInput.backKeyDown);
            this.rowingBoat |= this.movementInput.leftKeyDown || this.movementInput.rightKeyDown || this.movementInput.forwardKeyDown || this.movementInput.backKeyDown;
        }
    }

    public boolean isRowingBoat() {
        return this.rowingBoat;
    }

    @Override
    @Nullable
    public EffectInstance removeActivePotionEffect(@Nullable Effect potioneffectin) {
        if (potioneffectin == Effects.NAUSEA) {
            this.prevTimeInPortal = 0.0f;
            this.timeInPortal = 0.0f;
        }
        return super.removeActivePotionEffect(potioneffectin);
    }

    @Override
    public void move(MoverType typeIn, Vector3d pos) {
        double d0 = this.getPosX();
        double d1 = this.getPosZ();
        EventMove eventMove = new EventMove(pos.x, pos.y, pos.z);
        Load.getInstance().getEvents().call(eventMove);
        super.move(typeIn, new Vector3d(eventMove.getX(), eventMove.getY(), eventMove.getZ()));
        this.updateAutoJump((float)(this.getPosX() - d0), (float)(this.getPosZ() - d1));
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    protected void updateAutoJump(float movementX, float movementZ) {
        if (this.canAutoJump()) {
            Vector3d vector3d = this.getPositionVec();
            Vector3d vector3d1 = vector3d.add(movementX, 0.0, movementZ);
            Vector3d vector3d2 = new Vector3d(movementX, 0.0, movementZ);
            float f = this.getAIMoveSpeed();
            float f1 = (float)vector3d2.lengthSquared();
            if (f1 <= 0.001f) {
                Vector2f vector2f = this.movementInput.getMoveVector();
                float f2 = f * vector2f.x;
                float f3 = f * vector2f.y;
                float f4 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180));
                float f5 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180));
                vector3d2 = new Vector3d(f2 * f5 - f3 * f4, vector3d2.y, f3 * f5 + f2 * f4);
                f1 = (float)vector3d2.lengthSquared();
                if (f1 <= 0.001f) {
                    return;
                }
            }
            float f12 = MathHelper.fastInvSqrt(f1);
            Vector3d vector3d12 = vector3d2.scale(f12);
            Vector3d vector3d13 = this.getForward();
            float f13 = (float)(vector3d13.x * vector3d12.x + vector3d13.z * vector3d12.z);
            if (!(f13 < -0.15f)) {
                BlockState blockstate1;
                ISelectionContext iselectioncontext = ISelectionContext.forEntity(this);
                BlockPos blockpos = new BlockPos(this.getPosX(), this.getBoundingBox().maxY, this.getPosZ());
                BlockState blockstate = this.world.getBlockState(blockpos);
                if (blockstate.getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty() && (blockstate1 = this.world.getBlockState(blockpos = blockpos.up())).getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
                    float f14;
                    float f6 = 7.0f;
                    float f7 = 1.2f;
                    if (this.isPotionActive(Effects.JUMP_BOOST)) {
                        f7 += (float)(this.getActivePotionEffect(Effects.JUMP_BOOST).getAmplifier() + 1) * 0.75f;
                    }
                    float f8 = Math.max(f * 7.0f, 1.0f / f12);
                    Vector3d vector3d4 = vector3d1.add(vector3d12.scale(f8));
                    float f9 = this.getWidth();
                    float f10 = this.getHeight();
                    AxisAlignedBB axisalignedbb = new AxisAlignedBB(vector3d, vector3d4.add(0.0, f10, 0.0)).grow(f9, 0.0, f9);
                    Vector3d lvt_19_1_ = vector3d.add(0.0, 0.51f, 0.0);
                    vector3d4 = vector3d4.add(0.0, 0.51f, 0.0);
                    Vector3d vector3d5 = vector3d12.crossProduct(new Vector3d(0.0, 1.0, 0.0));
                    Vector3d vector3d6 = vector3d5.scale(f9 * 0.5f);
                    Vector3d vector3d7 = lvt_19_1_.subtract(vector3d6);
                    Vector3d vector3d8 = vector3d4.subtract(vector3d6);
                    Vector3d vector3d9 = lvt_19_1_.add(vector3d6);
                    Vector3d vector3d10 = vector3d4.add(vector3d6);
                    Iterator iterator = this.world.func_234867_d_(this, axisalignedbb, entity -> true).flatMap(shape -> shape.toBoundingBoxList().stream()).iterator();
                    float f11 = Float.MIN_VALUE;
                    while (iterator.hasNext()) {
                        AxisAlignedBB axisalignedbb1 = (AxisAlignedBB)iterator.next();
                        if (!axisalignedbb1.intersects(vector3d7, vector3d8) && !axisalignedbb1.intersects(vector3d9, vector3d10)) continue;
                        f11 = (float)axisalignedbb1.maxY;
                        Vector3d vector3d11 = axisalignedbb1.getCenter();
                        BlockPos blockpos1 = new BlockPos(vector3d11);
                        int i = 1;
                        while ((float)i < f7) {
                            BlockState blockstate3;
                            BlockPos blockpos2 = blockpos1.up(i);
                            BlockState blockstate2 = this.world.getBlockState(blockpos2);
                            VoxelShape voxelshape = blockstate2.getCollisionShape(this.world, blockpos2, iselectioncontext);
                            if (!voxelshape.isEmpty() && (double)(f11 = (float)voxelshape.getEnd(Direction.Axis.Y) + (float)blockpos2.getY()) - this.getPosY() > (double)f7) {
                                return;
                            }
                            if (i > 1 && !(blockstate3 = this.world.getBlockState(blockpos = blockpos.up())).getCollisionShape(this.world, blockpos, iselectioncontext).isEmpty()) {
                                return;
                            }
                            ++i;
                        }
                        break block0;
                    }
                    if (f11 != Float.MIN_VALUE && !((f14 = (float)((double)f11 - this.getPosY())) <= 0.5f) && !(f14 > f7)) {
                        this.autoJumpTime = 1;
                    }
                }
            }
        }
    }

    private boolean canAutoJump() {
        return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && (double)this.getJumpFactor() >= 1.0;
    }

    private boolean isMoving() {
        Vector2f vector2f = this.movementInput.getMoveVector();
        return vector2f.x != 0.0f || vector2f.y != 0.0f;
    }

    private boolean isUsingSwimmingAnimation() {
        double d0 = 0.8;
        return this.canSwim() ? this.movementInput.isMovingForward() : (double)this.movementInput.moveForward >= 0.8;
    }

    public float getWaterBrightness() {
        if (!this.areEyesInFluid(FluidTags.WATER)) {
            return 0.0f;
        }
        float f = 600.0f;
        float f1 = 100.0f;
        if ((float)this.counterInWater >= 600.0f) {
            return 1.0f;
        }
        float f2 = MathHelper.clamp((float)this.counterInWater / 100.0f, 0.0f, 1.0f);
        float f3 = (float)this.counterInWater < 100.0f ? 0.0f : MathHelper.clamp(((float)this.counterInWater - 100.0f) / 500.0f, 0.0f, 1.0f);
        return f2 * 0.6f + f3 * 0.39999998f;
    }

    @Override
    public boolean canSwim() {
        return this.eyesInWaterPlayer;
    }

    @Override
    protected boolean updateEyesInWaterPlayer() {
        boolean flag = this.eyesInWaterPlayer;
        boolean flag1 = super.updateEyesInWaterPlayer();
        if (this.isSpectator()) {
            return this.eyesInWaterPlayer;
        }
        if (!flag && flag1) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
            this.mc.getSoundHandler().play(new UnderwaterAmbientSounds.UnderWaterSound(this));
        }
        if (flag && !flag1) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundCategory.AMBIENT, 1.0f, 1.0f, false);
        }
        return this.eyesInWaterPlayer;
    }

    @Override
    public Vector3d getLeashPosition(float partialTicks) {
        if (this.mc.gameSettings.getPointOfView().func_243192_a()) {
            float f = MathHelper.lerp(partialTicks * 0.5f, this.rotationYaw, this.prevRotationYaw) * ((float)Math.PI / 180);
            float f1 = MathHelper.lerp(partialTicks * 0.5f, this.rotationPitch, this.prevRotationPitch) * ((float)Math.PI / 180);
            double d0 = this.getPrimaryHand() == HandSide.RIGHT ? -1.0 : 1.0;
            Vector3d vector3d = new Vector3d(0.39 * d0, -0.6, 0.3);
            return vector3d.rotatePitch(-f1).rotateYaw(-f).add(this.getEyePosition(partialTicks));
        }
        return super.getLeashPosition(partialTicks);
    }
}
