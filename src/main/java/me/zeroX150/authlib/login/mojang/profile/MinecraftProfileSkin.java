package me.zeroX150.authlib.login.mojang.profile;

import me.zeroX150.authlib.struct.OnlineTexture;

/**
 * The class Minecraft skin stores cape data from {@link me.zeroX150.authlib.login.mojang.profile.MinecraftProfile}
 */
public class MinecraftProfileSkin extends OnlineTexture {

    private String variant;

    /**
     * Instantiates a new Minecraft skin.
     */
    public MinecraftProfileSkin() {
    }

    /**
     * Instantiates a new Minecraft skin.
     *
     * @param variant the variant
     */
    public MinecraftProfileSkin(String variant) {
        this.variant = variant;
    }

    /**
     * Instantiates a new Minecraft skin.
     *
     * @param id      the id
     * @param state   the state
     * @param url     the url
     * @param alias   the alias
     * @param variant the variant
     */
    public MinecraftProfileSkin(String id, String state, String url, String alias, String variant) {
        super(id, state, url, alias);
        this.variant = variant;
    }

    /**
     * Gets variant.
     *
     * @return the variant
     */
    public String getVariant() {
        return variant;
    }


    @Override public String toString() {
        return "MinecraftSkin{" + "id='" + getId() + '\'' + ", state='" + getState() + '\'' + ", url='" + getUrl() + '\'' + ", alias='" + getAlias() + '\'' + "variant='" + variant + '\'' + '}';
    }
}
