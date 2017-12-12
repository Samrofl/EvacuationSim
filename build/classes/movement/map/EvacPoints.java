/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement.map;

import core.Coord;
import core.Settings;

import java.util.ArrayList;
import java.util.Random;

/**
 * Handler for evacuation point data. Similar to POIs without probability.
 */
public class EvacPoints {
	/** Evacuation Points settings namespace ({@value})*/
	public static final String EVAC_NS = "EvacPoints";
	/** Evacuation Points file path -prefix id ({@value})*/
	public static final String EVAC_COORDS_S = "evacPoints";

	/**
	 * Per node group setting used for selecting evac point groups
	 * ({@value}).<BR>Syntax:
	 * <CODE>poiGroupIndex1, groupIndex2,
	 *  etc...</CODE><BR>
	 */
	public static final String EVAC_SELECT_S = "evac";
	/** list of all this POI instance's POI lists */
	private ArrayList<Coord> evacNodes;
	/** (pseudo) random number generator */
	private Random rng;

	/**
	 * Constructor.
	 * @param settings The Settings object where settings are read from
	 * @param rng The random number generator to use
	 */
	public EvacPoints(Settings settings, Random rng) {
		this.rng = rng;
		evacNodes = new ArrayList<>();
		readEvacNodes(settings);
	}

	/**
	 * Selects the closest evacuation point.
	 * @return A destination among evacuation points
	 */
	public Coord selectDestination(Coord host) {
		if (evacNodes.size() == 0) {
			return new Coord(0,0);
		}
		Coord closest = evacNodes.get(0);
		double closestDist = closest.distance(host);
		for (Coord point : evacNodes) {
			double dist = point.distance(host);
			if (dist < closestDist) {
				closest = point;
				closestDist = dist;
			}
		}
		return closest;
	}

	/**
	 * Reads evacuation selections from given Settings and
	 * stores them to <CODE>evacNodes</CODE>
	 * @param s The settings file where group specific settings are read
	 * @throws Settings error if there was an error while reading the file or
	 * some of the settings had invalid value(s).
	 */
	private void readEvacNodes(Settings s) {
		// read POIs from the requested indexes
		double[] rawPoints = s.getCsvDoubles(EVAC_COORDS_S);
		for (int i = 0; i < rawPoints.length; i += 2) {
			evacNodes.add(new Coord(rawPoints[i], rawPoints[i+1]));
		}
	}
}
