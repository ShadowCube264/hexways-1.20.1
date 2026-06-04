package com.mindlesstoys.stickia.hexways.casting.mishaps

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.Mishap
import at.petrak.hexcasting.api.pigment.FrozenPigment
import at.petrak.hexcasting.api.utils.aqua
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component.translatable
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.DyeColor
import kotlin.random.Random

class MishapPortalEntity(val got: Entity) : Mishap() {
    val TPDist = 5.0

    override fun accentColor(ctx: CastingEnvironment, errorCtx: Context): FrozenPigment = dyeColor(DyeColor.CYAN) //*fancy*
    override fun errorMessage(ctx: CastingEnvironment, errorCtx: Context) = error(
        "bad_entity",
        translatable("entity.hexways.hexportal").withStyle(ChatFormatting.DARK_AQUA),
        got.displayName.plainCopy().aqua
    )

    override fun execute(env: CastingEnvironment, errorCtx: Context, stack: MutableList<Iota>) {
        if (env.castingEntity != null){
            env.castingEntity!!.teleportRelative(Random(7895446).nextDouble()*TPDist,0.0,Random(4275214).nextDouble()*TPDist)
            env.mishapEnvironment.blind(100)
        }
    }
}