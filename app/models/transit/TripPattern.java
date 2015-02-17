package models.transit;


import static java.util.Collections.sort;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import models.Model;

import org.geotools.ows.bindings.UpdateSequenceTypeBinding;
import org.hibernate.annotations.Type;

import play.Logger;

public class TripPattern extends Model implements Serializable {
	public static final long serialVersionUID = 1;

    public String name;
    public String headsign;

    public String encodedShape;
    
    public String shapeId;

    public String routeId;
    
    public String agencyId;

    public List<TripPatternStop> patternStops;

    public Boolean longest;

    public Boolean weekday;
    public Boolean saturday;
    public Boolean sunday;

    public Boolean useFrequency;

    public Integer startTime;
    public Integer endTime;

    public Integer headway;

    public TripPattern()
    {

    }

    public TripPattern(String name, String headsign, TripShape shape, Route route)
    {
    	this.name = name;
    	this.headsign = headsign;
    	this.shapeId = shape.id;
    	this.routeId = route.id;
    }
}
