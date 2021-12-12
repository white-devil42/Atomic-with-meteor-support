package me.zeroX150.authlib.login.mojang.profile;

import me.zeroX150.authlib.struct.OnlineTexture;

/**
 * The class Minecraft cape stores cape data from {@link me.zeroX150.authlib.login.mojang.profile.MinecraftProfile}
 */
public class MinecraftProfileCape extends OnlineTexture {

    /**
     * Instantiates a new Minecraft cape.
     */
    public MinecraftProfileCape() {
    }

    /**
     * Instantiates a new Minecraft cape.
     *
     * @param id    the id
     * @param state the state
     * @param url   the url
     * @param alias the alias
     */
    public MinecraftProfileCape(String id, String state, String url, String alias) {
        super(id, state, url, alias);
    }

}
