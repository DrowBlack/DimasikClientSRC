package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum ZoomLayer implements IAreaTransformer1
{
    NORMAL,
    FUZZY{

        @Override
        protected int pickZoomed(IExtendedNoiseRandom<?> context, int first, int second, int third, int fourth) {
            return context.pickRandom(first, second, third, fourth);
        }
    };


    @Override
    public int getOffsetX(int x) {
        return x >> 1;
    }

    @Override
    public int getOffsetZ(int z) {
        return z >> 1;
    }

    @Override
    public int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z) {
        int i = area.getValue(this.getOffsetX(x), this.getOffsetZ(z));
        context.setPosition(x >> 1 << 1, z >> 1 << 1);
        int j = x & 1;
        int k = z & 1;
        if (j == 0 && k == 0) {
            return i;
        }
        int l = area.getValue(this.getOffsetX(x), this.getOffsetZ(z + 1));
        int i1 = context.pickRandom(i, l);
        if (j == 0 && k == 1) {
            return i1;
        }
        int j1 = area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z));
        int k1 = context.pickRandom(i, j1);
        if (j == 1 && k == 0) {
            return k1;
        }
        int l1 = area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 1));
        return this.pickZoomed(context, i, j1, l, l1);
    }

    protected int pickZoomed(IExtendedNoiseRandom<?> context, int first, int second, int third, int fourth) {
        if (second == third && third == fourth) {
            return second;
        }
        if (first == second && first == third) {
            return first;
        }
        if (first == second && first == fourth) {
            return first;
        }
        if (first == third && first == fourth) {
            return first;
        }
        if (first == second && third != fourth) {
            return first;
        }
        if (first == third && second != fourth) {
            return first;
        }
        if (first == fourth && second != third) {
            return first;
        }
        if (second == third && first != fourth) {
            return second;
        }
        if (second == fourth && first != third) {
            return second;
        }
        return third == fourth && first != second ? third : context.pickRandom(first, second, third, fourth);
    }
}
