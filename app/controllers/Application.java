package controllers;

import play.*;
import play.i18n.Lang;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void scaffolding() {
        render();
    }

    public static void search() {
        render();
    }

    public static void route() {
        render();
    }
    
    public static void setLang(String lang) {
    	Lang.change(lang);
    	ok();
    }
}