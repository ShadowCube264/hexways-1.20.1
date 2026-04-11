package com.mindlesstoys.stickia.hexways.casting

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent.IsVecInRange
import at.petrak.hexcasting.common.lib.HexAttributes
import com.mindlesstoys.stickia.hexways.entites.EntityRegistry.HEXPORTAL_ENTITY_TYPE
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

class Key(val id: Int) : CastingEnvironmentComponent.Key<PortalAmbit>

class PortalAmbit(private val env: CastingEnvironment) : IsVecInRange {
   
    //before we start the code. HOLY SHIT LETS GOOO
    //Thank you Hex Devs for being awesome
    //custom ambit with no mixins is amazing

    private val id = Keygen.randid()
    private val key = Key(id)

    override fun getKey(): CastingEnvironmentComponent.Key<*> = key

    override fun onIsVecInRange(pos: Vec3?, inAmbit: Boolean): Boolean {

        val ambit = env.castingEntity!!.getAttributeValue(HexAttributes.AMBIT_RADIUS)
        var inAmbitMaybe = inAmbit

        if (!inAmbitMaybe && env.castingEntity != null && pos != null) {
            val eyePos = env.castingEntity!!.eyePosition
            val aabb = AABB(eyePos.add(ambit, ambit, ambit), eyePos.add(-ambit, -ambit, -ambit))
            val portals = env.castingEntity!!.level().getEntities(HEXPORTAL_ENTITY_TYPE, aabb) {true}

            for (e: HexPortal in portals) {
                val ambitLeft = (ambit - e.eyePosition.distanceTo(eyePos)) / 2

                //massive bug in 1.19 HexWays being patched here...
                if (e.ambitTraversable && e.destDim == env.world.dimension() && e.destPos.distanceTo(pos) <= ambitLeft) {
                    // $ println(e.destPos.distanceTo(pos))
                    return true
                }
            }
        }
        return inAmbitMaybe
    }

    private object Keygen { //code from HexSky
        val rand = Random(2819038167)
        fun randid() = rand.nextInt()
    }
}