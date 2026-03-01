package dimasik.events.main.player;

import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

public class EventObsidianPlace
implements Event {
    private final Block block;
    private final BlockPos pos;

    @Generated
    public Block getBlock() {
        return this.block;
    }

    @Generated
    public BlockPos getPos() {
        return this.pos;
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EventObsidianPlace)) {
            return false;
        }
        EventObsidianPlace other = (EventObsidianPlace)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Block this$block = this.getBlock();
        Block other$block = other.getBlock();
        if (this$block == null ? other$block != null : !this$block.equals(other$block)) {
            return false;
        }
        BlockPos this$pos = this.getPos();
        BlockPos other$pos = other.getPos();
        return !(this$pos == null ? other$pos != null : !((Object)this$pos).equals(other$pos));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof EventObsidianPlace;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Block $block = this.getBlock();
        result = result * 59 + ($block == null ? 43 : $block.hashCode());
        BlockPos $pos = this.getPos();
        result = result * 59 + ($pos == null ? 43 : ((Object)$pos).hashCode());
        return result;
    }

    @Generated
    public String toString() {
        return "EventObsidianPlace(block=" + String.valueOf(this.getBlock()) + ", pos=" + String.valueOf(this.getPos()) + ")";
    }

    @Generated
    public EventObsidianPlace(Block block, BlockPos pos) {
        this.block = block;
        this.pos = pos;
    }
}
