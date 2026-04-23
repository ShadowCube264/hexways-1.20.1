package com.mindlesstoys.stickia.hexways.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.red
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component.translatable
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.resources.ResourceKey
import kotlin.random.Random

class MishapBadDim(val got: ResourceKey<Level>, val isOtherDim: Boolean = false) : Mishap() {
    val TPDist = 5.0

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.ORANGE) //*fancy*
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = error(
        "hexways_bad_dim",
        got.location().toString().red
    )

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        if (env.castingEntity != null){
            env.castingEntity!!.teleportRelative(Random(7895446).nextDouble()*TPDist,0.0,Random(4275214).nextDouble()*TPDist)
        }
        if (isOtherDim) {
            stack[stack.size - 1] = GarbageIota()
        }
    }

    companion object {
        @JvmStatic
        fun of(errDim: ResourceKey<Level>): MishapBadDim {
            return MishapBadDim(errDim)
        }
    }
}