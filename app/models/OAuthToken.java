package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.transit.Agency;

public class OAuthToken extends Model implements Serializable {
	public static final long serialVersionUID = 1;
	
    public final String token;
    public long creationDate;
    public String agencyId;
    
    public OAuthToken (String token, String agencyId) {
    	super(Account.hash(token));
    	this.token = Account.hash(token);
    	this.agencyId = agencyId;
    	this.creationDate = System.currentTimeMillis();
    }
}
