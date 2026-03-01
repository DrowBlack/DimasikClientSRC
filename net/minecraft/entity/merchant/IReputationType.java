package net.minecraft.entity.merchant;

public interface IReputationType {
    public static final IReputationType ZOMBIE_VILLAGER_CURED = IReputationType.register("zombie_villager_cured");
    public static final IReputationType GOLEM_KILLED = IReputationType.register("golem_killed");
    public static final IReputationType VILLAGER_HURT = IReputationType.register("villager_hurt");
    public static final IReputationType VILLAGER_KILLED = IReputationType.register("villager_killed");
    public static final IReputationType TRADE = IReputationType.register("trade");

    public static IReputationType register(final String key) {
        return new IReputationType(){

            public String toString() {
                return key;
            }
        };
    }
}
