package dimasik.managers.mods.cape.wavecapes.config;

import dimasik.managers.mods.cape.wavecapes.CapeMovement;
import dimasik.managers.mods.cape.wavecapes.CapeStyle;
import dimasik.managers.mods.cape.wavecapes.WindMode;

public class Config {
    public WindMode windMode = WindMode.WAVES;
    public CapeStyle capeStyle = CapeStyle.SMOOTH;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
    public int gravity = 25;
    public int heightMultiplier = 5;
    public int strafeMultiplier = 3;
}
