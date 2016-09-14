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
    	GlobalTx tx = VersionedDataStore.getGlobalTx();
    	
    	try {
	        if("admin".equals(profile))
	        	return tx.accounts.containsKey(connected()) && tx.accounts.get(connected()).isAdmin(); 
	        else
	        	return false;
    	}
    	finally {
    		tx.rollback();
    	}
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