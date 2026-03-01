package net.minecraft.client.particle;

import java.util.Objects;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.MetaParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;

public class HugeExplosionParticle
extends MetaParticle {
    private int timeSinceStart;
    private final int maximumTime = 8;

    private HugeExplosionParticle(ClientWorld world, double x, double y, double z) {
        super(world, x, y, z, 0.0, 0.0, 0.0);
    }

    @Override
    public void tick() {
        for (int i = 0; i < 6; ++i) {
            double d0 = this.posX + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            double d1 = this.posY + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            double d2 = this.posZ + (this.rand.nextDouble() - this.rand.nextDouble()) * 4.0;
            float f = this.timeSinceStart;
            Objects.requireNonNull(this);
            this.world.addParticle(ParticleTypes.EXPLOSION, d0, d1, d2, f / 8.0f, 0.0, 0.0);
        }
        ++this.timeSinceStart;
        if (this.timeSinceStart == this.maximumTime) {
            this.setExpired();
        }
    }

    public static class Factory
    implements IParticleFactory<BasicParticleType> {
        @Override
        public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new HugeExplosionParticle(worldIn, x, y, z);
        }
    }
}
