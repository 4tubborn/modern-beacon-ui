package stubborn.beaconui.client.gui.screen;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import stubborn.beaconui.client.config.BeaconUIConfig;

public final class BeaconUIConfigScreen {
    private BeaconUIConfigScreen() {}

    public static Screen create(Screen parent) {
        BeaconUIConfig config = BeaconUIConfig.getInstance();

        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent)
                .setTitle(Component.translatable("options.beaconui.title"));

        ConfigEntryBuilder entries = builder.entryBuilder();

        // General Cate
        ConfigCategory general = builder.getOrCreateCategory(
                Component.translatable("options.beaconui.category.general")
        );

        general.addEntry(
                entries.startBooleanToggle(
                                Component.translatable("options.beaconui.patch_quick_move"),
                                config.patchQuickMove
                        )
                        .setDefaultValue(BeaconUIConfig.DEFAULT_PATCH_QUICK_MOVE)
                        .setSaveConsumer(value -> config.patchQuickMove = value)
                        .build()
        );

        builder.setSavingRunnable(config::save);

        return builder.build();
    }
}