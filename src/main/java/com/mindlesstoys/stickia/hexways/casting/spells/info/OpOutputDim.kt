package com.mindlesstoys.stickia.hexways.casting.spells.info // $ entire package for a single pattern, of course :)

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import net.beholderface.oneironaut.casting.iotatypes.DimIota
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import qouteall.imm_ptl.core.portal.Portal
import qouteall.imm_ptl.core.portal.PortalManipulation
import com.mindlesstoys.stickia.hexways.entites.HexPortal
import com.mindlesstoys.stickia.hexways.casting.mishaps.MishapPortalEntity

class OpOutputDim : ConstMediaAction {

    override val argc: Int = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val prtEnt: Entity = args.getEntity(0,argc)

        env.assertEntityInRange(prtEnt)

        if (prtEnt !is HexPortal) {
            throw MishapPortalEntity(prtEnt)
        }

        var prt = (prtEnt as Portal)

        return listOf(DimIota(prt.destDim))
    }
}