package com.mindlesstoys.stickia.hexways.casting.spells.upkeep

import at.petrak.hexcasting.api.casting.asActionResult
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.entity.Entity

class OpGetPortalFuel : ConstMediaAction {
    override val argc: Int = 1
    override val mediaCost : Long = 0

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): List<Iota> {
        val prtEnt: Entity = args.getEntity(0,argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }

        return ((prtEnt.mediaReserve.toDouble())/MediaConstants.DUST_UNIT.toDouble()).asActionResult
    }
}