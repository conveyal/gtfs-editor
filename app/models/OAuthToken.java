package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import models.transit.Agency;
import play.db.jpa.Model;

public class OAuthToken implements Serializable {
	public static final long serialVersionUID = 1;
	
    public String token;
    public Long creationDate;
    public String agencyId;
}
