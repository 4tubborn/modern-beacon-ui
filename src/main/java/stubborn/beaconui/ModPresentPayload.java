package stubborn.beaconui;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import static stubborn.beaconui.ModernBeaconGUI.MOD_ID;

public record ModPresentPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ModPresentPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    ModernBeaconGUI.id("present")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, ModPresentPayload> CODEC =
            StreamCodec.unit(new ModPresentPayload());

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}