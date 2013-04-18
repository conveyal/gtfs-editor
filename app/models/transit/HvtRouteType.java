package models.transit;

public enum HvtRouteType {

	// using the TPEG/HVT "standard" as documented in the 3/20/08 Google Group message from Joe Hughes. Oddly, this seems to be the document of record for this change!
	// https://groups.google.com/forum/?fromgroups=#!msg/gtfs-changes/keT5rTPS7Y0/71uMz2l6ke0J
	
	RAIL, // 100 Railway Service
	RAIL_HS, // 101 High Speed Rail Service
	RAIL_LD, // 102 Long Distance Trains
	RAIL_SHUTTLE, // 108 Rail Shuttle (within complex)
	RAIL_SUBURBAN, // 109 Suburban Railway 
	
	COACH, // 200 Coach Service
	COACH_INTERNATIONAL, // 201 International Coach Service
	COACH_NATIONAL, // 202 National Coach Service
	COACH_REGIONAL, // 204 Regional Coach Service
	COACH_COMMUTER, // 208 Commuter Coach Service
	
	URBANRAIL, // 400 Urban Railway Service
	URBANRAIL_METRO, // 401 Metro Service
	URBANRAIL_UNDERGROUND, // 402 Underground Service
	URBANRAIL_MONORAIL, // 405 Monorail
	
	BUS, // 700 Bus Service
	BUS_REGIONAL, // 701 Regional Bus Service
	BUS_EXPRESS, // 702 Express Bus Service
	BUS_LOCAL, // 704 Local Bus Service
	BUS_UNSCHEDULED, // 70X Unscheduled Bus Service (used for "informal" services like jeepneys, collectivos, etc.) 
					 // need to formally assign HVT id to this type -- unclear how to do this given there's no registry.
	
	TROLLEYBUS, // 800 Trolleybus Service
	
	TRAM, // 900 Tram Service
	
	WATER, // 1000 Water Transport Service

	AIR, // 1100 Air Service

	TELECABIN, // 1300 Telecabin Service
	FUNICULAR, // 1400 Funicular Service

	MISCELLANEOUS, // 1700 Miscellaneous Service
	MISCELLANEOUS_CABLE_CAR, //1701 Cable Car
	MISCELLANEOUS_HORSE_CARRIAGE, // 1702 Horse-Drawn Carriage
}



















