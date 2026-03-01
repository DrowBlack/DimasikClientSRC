package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.entity.player.PlayerEntity;

public class EventPlayerRender
implements Event {
    private final PlayerRenderer renderer;
    private final IRenderTypeBuffer buffers;
    private final PlayerEntity entityPlayer;

    @Generated
    public EventPlayerRender(PlayerRenderer renderer, IRenderTypeBuffer buffers, PlayerEntity entityPlayer) {
        this.renderer = renderer;
        this.buffers = buffers;
        this.entityPlayer = entityPlayer;
    }

    @Generated
    public PlayerRenderer getRenderer() {
        return this.renderer;
    }

    @Generated
    public IRenderTypeBuffer getBuffers() {
        return this.buffers;
    }

    @Generated
    public PlayerEntity getEntityPlayer() {
        return this.entityPlayer;
    }

    public static class Post
    extends EventPlayerRender {
        public Post(PlayerRenderer renderer, IRenderTypeBuffer buffers, PlayerEntity playerEntity) {
            super(renderer, buffers, playerEntity);
        }
    }

    public static class Pre
    extends EventPlayerRender {
        public Pre(PlayerRenderer renderer, IRenderTypeBuffer buffers, PlayerEntity playerEntity) {
            super(renderer, buffers, playerEntity);
        }
    }
}
