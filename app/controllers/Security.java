package controllers;
 
import models.*;
 
public class Security extends Secure.Security {
	
    static boolean authenticate(String username, String password) {
        return Account.connect(username, password);
    }
    
    static boolean check(String profile) {
        if("admin".equals(profile)) {
        	Account account = Account.find("username", connected()).<Account>first();
        	
        	if(account == null) {
        		return false;
        	}
        		
            return account.isAdmin();
        }
   
        return false;
    }
 
    static Account getAccount()
    {
    	return Account.find("username", connected()).<Account>first();
    }
    
}