package com.mindlesstoys.stickia.hexways.casting.spells.edit

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import qouteall.imm_ptl.core.portal.Portal
import qouteall.imm_ptl.core.portal.PortalManipulation
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import com.mindlesstoys.stickia.hexways.entites.HexPortal

class OpRemovePortal : SpellAction {
    /**
     * The number of arguments from the stack that this action requires.
     */
    override val argc: Int = 1
    val cost: Long = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val prtEnt: Entity = args.getEntity(0,argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }


        return SpellAction.Result (
            Spell(prtEnt),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0))
        )
    }

    private data class Spell(var prtEntity: Entity) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val prt = (prtEntity as Portal)
            prt.run {
                PortalManipulation.removeConnectedPortals(prt, {});
                prt.remove(Entity.RemovalReason.KILLED)
            }
        }
    }
}