package stubborn.beaconui.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import stubborn.beaconui.ModPresentPayload;
import stubborn.beaconui.client.config.BeaconUIConfig;
import stubborn.beaconui.util.RuntimeEnvironment;

public class ModernBeaconGUIClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		BeaconUIConfig.load();

		ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
			RuntimeEnvironment.setServerInstalled(
					ClientPlayNetworking.canSend(ModPresentPayload.TYPE)
			);
		});

		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
			RuntimeEnvironment.setServerInstalled(false);
		});
	}
}