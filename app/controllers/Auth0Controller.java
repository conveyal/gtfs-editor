package controllers;

import play.Play;
import play.mvc.Controller;

public class Auth0Controller extends Controller {
    public static void auth0Login(String token) {
        System.out.println("auth0Login token="+token);

        // get user profile from Auth0 tokeninfo API

        // session.put('username', ....)

        ok();
    }

}
