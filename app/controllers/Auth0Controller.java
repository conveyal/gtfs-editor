package controllers;

import com.fasterxml.jackson.databind.ObjectMapper;

import play.Play;
import play.mvc.Controller;
import utils.Auth0UserProfile;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.Exception;
import java.net.URL;
import java.util.regex.Pattern;

public class Auth0Controller extends Controller {
    public static void auth0Login(String token) {
        System.out.println("auth0Login token="+token);

        // get user profile from Auth0 tokeninfo API

        session.put("token", token);

        try {
            Auth0UserProfile profile = getUserInfo(token);
            session.put("username", profile.getEmail());
            
            String projectID = Play.configuration.getProperty("application.projectId");

            String editableFeeds = StringUtils.join(profile.getEditableFeeds(projectID), ",");
            session.put("editableFeeds", editableFeeds);

            String manageableFeeds = StringUtils.join(profile.getManageableFeeds(projectID), ",");
            session.put("manageableFeeds", manageableFeeds);

            String approveableFeeds = StringUtils.join(profile.getApproveableFeeds(projectID), ",");
            session.put("approveableFeeds", approveableFeeds);

            String isProjectAdmin = profile.canAdministerProject(projectID) ? "true" : "false";
            session.put("isProjectAdmin", isProjectAdmin);

        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in user auth, redirecting to /auth0logout");
            redirect("/auth0logout");
        }

        ok();
    }

    public static void auth0Logout(String token) {
        System.out.println("logging out");
        session.clear();
        redirect("/");
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

        URL url = new URL("https://" + Play.configuration.getProperty("application.auth0Domain") + "/tokeninfo");
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
        InputStreamReader is = new InputStreamReader(con.getInputStream());
        if (is == null) {
            return null;
        }
        BufferedReader in = new BufferedReader(is);
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
