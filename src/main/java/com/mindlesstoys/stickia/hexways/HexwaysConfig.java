package com.mindlesstoys.stickia.hexways;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = Hexways.MOD_ID)
public class HexwaysConfig implements ConfigData {
    boolean enableOneironautCompat = true;
    public double minPortalSize = 0.1;
    public double maxPortalSize = 10.0;
}
