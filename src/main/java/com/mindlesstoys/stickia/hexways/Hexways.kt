package com.mindlesstoys.stickia.hexways

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import com.mindlesstoys.stickia.hexways.casting.PatternRegistry
import com.mindlesstoys.stickia.hexways.casting.PortalAmbit
import com.mindlesstoys.stickia.hexways.entites.EntityRegistry
import net.fabricmc.api.ModInitializer
import org.slf4j.LoggerFactory

object Hexways : ModInitializer {
    public final val LOGGER = LoggerFactory.getLogger("hexways")
	const val MOD_ID = "hexways"

	// $ Some of the original comments are brilliant, starting mine with a '$' so you can tell them apart :) - Shadow

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		LOGGER.info("Hex Ways teleporting into your logs")
		PatternRegistry.init()
		EntityRegistry.init()

		//custom ambit with no mixins lets go!
		CastingEnvironment.addCreateEventListener { env: CastingEnvironment ->
			env.addExtension<CastingEnvironmentComponent>(PortalAmbit(env))
		}
	}
}