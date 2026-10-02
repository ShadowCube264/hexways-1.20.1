package com.mindlesstoys.stickia.hexways.casting.spells.upkeep

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getEntity
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.entity.Entity
import ram.talia.hexal.api.getStrictlyPositiveLong

class OpAddFuelPortal : SpellAction {

    override val argc: Int = 2

    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment
    ): SpellAction.Result {
        val prtEnt: Entity = args.getEntity(0,argc)
        val addedFuel: Long = args.getStrictlyPositiveLong(1,argc)

        env.assertEntityInRange(prtEnt)
        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }

        return SpellAction.Result(Spell(prtEnt as HexPortal,addedFuel),addedFuel* MediaConstants.DUST_UNIT,listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0)))

    }

    data class Spell(val prt: HexPortal,val addedFuel: Long): RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            prt.addMediaReserve(addedFuel* MediaConstants.DUST_UNIT)
        }
    }
}