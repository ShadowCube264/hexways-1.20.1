package com.mindlesstoys.stickia.hexways.entites;

import at.petrak.hexcasting.api.misc.MediaConstants;
import com.mindlesstoys.stickia.hexways.Hexways;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import qouteall.imm_ptl.core.portal.Portal;
import qouteall.imm_ptl.core.portal.PortalManipulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HexPortal extends Portal {
    public HexPortal(EntityType<?> entityType, Level world) {
        super(entityType, world);
    }
    public boolean ambitTraversable = true;
    public boolean isMirror = true;

    //public boolean consumeMedia = true;
    protected float mediaUpkeepMultiplier = 1f;
    protected float mediaUpkeepBase = 0f;
    protected long mediaReserve = 0;
    private boolean alreadyTickedFuel = false;

    @Override
    public void tick() {
        super.tick();

        if (Objects.requireNonNull(Hexways.INSTANCE.getConfig()).enablePortalUpkeep /*&& this.consumeMedia*/) {

            if (this.level().isClientSide()){
                return;
            }

            //making sure to only substract upkeep once for the whole portal cluster
            if (this.alreadyTickedFuel) {
                //If we are here, another portal in the cluster already ticked and did the upkeep calculation
                this.alreadyTickedFuel = false;
                return;
            }
            //mark all portals in the full cluster except this one as already ticked. Don't mark this one, it's already
            //getting ticked right now and if we keep it marked it may be skipped next tick.
            getFullPortalCluster(this).forEach(p->{p.alreadyTickedFuel=true;});
            this.alreadyTickedFuel = false;
            //Hexways.INSTANCE.getLOGGER().atInfo().log("Ticking portal upkeep. current fuel is "+this.getMediaReserve());

            //temp magic number fuel consumption for testing
            // long cost = 1 * MediaConstants.DUST_UNIT;
            long cost = (long) (mediaUpkeepBase * mediaUpkeepMultiplier);

            //Hexways.INSTANCE.getLOGGER().atInfo().log("                               cost is "+cost);
            this.addMediaReserve(-cost);
            //Hexways.INSTANCE.getLOGGER().atInfo().log("                         final fuel is "+this.getMediaReserve());


            if (this.getMediaReserve()<0) {
                getFullPortalCluster(this).forEach(p->{p.remove(RemovalReason.KILLED);});
            }
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putBoolean("ambitTraversable", this.ambitTraversable);
        compoundTag.putBoolean("isMirror", this.isMirror);
        //compoundTag.putBoolean("consumeMedia", this.consumeMedia);
        compoundTag.putFloat("mediaUpkeepMultiplier", this.mediaUpkeepMultiplier);
        compoundTag.putFloat("mediaUpkeepBase", this.mediaUpkeepBase);
        compoundTag.putLong("mediaReserve",this.mediaReserve);
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
        /*if (compoundTag.contains("consumeMedia")){
            this.consumeMedia = compoundTag.getBoolean("consumeMedia");
        }*/
        if (compoundTag.contains("mediaUpkeepMultiplier")){
            this.mediaUpkeepMultiplier = compoundTag.getFloat("mediaUpkeepMultiplier");
        }
        if (compoundTag.contains("mediaUpkeepBase")){
            this.mediaUpkeepBase = compoundTag.getFloat("mediaUpkeepBase");
        }
        if (compoundTag.contains("mediaReserve")){
            this.mediaReserve = compoundTag.getLong("mediaReserve");
        }
    }

    /*
    One Hex portal Cluster can be made of two or four portals total (since Hex portals all have the flipped portal)
    Those methods allow for dealing with the whole portal cluster as having a single reserve, despite them having separate reserves in practice.
     */

    public long getMediaReserve() {
        var resultWrapper = new Object(){long result = 0;};
        getFullPortalCluster(this).forEach(p->{resultWrapper.result+=p.mediaReserve;});
        return resultWrapper.result;
    }

    public void setMediaReserve(long m) {
        final int count = getFullPortalCluster(this).size();
        getFullPortalCluster(this).forEach(p->{p.mediaReserve=m/count;});
        this.mediaReserve+=m%count;
    }

    public void setMediaUpkeepBase(float base) {
        getFullPortalCluster(this).forEach(p->{p.mediaUpkeepBase =base;});
    }

    public float getMediaUpkeepBase() {
        return this.mediaUpkeepBase;
    }

    public void setMediaUpkeepMultiplier(float mult) {
        getFullPortalCluster(this).forEach(p->{p.mediaUpkeepMultiplier =mult;});
    }

    public float getMediaUpkeepMultiplier() {
        return this.mediaUpkeepMultiplier;
    }

    public void addMediaReserve(long m) {
        this.setMediaReserve(this.getMediaReserve()+m);
    }

    //honestly surprised this isn't a Util or PortalManipulation function in IP already
    private static List<HexPortal> getFullPortalCluster(HexPortal hexPortal) {
        ArrayList<HexPortal> result = new ArrayList<>();
        result.add(hexPortal);

        PortalManipulation.getPortalCluster(
                hexPortal.level(),
                hexPortal.getOriginPos(),
                hexPortal.getNormal().scale(-1),
                (portal -> {return portal instanceof HexPortal;})
        ).forEach( p -> {result.add((HexPortal) p);});

        PortalManipulation.getPortalCluster(
                hexPortal.getDestWorld(),
                hexPortal.getDestPos(),
                hexPortal.transformLocalVecNonScale(hexPortal.getNormal().scale(-1)),
                (portal -> {return portal instanceof HexPortal;})
        ).forEach( p -> {result.add((HexPortal) p);});

        PortalManipulation.getPortalCluster(
                hexPortal.getDestWorld(),
                hexPortal.getDestPos(),
                hexPortal.transformLocalVecNonScale(hexPortal.getNormal()),
                (portal -> {return portal instanceof HexPortal;})
        ).forEach( p -> {result.add((HexPortal) p);});

        return result;
    }
}
