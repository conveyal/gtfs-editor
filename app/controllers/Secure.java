package controllers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.ObjectMapper;
import datastore.VersionedDataStore;
import datastore.GlobalTx;
import models.OAuthToken;
import models.transit.Agency;
import play.Logger;
import play.Play;
import play.mvc.*;
import play.mvc.Http.Request;
import play.data.validation.*;
import play.libs.*;
import play.utils.*;

import com.auth0.jwt.JWTVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import play.mvc.Controller;
import play.mvc.Http.Request;
import utils.Auth0UserProfile;

import javax.net.ssl.HttpsURLConnection;

public class Secure extends Controller {

    @Before(unless={"login", "authenticate", "logout", "get_token"})
    static void checkAccess() throws Throwable {
        // Authent, or OAuth
        // don't persist an OAuth key if the user is authenticated
        if (request.params.get("oauth_token") != null && !session.contains("username"))
            // persist the token
            session.put("oauth_token", request.params.get("oauth_token"));
        if(!session.contains("username") && !Application.checkOAuth(request, session)) {
            flash.put("url", "GET".equals(request.method) ? request.url : Play.ctxPath + "/"); // seems a good default
            login();
        }
        // Checks
        Check check = getActionAnnotation(Check.class);
        if(check != null) {
            check(check);
        }
        check = getControllerInheritedAnnotation(Check.class);
        if(check != null) {
            check(check);
        }
    }

    private static void check(Check check) throws Throwable {
        for(String profile : check.value()) {
            boolean hasProfile = (Boolean)Security.invoke("check", profile);
            if(!hasProfile) {
                Security.invoke("onCheckFailed", profile);
            }
        }
    }

    // ~~~ Login

    public static void login() throws Throwable {
        Http.Cookie remember = request.cookies.get("rememberme");
        if(remember != null) {
            int firstIndex = remember.value.indexOf("-");
            int lastIndex = remember.value.lastIndexOf("-");
            if (lastIndex > firstIndex) {
                String sign = remember.value.substring(0, firstIndex);
                String restOfCookie = remember.value.substring(firstIndex + 1);
                String username = remember.value.substring(firstIndex + 1, lastIndex);
                String time = remember.value.substring(lastIndex + 1);
                Date expirationDate = new Date(Long.parseLong(time)); // surround with try/catch?
                Date now = new Date();
                if (expirationDate == null || expirationDate.before(now)) {
                    logout();
                }
                if(Crypto.sign(restOfCookie).equals(sign)) {
                    session.put("username", username);
                    redirectToOriginalURL();
                }
            }
        }
        flash.keep("url");
        render();
    }

    public static void authenticate(@Required String username, String password, boolean remember) throws Throwable {
        // Check tokens
        Boolean allowed = false;

        try {
            // This is the deprecated method name
            allowed = (Boolean)Security.invoke("authentify", username, password);
        } catch (UnsupportedOperationException e ) {
            // This is the official method name
            allowed = (Boolean)Security.invoke("authenticate", username, password);
        }
        if(validation.hasErrors() || !allowed) {
            flash.keep("url");
            flash.error("secure.error");
            params.flash();
            login();
        }
        // Mark user as connected
        session.put("username", username);
        // Remember if needed
        if(remember) {
            Date expiration = new Date();
            String duration = "30d";  // maybe make this override-able
            expiration.setTime(expiration.getTime() + Time.parseDuration(duration));
            response.setCookie("rememberme", Crypto.sign(username + "-" + expiration.getTime()) + "-" + username + "-" + expiration.getTime(), duration);

        }
        // Redirect to the original URL (or /)
        redirectToOriginalURL();
    }
    public static void authenticateAuth0(@Required String token, Auth0UserProfile profile) throws Throwable {
        // Check tokens
//        Auth0UserProfile user = new Auth0UserProfile;
//        user = profile;
//        session.put("username", profile.getEmail());
        // Mark user as connected
//        session.put("username", username);
        // Remember if needed

        // Redirect to the original URL (or /)
    }

    public static void logout() throws Throwable {
        Security.invoke("onDisconnect");
        session.clear();
        response.removeCookie("rememberme");
        Security.invoke("onDisconnected");
        flash.success("secure.logout");
        login();
    }
    
    // Get an OAuth token, possibly with particular agencies
    public static void get_token (@Required String client_id, @Required String client_secret, String agencyId) {
        // check if the client secret and client ID are correct, and if OAuth is enabled
        if (!"true".equals(Play.configuration.getProperty("application.oauthEnabled"))) {
            badRequest();
        } else if (client_id.equals(Play.configuration.getProperty("application.managerId")) &&
                client_secret.equals(Play.configuration.getProperty("application.managerSecret"))) {
            // create an OAuth key
        	String tokenRaw = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
            OAuthToken token = new OAuthToken(tokenRaw, agencyId);
            
            GlobalTx tx = VersionedDataStore.getGlobalTx();
            tx.tokens.put(token.id, token);
            tx.commit();
            
            renderText(tokenRaw);
        }
        else {
            Logger.info("Invalid client ID or secret");
            badRequest();
        }
    }
    protected static Auth0UserProfile verifyUser(String token) {
        /*org.apache.commons.codec.binary.Base64 clientSecret = new org.apache.commons.codec.binary.Base64(true);
        JWTVerifier jwtVerifier = new JWTVerifier(
                clientSecret.decode("DxDDNzdWeWl3B-BQkgfZF2YUPqIbQFg7yCjIPJdhu5ZdlibKSUBuhT7phAtrpMyG"),
                "dR7GdOhtI3HFNxfm4HySDL4Ke8uyGfTe"
        );*/

        try {
            //Map<String, Object> decoded = jwtVerifier.verify(token);

            String userInfo = getUserInfo(token);

            ObjectMapper m = new ObjectMapper();
            return m.readValue(userInfo, Auth0UserProfile.class);

        } catch (Exception e) {
            System.out.println("error validating token");
            e.printStackTrace();
        }

        return null;
    }
    protected static String getToken() {
        String token = null;
        final String authorizationHeader = request.params.get("authorization");
        if (authorizationHeader == null) return null;

        // check format (Authorization: Bearer [token])
        String[] parts = authorizationHeader.split(" ");
        if (parts.length != 2) return null;

        String scheme = parts[0];
        String credentials = parts[1];

        Pattern pattern = Pattern.compile("^Bearer$", Pattern.CASE_INSENSITIVE);
        if (pattern.matcher(scheme).matches()) {
            token = credentials;
        }
        return token;
    }
    protected static String getUserInfo(String token) throws Exception {

        URL url = new URL("https://conveyal.eu.auth0.com/tokeninfo");
        HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "USER_AGENT");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "id_token=" + token;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    // ~~~ Utils

    static void redirectToOriginalURL() throws Throwable {
        Security.invoke("onAuthenticated");
        String url = flash.get("url");
        if(url == null) {
            url = Play.ctxPath + "/";
        }
        redirect(url);
    }

    public static class Security extends Controller {

        /**
         * @Deprecated
         * 
         * @param username
         * @param password
         * @return
         */
        static boolean authentify(String username, String password) {
            throw new UnsupportedOperationException();
        }

        /**
         * This method is called during the authentication process. This is where you check if
         * the user is allowed to log in into the system. This is the actual authentication process
         * against a third party system (most of the time a DB).
         *
         * @param username
         * @param password
         * @return true if the authentication process succeeded
         */
        static boolean authenticate(String username, String password) {
            return true;
        }

        /**
         * This method checks that a profile is allowed to view this page/method. This method is called prior
         * to the method's controller annotated with the @Check method. 
         *
         * @param profile
         * @return true if you are allowed to execute this controller method.
         */
        static boolean check(String profile) {
            return true;
        }

        /**
         * This method returns the current connected username
         * @return
         */
        static String connected() {
            return session.get("username");
        }

        /**
         * Indicate if a (non-anonymous) user is currently connected
         * @return  true if the user is connected
         */
        public static boolean isConnected() {
            return session.contains("username");
        }

        /**
         * This method is called after a successful authentication.
         * You need to override this method if you with to perform specific actions (eg. Record the time the user signed in)
         */
        static void onAuthenticated() {
        }

         /**
         * This method is called before a user tries to sign off.
         * You need to override this method if you wish to perform specific actions (eg. Record the name of the user who signed off)
         */
        static void onDisconnect() {
        }

         /**
         * This method is called after a successful sign off.
         * You need to override this method if you wish to perform specific actions (eg. Record the time the user signed off)
         */
        static void onDisconnected() {
        }

        /**
         * This method is called if a check does not succeed. By default it shows the not allowed page (the controller forbidden method).
         * @param profile
         */
        static void onCheckFailed(String profile) {
            forbidden();
        }

        private static Object invoke(String m, Object... args) throws Throwable {

            try {
                return Java.invokeChildOrStatic(Security.class, m, args);       
            } catch(InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }

}
