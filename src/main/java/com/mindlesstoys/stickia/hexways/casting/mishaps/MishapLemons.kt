package com.mindlesstoys.stickia.hexways.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.GarbageIota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.red
import at.petrak.hexcasting.interop.inline.InlinePatternData
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component.translatable
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import net.minecraft.world.level.Level
import net.minecraft.resources.ResourceKey
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import kotlin.random.Random
import com.mindlesstoys.stickia.hexways.casting.PatternRegistry

class MishapLemons(val lemon: Entity) : Mishap() {

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.YELLOW)
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = error(
        if (Random.nextBoolean()) "hexways.lemons" else "hexways.lemons2",
        InlinePatternData(PatternRegistry.OP_OUTPUT_DIM.get().prototype()).asText(true)
    )

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        spontaneouslyCombust(lemon, env.world)
        if (env.castingEntity != null) spontaneouslyCombust(env.castingEntity!!, env.world)
    }

    fun spontaneouslyCombust(e: Entity, level: ServerLevel) {
        val pos = e.position()!!
        level.sendParticles(ParticleTypes.EXPLOSION, pos.x, pos.y, pos.z, 3, 1.0, 1.0, 1.0, 2.0)
        level.sendParticles(ParticleTypes.LAVA, pos.x, pos.y, pos.z, 16, 0.5, 0.5, 0.5, 1.0)
        if (!e.fireImmune()) e.setSecondsOnFire(8)
    }
}