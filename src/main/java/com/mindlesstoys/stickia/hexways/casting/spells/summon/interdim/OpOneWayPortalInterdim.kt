package com.mindlesstoys.stickia.hexways.casting.spells.summon.interdim

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapBadDim
import at.petrak.hexcasting.api.misc.MediaConstants
import at.petrak.hexcasting.api.mod.HexConfig
import com.mindlesstoys.stickia.hexways.PortalHexUtils
import com.mindlesstoys.stickia.hexways.PortalHexUtils.Companion.PortalVecRotate
import com.mindlesstoys.stickia.hexways.entites.EntityRegistry.HEXPORTAL_ENTITY_TYPE
import net.minecraft.world.phys.Vec3
import net.minecraft.world.level.Level
import net.minecraft.resources.ResourceKey
import net.beholderface.oneironaut.getDimIota
import net.beholderface.oneironaut.casting.environments.ExtradimensionalCastEnv
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import org.joml.Vector3f
import qouteall.imm_ptl.core.api.PortalAPI
import qouteall.imm_ptl.core.portal.Portal
import ram.talia.hexal.api.casting.castables.VarargSpellAction

class OpOneWayPortalInterdim : VarargSpellAction {
    
    override fun argc(stack: List<Iota>): Int {
        return if (stack[0] is DimIota) 5 else 4
    }

    override fun execute(args: List<Iota>, argc: Int, env: CastingEnvironment): SpellAction.Result {
        val prtPos: Vec3 = args.getVec3(0,argc)
        val prtPosOut: Vec3 = args.getVec3(1,argc)
        val prtRot: Vec3 = args.getVec3(2,argc)
        val prtSize: Double = args.getDoubleBetween(3,1.0/10.0,10.0,argc)
        var dest: ResourceKey<Level> = env.world.dimension()

        if (argc == 5) {
            dest = args.getDimIota(4, argc).worldKey
            if (!HexConfig.server().canTeleportInThisDimension(dest)) {
                throw MishapBadDim(dest, true)
            }
        }

        if (!HexConfig.server().canTeleportInThisDimension(env.world.dimension())) {
            throw MishapBadDim(env.world.dimension())
        }

        // $ val cost = (prtPos.distanceTo(prtPosOut)*MediaConstants.SHARD_UNIT).toLong()

        // $ https://www.desmos.com/calculator/saezix1aud
        val distance = prtPos.distanceTo(prtPosOut)
        val cost = ((32 * (Math.log(distance / 16 + 1))).toLong() + 32) * MediaConstants.DUST_UNIT

        val prtPos3f = Vector3f(prtPos.x.toFloat(), prtPos.y.toFloat(), prtPos.z.toFloat())

        env.assertVecInRange(prtPos)
        
        if (dest == env.world.dimension()) {
            env.assertVecInRange(prtPosOut)
        } else if (env is PlayerBasedCastEnv) {
            val tempEnv = ExtradimensionalCastEnv(env.caster, env, env.world.server.getLevel(dest), null)
            tempEnv.assertVecInRange(prtPosOut)
        } else {
            throw MishapBadLocation(prtPosOut, "too_far")
        }

        return SpellAction.Result(
            Spell(prtPos3f, prtPosOut, prtRot, prtSize, dest),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0),ParticleSpray.burst(prtPos, 1.0))
        )

    }

    data class Spell(val prtPos: Vector3f, val prtPosOut: Vec3, val prtRot: Vec3, val prtSize: Double, val prtDim: ResourceKey<Level>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val portalIn: Portal? = HEXPORTAL_ENTITY_TYPE.create(env.world)

            portalIn!!.originPos = Vec3(prtPos)
            portalIn.setDestinationDimension(prtDim)
            portalIn.setDestination(prtPosOut)
            portalIn.setOrientationAndSize(
                PortalVecRotate(prtRot)[0],
                PortalVecRotate(prtRot)[1],
                prtSize,
                prtSize
            )
            PortalHexUtils.MakePortalNGon(portalIn,6 ,0.0)

            // $ Really? These need two entities?? Alright..
            val portalInOp = PortalAPI.createFlippedPortal(portalIn)
            PortalHexUtils.MakePortalNGon(portalInOp,6,0.0,true)
            
            portalIn.originWorld.addFreshEntity(portalInOp)
            portalIn.originWorld.addFreshEntity(portalIn)
        }
    }
}