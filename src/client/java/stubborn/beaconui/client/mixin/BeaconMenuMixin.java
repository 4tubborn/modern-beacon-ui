package stubborn.beaconui.client.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import stubborn.beaconui.client.config.BeaconUIConfig;
import stubborn.beaconui.client.util.BeaconAdjust;
import stubborn.beaconui.util.RuntimeEnvironment;

//参考 GrindstoneMenu
//参考 EnchantmentMenu
//参考 SmithingMenu
@Mixin(BeaconMenu.class)
public abstract class BeaconMenuMixin extends AbstractContainerMenu {

    @Final @Shadow
    private static int PAYMENT_SLOT;
    @Final @Shadow
    private static int INV_SLOT_START;
    @Final @Shadow
    private static int INV_SLOT_END;
    @Final @Shadow
    private static int USE_ROW_SLOT_START;
    @Final @Shadow
    private static int USE_ROW_SLOT_END ;

    @Final @Shadow
    private BeaconMenu.PaymentSlot paymentSlot;

    @Final @Shadow
    private Container beacon;

    @Final @Shadow
    private ContainerLevelAccess access;

    protected BeaconMenuMixin(@Nullable MenuType<?> menuType, int i) {
        super(menuType, i);
    }

    // Payment slot
    @WrapOperation(
            method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At(
                    value = "NEW",
                    target = "(Lnet/minecraft/world/Container;III)Lnet/minecraft/world/inventory/BeaconMenu$PaymentSlot;"
            )
    )
    private BeaconMenu.PaymentSlot modifyPaymentSlot(
            Container container, int index, int x, int y, Operation<BeaconMenu.PaymentSlot> original
    ) {
        return original.call(container, index,
                x + BeaconAdjust.OFFSET_X, y + BeaconAdjust.OFFSET_Y + 4);
    }

    // Inventory
    @WrapOperation(
            method = "<init>(ILnet/minecraft/world/Container;Lnet/minecraft/world/inventory/ContainerData;Lnet/minecraft/world/inventory/ContainerLevelAccess;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/BeaconMenu;addStandardInventorySlots(Lnet/minecraft/world/Container;II)V"
            )
    )
    private void modifyStandardInventorySlots(
            BeaconMenu instance, Container playerInventory, int x, int y, Operation<Void> original
    ) {
        original.call(instance, playerInventory, x + BeaconAdjust.OFFSET_X , y + BeaconAdjust.OFFSET_Y);
    }

    @Inject(method = "removed", at = @At("HEAD"), cancellable = true)
    public void beaconui$removed(Player player, CallbackInfo ci) {
        super.removed(player);
        this.access.execute((level, blockPos) ->
                this.clearContainer(player, this.beacon)
        );

        ci.cancel();
    }

    @Inject(method = "quickMoveStack", at = @At("HEAD"), cancellable = true)
    private void beaconui$quickMoveStack(Player player, int i, CallbackInfoReturnable<ItemStack> cir) {
        if(!BeaconUIConfig.getInstance().patchQuickMove) return;
        if (player.level().isClientSide() && !RuntimeEnvironment.isServerInstalled()) {
            return;
        }

        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();

            if (i == PAYMENT_SLOT) {
                if (!this.moveItemStackTo(itemStack2, INV_SLOT_START, USE_ROW_SLOT_END, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }

                slot.onQuickCraft(itemStack2, itemStack);
            } else if (this.paymentSlot.mayPlace(itemStack2)) {
                if (this.paymentSlot.hasItem()) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }

                ItemStack itemStack3 = itemStack2.copyWithCount(1);
                itemStack2.shrink(1);
                this.paymentSlot.setByPlayer(itemStack3);
            } else if (i >= INV_SLOT_START && i < INV_SLOT_END) {
                if (!this.moveItemStackTo(itemStack2, USE_ROW_SLOT_START, USE_ROW_SLOT_END, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            } else if (i >= USE_ROW_SLOT_START && i < USE_ROW_SLOT_END) {
                if (!this.moveItemStackTo(itemStack2, INV_SLOT_START, INV_SLOT_END, false)) {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                cir.setReturnValue(ItemStack.EMPTY);
                return;
            }

            slot.onTake(player, itemStack2);
        }

        cir.setReturnValue(itemStack);
    }
}