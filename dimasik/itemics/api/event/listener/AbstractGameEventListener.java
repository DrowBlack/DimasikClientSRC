package dimasik.itemics.api.event.listener;

import dimasik.itemics.api.event.events.BlockInteractEvent;
import dimasik.itemics.api.event.events.ChatEvent;
import dimasik.itemics.api.event.events.ChunkEvent;
import dimasik.itemics.api.event.events.PacketEvent;
import dimasik.itemics.api.event.events.PathEvent;
import dimasik.itemics.api.event.events.PlayerUpdateEvent;
import dimasik.itemics.api.event.events.RenderEvent;
import dimasik.itemics.api.event.events.RotationMoveEvent;
import dimasik.itemics.api.event.events.SprintStateEvent;
import dimasik.itemics.api.event.events.TabCompleteEvent;
import dimasik.itemics.api.event.events.TickEvent;
import dimasik.itemics.api.event.events.WorldEvent;
import dimasik.itemics.api.event.listener.IGameEventListener;

public interface AbstractGameEventListener
extends IGameEventListener {
    @Override
    default public void onTick(TickEvent event) {
    }

    @Override
    default public void onPlayerUpdate(PlayerUpdateEvent event) {
    }

    @Override
    default public void onSendChatMessage(ChatEvent event) {
    }

    @Override
    default public void onPreTabComplete(TabCompleteEvent event) {
    }

    @Override
    default public void onChunkEvent(ChunkEvent event) {
    }

    @Override
    default public void onRenderPass(RenderEvent event) {
    }

    @Override
    default public void onWorldEvent(WorldEvent event) {
    }

    @Override
    default public void onSendPacket(PacketEvent event) {
    }

    @Override
    default public void onReceivePacket(PacketEvent event) {
    }

    @Override
    default public void onPlayerRotationMove(RotationMoveEvent event) {
    }

    @Override
    default public void onPlayerSprintState(SprintStateEvent event) {
    }

    @Override
    default public void onBlockInteract(BlockInteractEvent event) {
    }

    @Override
    default public void onPlayerDeath() {
    }

    @Override
    default public void onPathEvent(PathEvent event) {
    }
}
