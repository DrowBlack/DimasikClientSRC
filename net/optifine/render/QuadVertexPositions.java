package net.optifine.render;

import net.optifine.render.VertexPosition;
import net.optifine.util.IntExpiringCache;
import net.optifine.util.RandomUtils;

public class QuadVertexPositions
extends IntExpiringCache<VertexPosition[]> {
    public QuadVertexPositions() {
        super(60000 + RandomUtils.getRandomInt(10000));
    }

    @Override
    protected VertexPosition[] make() {
        return new VertexPosition[]{new VertexPosition(), new VertexPosition(), new VertexPosition(), new VertexPosition()};
    }
}
