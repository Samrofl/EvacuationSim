package movement;

import core.Coord;
import movement.map.MapNode;

import java.util.HashMap;
import java.util.Map;

public class ObstacleRect {
    double centreX, centreY;
    double width, height;
    double rotation;

    Coord A, B, C, D;
    Vector2[] sides;

    public ObstacleRect(double centreX, double centreY, double width, double height, double rotation) {
        this.centreX = centreX;
        this.centreY = centreY;
        this.width = width;
        this.height = height;
        this.rotation = rotation;
        A = rotatePointAroundCentre(new Coord(centreX - this.width, centreY + this.height));
        B = rotatePointAroundCentre(new Coord(centreX + this.width, centreY + this.height));
        C = rotatePointAroundCentre(new Coord(centreX + this.width, centreY - this.height));
        D = rotatePointAroundCentre(new Coord(centreX - this.width, centreY - this.height));
        sides = new Vector2[4];
        sides[0] = new Vector2(A, B);   // points are laid out (relatively) in this fashion:
        sides[1] = new Vector2(A, D);   // A----B
        sides[2] = new Vector2(C, B);   // |    |
        sides[3] = new Vector2(C, D);   // D----C
    }

    public boolean willIntersect(Coord point) { // returns true if point is inside the rectangle, otherwise false
        Vector2 AM = new Vector2(A, point);
        return (0 <= AM.dotProduct(sides[0]) && AM.dotProduct(sides[0]) <= sides[0].dotProduct(sides[0])) && (0 <= AM.dotProduct(sides[1]) && AM.dotProduct(sides[1]) <= sides[1].dotProduct(sides[1]));
    }

    public double intersectDistance(Coord personLocation, Coord point, double maxDistance) {
        double distance = maxDistance;
        Vector2 P = new Vector2(personLocation, point);
        for (Vector2 v : sides) {
            Coord poi = P.pointOfIntersection(v);
            if (poi == null) {
                continue;
            }
            double d = poi.distance(personLocation);
            if (d < distance) {
                distance = d;
            }
        }
        return distance;
    }

    private Coord rotatePointAroundCentre(Coord oldPoint) {
        double newX = ((oldPoint.getX()-centreX) * Math.cos(rotation)) - ((oldPoint.getY()-centreY) * Math.sin(rotation));
        double newY = ((oldPoint.getY()-centreY) * Math.cos(rotation)) + ((oldPoint.getX()-centreX) * Math.sin(rotation));
        return new Coord(newX+centreX, newY+centreY);
    }

    public Map<? extends Coord,? extends MapNode> getMap() {    // used to draw rectangle in GUI
        Map<Coord, MapNode> map = new HashMap<>();
        MapNode p0 = new MapNode(A);
        MapNode p1 = new MapNode(B);
        MapNode p2 = new MapNode(C);
        MapNode p3 = new MapNode(D);
        p0.addNeighbor(p1);
        p1.addNeighbor(p2);
        p2.addNeighbor(p3);
        p3.addNeighbor(p0);
        map.put(p0.getLocation(), p0);
        map.put(p1.getLocation(), p1);
        map.put(p2.getLocation(), p2);
        map.put(p3.getLocation(), p3);
        return map;
    }
}
