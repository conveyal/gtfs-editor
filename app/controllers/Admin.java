package controllers;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;

import java.awt.Color;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

import javax.persistence.Query;

import org.apache.commons.lang.StringUtils;
import org.geotools.geometry.jts.JTS;
import org.opengis.referencing.operation.MathTransform;


import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;


import models.*;
import models.transit.Agency;

@With(Secure.class)
public class Admin extends Controller {
	
	@Before
    static void setConnectedUser() {
        if(Security.isConnected() && Security.check("admin")) {
            renderArgs.put("user", Security.connected());
        }
        else
        	Application.index();
    }
	
	
	public static void accounts() {
		
		List<Account> accounts = Account.find("order by username").fetch();
		
		List<Agency> agencies = Agency.findAll();
		
		render(accounts, agencies);
	}
	
	public static void createAccount(String username, String password, String email, Boolean admin, Boolean taxi, Boolean citom, Long agencyId)
	{
		if(!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && Account.find("username = ?", username).first() == null )
			new Account(username, password, email, admin, agencyId);
		
		Admin.accounts();
	}
	
	public static void updateAccount(String username, String email, Boolean active, Boolean admin, Boolean taxi, Boolean citom, Long agencyId)
	{
		Account.update(username, email, active, admin, agencyId);
		
		Admin.accounts();
	}
	
	public static void getAccount(String username) {
		
		Account account = Account.find("username = ?", username).first();
		
		renderJSON(account);
	}
	
	
	public static void checkUsername(String username) {
		
		if(Account.find("username = ?", username).first() != null)
			badRequest();
		else
			ok();
	
	}
	
	public static void changePassword(String currentPassword, String newPassword)
	{
		Account.changePassword(Security.connected(), currentPassword, newPassword);
	}
	
	public static void resetPassword(String username, String newPassword)
	{
		Account.resetPassword(username, newPassword);
	}
	
	

}