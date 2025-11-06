package com.mindlesstoys.stickia.hexways.casting.spells.edit

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.Vec3
import com.mindlesstoys.stickia.hexways.PortalHexUtils.Companion.moveOrSetPrt
import qouteall.imm_ptl.core.portal.Portal
import qouteall.imm_ptl.core.portal.PortalManipulation

class OpMoveInput : SpellAction {
    override val argc = 2
    override fun execute(
        args: List<Iota>,
        env: CastingEnvironment,
    ): SpellAction.Result {
        val prt: Entity = args.getEntity(0,argc)
        val prtPos: Vec3 = args.getVec3(1,argc)
        if (prt !is HexPortal){
            throw MishapPortalEntity.of(prt)
        }
        env.assertEntityInRange(prt)
        env.assertVecInRange(prtPos)
        val cost = (prt.position().distanceTo(prtPos)*MediaConstants.SHARD_UNIT).toLong()

        return SpellAction.Result(
            Spell(prt,prtPos),
            cost,
            listOf(ParticleSpray.burst(env.mishapSprayPos(), 1.0))
        )

    }

    data class Spell(val prt: HexPortal, val pos: Vec3) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            var portalOutOp: Portal? = null
            val portalInOp = PortalManipulation.findFlippedPortal(prt)
            val portalOut = PortalManipulation.findReversePortal(prt)
            if (portalOut !== null) {
                portalOutOp = (PortalManipulation.findFlippedPortal(portalOut))
            }
            moveOrSetPrt(prt,pos,false)
            moveOrSetPrt(portalInOp,pos,false)
            moveOrSetPrt(portalOut,pos,true)
            moveOrSetPrt(portalOutOp,pos,true)
        }
    }
}