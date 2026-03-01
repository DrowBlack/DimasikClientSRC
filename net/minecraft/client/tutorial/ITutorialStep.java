package net.minecraft.client.tutorial;

import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;

public interface ITutorialStep {
    default public void onStop() {
    }

    default public void tick() {
    }

    default public void handleMovement(MovementInput input) {
    }

    default public void onMouseMove(double velocityX, double velocityY) {
    }

    default public void onMouseHover(ClientWorld worldIn, RayTraceResult result) {
    }

    default public void onHitBlock(ClientWorld worldIn, BlockPos pos, BlockState state, float diggingStage) {
    }

    default public void openInventory() {
    }

    default public void handleSetSlot(ItemStack stack) {
    }
}
