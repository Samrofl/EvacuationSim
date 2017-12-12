/*
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details.
 */
package movement;

import core.Coord;
import core.Settings;
import movement.map.IMap;
import movement.map.MapNode;
import movement.map.SimMap;

import java.util.HashMap;
import java.util.Map;

/**
 * Map based movement model which gives out Paths that use the
 * roads of a SimMap.
 */
public class RoomBasedMovement extends MovementModel implements IMap {
	/** map based movement model's settings namespace ({@value})*/
	public static final String ROOM_BASED_MOVEMENT_NS = "RoomBasedMovement";
	public static final String ROOM_WIDTH_S = "width";
	public static final String ROOM_HEIGHT_S = "height";
	public static final String NUM_OBSTACLES_S = "noOfObstacles";
	public static final String OBSTACLES_S = "obstacles";
	public double width;
	public double height;

	ObstacleRect[] obstacles;

    /**
	 * Creates a new MapBasedMovement based on a Settings object's settings.
	 * @param settings The Settings object where the settings are read from
	 */
	public RoomBasedMovement(Settings settings) {
            super(settings);
            Settings s = new Settings(ROOM_BASED_MOVEMENT_NS);
            width = s.getDouble(ROOM_WIDTH_S, 1000);
            height = s.getDouble(ROOM_HEIGHT_S, 1000);
            int numObstacles = s.getInt(NUM_OBSTACLES_S, 0);
            obstacles = new ObstacleRect[numObstacles];
            double[] obs = s.getCsvDoubles(OBSTACLES_S, numObstacles*5);
            for (int i = 0; i < numObstacles; i++) {
                obstacles[i] = new ObstacleRect(obs[i*5], obs[(i*5)+1], obs[(i*5)+2], obs[(i*5)+3], obs[(i*5)+4]);
            }
	}

	@Override
	public Path getPath() {
		Path p = new Path(generateSpeed());
		p.addWaypoint(new Coord(rng.nextDouble()*width, rng.nextDouble()*height));
		return p;
	}

	/**
	 * Copyconstructor.
	 * @param rbm The MapBasedMovement object to base the new object to
	 */
	protected RoomBasedMovement(RoomBasedMovement rbm) {
		super(rbm);
		this.width = rbm.width;
		this.height = rbm.height;
		this.obstacles = rbm.obstacles;
	}

	/**
	 * Returns a (random) coordinate that is within the defined rectangle
	 */
	@Override
	public Coord getInitialLocation() {
		return new Coord(rng.nextDouble()*width, rng.nextDouble()*height);
	}

	@Override
	public RoomBasedMovement replicate() {
		return new RoomBasedMovement(this);
	}

    public SimMap getMap() {
        Map<Coord, MapNode> map = new HashMap<>();
        MapNode p0 = new MapNode(new Coord(0,0));
		MapNode p1 = new MapNode(new Coord(0,height));
		MapNode p2 = new MapNode(new Coord(width,0));
		MapNode p3 = new MapNode(new Coord(width,height));
		p0.addNeighbor(p1);
		p1.addNeighbor(p3);
		p3.addNeighbor(p2);
		p2.addNeighbor(p0);
        map.put(p0.getLocation(),	p0);
		map.put(p1.getLocation(),	p1);
		map.put(p2.getLocation(),	p2);
		map.put(p3.getLocation(),	p3);
		for (ObstacleRect obstacle : obstacles) {
		    map.putAll(obstacle.getMap());
        }
		return new SimMap(map);
    }
}
