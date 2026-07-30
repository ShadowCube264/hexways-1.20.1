package com.mindlesstoys.stickia.hexways

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent
import com.mindlesstoys.stickia.hexways.casting.PatternRegistry
import com.mindlesstoys.stickia.hexways.casting.PortalAmbit
import com.mindlesstoys.stickia.hexways.entites.EntityRegistry
import com.mindlesstoys.stickia.hexways.HexwaysConfig
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.LoggerFactory
import me.shedaniel.autoconfig.AutoConfig
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer
import vazkii.patchouli.api.PatchouliAPI

object Hexways : ModInitializer {
    public final val LOGGER = LoggerFactory.getLogger("hexways")
	const val MOD_ID = "hexways"
	private var oneironautLoaded = false
	private var config: HexwaysConfig? = null

	// $ Some of the original comments are brilliant, starting mine with a '$' so you can tell them apart :) - Shadow

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		oneironautLoaded = FabricLoader.getInstance().isModLoaded("oneironaut")

		LOGGER.info("Hex Ways teleporting into your logs")
		config = AutoConfig.register(HexwaysConfig::class.java, ::GsonConfigSerializer).get()
		PatternRegistry.init()
		EntityRegistry.init()
		PatchouliAPI.get().setConfigFlag("hexways:oneironaut_loaded", isOneironautLoaded())

		//custom ambit with no mixins lets go!
		CastingEnvironment.addCreateEventListener { env: CastingEnvironment ->
			env.addExtension<CastingEnvironmentComponent>(PortalAmbit(env))
		}
	}

	@JvmStatic
	fun isOneironautLoaded(): Boolean {
		return oneironautLoaded && config!!.enableOneironautCompat;
	}
}