package com.mindlesstoys.stickia.hexways.casting;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import com.mindlesstoys.stickia.hexways.Hexways;
import com.mindlesstoys.stickia.hexways.casting.spells.edit.*;
import com.mindlesstoys.stickia.hexways.casting.spells.summon.*;
import com.mindlesstoys.stickia.hexways.casting.spells.info.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import java.util.LinkedHashMap;
import java.util.Map;

public class PatternRegistry {
    private static final Map<ResourceLocation, ActionRegistryEntry> PATTERNS = new LinkedHashMap<>();

    //portal makers
    public static final ActionRegistryEntry OP_ONE_WAY = make("onewayportal", new ActionRegistryEntry(HexPattern.fromAngles("awqwqwadadadaadadaqwdee", HexDir.EAST), new OpOneWayPortal()));
    public static final ActionRegistryEntry OP_TWO_WAY = make("twowayportal", new ActionRegistryEntry(HexPattern.fromAngles("wdeeqawqwqwadeaqadeaedaqae", HexDir.WEST), new OpTwoWayPortal()));
    public static final ActionRegistryEntry OP_SCRY_PORTAL = make("summonscry", new ActionRegistryEntry(HexPattern.fromAngles("eedwwdwewewd", HexDir.NORTH_EAST), new OpScryPortal()));

    //portal editors
    public static final ActionRegistryEntry OP_MOVE_IN_PORTAL = make("moveportalinput", new ActionRegistryEntry(HexPattern.fromAngles("qqawwawqwqwaewaw", HexDir.NORTH_EAST), new OpMoveInput()));
    public static final ActionRegistryEntry OP_MOVE_OUT_PORTAL = make("moveportaloutput", new ActionRegistryEntry(HexPattern.fromAngles("eedwwdwewewdqwdw", HexDir.NORTH_EAST), new OpMoveOutput()));
    public static final ActionRegistryEntry OP_ROTATE_PORTAL = make("rotateportal", new ActionRegistryEntry(HexPattern.fromAngles("waqqedwewewdqwdw", HexDir.EAST), new OpRotatePortal())); // $ ROTAT E
    public static final ActionRegistryEntry OP_SET_PORTAL_SIDES = make("setportalsides", new ActionRegistryEntry(HexPattern.fromAngles("waqqqadawqadadaq", HexDir.EAST), new OpSetPortalSides()));
    public static final ActionRegistryEntry OP_REMOVE_PORTAL = make("removeportal", new ActionRegistryEntry(HexPattern.fromAngles("wdeeqawqwqwaedaqwqad", HexDir.WEST), new OpRemovePortal()));
    public static final ActionRegistryEntry OP_RESIZE_PORTAL = make("resizeportal", new ActionRegistryEntry(HexPattern.fromAngles("weaqaweewwawqwaw", HexDir.WEST), new OpResizePortal()));
    public static final ActionRegistryEntry OP_OUTPUT_INFO = make("getoutputinfo", new ActionRegistryEntry(HexPattern.fromAngles("waqqedwewewdawdwwwdw", HexDir.EAST), new OpOutputInfo()));
    
    static public void init() {
        for (Map.Entry<ResourceLocation, ActionRegistryEntry> entry : PATTERNS.entrySet()) {
            Registry.register(HexActions.REGISTRY, entry.getKey(), entry.getValue());
        }
    }
    private static ActionRegistryEntry make(String name, ActionRegistryEntry entry) {
        PATTERNS.put(
                new ResourceLocation(Hexways.MOD_ID, name),
                entry
        );
        return entry;
    }
}
