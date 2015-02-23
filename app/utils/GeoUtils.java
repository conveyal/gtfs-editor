package utils;

import java.awt.geom.Point2D;

import org.geotools.referencing.GeodeticCalculator;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.WKTWriter;

public class GeoUtils {
	public static GeometryFactory geometyFactory = new GeometryFactory();
	
	/**
	 * Create a buffer around the given point of the given size in meters. Uses geography rather than cartesian distance.
	 * @param point the point to buffer, in WGS84 geographic coordinates
	 * @param dist the buffer size, in meters
	 * @param npoints the number of points in the buffer
	 */
	public static Polygon bufferGeographicPoint (Coordinate point, double dist, int npoints) {
		GeodeticCalculator calc = new GeodeticCalculator();
		calc.setStartingGeographicPoint(point.x, point.y);
		
		Coordinate[] coords = new Coordinate[npoints + 1];
		
		for (int i = 0; i < npoints; i++) {
			double deg = -180 + i * 360 / npoints;
			calc.setDirection(deg, dist);
			Point2D dest = calc.getDestinationGeographicPoint();
			coords[i] = new Coordinate(dest.getX(), dest.getY());
		}
		
		coords[npoints] = (Coordinate) coords[0].clone();
		
		LinearRing ring = geometyFactory.createLinearRing(coords);
		LinearRing[] holes = new LinearRing[0];
		
		return geometyFactory.createPolygon(ring, holes);
	}
	
	/** get the distances from the start of the line string to every coordinate along the line string */
	public static double[] getCoordDistances(LineString line) {
		double[] coordDist = new double[line.getNumPoints()];
		coordDist[0] = 0;
		
		Coordinate prev = line.getCoordinateN(0);
		GeodeticCalculator gc = new GeodeticCalculator();
		for (int j = 1; j < coordDist.length; j++) {
			Coordinate current = line.getCoordinateN(j);
			gc.setStartingGeographicPoint(prev.x, prev.y);
			gc.setDestinationGeographicPoint(current.x, current.y);
			coordDist[j] = coordDist[j - 1] + gc.getOrthodromicDistance();
			prev = current;
		}
		
		return coordDist;
	}

}
