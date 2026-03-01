package com.mojang.datafixers;

import com.mojang.datafixers.View;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.RecursivePoint;
import java.util.BitSet;
import java.util.Objects;
import org.apache.commons.lang3.ObjectUtils;

public final class RewriteResult<A, B> {
    protected final View<A, B> view;
    protected final BitSet recData;

    public RewriteResult(View<A, B> view, BitSet recData) {
        this.view = view;
        this.recData = recData;
    }

    public static <A, B> RewriteResult<A, B> create(View<A, B> view, BitSet recData) {
        return new RewriteResult<A, B>(view, recData);
    }

    public static <A> RewriteResult<A, A> nop(Type<A> type) {
        return new RewriteResult<A, A>(View.nopView(type), new BitSet());
    }

    public <C> RewriteResult<C, B> compose(RewriteResult<C, A> that) {
        BitSet newData;
        if (this.view.type() instanceof RecursivePoint.RecursivePointType && that.view.type() instanceof RecursivePoint.RecursivePointType) {
            newData = ObjectUtils.clone(this.recData);
            newData.or(that.recData);
        } else {
            newData = this.recData;
        }
        return RewriteResult.create(this.view.compose(that.view), newData);
    }

    public BitSet recData() {
        return this.recData;
    }

    public View<A, B> view() {
        return this.view;
    }

    public String toString() {
        return "RR[" + this.view + "]";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        RewriteResult that = (RewriteResult)o;
        return Objects.equals(this.view, that.view);
    }

    public int hashCode() {
        return Objects.hash(this.view);
    }
}
