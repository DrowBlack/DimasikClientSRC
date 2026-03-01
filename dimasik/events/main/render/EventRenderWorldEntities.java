package dimasik.events.main.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.events.api.main.Event;
import lombok.Generated;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.math.vector.Matrix4f;

public class EventRenderWorldEntities
implements Event {
    private MatrixStack matrix;
    private Matrix4f projectionMatrix;
    private ActiveRenderInfo activeRenderInfo;
    private WorldRenderer context;
    private float partialTicks;
    private long finishTimeNano;
    private double x;
    private double y;
    private double z;
    private IRenderTypeBuffer vertex;

    @Generated
    public MatrixStack getMatrix() {
        return this.matrix;
    }

    @Generated
    public Matrix4f getProjectionMatrix() {
        return this.projectionMatrix;
    }

    @Generated
    public ActiveRenderInfo getActiveRenderInfo() {
        return this.activeRenderInfo;
    }

    @Generated
    public WorldRenderer getContext() {
        return this.context;
    }

    @Generated
    public float getPartialTicks() {
        return this.partialTicks;
    }

    @Generated
    public long getFinishTimeNano() {
        return this.finishTimeNano;
    }

    @Generated
    public double getX() {
        return this.x;
    }

    @Generated
    public double getY() {
        return this.y;
    }

    @Generated
    public double getZ() {
        return this.z;
    }

    @Generated
    public IRenderTypeBuffer getVertex() {
        return this.vertex;
    }

    @Generated
    public void setMatrix(MatrixStack matrix) {
        this.matrix = matrix;
    }

    @Generated
    public void setProjectionMatrix(Matrix4f projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }

    @Generated
    public void setActiveRenderInfo(ActiveRenderInfo activeRenderInfo) {
        this.activeRenderInfo = activeRenderInfo;
    }

    @Generated
    public void setContext(WorldRenderer context) {
        this.context = context;
    }

    @Generated
    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    @Generated
    public void setFinishTimeNano(long finishTimeNano) {
        this.finishTimeNano = finishTimeNano;
    }

    @Generated
    public void setX(double x) {
        this.x = x;
    }

    @Generated
    public void setY(double y) {
        this.y = y;
    }

    @Generated
    public void setZ(double z) {
        this.z = z;
    }

    @Generated
    public void setVertex(IRenderTypeBuffer vertex) {
        this.vertex = vertex;
    }

    @Generated
    public EventRenderWorldEntities(MatrixStack matrix, Matrix4f projectionMatrix, ActiveRenderInfo activeRenderInfo, WorldRenderer context, float partialTicks, long finishTimeNano, double x, double y, double z, IRenderTypeBuffer vertex) {
        this.matrix = matrix;
        this.projectionMatrix = projectionMatrix;
        this.activeRenderInfo = activeRenderInfo;
        this.context = context;
        this.partialTicks = partialTicks;
        this.finishTimeNano = finishTimeNano;
        this.x = x;
        this.y = y;
        this.z = z;
        this.vertex = vertex;
    }
}
