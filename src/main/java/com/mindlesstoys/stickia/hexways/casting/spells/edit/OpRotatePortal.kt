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
import qouteall.q_misc_util.my_util.DQuaternion
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import com.mindlesstoys.stickia.hexways.PortalHexUtils
import com.mindlesstoys.stickia.hexways.entites.HexPortal

class OpRotatePortal : SpellAction {
    /**
     * The number of arguments from the stack that this action requires.
     */
    override val argc: Int = 3
    private val cost: Long = 0

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val prtEnt: Entity = args.getEntity(0,argc)
        val prtRot: Vec3 = args.getVec3(1,argc)
        val onlyRotateInput: Boolean = args.getBool(2, argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }

        // $ This.. seems like code I shouldn't use, considering portals aren't necessarily two-way

        //Use these for rotate output

        //val maybeNull = PortalManipulation.findReversePortal(prtEnt as Portal)

        //if  (maybeNull !== null){
        //    throw MishapBadEntity(prtEnt, Component.translatable("1wayportaltranslate"))
        //}

        return SpellAction.Result(
            Spell(prtEnt, prtRot, onlyRotateInput),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0))
        )
    }

    private data class Spell(var prtEntity: Entity, var prtRot: Vec3, var onlyRotateInput: Boolean) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val prt = (prtEntity as Portal)
            var revFlipPrt: Portal? = null

            val flipPrt = PortalManipulation.findFlippedPortal(prt)
            val revPrt = PortalManipulation.findReversePortal(prt)
            if (revPrt !== null) {
               revFlipPrt = PortalManipulation.findFlippedPortal(revPrt)
            }

            prt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1])

            if (flipPrt !== null) {
                flipPrt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot.reverse())[0], PortalHexUtils.PortalVecRotate(prtRot.reverse())[1])
                flipPrt.reloadAndSyncToClient()
            }
            
            if (!onlyRotateInput) {
                prt.setRotation(null)
                prt.reloadAndSyncToClient()
                if (flipPrt !== null) {
                    flipPrt.setRotation(null)
                    flipPrt.reloadAndSyncToClient()
                }
                if (revPrt !== null) {
                    revPrt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot.reverse())[0], PortalHexUtils.PortalVecRotate(prtRot.reverse())[1])
                    revPrt.setRotation(null)
                    revPrt.reloadAndSyncToClient()
                }
                if (revFlipPrt !== null) {
                    revFlipPrt.setOrientation(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1])
                    revFlipPrt.setRotation(null)
                    revFlipPrt.reloadAndSyncToClient()
                }
            } else {
                val quat = DQuaternion.fromFacingVecs(PortalHexUtils.PortalVecRotate(prtRot)[0], PortalHexUtils.PortalVecRotate(prtRot)[1])
                val inverseQuat = DQuaternion.fromFacingVecs(PortalHexUtils.PortalVecRotate(prtRot.reverse())[0], PortalHexUtils.PortalVecRotate(prtRot.reverse())[1])
                
                if (revPrt !== null) {
                    val otherQuat = revPrt.getOrientationRotation()
                    revPrt.setOtherSideOrientation(quat)
                    revPrt.reloadAndSyncToClient()

                    prt.setOtherSideOrientation(otherQuat)
                    prt.reloadAndSyncToClient()
                }
                if (revFlipPrt !== null) {
                    val otherQuat = revFlipPrt.getOrientationRotation()
                    revFlipPrt.setOtherSideOrientation(inverseQuat)
                    revFlipPrt.reloadAndSyncToClient()

                    if (flipPrt !== null) {
                        flipPrt.setOtherSideOrientation(otherQuat)
                        flipPrt.reloadAndSyncToClient()
                    }
                }
            }
        }
    }
}