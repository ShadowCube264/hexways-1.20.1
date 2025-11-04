package com.mindlesstoys.stickia.hexways.entites;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.portal.Portal;

public class HexPortal extends Portal {
    public HexPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }
    public boolean ambitTraversable = true;
    public boolean isMirror = true;

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("ambitTraversable", this.ambitTraversable);
        compoundTag.putBoolean("isMirror", this.isMirror);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("ambitTraversable")) {
            this.ambitTraversable = compoundTag.getBoolean("ambitTraversable");
        }
        if (compoundTag.contains("isMirror")){
            this.isMirror = compoundTag.getBoolean("isMirror");
        }
    }
}
