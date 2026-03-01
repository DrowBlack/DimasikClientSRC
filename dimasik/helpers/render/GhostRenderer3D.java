package dimasik.helpers.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import dimasik.Load;
import dimasik.events.main.render.EventTest;
import dimasik.helpers.interfaces.IFastAccess;
import dimasik.helpers.render.ColorHelpers;
import dimasik.helpers.visual.VisualHelpers;
import dimasik.modules.render.TargetESP;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector4f;

public class GhostRenderer3D
implements IFastAccess {
    private Vector3d prevPosition = Vector3d.ZERO;
    private Vector3d position;
    private Vector3d motion;
    private final ArrayList<Vector4f> tail = new ArrayList();
    private float alpha = 1.0f;
    private final float size;
    TargetESP target = Load.getInstance().getHooks().getModuleManagers().getTargetESP();

    public GhostRenderer3D(Vector3d position, Vector3d motion, float size) {
        this.position = position;
        this.motion = motion;
        this.size = size;
    }

    public Vector3d getPosition() {
        return this.position;
    }

    public void setPosition(Vector3d position) {
        this.position = position;
    }

    public Vector3d getMotion() {
        return this.motion;
    }

    public void setMotion(Vector3d motion) {
        this.motion = motion;
    }

    public float getAlpha() {
        return this.alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public void onRender(EventTest e) {
        this.prevPosition = new Vector3d(this.position.x, this.position.y, this.position.z);
        this.position = this.position.add(this.motion);
        MatrixStack ms = e.getMatrixStack();
        float size = this.size;
        float length = 30.0f;
        double x = this.position.x;
        double y = this.position.y;
        double z = this.position.z;
        this.tail.add(new Vector4f((float)x, (float)y + 0.7f, (float)z, length));
        ArrayList<Vector4f> vec4f = new ArrayList<Vector4f>();
        for (int i = 0; i < this.tail.size(); ++i) {
            Vector4f vec = this.tail.get(i);
            if (!(vec.getW() > 0.0f)) continue;
            float miniSize = size * vec.getW() / length;
            double posX = (double)vec.getX() - GhostRenderer3D.mc.getRenderManager().info.getProjectedView().x;
            double posY = (double)vec.getY() - GhostRenderer3D.mc.getRenderManager().info.getProjectedView().y;
            double posZ = (double)vec.getZ() - GhostRenderer3D.mc.getRenderManager().info.getProjectedView().z;
            if (VisualHelpers.isInView(new Vector3d(vec.getX(), vec.getY(), vec.getZ()))) {
                ms.push();
                ms.translate(posX, posY, posZ);
                ms.rotate(GhostRenderer3D.mc.getRenderManager().info.getRotation());
                VisualHelpers.drawCleanImage(ms, -miniSize / 2.0f, -miniSize / 2.0f, -miniSize / 2.0f, miniSize, miniSize, ColorHelpers.setAlpha2(ColorHelpers.getThemeColor2(1), 70), ColorHelpers.setAlpha2(ColorHelpers.getThemeColor2(1), 70), ColorHelpers.setAlpha2(ColorHelpers.getThemeColor2(1), 70), ColorHelpers.setAlpha2(ColorHelpers.getThemeColor2(1), 70));
                ms.pop();
            }
            vec.set(vec.getX(), vec.getY() + 0.004f / Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 300.0f, vec.getZ(), vec.getW() - 0.3f / Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 300.0f);
            if (!(vec.getW() <= 0.0f)) continue;
            vec4f.add(vec);
        }
        if (this.alpha < 0.0f) {
            // empty if block
        }
        for (Vector4f vec : vec4f) {
            this.tail.remove(vec);
        }
    }

    public void update() {
        this.alpha = 1.0f;
        this.motion = this.motion.mul(0.95 / (double)Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 300.0, 0.95 / (double)Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 300.0, 0.95 / (double)Math.max((float)Minecraft.getDebugFPS(), 5.0f) * 300.0);
    }
}
