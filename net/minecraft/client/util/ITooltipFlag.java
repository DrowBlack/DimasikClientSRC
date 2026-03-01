package net.minecraft.client.util;

public interface ITooltipFlag {
    public boolean isAdvanced();

    public static enum TooltipFlags implements ITooltipFlag
    {
        NORMAL(false),
        ADVANCED(true);

        private final boolean isAdvanced;

        private TooltipFlags(boolean advanced) {
            this.isAdvanced = advanced;
        }

        @Override
        public boolean isAdvanced() {
            return this.isAdvanced;
        }
    }
}
