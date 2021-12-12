/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.game;

import me.zeroX150.atomic.Atomic;
import me.zeroX150.atomic.helper.manager.CapeManager;
import net.minecraft.resource.NamespaceResourceManager;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceImpl;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(NamespaceResourceManager.class) public class NamespaceResourceManagerMixin {

    @Inject(method = "getResource", cancellable = true, at = @At("HEAD")) public void atomic_overwriteCapesResource(Identifier id, CallbackInfoReturnable<Resource> cir) {
        if (id.getNamespace().equalsIgnoreCase("atomic") && id.getPath().startsWith("capes/")) {
            String ownerUUID = id.getPath().replace("capes/", "");
            try {
                UUID f = UUID.fromString(ownerUUID);
                CapeManager.CapeEntry cape = CapeManager.capes.stream().filter(capeEntry -> capeEntry.owner().equals(f)).findFirst().orElse(null);
                if (cape == null) {
                    cir.setReturnValue(null);
                    return;
                }
                if (!cape.downloaded().get()) {
                    CapeManager.download(cape);
                }
                cir.setReturnValue(new ResourceImpl("atomicCapes", id, cape.location().toURI().toURL().openStream(), null));
            } catch (Exception e) {
                Atomic.log(Level.ERROR, "Failed to display cape for " + ownerUUID);
                e.printStackTrace();
                cir.setReturnValue(null);
            }
        }
    }
}
