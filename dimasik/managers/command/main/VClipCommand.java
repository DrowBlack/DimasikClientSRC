package dimasik.managers.command.main;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dimasik.managers.command.api.Command;
import dimasik.utils.client.ChatUtils;
import net.minecraft.block.Blocks;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.BlockPos;

public class VClipCommand
extends Command {
    public VClipCommand() {
        super("\u041f\u043e\u0437\u0432\u043e\u043b\u044f\u0435\u0442 \u0442\u0435\u043b\u0435\u043f\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u0430\u0442\u044c\u0441\u044f \u043f\u043e \u0432\u0435\u0440\u0442\u0438\u043a\u0430\u043b\u0438", "vclip", "verticalclip");
    }

    public void vclipDown() {
        BlockPos playerPos = new BlockPos(VClipCommand.mc.player.getPosX(), VClipCommand.mc.player.getPosY(), VClipCommand.mc.player.getPosZ());
        float offset = this.findOffsetToNextAirPocket(playerPos);
        if (offset == 0.0f) {
            return;
        }
        double clipPos = VClipCommand.mc.player.getPosY() + (double)offset;
        try {
            int i;
            VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
            VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
            for (i = 0; i < 10; ++i) {
                VClipCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(VClipCommand.mc.player.getPosX(), VClipCommand.mc.player.getPosY(), VClipCommand.mc.player.getPosZ(), false));
            }
            for (i = 0; i < 10; ++i) {
                VClipCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(VClipCommand.mc.player.getPosX(), clipPos, VClipCommand.mc.player.getPosZ(), false));
                VClipCommand.mc.player.setPosition(VClipCommand.mc.player.getPosX(), clipPos, VClipCommand.mc.player.getPosZ());
            }
            VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
            VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    @Override
    public void build(LiteralArgumentBuilder<ISuggestionProvider> builder) {
        builder.then((ArgumentBuilder<ISuggestionProvider, ?>)this.argument("value", IntegerArgumentType.integer()).executes(ctx -> {
            int clip = ctx.getArgument("value", Integer.class);
            double clipPos = VClipCommand.mc.player.getPosY() + (double)clip;
            try {
                int i;
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                for (i = 0; i < 10; ++i) {
                    VClipCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(VClipCommand.mc.player.getPosX(), VClipCommand.mc.player.getPosY(), VClipCommand.mc.player.getPosZ(), false));
                }
                for (i = 0; i < 10; ++i) {
                    VClipCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(VClipCommand.mc.player.getPosX(), clipPos, VClipCommand.mc.player.getPosZ(), false));
                    VClipCommand.mc.player.setPosition(VClipCommand.mc.player.getPosX(), clipPos, VClipCommand.mc.player.getPosZ());
                }
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                ChatUtils.addClientMessage("\u0412\u044b \u0431\u044b\u043b\u0438 \u043a\u043b\u0438\u043f\u043d\u0443\u0442\u044b \u043d\u0430 " + clip + "!");
                return 1;
            }
            catch (Exception ignored) {
                return 0;
            }
        }));
        builder.then((ArgumentBuilder<ISuggestionProvider, ?>)this.literal("down").executes(ctx -> {
            this.vclipDown();
            return 1;
        }));
    }

    @Override
    public void run(String[] args) throws Exception {
        if (args.length > 1) {
            if (args[1].equalsIgnoreCase("down")) {
                this.vclipDown();
                return;
            }
            int clip = Integer.parseInt(args[1]);
            double clipPos = VClipCommand.mc.player.getPosY() + (double)clip;
            try {
                int i;
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                for (i = 0; i < 10; ++i) {
                    VClipCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(VClipCommand.mc.player.getPosX(), VClipCommand.mc.player.getPosY(), VClipCommand.mc.player.getPosZ(), false));
                }
                for (i = 0; i < 10; ++i) {
                    VClipCommand.mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(VClipCommand.mc.player.getPosX(), clipPos, VClipCommand.mc.player.getPosZ(), false));
                    VClipCommand.mc.player.setPosition(VClipCommand.mc.player.getPosX(), clipPos, VClipCommand.mc.player.getPosZ());
                }
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.START_SPRINTING));
                VClipCommand.mc.player.connection.sendPacket(new CEntityActionPacket(VClipCommand.mc.player, CEntityActionPacket.Action.STOP_SPRINTING));
                ChatUtils.addClientMessage("\u0412\u044b \u0431\u044b\u043b\u0438 \u043a\u043b\u0438\u043f\u043d\u0443\u0442\u044b \u043d\u0430 " + args[1] + "!");
            }
            catch (Exception exception) {}
        } else if (args.length == 1 && args[0].equalsIgnoreCase("down")) {
            this.vclipDown();
        } else if (args.length == 0) {
            this.error();
        } else {
            this.error();
        }
    }

    private float findOffsetToNextAirPocket(BlockPos playerPos) {
        for (int i = -1; i > -255; --i) {
            BlockPos airPos = playerPos.add(0, i, 0);
            BlockPos blockBelow = airPos.down();
            if (VClipCommand.mc.world.getBlockState(airPos).getBlock() == Blocks.AIR && VClipCommand.mc.world.getBlockState(blockBelow).getBlock() != Blocks.AIR && VClipCommand.mc.world.getBlockState(blockBelow).getBlock() != Blocks.BEDROCK) {
                return i;
            }
            if (VClipCommand.mc.world.getBlockState(airPos).getBlock() != Blocks.BEDROCK) continue;
            return 0.0f;
        }
        return 0.0f;
    }

    @Override
    public void error() {
        ChatUtils.addClientMessage("\u041d\u0435\u043f\u0440\u0430\u0432\u0438\u043b\u044c\u043d\u043e\u0435 \u0438\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435 \u043a\u043e\u043c\u0430\u043d\u0434\u044b!");
        ChatUtils.addClientMessage("\u0418\u0441\u043f\u043e\u043b\u044c\u0437\u043e\u0432\u0430\u043d\u0438\u0435:");
        ChatUtils.addClientMessage(".vclip <blocks>");
        ChatUtils.addClientMessage(".vclip down");
    }
}
