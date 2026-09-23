package stubborn.beaconui.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BeaconScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import stubborn.beaconui.ModernBeaconGUI;
import stubborn.beaconui.client.gui.screen.BeaconUIConfigScreen;
import stubborn.beaconui.client.util.BeaconAdjust;

import java.util.List;

import static stubborn.beaconui.ModernBeaconGUI.MOD_ID;

//参考 SmithingScreen
@Mixin(BeaconScreen.class)
public abstract class BeaconScreenMixin extends AbstractContainerScreen<BeaconMenu> {

    @Shadow
    private static final Identifier BEACON_LOCATION = ModernBeaconGUI.id("textures/gui/container/beacon.png");

    @Unique
    private final CyclingSlotBackground beaconui$paymentIcon = new CyclingSlotBackground(0);

    @Unique
    private static final Component PAYMENT_TOOLTIP = Component.translatable("container.beacon.payment_tooltip");

    // Magical numbers
    @Unique private static final int PRIMARY_X_OFFSET = 11 + BeaconAdjust.OFFSET_X;
    @Unique private static final int SECONDARY_X_OFFSET = -12 + BeaconAdjust.OFFSET_X;
    @Unique private static final int EFFECT_Y_OFFSET = 31 + BeaconAdjust.OFFSET_Y;
    @Unique private static final int CONFIRM_Y_OFFSET = 4 + BeaconAdjust.OFFSET_Y;
    @Unique private static final int LABEL_Y_OFFSET = 30 + BeaconAdjust.OFFSET_Y;

    @Unique
    private static final List<Identifier> BEACON_PAYMENT_ICONS = List.of(
            Identifier.withDefaultNamespace("container/slot/ingot"),
            Identifier.withDefaultNamespace("container/slot/diamond"),
            Identifier.withDefaultNamespace("container/slot/emerald")
    );

    public BeaconScreenMixin(BeaconMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    // Move the cancel button outside the screen (Deprecated)
    // Skip creating the cancel button (Deprecated)
    // Set visible to false
    @WrapOperation(
            method = "init",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;addBeaconButton(Lnet/minecraft/client/gui/components/AbstractWidget;)V"
            )
    )
    private void beaconui$addBeaconButton(BeaconScreen instance, AbstractWidget widget, Operation<Void> original) {
        if (widget instanceof BeaconScreen.BeaconCancelButton) {
            widget.visible = false;
        }
        original.call(instance, widget);
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void beaconui$resize(CallbackInfo ci) {
        this.imageWidth = BeaconAdjust.NEW_WIDTH;
        this.imageHeight = BeaconAdjust.NEW_HEIGHT;
        //this.imageWidth = 230;
        //this.imageHeight = 219;
    }

    // Effect Buttons
    @WrapOperation(
            method = "init",
            at = @At(
                    value = "NEW", target = "(Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;IILnet/minecraft/core/Holder;ZI)Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconPowerButton;"
            )
    )
    private BeaconScreen.BeaconPowerButton beaconui$modifyAllPowerButtons(
            BeaconScreen outer, int x, int y, Holder<MobEffect> effect, boolean isPrimary, int tier, Operation<BeaconScreen.BeaconPowerButton> original
    ) {
        // 根据构造函数的 isPrimary 参数决定 X 轴偏移，Y 轴统一加上偏移量
        int xOffset = isPrimary ? PRIMARY_X_OFFSET : SECONDARY_X_OFFSET;
        return original.call(outer, x + xOffset, y + EFFECT_Y_OFFSET, effect, isPrimary, tier);
    }

    // Upgrade button
    @WrapOperation(
            method = "init",
            at = @At(
                    value = "NEW", target = "(Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;IILnet/minecraft/core/Holder;)Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconUpgradePowerButton;"
            )
    )
    private BeaconScreen.BeaconUpgradePowerButton beaconui$adjustUpgradePowerButton(
            BeaconScreen outer, int x, int y, Holder<MobEffect> effect, Operation<BeaconScreen.BeaconUpgradePowerButton> original
    ) {
        return original.call(outer, x + SECONDARY_X_OFFSET, y + EFFECT_Y_OFFSET, effect);
    }

    // Confirm button
    @WrapOperation(
            method = "init",
            at = @At(
                    value = "NEW", target = "(Lnet/minecraft/client/gui/screens/inventory/BeaconScreen;II)Lnet/minecraft/client/gui/screens/inventory/BeaconScreen$BeaconConfirmButton;"
            )
    )
    private BeaconScreen.BeaconConfirmButton beaconui$adjustConfirmButton(
            BeaconScreen outer, int x, int y, Operation<BeaconScreen.BeaconConfirmButton> original
    ) {
        return original.call(outer, x + BeaconAdjust.OFFSET_X, y + CONFIRM_Y_OFFSET);
    }

    // Labels for primary effects
    @WrapOperation(
            method = "renderLabels",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V",
                    ordinal = 0
            )
    )
    private void beaconui$adjustPrimaryLabel(GuiGraphics instance, Font font, Component text, int x, int y, int color, Operation<Void> original) {
        original.call(instance, font, text,
                x + PRIMARY_X_OFFSET, y + LABEL_Y_OFFSET,
                color);
    }

    // Labels for secondary effects
    @WrapOperation(
            method = "renderLabels",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V",
                    ordinal = 1
            )
    )
    private void beaconui$adjustSecondaryLabel(GuiGraphics instance, Font font, Component text, int x, int y, int color, Operation<Void> original) {
        original.call(instance, font, text,
                x + SECONDARY_X_OFFSET, y + LABEL_Y_OFFSET,
                color);
    }

    // Cancel rendering item sprites
    @WrapOperation(
            method = "renderBg",
            at = @At(
                    value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/item/ItemStack;II)V"
            )
    )
    private void beaconui$removePaymentItems(GuiGraphics guiGraphics, ItemStack stack, int x, int y, Operation<Void> original) {
    }

    // Add payment cycling icons
    @Inject(method = "containerTick", at = @At("TAIL"))
    private void beaconui$containerTick(CallbackInfo ci) {
        this.beaconui$paymentIcon.tick(BEACON_PAYMENT_ICONS);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void beaconui$renderPaymentIcon(GuiGraphics guiGraphics, float f, int i, int j, CallbackInfo ci) {
        this.beaconui$paymentIcon.render(this.menu, guiGraphics, f, this.leftPos, this.topPos);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void beaconui$renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.hoveredSlot != null && this.hoveredSlot.index == 0 && this.hoveredSlot.getItem().isEmpty()) {
            guiGraphics.setTooltipForNextFrame(this.font, this.font.split(PAYMENT_TOOLTIP, 115), mouseX, mouseY);
        }
    }
}
