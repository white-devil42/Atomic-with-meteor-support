package me.zeroX150.authlib.login.microsoft;

import me.zeroX150.authlib.struct.AuthToken;

/**
 * The class Xbox live token stores token and uhs
 */
public class XboxLiveToken extends AuthToken {

    protected String token;
    protected String uhs;

    /**
     * Instantiates a new Xbox live token.
     */
    public XboxLiveToken() {
    }

    /**
     * Instantiates a new Xbox live token.
     *
     * @param token the token
     * @param uhs   the uhs
     */
    public XboxLiveToken(String token, String uhs) {
        this.token = token;
        this.uhs = uhs;
    }

    /**
     * Gets token.
     *
     * @return the token
     */
    public String getToken() {
        return token;
    }

    /**
     * Gets uhs.
     *
     * @return the uhs
     */
    public String getUhs() {
        return uhs;
    }

}
