package net.minecraft.client.gui.overlay;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dimasik.Load;
import dimasik.events.main.render.EventGameOverlay;
import dimasik.helpers.interfaces.IFont;
import java.util.Map;
import java.util.UUID;
import lombok.Generated;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.BossInfo;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.reflect.Reflector;

public class BossOverlayGui
extends AbstractGui {
    private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation("textures/gui/bars.png");
    private final Minecraft client;
    private final Map<UUID, ClientBossInfo> mapBossInfos = Maps.newLinkedHashMap();

    public BossOverlayGui(Minecraft clientIn) {
        this.client = clientIn;
    }

    public void func_238484_a_(MatrixStack p_238484_1_) {
        EventGameOverlay eventGameOverlay = new EventGameOverlay(EventGameOverlay.OverlayType.BossBar);
        Load.getInstance().getEvents().call(eventGameOverlay);
        if (eventGameOverlay.isCancelled()) {
            return;
        }
        if (!this.mapBossInfos.isEmpty()) {
            int i = this.client.getMainWindow().getScaledWidth();
            int j = 12;
            for (ClientBossInfo clientbossinfo : this.mapBossInfos.values()) {
                int k = i / 2 - 91;
                RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
                boolean flag = true;
                int l = 19;
                if (Reflector.ForgeHooksClient_bossBarRenderPre.exists()) {
                    Object object = Reflector.ForgeHooksClient_bossBarRenderPre.call(p_238484_1_, this.client.getMainWindow(), clientbossinfo, k, j, 19);
                    flag = !Reflector.callBoolean(object, Reflector.Event_isCanceled, new Object[0]);
                    l = Reflector.callInt(object, Reflector.RenderGameOverlayEvent_BossInfo_getIncrement, new Object[0]);
                }
                if (flag) {
                    this.client.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
                    this.func_238485_a_(p_238484_1_, k, j, clientbossinfo);
                    ITextComponent itextcomponent = clientbossinfo.getName();
                    float i1 = IFont.sf_medium.getWidth(itextcomponent, 9.8f);
                    float j1 = (float)(i / 2) - i1 / 2.0f;
                    int k1 = j - 9;
                    int l1 = 0xFFFFFF;
                    if (Config.isCustomColors()) {
                        l1 = CustomColors.getBossTextColor(l1);
                    }
                    IFont.sf_medium.drawText(p_238484_1_, itextcomponent, j1, (float)k1 - 2.6f, 9.8f, 255.0f);
                }
                Reflector.ForgeHooksClient_bossBarRenderPost.callVoid(p_238484_1_, this.client.getMainWindow());
                if ((j += l) < this.client.getMainWindow().getScaledHeight() / 3) continue;
                break;
            }
        }
    }

    private void func_238485_a_(MatrixStack p_238485_1_, int p_238485_2_, int p_238485_3_, BossInfo p_238485_4_) {
        int i;
        this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, p_238485_4_.getColor().ordinal() * 5 * 2, 182, 5);
        if (p_238485_4_.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, 80 + (p_238485_4_.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
        }
        if ((i = (int)(p_238485_4_.getPercent() * 183.0f)) > 0) {
            this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, p_238485_4_.getColor().ordinal() * 5 * 2 + 5, i, 5);
            if (p_238485_4_.getOverlay() != BossInfo.Overlay.PROGRESS) {
                this.blit(p_238485_1_, p_238485_2_, p_238485_3_, 0, 80 + (p_238485_4_.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
            }
        }
    }

    public void read(SUpdateBossInfoPacket packetIn) {
        if (packetIn.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
            this.mapBossInfos.put(packetIn.getUniqueId(), new ClientBossInfo(packetIn));
        } else if (packetIn.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
            this.mapBossInfos.remove(packetIn.getUniqueId());
        } else {
            this.mapBossInfos.get(packetIn.getUniqueId()).updateFromPacket(packetIn);
        }
    }

    public void clearBossInfos() {
        this.mapBossInfos.clear();
    }

    public boolean shouldPlayEndBossMusic() {
        if (!this.mapBossInfos.isEmpty()) {
            for (BossInfo bossInfo : this.mapBossInfos.values()) {
                if (!bossInfo.shouldPlayEndBossMusic()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldDarkenSky() {
        if (!this.mapBossInfos.isEmpty()) {
            for (BossInfo bossInfo : this.mapBossInfos.values()) {
                if (!bossInfo.shouldDarkenSky()) continue;
                return true;
            }
        }
        return false;
    }

    public boolean shouldCreateFog() {
        if (!this.mapBossInfos.isEmpty()) {
            for (BossInfo bossInfo : this.mapBossInfos.values()) {
                if (!bossInfo.shouldCreateFog()) continue;
                return true;
            }
        }
        return false;
    }

    @Generated
    public Map<UUID, ClientBossInfo> getMapBossInfos() {
        return this.mapBossInfos;
    }
}
