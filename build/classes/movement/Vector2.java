package movement;

import core.Coord;

public class Vector2 {

    private Coord start, end;
    private double dx, dy, m, c;
    private LineType line;

    public Vector2(Coord start, Coord end) {
        this.start = start;
        this.end = end;
        dx = end.getX() - start.getX();
        dy = end.getY() - start.getY();
        if (dx == 0) {
            line = LineType.X;
        } else if (dy == 0) {
            line = LineType.Y;
        } else {
            line = LineType.YMXC;
            m = dy/dx;
            c = start.getY() - (m*start.getX());
        }
    }

    public double dotProduct(Vector2 v) {
        return (dx * v.dx) + (dy * v.dy);
    }

    public Coord pointOfIntersection(Vector2 v) {
        Coord poi = null;
        switch (v.line) {
            case X:
                double vx = v.start.getX();
                if (line == LineType.Y) {
                    poi = new Coord(vx, start.getY());
                } else if (line == LineType.YMXC) {
                    poi = new Coord(vx, (m * vx) + c);
                }
                if (v.withinRangeOfLine(poi)) { return poi; }
                break;
            case Y:
                double vy = v.start.getY();
                if (line == LineType.X) {
                    poi = new Coord(start.getX(), vy);
                } else if (line == LineType.YMXC) {
                    poi = new Coord((vy - c) / m, vy);
                }
                if (v.withinRangeOfLine(poi)) { return poi; }
                break;
            case YMXC:
                if (line == LineType.X || line == LineType.Y) {
                    return v.pointOfIntersection(this);
                } else {
                    double intersectX = (v.c - this.c) / (this.m - v.m);
                    poi = new Coord(intersectX, (m * intersectX) + c);
                    if (v.withinRangeOfLine(poi)) { return poi; }
                }
        }
        return null;
    }

    private boolean withinRangeOfLine(Coord poi) {  // is a point between the start and end points?
        return poi != null && start.distance(poi) + end.distance(poi) == start.distance(end);
    }
}
