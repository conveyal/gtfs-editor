package controllers;
 
import datastore.VersionedDataStore;
import datastore.GlobalTx;
import models.*;
 
public class Security extends Secure.Security {
	
    static boolean authenticate(String username, String password) {
        GlobalTx tx = VersionedDataStore.getGlobalTx();
        
        try {
        	return tx.accounts.containsKey(username) && tx.accounts.get(username).checkPassword(password);
        }
        finally {
        	tx.rollback();
        }
        
    }
    
    static boolean check(String profile) {
        if("manager".equals(profile)) {
            return session.contains("manageableFeeds") && session.get("manageableFeeds").length() > 0;
        }
        if("editor".equals(profile)) {
            return session.contains("editableFeeds") && session.get("editableFeeds").length() > 0;
        }
        if("admin".equals(profile)) {
            return session.contains("isProjectAdmin") && session.get("isProjectAdmin").equals("true");
        }
        return false;
    }
 
    static Account getAccount()
    {
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	try {
    		return tx.accounts.get(connected());
    	}
    	finally {
    		tx.rollback();
    	}
    }
    
}