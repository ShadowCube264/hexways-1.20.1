package com.mindlesstoys.stickia.hexways.casting.spells.edit

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.iota.Iota
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import qouteall.imm_ptl.core.portal.Portal
import qouteall.imm_ptl.core.portal.PortalManipulation
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import com.mindlesstoys.stickia.hexways.PortalHexUtils

class OpRotatePortal : SpellAction {
    /**
     * The number of arguments from the stack that this action requires.
     */
    override val argc: Int = 2
    private val cost: Long = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val prtEnt: Entity = args.getEntity(0,argc)
        val prtRot: Vec3 = args.getVec3(1,argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt.type !== Portal.entityType) {
            throw MishapPortalEntity(prtEnt)
        }

        // $ This.. seems like code I shouldn't use, considering portals aren't necessarily two-way

        //Use these for rotate output

        //val maybeNull = PortalManipulation.findReversePortal(prtEnt as Portal)

        //if  (maybeNull !== null){
        //    throw MishapBadEntity(prtEnt, Component.translatable("1wayportaltranslate"))
        //}

        return SpellAction.Result(
            Spell(prtEnt, prtRot),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0))
        )
    }

    private data class Spell(var prtEntity: Entity, var prtRot: Vec3) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val prt = (prtEntity as Portal)
            var revFlipPrt: Portal? = null

            val flipPrt = PortalManipulation.findFlippedPortal(prt)
            val revPrt = PortalManipulation.findReversePortal(prt)
            if (revPrt !== null) {
               revFlipPrt = PortalManipulation.findFlippedPortal(revPrt)
            }

            prt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1])
            prt.reloadAndSyncToClient()

            if (flipPrt !== null) {
                flipPrt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1].multiply(Vec3(-1.0,-1.0,-1.0)))
                flipPrt.reloadAndSyncToClient()
            }
            if (revPrt !== null) {
                revPrt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1])
                revPrt.reloadAndSyncToClient()
            }
            if (revFlipPrt !== null) {
                revFlipPrt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1].multiply(Vec3(-1.0,-1.0,-1.0)))
                revFlipPrt.reloadAndSyncToClient()
            }
        }
    }
}