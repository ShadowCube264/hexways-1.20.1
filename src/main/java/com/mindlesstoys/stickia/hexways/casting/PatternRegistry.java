package com.mindlesstoys.stickia.hexways.casting;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.lib.hex.HexActions;
import com.mindlesstoys.stickia.hexways.Hexways;
import com.mindlesstoys.stickia.hexways.casting.spells.edit.*;
import com.mindlesstoys.stickia.hexways.casting.spells.summon.*;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import at.petrak.hexcasting.api.casting.castables.Action;

import java.util.LinkedHashMap;
import java.util.Map;

public class PatternRegistry {
    private static final Map<ResourceLocation, ActionRegistryEntry> PATTERNS = new LinkedHashMap<>();

    //portal makers
    public static final HexPattern OP_ONE_WAY = make("awqwqwadadadaadadaqwdee",HexDir.EAST,"onewayportal",new OpOneWayPortal());
    public static final HexPattern OP_TWO_WAY = make("wdeeqawqwqwadeaqadeaedaqae",HexDir.WEST,"twowayportal",new OpTwoWayPortal());
    public static final HexPattern OP_SCRY_PORTAL = make("eedwwdwewewd", HexDir.NORTH_EAST,"summonscry", new OpScryPortal());

    //portal editors
    public static final HexPattern OP_MOVE_IN_PORTAL = make("qqawwawqwqwaewaw",HexDir.NORTH_EAST,"moveportalinput", new OpMoveInput());
    public static final HexPattern OP_MOVE_OUT_PORTAL = make("eedwwdwewewdqwdw",HexDir.NORTH_EAST,"moveportaloutput", new OpMoveOutput());
    public static final HexPattern OP_ROTATE_PORTAL = make("waqqedwewewdqwdw", HexDir.EAST, "rotateportal", new OpRotatePortal()); // $ ROTAT E
    public static final HexPattern OP_SET_PORTAL_SIDES = make("waqqqadawqadadaq", HexDir.EAST, "setportalsides", new OpSetPortalSides());

    /*TODO: MAKE THESE PATTERNS:
     *ROTATEPORTAL, Portal
     *
     *
     *
    */
    static public void init() {
        for (Map.Entry<ResourceLocation, ActionRegistryEntry> entry : PATTERNS.entrySet()) {
            Registry.register(HexActions.REGISTRY, entry.getKey(), entry.getValue());
        }
    }
    private static HexPattern make(String signature, HexDir dir, String name, Action act ) { //gotten from ComplexHex lmao
        PATTERNS.put(
                new ResourceLocation(Hexways.MOD_ID, name),
                new ActionRegistryEntry(HexPattern.fromAngles(signature, dir), act)
        );
        return HexPattern.fromAngles(signature, dir);
    }
}
