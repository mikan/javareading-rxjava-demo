package com.github.mikan.jrd.model;

import io.reactivex.Flowable;
import javafx.geometry.Point2D;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DroneTracker {

    private Flowable<Point2D> dronePositionPublisher;
    private Point2D currentPosition;

    public DroneTracker(Point2D initialPosition, long time, TimeUnit unit) {
        currentPosition = initialPosition;
        dronePositionPublisher = Flowable.interval(time, unit)
                .map(i -> {
                    updatePosition();
                    return currentPosition;
                });
    }

    public Flowable<Point2D> getPublisher() {
        return dronePositionPublisher;
    }


    private void updatePosition() {
        Random random = new Random();
        double deltaX = random.nextGaussian() + 1;
        double deltaY = random.nextGaussian() + 1;
        currentPosition = currentPosition.add(deltaX, deltaY);
    }
}
