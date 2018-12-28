package sample;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.util.Scanner;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * This is the framework for laying out a basic graphing widget intended for wpilib/Shuffleboard
 * Every time you run this application, it will create a new graph and start pointing random values
 * Random values are generated randomly via a Runnable and then updated live by Thread.
 * <p>
 * Once implemented into the robot itself, it will pull values from the Drive train (the coordinates) and pass them
 * through this code, which then you can pull the widget up on your Shuffleboard and track the position of your robot
 * visually.
 */

public class Controller {

    Scanner pathX;
    Scanner pathY;


    @FXML
    NumberAxis xAxis = new NumberAxis();

    @FXML
    NumberAxis yAxis = new NumberAxis();

    @FXML
    LineChart<Number, Number> coordinate;

    XYChart.Series<Number, Number> series = new XYChart.Series();

    // hold data point for y
    public ConcurrentLinkedQueue<Number> pointY = new ConcurrentLinkedQueue<>();

    // hold data point for x
    public ConcurrentLinkedQueue<Number> pointX = new ConcurrentLinkedQueue<>();

    // generate random values
    public ExecutorService executor;


    @FXML
    public void initialize() {

        // Create a LineChart
        coordinate.setAnimated(true);
        coordinate.setTitle("Live Robot Coordinates");
        coordinate.setHorizontalGridLinesVisible(true);
        coordinate.setVerticalGridLinesVisible(true);
        coordinate.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);

        series.setName("Coords");

        // Add Chart Series
        coordinate.getData().addAll(series);

        executor = Executors.newCachedThreadPool(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                return thread;
            }
        });

        AddToQueue addToQueue = new AddToQueue();
        executor.execute(addToQueue);
        //-- Prepare Timeline
        prepareTimeline();
    }

    public void addDataToSeries() {
        for (int i = 0; i < 1; i++) { //-- add robot coords to Map/arraylist, get the size of Map or arraylist
            if (pointY.isEmpty())
                break;
            series.getData().add(new XYChart.Data<>(pointX.remove(), pointY.remove()));
            // x-value is pointX.remove
            // y-value is pointY.remove
        }
    }

    public void prepareTimeline() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                addDataToSeries();
            }
        }.start();
    }


    // just a method to get random values
    public class AddToQueue implements Runnable {  // this should be in Point2D?
        public void run() {

            try {

                int randomX = (int) (Math.random() * 10);
                int randomY = (int) (Math.random() * 10);
                System.out.println(randomX + ", " + randomY);
                pointX.add(randomX);
                pointY.add(randomY);


                Thread.sleep(1000);
                executor.execute(this::run);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
