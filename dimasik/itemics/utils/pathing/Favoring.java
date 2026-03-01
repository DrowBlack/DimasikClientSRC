package dimasik.itemics.utils.pathing;

import dimasik.itemics.api.pathing.calc.IPath;
import dimasik.itemics.api.utils.BetterBlockPos;
import dimasik.itemics.api.utils.Helper;
import dimasik.itemics.api.utils.IPlayerContext;
import dimasik.itemics.pathing.movement.CalculationContext;
import dimasik.itemics.utils.pathing.Avoidance;
import it.unimi.dsi.fastutil.longs.Long2DoubleOpenHashMap;

public final class Favoring {
    private final Long2DoubleOpenHashMap favorings = new Long2DoubleOpenHashMap();

    public Favoring(IPlayerContext ctx, IPath previous, CalculationContext context) {
        this(previous, context);
        for (Avoidance avoid : Avoidance.create(ctx)) {
            avoid.applySpherical(this.favorings);
        }
        Helper.HELPER.logDebug("Favoring size: " + this.favorings.size());
    }

    public Favoring(IPath previous, CalculationContext context) {
        this.favorings.defaultReturnValue(1.0);
        double coeff = context.backtrackCostFavoringCoefficient;
        if (coeff != 1.0 && previous != null) {
            previous.positions().forEach(pos -> this.favorings.put(BetterBlockPos.longHash(pos), coeff));
        }
    }

    public boolean isEmpty() {
        return this.favorings.isEmpty();
    }

    public double calculate(long hash) {
        return this.favorings.get(hash);
    }
}
