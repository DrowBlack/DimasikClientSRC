package net.minecraft.util.math.shapes;

public interface IBooleanFunction {
    public static final IBooleanFunction FALSE = (p_223272_0_, p_223272_1_) -> false;
    public static final IBooleanFunction NOT_OR = (p_223271_0_, p_223271_1_) -> !p_223271_0_ && !p_223271_1_;
    public static final IBooleanFunction ONLY_SECOND = (p_223270_0_, p_223270_1_) -> p_223270_1_ && !p_223270_0_;
    public static final IBooleanFunction NOT_FIRST = (p_223269_0_, p_223269_1_) -> !p_223269_0_;
    public static final IBooleanFunction ONLY_FIRST = (p_223268_0_, p_223268_1_) -> p_223268_0_ && !p_223268_1_;
    public static final IBooleanFunction NOT_SECOND = (p_223267_0_, p_223267_1_) -> !p_223267_1_;
    public static final IBooleanFunction NOT_SAME = (p_223266_0_, p_223266_1_) -> p_223266_0_ != p_223266_1_;
    public static final IBooleanFunction NOT_AND = (p_223265_0_, p_223265_1_) -> !p_223265_0_ || !p_223265_1_;
    public static final IBooleanFunction AND = (p_223264_0_, p_223264_1_) -> p_223264_0_ && p_223264_1_;
    public static final IBooleanFunction SAME = (p_223263_0_, p_223263_1_) -> p_223263_0_ == p_223263_1_;
    public static final IBooleanFunction SECOND = (p_223262_0_, p_223262_1_) -> p_223262_1_;
    public static final IBooleanFunction CAUSES = (p_223261_0_, p_223261_1_) -> !p_223261_0_ || p_223261_1_;
    public static final IBooleanFunction FIRST = (p_223260_0_, p_223260_1_) -> p_223260_0_;
    public static final IBooleanFunction CAUSED_BY = (p_223259_0_, p_223259_1_) -> p_223259_0_ || !p_223259_1_;
    public static final IBooleanFunction OR = (p_223258_0_, p_223258_1_) -> p_223258_0_ || p_223258_1_;
    public static final IBooleanFunction TRUE = (p_223257_0_, p_223257_1_) -> true;

    public boolean apply(boolean var1, boolean var2);
}
