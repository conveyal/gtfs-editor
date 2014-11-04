package models.transit;

public enum StopTimePickupDropOffType {
	SCHEDULED,
	NONE,
	AGENCY,
	DRIVER;

    public Integer toGtfsValue() {
        switch (this) {
        case SCHEDULED:
            return 0;
        case NONE:
            return 1;
        case AGENCY:
            return 2;
        case DRIVER:
            return 3;
        default:
            // can't happen, but Java requires a default statement
            return null;
        }
    }
}