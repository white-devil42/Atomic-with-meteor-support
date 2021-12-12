package me.zeroX150.authlib.struct;

/**
 * Stores data for an online texture
 */
public abstract class OnlineTexture {

    private String id;
    private String state;
    private String url;
    private String alias;

    /**
     * Instantiates a new Texture variable.
     */
    public OnlineTexture() {
    }

    /**
     * Instantiates a new Texture variable.
     *
     * @param id    the id
     * @param state the state
     * @param url   the url
     * @param alias the alias
     */
    public OnlineTexture(String id, String state, String url, String alias) {
        this.id = id;
        this.state = state;
        this.url = url;
        this.alias = alias;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Gets state.
     *
     * @return the state
     */
    public String getState() {
        return state;
    }

    /**
     * Gets url.
     *
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * Gets alias.
     *
     * @return the alias
     */
    public String getAlias() {
        return alias;
    }

    @Override public String toString() {
        return "TextureVariable{" + "id='" + id + '\'' + ", state='" + state + '\'' + ", url='" + url + '\'' + ", alias='" + alias + '\'' + '}';
    }
}
