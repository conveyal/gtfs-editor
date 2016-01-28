package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import play.Play;
import play.mvc.Controller;
import utils.Auth0UserProfile;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

public class Auth0Controller extends Controller {
    public static void auth0Login(String token) {
        System.out.println("auth0Login token="+token);

        // get user profile from Auth0 tokeninfo API

        session.put("username", "test");
        session.put("token", token);
        System.out.println("user=" + session.get("username"));

        ok();
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

    protected static Auth0UserProfile getUserInfo(String token) throws Exception {

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

        String userInfo = response.toString();

        ObjectMapper m = new ObjectMapper();
        return m.readValue(userInfo, Auth0UserProfile.class);


    }
}
