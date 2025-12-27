package com.mindlesstoys.stickia.hexways.casting.spells.edit

import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import com.mindlesstoys.stickia.hexways.PortalHexUtils
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import qouteall.imm_ptl.core.portal.Portal
import qouteall.imm_ptl.core.portal.PortalManipulation
import kotlin.math.roundToInt
import org.apache.commons.codec.binary.Hex

class OpSetPortalSides : SpellAction {
    /**
     * The number of arguments from the stack that this action requires.
     */
    override val argc: Int = 3
    private val cost: Long = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val prtEnt: Entity = args.getEntity(0,argc)
        val prtSides: Int = args.getIntBetween(1,3,16,argc)
        val prtRoll: Double = args.getDoubleBetween(2,0.0,1.0,argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }

        return SpellAction.Result(
            Spell(prtEnt, prtSides, prtRoll),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0))
        )
    }

    private data class Spell(var prtEntity: Entity, var prtSides: Int, var prtRoll: Double) : RenderedSpell {

        override fun cast(env: CastingEnvironment) {
            val prt = (prtEntity as Portal)
            var revFlipPrt: Portal? = null
            //val prtKeepCasts = (prt as HexPortalEntity)
            //prtKeepCasts.portalSides = prtSides
            //prtKeepCasts.portalRoll = prtRoll

            val flipPrt = PortalManipulation.findFlippedPortal(prt)
            val revPrt = PortalManipulation.findReversePortal(prt)

            if (revPrt !== null) {
               revFlipPrt = PortalManipulation.findFlippedPortal(revPrt)
            }

            PortalHexUtils.MakePortalNGon(prt, prtSides, prtRoll)
            prt.reloadAndSyncToClient()

            if (flipPrt != null) {
                PortalHexUtils.MakePortalNGon(flipPrt, prtSides, prtRoll, true)
                flipPrt.reloadAndSyncToClient()
            }

            if (revPrt != null) {
                PortalHexUtils.MakePortalNGon(revPrt, prtSides, prtRoll, true)
                revPrt.reloadAndSyncToClient()
            }
            if (revFlipPrt != null) {
                PortalHexUtils.MakePortalNGon(revFlipPrt, prtSides, prtRoll)
                revFlipPrt.reloadAndSyncToClient()
            }
        }
    }
}