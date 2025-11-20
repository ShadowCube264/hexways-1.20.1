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

class OpResizePortal : SpellAction {
    /**
     * The number of arguments from the stack that this action requires.
     */
    override val argc: Int = 3

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val prtEnt: Entity = args.getEntity(0,argc)
        val prtHeight: Double = args.getDoubleBetween(1,1.0/10.0,10.0,argc) // $ Sorry, I could not leave this as "Hight" lol
        val prtWidth: Double = args.getDoubleBetween(2,1.0/10.0,10.0,argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }

        val cost = ((prtWidth + prtHeight)/2).toInt() * 2 * MediaConstants.SHARD_UNIT
        //16 * MediaConstants.SHARD_UNIT
        return SpellAction.Result(
            Spell(prtEnt,prtHeight,prtWidth),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0))
        )
    }

    private data class Spell(var prtEntity: Entity, var prtHeight: Double, var prtWidth: Double) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val prt = (prtEntity as Portal)
            var revFlipPrt: Portal? = null
            //val prtKeepCasts = (prt as HexPortalEntity)
            //val prtSides = prtKeepCasts.portalSides
            //val prtRoll = prtKeepCasts.portalRoll

            val flipPrt = PortalManipulation.findFlippedPortal(prt)
            val revPrt = PortalManipulation.findReversePortal(prt)
            if (revPrt !== null) {
               revFlipPrt = PortalManipulation.findFlippedPortal(revPrt)
            }

            prt.width = prtWidth
            prt.height = prtHeight
            PortalHexUtils.MakePortalNGon(prt, 6, 0.0)
            prt.reloadAndSyncToClient()

            if (flipPrt != null) {
                flipPrt.width = prtWidth
                flipPrt.height = prtHeight
                PortalHexUtils.MakePortalNGon(flipPrt, 6, 0.0, true)
                flipPrt.reloadAndSyncToClient()
            }

            if (revPrt != null) {
                revPrt.width = prtWidth
                revPrt.height = prtHeight
                PortalHexUtils.MakePortalNGon(revPrt, 6, 0.0, true)
                revPrt.reloadAndSyncToClient()
            }
            if (revFlipPrt != null && revFlipPrt != prt) {
                revFlipPrt.width = prtWidth
                revFlipPrt.height = prtHeight
                PortalHexUtils.MakePortalNGon(revFlipPrt, 6, 0.0)
                revFlipPrt.reloadAndSyncToClient()
            }
        }
    }
}