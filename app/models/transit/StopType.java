package models.transit;


import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;

import models.Model;

import org.hibernate.annotations.Type;

public class StopType extends Model {
	
    public String stopType;
	public String description;

	public Boolean interpolated;
	public Boolean majorStop;    
}
