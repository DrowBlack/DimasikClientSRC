package dimasik.managers.mods.cape.wavecapes.sim;

import dimasik.managers.mods.cape.wavecapes.math.Vector3;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.MathHelper;

public class StickSimulation {
    public List<Point> points = new ArrayList<Point>();
    public List<Stick> sticks = new ArrayList<Stick>();
    public Vector3 gravityDirection = new Vector3(0.0f, -1.0f, 0.0f);
    public float gravity = 25.0f;
    public int numIterations = 30;
    private final float maxBend;
    public boolean sneaking = false;

    public StickSimulation() {
        this.maxBend = 20.0f;
    }

    public boolean init(int partCount) {
        if (this.points.size() != partCount) {
            this.points.clear();
            this.sticks.clear();
            for (int i = 0; i < partCount; ++i) {
                Point point = new Point();
                point.position.y = -i;
                point.position.x = -i;
                point.locked = i == 0;
                this.points.add(point);
                if (i <= 0) continue;
                this.sticks.add(new Stick(this.points.get(i - 1), point, 1.0f));
            }
            return true;
        }
        return false;
    }

    public void simulate() {
        this.applyGravity();
        this.preventClipping();
        this.preventSelfClipping();
        this.applyMotion();
        this.preventSelfClipping();
        this.preventHardBends();
        this.limitLength();
    }

    private void applyGravity() {
        float deltaTime = 0.05f;
        Vector3 down = this.gravityDirection.clone().mul(this.gravity * deltaTime);
        Vector3 tmp = new Vector3(0.0f, 0.0f, 0.0f);
        for (Point p : this.points) {
            if (p.locked) continue;
            tmp.copy(p.position);
            p.position.add(down);
            p.prevPosition.copy(tmp);
        }
    }

    private void applyMotion() {
        for (int i = 0; i < this.numIterations; ++i) {
            for (int x = this.sticks.size() - 1; x >= 0; --x) {
                Stick stick = this.sticks.get(x);
                Vector3 stickCentre = stick.pointA.position.clone().add(stick.pointB.position).div(2.0f);
                Vector3 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
                if (!stick.pointA.locked) {
                    stick.pointA.position = stickCentre.clone().add(stickDir.clone().mul(stick.length / 2.0f));
                }
                if (stick.pointB.locked) continue;
                stick.pointB.position = stickCentre.clone().subtract(stickDir.clone().mul(stick.length / 2.0f));
            }
        }
    }

    private void limitLength() {
        for (Stick stick : this.sticks) {
            Vector3 stickDir = stick.pointA.position.clone().subtract(stick.pointB.position).normalize();
            if (stick.pointB.locked) continue;
            stick.pointB.position = stick.pointA.position.clone().subtract(stickDir.mul(stick.length));
        }
    }

    private void preventSelfClipping() {
        boolean clipped;
        int runs = 0;
        do {
            clipped = false;
            for (int a = 0; a < this.points.size(); ++a) {
                for (int b = a + 1; b < this.points.size(); ++b) {
                    Point pA = this.points.get(a);
                    Point pB = this.points.get(b);
                    Vector3 stickDir = pA.position.clone().subtract(pB.position);
                    if (!((double)stickDir.sqrMagnitude() < 0.99)) continue;
                    clipped = true;
                    ++runs;
                    stickDir.normalize();
                    Vector3 centre = pA.position.clone().add(pB.position).div(2.0f);
                    if (!pA.locked) {
                        pA.position = centre.clone().add(stickDir.clone().mul(0.5f));
                    }
                    if (pB.locked) continue;
                    pB.position = centre.clone().subtract(stickDir.clone().mul(0.5f));
                }
            }
        } while (clipped && runs < 32);
    }

    private void preventHardBends() {
        for (int i = 1; i < this.points.size() - 2; ++i) {
            Vector3 replacement;
            double angle = this.getAngle(this.points.get((int)i).position, this.points.get((int)(i - 1)).position, this.points.get((int)(i + 1)).position);
            if (angle < (double)(-this.maxBend)) {
                this.points.get((int)(i + 1)).position = replacement = this.getReplacement(this.points.get((int)i).position, this.points.get((int)(i - 1)).position, -this.maxBend * 2.0f);
            }
            if (!(angle > (double)this.maxBend)) continue;
            this.points.get((int)(i + 1)).position = replacement = this.getReplacement(this.points.get((int)i).position, this.points.get((int)(i - 1)).position, this.maxBend * 2.0f);
        }
    }

    private void preventClipping() {
        Point basePoint = this.points.get(0);
        for (int i = 1; i < this.points.size(); ++i) {
            float maxZ;
            float z;
            Point p = this.points.get(i);
            if (p.position.x - basePoint.position.x > 0.0f) {
                p.position.x = basePoint.position.x;
            }
            if ((z = basePoint.position.z - p.position.z) > (maxZ = (float)i / (float)this.points.size() * ((float)i / (float)this.points.size()) * 5.0f)) {
                p.position.z = basePoint.position.z - maxZ;
            }
            if (!(z < -maxZ)) continue;
            p.position.z = basePoint.position.z + maxZ;
        }
    }

    private Vector3 getReplacement(Vector3 middle, Vector3 prev, double target) {
        Vector3 dir = middle.clone().subtract(prev);
        dir.rotateDegrees((float)target).add(middle);
        return dir;
    }

    private double getAngle(Vector3 a, Vector3 b, Vector3 c) {
        float abx = b.x - a.x;
        float aby = b.y - a.y;
        float cbx = b.x - c.x;
        float cby = b.y - c.y;
        float dot = abx * cbx + aby * cby;
        float cross = abx * cby - aby * cbx;
        double alpha = MathHelper.atan2(cross, dot);
        return alpha * 180.0 / Math.PI;
    }

    public void setGravityDirection(Vector3 gravityDirection) {
        this.gravityDirection = gravityDirection;
    }

    public float getGravity() {
        return this.gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public boolean isSneaking() {
        return this.sneaking;
    }

    public void setSneaking(boolean sneaking) {
        this.sneaking = sneaking;
    }

    public boolean empty() {
        return this.sticks.isEmpty();
    }

    public void applyMovement(Vector3 movement) {
        this.points.get((int)0).prevPosition.copy(this.points.get((int)0).position);
        this.points.get((int)0).position.add(movement);
    }

    public List<Point> getPoints() {
        return this.points;
    }

    public static class Point {
        public Vector3 position = new Vector3(0.0f, 0.0f, 0.0f);
        public Vector3 prevPosition = new Vector3(0.0f, 0.0f, 0.0f);
        public boolean locked;

        public float getLerpX(float delta) {
            return MathHelper.lerp(delta, this.prevPosition.x, this.position.x);
        }

        public float getLerpY(float delta) {
            return MathHelper.lerp(delta, this.prevPosition.y, this.position.y);
        }

        public float getLerpZ(float delta) {
            return MathHelper.lerp(delta, this.prevPosition.z, this.position.z);
        }

        public Vector3 getLerpedPos(float delta) {
            return new Vector3(this.getLerpX(delta), this.getLerpY(delta), this.getLerpZ(delta));
        }
    }

    public static class Stick {
        public Point pointA;
        public Point pointB;
        public float length;

        public Stick(Point pointA, Point pointB, float length) {
            this.pointA = pointA;
            this.pointB = pointB;
            this.length = length;
        }
    }
}
