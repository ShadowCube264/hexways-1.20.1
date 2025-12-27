package com.mindlesstoys.stickia.hexways.casting.spells.summon

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mindlesstoys.stickia.hexways.PortalHexUtils
import com.mindlesstoys.stickia.hexways.PortalHexUtils.Companion.PortalVecRotate
import com.mindlesstoys.stickia.hexways.entites.EntityRegistry.HEXPORTAL_ENTITY_TYPE
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import qouteall.imm_ptl.core.api.PortalAPI
import qouteall.imm_ptl.core.portal.Portal

class OpScryPortal : SpellAction {
    override val argc = 4

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val prtPos: Vec3 = args.getVec3(0,argc)
        val prtPosOut: Vec3 = args.getVec3(1,argc)
        val prtRot: Vec3 = args.getVec3(2,argc)
        val prtSize: Double = args.getDoubleBetween(3,1.0/10.0,10.0,argc)

        // $ val cost = (prtSize*prtPos.distanceTo(prtPosOut)*MediaConstants.DUST_UNIT/2).toLong()

        // $ https://www.desmos.com/calculator/saezix1aud
        val distance = prtPos.distanceTo(prtPosOut)
        val cost = (8 * MediaConstants.DUST_UNIT * (Math.log(distance / 8 + 1))).toLong() + 16

        val prtPos3f = Vector3f(prtPos.x.toFloat(), prtPos.y.toFloat(), prtPos.z.toFloat())

        env.assertVecInRange(prtPos)
        env.assertVecInRange(prtPosOut)

        return SpellAction.Result(
            Spell(prtPos3f,prtPosOut,prtRot, prtSize),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0), ParticleSpray.burst(prtPos, 1.0))
        )

    }

    data class Spell(val prtPos: Vector3f, val prtPosOut: Vec3, val prtRot: Vec3, val prtSize: Double) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val portalIn: Portal? = HEXPORTAL_ENTITY_TYPE.create(env.world)

            portalIn!!.originPos = Vec3(prtPos)
            portalIn.setDestinationDimension(env.world.dimension())
            portalIn.setDestination(prtPosOut)
            portalIn.setOrientationAndSize(
                PortalVecRotate(prtRot)[0],
                PortalVecRotate(prtRot)[1],
                prtSize,
                prtSize
            )
            PortalHexUtils.MakePortalNGon(portalIn,6 ,0.0)

            val portalInOp = PortalAPI.createFlippedPortal(portalIn)
            portalIn as HexPortal
            portalInOp as HexPortal

            portalIn.ambitTraversable = false
            portalInOp.ambitTraversable = false
            portalIn.teleportable = false
            portalInOp.teleportable = false
            portalIn.isInteractable = false
            portalInOp.isInteractable = false

            portalInOp.setOrientation(PortalVecRotate(prtRot)[0], PortalVecRotate(prtRot)[1].multiply(Vec3(-1.0,-1.0,-1.0)))

            portalIn.originWorld.addFreshEntity(portalIn)
            portalIn.originWorld.addFreshEntity(portalInOp)
        }
    }
}