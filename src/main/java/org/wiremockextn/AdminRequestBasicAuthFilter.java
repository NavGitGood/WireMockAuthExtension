package org.wiremockextn;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.jknack.handlebars.internal.lang3.StringUtils;
import com.github.tomakehurst.wiremock.extension.requestfilter.AdminRequestFilterV2;
import com.github.tomakehurst.wiremock.extension.requestfilter.RequestFilterAction;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;

/**
 * Checks every admin API request for HTTP basic authentication credentials.
 * Authorized credentials can be configured via environment variables "admin-users.conf" in
 * the current working directory.
 */

public class AdminRequestBasicAuthFilter implements AdminRequestFilterV2 {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BASIC_AUTH_PREFIX = "basic ";
    private final String authorizedCredentials;

    Logger logger = Logger.getLogger(this.getClass().getName());

    public AdminRequestBasicAuthFilter() throws IOException {
        final String authUser = System.getenv("AUTH_EXTENSION_USER");
        final String authPass = System.getenv("AUTH_EXTENSION_PASS");
        final String exceptionStr = "environment variable %s is not set";

        Base64.Encoder base64Encoder = Base64.getEncoder();

        if(!StringUtils.isNotEmpty(authUser)) {
            logger.log(Level.SEVERE, String.format(exceptionStr, "AUTH_EXTENSION_USER"));
            throw new IllegalStateException(String.format(exceptionStr, "AUTH_EXTENSION_USER"));
        }
        if(!StringUtils.isNotEmpty(authPass)) {
            logger.log(Level.SEVERE, String.format(exceptionStr, "AUTH_EXTENSION_PASS"));
            throw new IllegalStateException(String.format(exceptionStr, "AUTH_EXTENSION_PASS"));
        }
        authorizedCredentials = base64Encoder.encodeToString(String.format("%s:%s", authUser, authPass).getBytes(StandardCharsets.UTF_8));

    }

    @Override
    public RequestFilterAction filter(Request request, ServeEvent serveEvent) {
        if (!request.containsHeader(AUTHORIZATION_HEADER)) {
            return RequestFilterAction.stopWith(ResponseDefinition.notAuthorised());
        }

        String authorization = request.header(AUTHORIZATION_HEADER).firstValue();
        if (!authorization.toLowerCase().startsWith(BASIC_AUTH_PREFIX)) {
            return RequestFilterAction.stopWith(ResponseDefinition.notAuthorised());
        }

        String credentials = authorization.substring(BASIC_AUTH_PREFIX.length()).trim();
        if (!authorizedCredentials.contains(credentials)) {
            return RequestFilterAction.stopWith(ResponseDefinition.notAuthorised());
        }

        return RequestFilterAction.continueWith(request);
    }

    @Override
    public String getName() {
        return "admin-basic-auth";
    }

}
