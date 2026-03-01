package dimasik.itemics.behavior;

import dimasik.itemics.Itemics;
import dimasik.itemics.api.cache.IWaypoint;
import dimasik.itemics.api.cache.Waypoint;
import dimasik.itemics.api.command.IItemicsChatControl;
import dimasik.itemics.api.event.events.BlockInteractEvent;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.behavior.Behavior;
import dimasik.itemics.utils.BlockStateInterface;
import java.util.Set;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.state.properties.BedPart;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class WaypointBehavior
extends Behavior {
    public WaypointBehavior(Itemics itemics) {
        super(itemics);
    }

    @Override
    public void onBlockInteract(BlockInteractEvent event) {
        BetterBlockPos pos;
        BlockState state;
        if (!((Boolean)Itemics.settings().doBedWaypoints.value).booleanValue()) {
            return;
        }
        if (event.getType() == BlockInteractEvent.Type.USE && (state = BlockStateInterface.get(this.ctx, pos = BetterBlockPos.from(event.getPos()))).getBlock() instanceof BedBlock) {
            if (state.get(BedBlock.PART) == BedPart.FOOT) {
                pos = pos.offset(state.get(BedBlock.HORIZONTAL_FACING));
            }
            Set<IWaypoint> waypoints = this.itemics.getWorldProvider().getCurrentWorld().getWaypoints().getByTag(IWaypoint.Tag.BED);
            boolean exists = waypoints.stream().map(IWaypoint::getLocation).filter(pos::equals).findFirst().isPresent();
            if (!exists) {
                this.itemics.getWorldProvider().getCurrentWorld().getWaypoints().addWaypoint(new Waypoint("bed", IWaypoint.Tag.BED, pos));
            }
        }
    }

    @Override
    public void onPlayerDeath() {
        if (!((Boolean)Itemics.settings().doDeathWaypoints.value).booleanValue()) {
            return;
        }
        Waypoint deathWaypoint = new Waypoint("death", IWaypoint.Tag.DEATH, this.ctx.playerFeet());
        this.itemics.getWorldProvider().getCurrentWorld().getWaypoints().addWaypoint(deathWaypoint);
        StringTextComponent component = new StringTextComponent("Death position saved.");
        component.setStyle(component.getStyle().setFormatting(TextFormatting.WHITE).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to goto death"))).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("%s%s goto %s @ %d", IItemicsChatControl.FORCE_COMMAND_PREFIX, "wp", deathWaypoint.getTag().getName(), deathWaypoint.getCreationTimestamp()))));
        Helper.HELPER.logDirect(component);
    }
}
