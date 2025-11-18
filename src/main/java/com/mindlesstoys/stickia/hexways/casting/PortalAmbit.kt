package com.mindlesstoys.stickia.hexways.casting

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent.IsVecInRange
import com.mindlesstoys.stickia.hexways.entites.EntityRegistry.HEXPORTAL_ENTITY_TYPE
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.random.Random

class Key(val id: Int) : CastingEnvironmentComponent.Key<PortalAmbit>

class PortalAmbit(private val env: CastingEnvironment) : IsVecInRange {
    private val ambit = 32.0
    //before we start the code. HOLY SHIT LETS GOOO
    //Thank you Hex Devs for being awesome
    //custom ambit with no mixins is amazing
    private val id = Keygen.randid()
    private val key = Key(id)
    override fun getKey(): CastingEnvironmentComponent.Key<*> = key
    override fun onIsVecInRange(pos: Vec3?, inAmbit: Boolean): Boolean {
        var inAmbitMaybe = inAmbit
        if (!inAmbitMaybe) {
            if (env.castingEntity != null && pos != null) {
                val eyePos = env.castingEntity!!.eyePosition
                val aabb = AABB(eyePos.add(ambit,ambit,ambit),eyePos.add(-ambit,-ambit,-ambit))
                val portals = env.world.getEntities(HEXPORTAL_ENTITY_TYPE,aabb) {true}
                for (e: HexPortal in portals) {
                    val ambitLeft = (ambit - e.eyePosition.distanceTo(eyePos))/2
                    if (e.destPos.distanceTo(pos) <= ambitLeft
                        && e.ambitTraversable) { //massive bug in 1.19 HexWays being patched here...
                        println(e.destPos.distanceTo(pos))
                        inAmbitMaybe = true
                        break
                    }
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