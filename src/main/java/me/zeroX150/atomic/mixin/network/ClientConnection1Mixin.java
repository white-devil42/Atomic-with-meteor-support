/*
 * This file is part of the atomic client distribution.
 * Copyright (c) 2021-2021 0x150.
 */

package me.zeroX150.atomic.mixin.network;

import io.netty.channel.Channel;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import me.zeroX150.atomic.feature.gui.screen.ProxyManagerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.InetSocketAddress;

// dont ask me why but it works somehow
@Mixin(targets = "net/minecraft/network/ClientConnection$1") public class ClientConnection1Mixin {

    @Inject(method = "initChannel(Lio/netty/channel/Channel;)V", at = @At("HEAD")) public void atomic_applyProxy(Channel channel, CallbackInfo cir) {
        ProxyManagerScreen.Proxy currentProxy = ProxyManagerScreen.currentProxy;
        if (currentProxy != null) {
            if (currentProxy.type == ProxyManagerScreen.Proxy.ProxyType.SOCKS4) {
                channel.pipeline().addFirst(new Socks4ProxyHandler(new InetSocketAddress(currentProxy.getIp(), currentProxy.getPort()), currentProxy.username));
            } else {
                channel.pipeline().addFirst(new Socks5ProxyHandler(new InetSocketAddress(currentProxy.getIp(), currentProxy.getPort()), currentProxy.username, currentProxy.password));
            }
        }
    }
}
