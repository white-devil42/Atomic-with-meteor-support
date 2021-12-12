package me.zeroX150.atomic.mixin.game;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.nbt.NbtList$1") public class NbtListTypeMixin {
    @ModifyArg(method = "read(Ljava/io/DataInput;ILnet/minecraft/nbt/NbtTagSizeTracker;)Lnet/minecraft/nbt/NbtList;",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NbtType;read(Ljava/io/DataInput;ILnet/minecraft/nbt/NbtTagSizeTracker;)Lnet/minecraft/nbt/NbtElement;"))
    private int atomic_modifyDepthParameter(int depth) {
        return 0;
    }
}
