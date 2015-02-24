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

import datastore.VersionedDataStore;
import datastore.GlobalTx;
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
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		
		try {
			Collection<Account> accounts = tx.accounts.values();
			Collection<Agency> agencies = tx.agencies.values();
		
			render(accounts, agencies);
		} finally {
			tx.rollback();
		}
	}
	
	public static void createAccount(String username, String password, String email, Boolean admin, String agencyId)
	{		
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		
		if(!username.isEmpty() && !password.isEmpty() && !email.isEmpty() && !tx.accounts.containsKey(username)) {
			Account acct = new Account(username, password, email, admin, agencyId);
			tx.accounts.put(acct.id, acct);
			tx.commit();
		}
		else {
			tx.rollback();
		}
		
		Admin.accounts();
	}
	
	public static void updateAccount(String username, String email, Boolean active, Boolean admin, Boolean taxi, Boolean citom, String agencyId)
	{
		GlobalTx tx = VersionedDataStore.getGlobalTx();

		if (!tx.accounts.containsKey(username)) {
			badRequest();
			return;
		}
		
		Account acct = tx.accounts.get(username).clone();
		acct.email = email;
		acct.active = active;
		acct.admin = admin;
		acct.agencyId = agencyId;
		
		tx.accounts.put(acct.id, acct);

		tx.commit();
		
		Admin.accounts();
	}
	
	public static void getAccount(String username) {
		
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		Account account = tx.accounts.get(username);
		tx.rollback();
		
		if (account == null)
			notFound();
		else		
			renderJSON(account);
	}
	
	
	public static void checkUsername(String username) {
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		boolean exists = tx.accounts.containsKey(username);
		tx.rollback();
		
		if (!exists)
			badRequest();
		else
			ok();
	
	}
	
	public static void resetPassword(String username, String newPassword)
	{
		GlobalTx tx = VersionedDataStore.getGlobalTx();
		if (!tx.accounts.containsKey(username)) {
			tx.rollback();
			notFound();
			return;
		}
		
		Account acct = tx.accounts.get(username).clone();
		acct.updatePassword(newPassword);
		tx.accounts.put(acct.id, acct);
		
		tx.commit();
		
		ok();
		
	}

}