package controllers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import controllers.Secure.Security;

import models.Account;
import models.transit.Agency;
import play.Play;
import play.mvc.*;
import play.data.validation.*;
import play.data.validation.Validation.ValidationResult;
import play.libs.*;
import play.utils.*;

public class Bootstrap extends Controller {

    public static void index() {
    	
    	if(Account.count() == 0)
    		Bootstrap.adminForm();
    	
    	else if(Agency.count() == 0)
    		Bootstrap.agencyForm();
    	
    	else 
    		Application.index();
    }
    
    public static void adminForm() {
    	
    	if(Account.count() > 0)
    		Bootstrap.agencyForm();
    	
    	render();
    	
    }
    
    public static void createAdmin(String username, String password, String password2, String email) throws Throwable {
        
    	if(Account.count() > 0)
    		Bootstrap.index();
    	
    	validation.required(username).message("Username cannot be blank.");
    	validation.required(password).message("Password cannot be blank.");
    	validation.equals(password, password2).message("Passwords do not match.");
    	
    	if(validation.hasErrors()) {
    		params.flash();
    		validation.keep();
            adminForm();
        }
       	
    	new Account(username, password, email, true, null);
    	
    	Bootstrap.index();
    }
    
    public static void agencyForm() {
    	
    	if(Account.count() == 0)
    		Bootstrap.adminForm();
    	
    	if(Agency.count() > 0)
    		Application.index();
    	
    	render();
    }
    
    public static void createAgency( String gtfsId, String name, String url, @Required String timezone, @Required String language, String phone, Double defaultLat, Double defaultLon) throws Throwable {
    	
    	if(Agency.count() > 0)
    		Bootstrap.index();
    	
    	validation.required(gtfsId).message("Agency GTFS ID cannot be blank.");
    	validation.required(name).message("Agency name cannot be blank.");
    	validation.required(url).message("Agency URL cannot be blank.");
    	
    	if(validation.hasErrors()) {
    		params.flash();
    		validation.keep();
    		agencyForm();
        }
    	
    	Agency agency = new Agency(gtfsId, name, url, timezone, language, phone);
    	
    	agency.defaultLat = defaultLat;
    	agency.defaultLon = defaultLon;
    	
    	agency.save();
    	
    	Bootstrap.index();
    }
    
}

