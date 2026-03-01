package dimasik.modules.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.api.EventListener;
import dimasik.events.main.EventUpdate;
import dimasik.events.main.render.EventNameRender;
import dimasik.events.main.render.EventRender2D;
import dimasik.helpers.animation.EasingList;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.render.ScreenHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.managers.client.ClientManagers;
import dimasik.managers.mods.voicechat.voice.client.ClientManager;
import dimasik.managers.mods.voicechat.voice.client.ClientPlayerStateManager;
import dimasik.managers.mods.voicechat.voice.client.ClientVoicechat;
import dimasik.managers.module.Module;
import dimasik.managers.module.main.Category;
import dimasik.managers.module.option.main.MultiOption;
import dimasik.managers.module.option.main.MultiOptionValue;
import dimasik.managers.module.option.main.SelectOption;
import dimasik.managers.module.option.main.SelectOptionValue;
import dimasik.utils.client.StringUtils;
import dimasik.utils.math.MathUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SkullItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.EffectUtils;
import net.minecraft.scoreboard.Score;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import org.joml.Vector4i;

public class ESP
extends Module {
    private final HashMap<LivingEntity, Vector4f> positions = new HashMap();
    private final MultiOption elements = new MultiOption("Elements", new MultiOptionValue("Armor", true), new MultiOptionValue("Potions", true), new MultiOptionValue("Box", true), new MultiOptionValue("Health Bar", true));
    private final MultiOption selectTargets = new MultiOption("Targets", new MultiOptionValue("Naked", false), new MultiOptionValue("Players", true), new MultiOptionValue("Mobs", false), new MultiOptionValue("Animals", false), new MultiOptionValue("Villagers", false));
    private final SelectOption boxMode = new SelectOption("Box mode", 0, new SelectOptionValue("2D Box"), new SelectOptionValue("Corners")).visible(() -> this.elements.getSelected("Box"));
    private final MultiOption armorElements = new MultiOption("Armor Elements", new MultiOptionValue("Enchantments", true)).visible(() -> this.elements.getSelected("Armor"));
    private final EventListener<EventNameRender> tag = this::tag;
    private final EventListener<EventRender2D.Post> render = this::render;
    private final EventListener<EventUpdate> update = this::update;

    public ESP() {
        super("ESP", Category.RENDER);
        this.settings(this.selectTargets, this.elements, this.boxMode, this.armorElements);
    }

    @Override
    public void onEnabled() {
        super.onEnabled();
    }

    @Override
    public void onDisabled() {
        super.onDisabled();
    }

    public void tag(EventNameRender event) {
        event.setCancelled(true);
    }

    public void render(EventRender2D.Post event) {
        Vector4f position;
        if (ESP.mc.world == null) {
            return;
        }
        this.positions.clear();
        ArrayList<LivingEntity> targ = new ArrayList<LivingEntity>();
        targ.addAll(ESP.mc.world.getPlayers());
        AxisAlignedBB searchBox = ESP.mc.player.getBoundingBox().grow(256.0);
        targ.addAll(ESP.mc.world.getEntitiesWithinAABB(AnimalEntity.class, searchBox));
        targ.addAll(ESP.mc.world.getEntitiesWithinAABB(MonsterEntity.class, searchBox));
        targ.addAll(ESP.mc.world.getEntitiesWithinAABB(VillagerEntity.class, searchBox));
        for (LivingEntity livingEntity : targ) {
            if (!this.entisvalidatedoxdoxswatswat(livingEntity)) continue;
            double x = MathUtils.interpolate(livingEntity.getPosX(), livingEntity.lastTickPosX, (double)event.getPartialTicks());
            double y = MathUtils.interpolate(livingEntity.getPosY(), livingEntity.lastTickPosY, (double)event.getPartialTicks());
            double z = MathUtils.interpolate(livingEntity.getPosZ(), livingEntity.lastTickPosZ, (double)event.getPartialTicks());
            Vector3d size = new Vector3d(livingEntity.getBoundingBox().maxX - livingEntity.getBoundingBox().minX, livingEntity.getBoundingBox().maxY - livingEntity.getBoundingBox().minY, livingEntity.getBoundingBox().maxZ - livingEntity.getBoundingBox().minZ);
            AxisAlignedBB aabb = new AxisAlignedBB(x - size.x / 2.0, y, z - size.z / 2.0, x + size.x / 2.0, y + size.y, z + size.z / 2.0);
            Vector4f position2 = null;
            for (int i = 0; i < 8; ++i) {
                Vector2f vector = ScreenHelpers.worldToScreen(i % 2 == 0 ? aabb.minX : aabb.maxX, i / 2 % 2 == 0 ? aabb.minY : aabb.maxY, i / 4 % 2 == 0 ? aabb.minZ : aabb.maxZ);
                if (position2 == null) {
                    position2 = new Vector4f(vector.x, vector.y, 1.0f, 1.0f);
                    continue;
                }
                position2.x = Math.min(vector.x, position2.x);
                position2.y = Math.min(vector.y, position2.y);
                position2.z = Math.max(vector.x, position2.z);
                position2.w = Math.max(vector.y, position2.w);
            }
            this.positions.put(livingEntity, position2);
        }
        RenderSystem.enableBlend();
        RenderSystem.disableTexture();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(7425);
        buffer.endVertex();
        buffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        for (Map.Entry entry : this.positions.entrySet()) {
            Vector4i colors = new Vector4i(ColorHelpers.getThemeColor(2), ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(1), ColorHelpers.getThemeColor(2));
            new Vector4i(ColorHelpers.rgb(144, 238, 144), ColorHelpers.rgb(144, 238, 144), ColorHelpers.rgb(144, 238, 144), ColorHelpers.rgb(144, 238, 144));
            position = (Vector4f)entry.getValue();
            float hpOffset = 3.0f;
            float out = 0.5f;
            LivingEntity entity = (LivingEntity)entry.getKey();
            float hp = entity.getHealth();
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)entity;
                Score score = ESP.mc.world.getScoreboard().getOrCreateScore(player.getScoreboardName(), ESP.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
                if (mc.getCurrentServerData() != null) {
                    String serverIP = ESP.mc.getCurrentServerData().serverIP;
                    if (Load.getInstance().getHooks().getModuleManagers().getFixHP().isToggled()) {
                        hp = score.getScorePoints();
                    }
                }
            }
            if (this.elements.getSelected("Box")) {
                if (this.boxMode.getSelected("2D Box")) {
                    VisualHelpers.drawPlayerBox(position.x - 0.5f, position.y - 0.5f, position.z + 0.5f, position.w + 0.5f, 2.0, ColorHelpers.rgba(0, 0, 0, 128));
                    VisualHelpers.drawVectorBox(position.x, position.y, position.z, position.w, 1.0, colors);
                } else if (this.boxMode.getSelected("Corners")) {
                    double x = position.x;
                    double y = position.y;
                    double endX = position.z;
                    double endY = position.w;
                    int getColor = ColorHelpers.getThemeColor(1);
                    int getColor2 = ColorHelpers.getThemeColor(2);
                    int backcolor = ColorHelpers.rgb(26, 26, 26);
                    double xproc = 0.2;
                    double yproc = 0.15;
                    double xdist = endX - x;
                    double ydist = endY - y;
                    double xoff = xdist * xproc;
                    double yoff = ydist * yproc;
                    ESP.drawMcRect(x - 1.0, y - 1.0, x + xoff + 0.5, y + 1.0, backcolor);
                    ESP.drawMcRect(endX - xoff - 0.5, y - 1.0, endX + 1.0, y + 1.0, backcolor);
                    ESP.drawMcRect(x - 1.0, endY - 1.0, x + xoff + 0.5, endY + 1.0, backcolor);
                    ESP.drawMcRect(endX - xoff - 0.5, endY - 1.0, endX + 1.0, endY + 1.0, backcolor);
                    ESP.drawMcRect(x - 1.0, y + 0.5, x + 1.0, y + yoff + 1.0, backcolor);
                    ESP.drawMcRect(x - 1.0, endY - yoff - 1.0, x + 1.0, endY + 0.5, backcolor);
                    ESP.drawMcRect(endX - 1.0, y + 0.5, endX + 1.0, y + yoff + 1.0, backcolor);
                    ESP.drawMcRect(endX - 1.0, endY - yoff - 1.0, endX + 1.0, endY + 0.5, backcolor);
                    ESP.drawMcRect(x - 0.5, y - 0.5, x + xoff, y + 0.5, getColor);
                    ESP.drawMcRect(endX - xoff, y - 0.5, endX + 0.5, y + 0.5, getColor2);
                    ESP.drawMcRect(x - 0.5, endY - 0.5, x + xoff, endY + 0.5, getColor2);
                    ESP.drawMcRect(endX - xoff, endY - 0.5, endX + 0.5, endY + 0.5, getColor);
                    ESP.drawMcRect(x - 0.5, y + 0.5, x + 0.5, y + yoff, getColor);
                    ESP.drawMcRect(x - 0.5, endY - yoff, x + 0.5, endY, getColor2);
                    ESP.drawMcRect(endX - 0.5, y + 0.5, endX + 0.5, y + yoff, getColor2);
                    ESP.drawMcRect(endX - 0.5, endY - yoff, endX + 0.5, endY, getColor);
                }
            }
            if (!this.elements.getSelected("Health Bar")) continue;
            VisualHelpers.drawRectBuilding(position.x - hpOffset - out, position.y - out, position.x - hpOffset + 1.0f + out, position.w + out, ColorHelpers.rgba(0, 0, 0, 128));
            VisualHelpers.drawRectBuilding(position.x - hpOffset, position.y, position.x - hpOffset + 1.0f, position.w, ColorHelpers.rgba(0, 0, 0, 128));
            VisualHelpers.drawMCVerticalBuilding(position.x - hpOffset, position.y + (position.w - position.y) * (1.0f - MathHelper.clamp(hp / ((LivingEntity)entry.getKey()).getMaxHealth(), 0.0f, 1.0f)), position.x - hpOffset + 1.0f, position.w, ColorHelpers.rgb(255, 0, 0), ColorHelpers.rgb(113, 247, 106));
        }
        Tessellator.getInstance().draw();
        RenderSystem.shadeModel(7424);
        RenderSystem.enableTexture();
        RenderSystem.disableBlend();
        for (Map.Entry entry : this.positions.entrySet()) {
            LivingEntity entity = (LivingEntity)entry.getKey();
            position = (Vector4f)entry.getValue();
            if (entity instanceof PlayerEntity) {
                PlayerEntity player = (PlayerEntity)entity;
                this.renderTags(event.getMatrixStack(), position.x, position.y, position.z, position.w, player);
                if (this.elements.getSelected("Potions")) {
                    this.renderEffects(player, position.y, position.z, event.getMatrixStack());
                }
                if (!this.elements.getSelected("Armor")) continue;
                float maxOffsetY = 0.0f;
                maxOffsetY += 25.0f;
                ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(Arrays.asList(player.getHeldItemMainhand(), player.getHeldItemOffhand()));
                player.getArmorInventoryList().forEach(stacks::add);
                stacks.removeIf(w -> w.getItem() instanceof AirItem);
                int totalSize = stacks.size() * 10;
                AtomicInteger iterable = new AtomicInteger();
                float finalMaxOffsetY = maxOffsetY += 19.0f;
                double endX = position.z;
                double endY = position.w;
                MathUtils.scaleElements((position.x + position.z) / 2.0f, position.y - maxOffsetY - 5.0f, 0.7f, () -> this.renderArmorAndEnchantment(stacks, event.getMatrixStack(), position.x, position.z, position.y, finalMaxOffsetY, totalSize, iterable));
                continue;
            }
            this.tafroam(event.getMatrixStack(), position.x, position.y, position.z, position.w, entity);
        }
    }

    public void update(EventUpdate event) {
        for (Map.Entry<LivingEntity, Vector4f> entry : this.positions.entrySet()) {
            if (!(entry.getKey() instanceof PlayerEntity)) continue;
            PlayerEntity player = (PlayerEntity)entry.getKey();
            player.getFriendAnimation().update(Load.getInstance().getHooks().getFriendManagers().is(player.getGameProfile().getName()));
        }
    }

    private void renderTags(MatrixStack matrixStack, float posX, float posY, float endPosX, float endPosY, PlayerEntity player) {
        int color4;
        IFormattableTextComponent name = new StringTextComponent(Load.getInstance().getHooks().getFriendManagers().is(player.getGameProfile().getName()) ? "[F] " : "").setStyle(Style.EMPTY.setFormatting(TextFormatting.GREEN));
        if (StringUtils.prefix(player.getPrefix().getString().replace(" ", "")) != null) {
            ((TextComponent)name).append(StringUtils.prefix(player.getPrefix().getString().replace(" ", "")));
        } else {
            for (ITextComponent component : player.getPrefix().getSiblings()) {
                if (!StringUtils.smallCaps(component.getString().replace(" ", "")).contains("null")) {
                    ((TextComponent)name).append(new StringTextComponent(StringUtils.smallCaps(component.getString().replace(" ", ""))).setStyle(component.getStyle()));
                    continue;
                }
                ((TextComponent)name).append(new StringTextComponent(component.getString()).setStyle(component.getStyle()));
            }
        }
        if (!player.getPrefix().getString().isEmpty()) {
            ((TextComponent)name).appendString("  ");
        }
        float hp = player.getHealth();
        Score score = ESP.mc.world.getScoreboard().getOrCreateScore(player.getScoreboardName(), ESP.mc.world.getScoreboard().getObjectiveInDisplaySlot(2));
        if (mc.getCurrentServerData() != null) {
            String serverIP = ESP.mc.getCurrentServerData().serverIP;
            if (Load.getInstance().getHooks().getModuleManagers().getFixHP().isToggled()) {
                hp = score.getScorePoints();
            }
        }
        String targetHP = (float)((int)hp) > 900.0f ? "\u041d\u0415\u0418\u0417\u0412\u0415\u0421\u0422\u041d\u041e" : String.valueOf((int)hp + " HP");
        ((TextComponent)name).append(new StringTextComponent(player.getName().getString()).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE)));
        try {
            if (player.getDisplayName().getString().length() > 5 && player.getDisplayName().getString().split(player.getGameProfile().getName())[1].length() >= 5) {
                ((TextComponent)name).append(new StringTextComponent(player.getDisplayName().getString().split(player.getGameProfile().getName())[1].replace("+", "")).mergeStyle(player.getDisplayName().getStyle()));
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        ((TextComponent)name).append(new StringTextComponent("  [" + targetHP + "]").setStyle(Style.EMPTY.setFormatting(TextFormatting.RED)));
        if (player.getHeldItemOffhand().getItem() instanceof SkullItem) {
            Object firstStyle = null;
            Style lastStyle = null;
            for (ITextComponent component : player.getHeldItemOffhand().getDisplayName().getSiblings()) {
                if (firstStyle == null) {
                    firstStyle = component.getStyle();
                }
                lastStyle = component.getStyle();
            }
            ((TextComponent)name).append(new StringTextComponent("  [").setStyle((Style)firstStyle));
            for (ITextComponent component : player.getHeldItemOffhand().getDisplayName().getSiblings()) {
                ((TextComponent)name).append(component);
            }
            ((TextComponent)name).append(new StringTextComponent("]").setStyle(lastStyle));
        }
        if (player.getHeldItemMainhand().getItem() instanceof SkullItem) {
            ((TextComponent)name).appendString("  [");
            for (ITextComponent component : player.getHeldItemMainhand().getDisplayName().getSiblings()) {
                ((TextComponent)name).append(component);
            }
            ((TextComponent)name).appendString("]");
        }
        float size = 7.0f;
        float width = suisse_intl.getWidth(name, size) - 5.0f;
        float height = 10.0f;
        player.getFriendAnimation().animate(0.0f, 1.0f, 0.2f, EasingList.CIRC_OUT, ESP.mc.getTimer().renderPartialTicks);
        int color = ColorHelpers.interpolateColor(ColorHelpers.rgba(0, 0, 0, 120), ColorHelpers.rgba(0, 120, 0, 120), player.getFriendAnimation().getAnimationValue());
        ClientVoicechat client = ClientManager.getClient();
        ClientPlayerStateManager manager = ClientManager.getPlayerStateManager();
        boolean isVoiceClient = manager != null && !ClientManagers.isFuntime() && !manager.isPlayerDisconnected(player) && Load.getInstance().getHooks().getModuleManagers().getVoiceChat().isToggled();
        VisualHelpers.drawRoundedRect(matrixStack, (posX + endPosX) / 2.0f - width / 2.0f - (float)(player.getPrefix().getString().contains("\u25cf") ? 8 : 0), posY - height - 10.0f, width + 10.0f + (float)(player.getPrefix().getString().contains("\u25cf") ? 8 : 0) + (float)(isVoiceClient ? 2 : 0), height, 1.0f, color);
        if (isVoiceClient) {
            try {
                boolean isSpeak;
                boolean bl = isSpeak = client.getTalkCache().isWhispering(player) || client.getTalkCache().isTalking(player);
                int color3 = manager.isPlayerDisabled(player) ? ColorHelpers.rgba(255, 140, 0, 255) : (isSpeak ? ColorHelpers.rgba(0, 255, 0, 255) : ColorHelpers.rgba(255, 0, 0, 255));
                VisualHelpers.drawRoundedRect(matrixStack, (posX + endPosX) / 2.0f - width / 2.0f - 2.0f - (float)(player.getPrefix().getString().contains("\u25cf") ? 6 : 0), posY - height - 10.0f, 2.0f, height, 1.0f, color3);
                VisualHelpers.drawRoundedRect(matrixStack, (posX + endPosX) / 2.0f + width / 2.0f + 10.0f, posY - height - 10.0f, 2.0f, height, 1.0f, color3);
            }
            catch (Exception isSpeak) {
                // empty catch block
            }
        }
        suisse_intl.drawText(matrixStack, name, (posX + endPosX) / 2.0f - width / 2.0f + 2.5f, posY - height - 8.5f, size, 255.0f);
        int n = color4 = isVoiceClient ? new Color(80, 255, 80).getRGB() : new Color(255, 80, 80).getRGB();
        if (player.getPrefix().getString().contains("\u25cf")) {
            VisualHelpers.drawRoundedRect((posX + endPosX) / 2.0f - width / 2.0f + 2.5f - 8.0f, posY - height - 8.0f, 6.0f, 6.0f, 3.0f, color4);
        }
    }

    public boolean isInView(Entity ent) {
        if (mc.getRenderViewEntity() == null) {
            return false;
        }
        WorldRenderer.frustum.setCameraPosition(ESP.mc.getRenderManager().info.getProjectedView().x, ESP.mc.getRenderManager().info.getProjectedView().y, ESP.mc.getRenderManager().info.getProjectedView().z);
        return WorldRenderer.frustum.isBoundingBoxInFrustum(ent.getBoundingBox()) || ent.ignoreFrustumCheck;
    }

    private void tafroam(MatrixStack matrixStack, float posX, float posY, float endPosX, float endPosY, LivingEntity entity) {
        String baseName = entity.getName().getString();
        int hpInt = (int)entity.getHealth();
        IFormattableTextComponent name = new StringTextComponent(baseName).setStyle(Style.EMPTY.setFormatting(TextFormatting.WHITE));
        IFormattableTextComponent hp = new StringTextComponent("  [" + hpInt + " HP]").setStyle(Style.EMPTY.setFormatting(TextFormatting.RED));
        StringTextComponent full = new StringTextComponent("");
        full.append(name);
        full.append(hp);
        float size = 7.0f;
        float width = suisse_intl.getWidth(full, size) - 5.0f;
        float height = 10.0f;
        int bg = ColorHelpers.rgba(0, 0, 0, 120);
        float centerX = (posX + endPosX) / 2.0f;
        VisualHelpers.drawRoundedRect(matrixStack, centerX - width / 2.0f, posY - height - 10.0f, width + 10.0f, height, 1.0f, bg);
        suisse_intl.drawText(matrixStack, full, centerX - width / 2.0f + 2.5f, posY - height - 8.5f, size, 255.0f);
    }

    private boolean entisvalidatedoxdoxswatswat(LivingEntity entity) {
        if (entity == ESP.mc.player) {
            return false;
        }
        if (!this.isInView(entity)) {
            return false;
        }
        if (entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity)entity;
            boolean allowNaked = this.selectTargets.getSelected("Naked");
            boolean allowPlayers = this.selectTargets.getSelected("Players");
            if (allowNaked && this.isNaked(player)) {
                return true;
            }
            return allowPlayers;
        }
        if (entity instanceof VillagerEntity) {
            return this.selectTargets.getSelected("Villagers");
        }
        if (entity instanceof AnimalEntity) {
            return this.selectTargets.getSelected("Animals");
        }
        if (entity instanceof MonsterEntity) {
            return this.selectTargets.getSelected("Mobs");
        }
        return false;
    }

    private boolean isNaked(PlayerEntity player) {
        for (ItemStack stack : player.getArmorInventoryList()) {
            if (stack.getItem() instanceof AirItem) continue;
            return false;
        }
        return true;
    }

    private void renderArmorAndEnchantment(List<ItemStack> stacks, MatrixStack matrixStack, float posX, float endPosX, float posY, float finalMaxOffsetY, int totalSize, AtomicInteger iterable) {
        for (ItemStack stack : stacks) {
            if (stack.isEmpty()) continue;
            ESP.drawItemStack(stack, posX + (endPosX - posX) / 2.0f + (float)(iterable.get() * 20) - (float)totalSize + 2.0f, posY - finalMaxOffsetY + 18.0f, null, false);
            iterable.getAndIncrement();
            ArrayList<String> enchantment = this.getEnchantment(stack);
            float center = posX + (endPosX - posX) / 2.0f + (float)(iterable.get() * 20) - (float)totalSize - 15.5f;
            int i = 0;
            if (!this.elements.getSelected("Armor") || !this.armorElements.getSelected("Enchantments")) continue;
            for (String text : enchantment) {
                int finalI = i++;
                MathUtils.scaleElements(center, posY - finalMaxOffsetY + 12.0f - (float)(finalI * 7), 0.45f, () -> {
                    if (text.contains("Sh6") || text.contains("Pr5")) {
                        suisse_intl.drawText(matrixStack, text, center, posY - finalMaxOffsetY + 12.0f - (float)finalI * 7.5f, ColorHelpers.rgba(255, 80, 80, 255), 16.0f);
                    } else {
                        suisse_intl.drawText(matrixStack, text, center, posY - finalMaxOffsetY + 12.0f - (float)finalI * 7.5f, -1, 16.0f);
                    }
                });
            }
        }
    }

    public static void drawItemStack(ItemStack stack, double x, double y, String altText, boolean withoutOverlay) {
        RenderSystem.translated(x, y, 0.0);
        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (!withoutOverlay) {
            mc.getItemRenderer().renderItemOverlayIntoGUI(ESP.mc.fontRenderer, stack, 0, 0, altText);
        }
        RenderSystem.translated(-x, -y, 0.0);
    }

    private void handleSwordEnchantments(ArrayList<String> list, ItemStack stack) {
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int fireAspect = EnchantmentHelper.getEnchantmentLevel(Enchantments.FIRE_ASPECT, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        if (fireAspect > 0) {
            list.add("Fl" + fireAspect);
        }
        if (sharpness > 0) {
            list.add("Sh" + sharpness);
        }
        if (unbreaking > 0) {
            list.add("Un" + unbreaking);
        }
        if (mending > 0) {
            list.add("Me" + mending);
        }
    }

    private void handleToolEnchantments(ArrayList<String> list, ItemStack stack) {
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        if (unbreaking > 0) {
            list.add("Un" + unbreaking);
        }
        if (mending > 0) {
            list.add("Me" + mending);
        }
        if (efficiency > 0) {
            list.add("Eff" + efficiency);
        }
    }

    private void handleBowEnchantments(ArrayList<String> list, ItemStack stack) {
        int vanishingCurse = EnchantmentHelper.getEnchantmentLevel(Enchantments.VANISHING_CURSE, stack);
        int infinity = EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, stack);
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER, stack);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int flame = EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAME, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (infinity > 0) {
            list.add("In" + infinity);
        }
        if (power > 0) {
            list.add("Po" + power);
        }
        if (punch > 0) {
            list.add("Pu" + punch);
        }
        if (mending > 0) {
            list.add("Me" + mending);
        }
        if (flame > 0) {
            list.add("Fl" + flame);
        }
        if (unbreaking > 0) {
            list.add("Un" + unbreaking);
        }
    }

    private void handleAxeEnchantments(ArrayList<String> list, ItemStack stack) {
        int sharpness = EnchantmentHelper.getEnchantmentLevel(Enchantments.SHARPNESS, stack);
        int efficiency = EnchantmentHelper.getEnchantmentLevel(Enchantments.EFFICIENCY, stack);
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        if (sharpness > 0) {
            list.add("Sh" + sharpness);
        }
        if (efficiency > 0) {
            list.add("Eff" + efficiency);
        }
        if (unbreaking > 0) {
            list.add("Un" + unbreaking);
        }
    }

    private void handleArmorEnchantments(ArrayList<String> list, ItemStack stack) {
        int unbreaking = EnchantmentHelper.getEnchantmentLevel(Enchantments.UNBREAKING, stack);
        int mending = EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack);
        int feather = EnchantmentHelper.getEnchantmentLevel(Enchantments.FEATHER_FALLING, stack);
        int depth = EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, stack);
        int protection = EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, stack);
        int aquaF = EnchantmentHelper.getEnchantmentLevel(Enchantments.AQUA_AFFINITY, stack);
        if (aquaF > 0) {
            list.add("Aq" + aquaF);
        }
        if (depth > 0) {
            list.add("De" + depth);
        }
        if (feather > 0) {
            list.add("Fe" + feather);
        }
        if (protection > 0) {
            list.add("Pr" + protection);
        }
        if (mending > 0) {
            list.add("Me" + mending);
        }
        if (unbreaking > 0) {
            list.add("Un" + unbreaking);
        }
    }

    private ArrayList<String> getEnchantment(ItemStack stack) {
        ArrayList<String> list = new ArrayList<String>();
        Item item = stack.getItem();
        if (item instanceof AxeItem) {
            this.handleAxeEnchantments(list, stack);
        } else if (item instanceof ArmorItem) {
            this.handleArmorEnchantments(list, stack);
        } else if (item instanceof BowItem) {
            this.handleBowEnchantments(list, stack);
        } else if (item instanceof SwordItem) {
            this.handleSwordEnchantments(list, stack);
        } else if (item instanceof ToolItem) {
            this.handleToolEnchantments(list, stack);
        }
        return list;
    }

    private void renderEffects(PlayerEntity player, float y, float endX, MatrixStack matrices) {
        EffectInstance[] effects = player.getActivePotionEffects().toArray(new EffectInstance[0]);
        float offset = 0.0f;
        for (EffectInstance p : player.getActivePotionEffects()) {
            if (p == null) continue;
            String effectName = I18n.format(p.getEffectName(), new Object[0]);
            String effectAmplifier = I18n.format("enchantment.level." + (p.getAmplifier() + 1), new Object[0]);
            String effectDuration = EffectUtils.getPotionDurationString(p, 1.0f);
            String effectString = effectName + " " + effectAmplifier;
            int timeColor = ColorHelpers.rgba(255, 255, 255, 255);
            timeColor = p.getDuration() < 200 ? ColorHelpers.rgba(255, 128, 128, 255) : (p.getDuration() < 600 ? ColorHelpers.rgba(255, 217, 4, 255) : ColorHelpers.rgba(128, 255, 128, 255));
            suisse_intl.drawText(matrices, effectString, endX + 2.5f, y - 2.0f + offset, new Color(255, 255, 255, 200).getRGB(), 6.0f);
            suisse_intl.drawText(matrices, " " + effectDuration, endX + 2.5f + suisse_intl.getWidth(effectString, 6.0f), y - 2.0f + offset, timeColor, 6.0f);
            offset += suisse_intl.getHeight(6.0f);
        }
    }

    public static void drawMcRect(double left, double top, double right, double bottom, int color) {
        if (left < right) {
            double i = left;
            left = right;
            right = i;
        }
        if (top < bottom) {
            double j = top;
            top = bottom;
            bottom = j;
        }
        float f3 = (float)(color >> 24 & 0xFF) / 255.0f;
        float f = (float)(color >> 16 & 0xFF) / 255.0f;
        float f1 = (float)(color >> 8 & 0xFF) / 255.0f;
        float f2 = (float)(color & 0xFF) / 255.0f;
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        bufferbuilder.pos(left, bottom, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, bottom, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(right, top, 1.0).color(f, f1, f2, f3).endVertex();
        bufferbuilder.pos(left, top, 1.0).color(f, f1, f2, f3).endVertex();
    }
}
