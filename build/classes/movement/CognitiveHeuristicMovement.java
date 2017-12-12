package movement;

import core.Coord;
import core.Settings;
import movement.map.EvacPoints;

public class CognitiveHeuristicMovement extends RoomBasedMovement{

    /** cognitive heuristic model's settings namespace ({@value})*/
    public static final String COGNITIVE_MOVEMENT_NS = "CognitiveMovement";
    /** evac points file -setting id ({@value})*/
    public static final String FILE_S = "evacPointsFile";
    /** horizon distance (how far away somebody can see) **/
    public static final String HORIZON_S = "horizon";
    /** time taken for person to stop in unexpected circumstances **/
    public static final String STOPPING_TIME_S = "stoppingTime";
    /** resolution of angle for a person to scan left to right **/
    public static final String RESOLUTION_S = "resolution";

    private EvacPoints evacPoints;
    private double maxDistance;
    private double stoppingTime;
    private double resolution;
    private Person person;

    public CognitiveHeuristicMovement(Settings settings) {
        super(settings);
        this.evacPoints = new EvacPoints(settings, rng);
        Settings s = new Settings(COGNITIVE_MOVEMENT_NS);
        maxDistance = s.getDouble(HORIZON_S, 10);
        person = new Person(60 + (rng.nextDouble()*20), generateSpeed());
        stoppingTime = s.getDouble(STOPPING_TIME_S);
        resolution = s.getDouble(RESOLUTION_S, 0.05);   // size of angle increments to check surrounding obstacles
    }

    private CognitiveHeuristicMovement(CognitiveHeuristicMovement chm) {
        super(chm);
        this.evacPoints = chm.evacPoints;
        this.maxDistance = chm.maxDistance;
        this.person = new Person(60 + (rng.nextDouble()*20), generateSpeed());
        this.stoppingTime = chm.stoppingTime;
        this.resolution = chm.resolution;
    }

    @Override
    public Coord getInitialLocation() {
        Coord start = null;
        boolean safe = false;
        while (!safe) {
            start = new Coord(rng.nextDouble()*width, rng.nextDouble()*height);
            safe = true;
            for (ObstacleRect obs : obstacles) {
                if (obs.willIntersect(start)) {
                    safe = false;
                    break;
                }
            }
        }
        person.setLocation(start); // random place within room
        person.setGoal(evacPoints.selectDestination(person.getLocation())); // goal is closest evacPoint to location
        person.calculateGoalOriginAngle();
        person.setRotation(person.getAngleFromOriginToGoal());  // start person facing the goal
        return person.getLocation();
    }

    @Override
    public Path getPath() {
        if (person.isWithinRadius(person.getGoal())) {  // don't calculate person if already 'escaped'
            person.removeFromList();
            return null;
        }
        double minDistance = 2*Math.pow(maxDistance, 2); // maximum possible response from heuristic
        double minLocalRotation = 0;                // angle relative to person's current forward direction
        for (double localRotation = -person.getFieldOfView(); localRotation < person.getFieldOfView();
             localRotation += resolution) {         // from left to right in person's field of view,
            double d = heuristic(localRotation);    // minimise the heuristic
            if (d < minDistance) {
                minDistance = d;
                minLocalRotation = localRotation;
            }
        }
        person.setRotation(person.getRotation() + minLocalRotation);    // rotate person by the amount needed for smallest heuristic
        Path p = new Path(person.getPreferredSpeed());
        p.addWaypoint(calculateNextWaypoint());     // create path to 0.5m ahead of current direction
        person.setLocation(getHost().getLocation());
        return p;
    }

    private Coord calculateNextWaypoint() {
        double x = person.getLocation().getX() + (0.5 * Math.cos(person.getRotation()));
        double y = person.getLocation().getY() + (0.5 * Math.sin(person.getRotation()));
        return new Coord(x, y); // from person's new rotation, find the coordinate 0.5m ahead
    }

    private double heuristic(double localRotation) {
        double worldRotation = person.getRotation() + localRotation;    // the direction that would be faced if the person rotated by localRotation
        double collision = distanceToCollision(worldRotation);
        double angleToGoal = person.angleFromFacingToGoal(worldRotation);
        return (Math.pow(maxDistance, 2) + Math.pow(collision, 2)) - (2*maxDistance*collision*Math.cos(angleToGoal - localRotation));
    }

    private double distanceToCollision(double a) {
        double personX = person.getLocation().getX();
        double personY = person.getLocation().getY();
        double projdx = personX + (maxDistance * Math.cos(a));
        double projdy = personY + (maxDistance * Math.sin(a));
        Coord proj = new Coord(projdx, projdy); // where the person will be in maxDistance units, based on rotation
        for (Person p : Person.getPeople()) {
            if (!p.equals(person) && person.getLocation().distance(p.getLocation()) < maxDistance) {    // calculate if person would collide with another within the maxDistance
                if (p.isWithinRadius(proj)) {
                    return person.getLocation().distance(p.getLocation());
                }
            }
        }
        for (ObstacleRect o : obstacles) {
            if (o.willIntersect(proj)) {    // if the projected point lies within an obstacle
                return o.intersectDistance(person.getLocation(), proj, maxDistance);    // distance to the point of intersection
            }
        } // Wall collision calculations (only calculate if person is within maxDistance of walls)
          // Should be replaced with ObstacleRect system!
        if (personX <= maxDistance) {                   // left
            projdy = (personX / Math.cos(a)) * Math.sin(a);
            return Math.sqrt(Math.pow(personX, 2) + Math.pow(projdy, 2));
        } else if (personX >= width - maxDistance) {    // right
            double dx = width - personX;
            projdy = (dx / Math.cos(a)) * Math.sin(a);
            return Math.sqrt(Math.pow(dx, 2) + Math.pow(projdy, 2));
        } else if (personY <= maxDistance) {            // top
            projdx = (personY / Math.sin(a)) * Math.cos(a);
            return Math.sqrt(Math.pow(personY, 2) + Math.pow(projdx, 2));
        } else if (personY >= height - maxDistance) {   // bottom
            double dy = height - personY;
            projdx = (dy / Math.sin(a)) * Math.cos(a);
            return Math.sqrt(Math.pow(dy, 2) + Math.pow(projdx, 2));
        }
        return maxDistance;
    }

    @Override
    public CognitiveHeuristicMovement replicate() {
        return new CognitiveHeuristicMovement(this);
    }
}
