package movement;

import core.Coord;

import java.util.ArrayList;

public class Person {
    private static final double TAU;    // shortcut for 2 * pi
    private static ArrayList<Person> people;    // all people in World

    static {
        people = new ArrayList<>();
        TAU = 2 * Math.PI;
    }

    public static ArrayList<Person> getPeople() {
        return people;
    }

    private Coord location;
    private float speed;
    private double radius;
    private double preferredSpeed;
    private Coord goal;
    private double angleFromOriginToGoal;
    private double rotation;
    private double fieldOfView;

    public Person(double mass, double preferredSpeed) {
        setRadius(mass/320);
        setPreferredSpeed(preferredSpeed);
        setFieldOfView(Math.toRadians(150)/2);
        setGoal(new Coord(0,0));
        setLocation(new Coord(0,0));
        people.add(this);
    }

    public void calculateGoalOriginAngle() {    // optimisation so that only updates on location change
        angleFromOriginToGoal = Math.atan2(goal.getY() - location.getY(), goal.getX() - location.getX());
    }

    public double getAngleFromOriginToGoal() {
        return angleFromOriginToGoal;   // origin is defined as location of person, and angles starting at 0 to the right
    }

    public double angleFromFacingToGoal() {
        return 0 - (rotation - angleFromOriginToGoal);
    }

    public double angleFromFacingToGoal(double faceDirection) {
        return 0 - (faceDirection - angleFromOriginToGoal);
    }

    public boolean isWithinRadius(Coord c) {
        return location.distance(c) < radius;
    }

    public Coord getLocation() {
        return location;
    }

    public void setLocation(Coord location) {
        this.location = location;
        calculateGoalOriginAngle();
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getPreferredSpeed() {
        return preferredSpeed;
    }

    public void setPreferredSpeed(double preferredSpeed) {
        this.preferredSpeed = preferredSpeed;
    }

    public Coord getGoal() {
        return goal;
    }

    public void setGoal(Coord goal) {
        this.goal = goal;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {  // rotation should only be between 0 and TAU
        if (rotation > TAU) {
            this.rotation = rotation - TAU;
        } else if (rotation < 0) {
            this.rotation = rotation + TAU;
        } else {
            this.rotation = rotation;
        }
    }

    public double getFieldOfView() {
        return fieldOfView;
    }

    public void setFieldOfView(double fieldOfView) {
        this.fieldOfView = fieldOfView;
    }

    public void removeFromList() {
        people.remove(this);
    }
}
