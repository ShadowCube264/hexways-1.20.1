package com.mindlesstoys.stickia.hexways

import com.mindlesstoys.stickia.hexways.entites.HexPortal
import net.minecraft.world.phys.Vec3
import qouteall.imm_ptl.core.portal.GeometryPortalShape
import qouteall.imm_ptl.core.portal.Portal
import java.util.stream.Collectors
import java.util.stream.IntStream
import kotlin.math.cos
import kotlin.math.sin


class PortalHexUtils {
    companion object {
        // $ TODO: Rework for nicer portal flipping
        fun MakePortalNGon(portal: Portal, sides: Int, roll: Double, mirrored: Boolean = false) { //GOTTEN FROM IMMERSIVE PORTALS PortalCommand
            val shape = GeometryPortalShape()
            val twoPi = Math.PI * 2

            val offset: Double = if (mirrored) Math.PI else 0.0
            shape.triangles = IntStream.range(0, sides)
                .mapToObj { i: Int ->
                    GeometryPortalShape.TriangleInPlane(
                        0.0, 0.0,
                        portal.width * 0.5 * cos(offset + twoPi * ((i.toDouble()) / sides + roll)),
                        portal.height * 0.5 * sin(offset + twoPi * ((i.toDouble()) / sides + roll)),
                        portal.width * 0.5 * cos(offset + twoPi * ((i.toDouble() + 1) / sides + roll)),
                        portal.height * 0.5 * sin(offset + twoPi * ((i.toDouble() + 1) / sides + roll))
                    )
                }.collect(Collectors.toList())
            portal.specialShape = shape
        }

        fun PortalVecRotate(prtRot: Vec3): List<Vec3> {
            var PrtRotCors: Vec3 = prtRot.cross(Vec3(0.0, 1.0, 0.0))
            var PrtRotCorsCors: Vec3 = PrtRotCors.cross(prtRot)

            when (prtRot.y()) {
                1.0 -> {
                    PrtRotCors = Vec3(0.0,0.0,1.0)
                    PrtRotCorsCors = Vec3(1.0,0.0,0.0)
                }
                -1.0 -> {
                    PrtRotCors = Vec3(1.0,0.0,0.0)
                    PrtRotCorsCors = Vec3(0.0,0.0,-1.0)
                }
            }
            return listOf(PrtRotCors,PrtRotCorsCors)
        }
        // $ Double-sidedness and two-ways handled in the movement spell, rather than here
        fun moveOrSetPrt(prt: Portal?, pos: Vec3, out: Boolean){
            if (prt != null && prt is HexPortal){
                if (out){
                    prt.setDestination(pos)
                    prt.reloadAndSyncToClient()
                }else{
                    prt.moveTo(pos)
                    prt.reloadAndSyncToClient()
                }
            }
        }
    }
}