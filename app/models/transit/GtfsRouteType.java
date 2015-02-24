package models.transit;

import com.conveyal.gtfs.model.Entity;

public enum GtfsRouteType {
	TRAM,
	SUBWAY,
	RAIL, 
	BUS, 
	FERRY,
	CABLECAR,
	GONDOLA,
	FUNICULAR;
	
	public int toGtfs() {
		switch(this)
		{
			case TRAM:
				return 0;
			case SUBWAY:
				return 1;
			case RAIL:
				return 2;
			case BUS:
				return 3;
			case FERRY:
				return 4;
			case CABLECAR:
				return 5;
			case GONDOLA:
				return 6;
			case FUNICULAR:
				return 7;
			default:
				// can't happen
				return Entity.INT_MISSING;
	
		}
	}
	
	public static GtfsRouteType fromGtfs (int gtfsType) {
		switch (gtfsType)
		{
		case 0:
			return TRAM;
		case 1:
			return SUBWAY;
		case 2:
			return RAIL;
		case 3:
			return BUS;
		case 4:
			return FERRY;
		case 5:
			return CABLECAR;
		case 6:
			return GONDOLA;
		case 7:
			return FUNICULAR;
		default:
			return null;
		}
	}
	
	public HvtRouteType toHvt () {
		switch (this) {
		case TRAM:
			return HvtRouteType.TRAM;
		case SUBWAY:
			return HvtRouteType.URBANRAIL_METRO;
		case RAIL:
			return HvtRouteType.RAIL;
		case BUS:
			// TODO overly specific
			return HvtRouteType.BUS_LOCAL;
		case FERRY:
			return HvtRouteType.WATER;
		case CABLECAR:
			return HvtRouteType.MISCELLANEOUS_CABLE_CAR;
		case GONDOLA:
			return HvtRouteType.MISCELLANEOUS;
		case FUNICULAR:
			return HvtRouteType.FUNICULAR;
		default:
			return null;
		}
	}
}