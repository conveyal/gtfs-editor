package models;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

//import notifications.Mails;


import models.transit.Agency;

import org.apache.commons.codec.binary.Hex;
import org.hsqldb.lib.MD5;

import play.Play;

public class Account implements Serializable {
	public static final long serialVersionUID = 1;
	
    public String username;
    public String password;
    
    public String email;
    
    public String passwordChangeToken;
    
    public Date lastLogin;
    
    public Boolean active;
    
    public Boolean admin;
    
	public String agencyId;
    
    public Account(String username, String password, String email, Boolean admin, String agencyId)
    {
    	this.username = username;
    	this.email = email;
    	this.active = true;
    	this.admin = admin;
    	this.password = Account.hash(password);
    	this.agencyId = agencyId;
    }
    
    public Boolean isAdmin()
    {
    	if(this.admin != null && this.admin)
    		return true;
    	else
    		return false;
    }
   
    
    public static String hash(String password)
    {
    	try
    	{
    		byte[] bytes = (password.trim() + Play.secretKey).getBytes("UTF-8");

    		MessageDigest md = MessageDigest.getInstance("MD5");
    		byte[] digest = md.digest(bytes);
    	
    		String hexString = new String(Hex.encodeHex(digest));
    		
    		return hexString;
    	}
    	catch(Exception e)
    	{
    		return null;
    	}
    }
}
