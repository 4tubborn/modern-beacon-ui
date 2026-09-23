package stubborn.beaconui;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ModernBeaconGUI implements ModInitializer {
	public static final String MOD_ID = "beaconui";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		PayloadTypeRegistry.playC2S().register(
				ModPresentPayload.TYPE, ModPresentPayload.CODEC
		);

		ServerPlayNetworking.registerGlobalReceiver(
				ModPresentPayload.TYPE,
				(payload, context) -> {
					// 什么都不用做
				}
		);
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
