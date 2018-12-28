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

//        xAxis.setForceZeroInRange(false);
//        xAxis.setAutoRanging(false);
//        xAxis.setTickLabelsVisible(true);
//        xAxis.setTickMarkVisible(true);
//        xAxis.setMinorTickVisible(true);
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
        for (int i = 0; i < 20; i++) { //-- add robot coords to Map/arraylist, get the size of Map or arraylist
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

//                Scanner lineX = new Scanner(new File("src/sample/pathX.txt"));
//                Scanner lineY = new Scanner(new File("src/sample/pathY.txt"));
//
//                ArrayList<Double> listX = new ArrayList<>();
//                while (lineX.hasNext()) {
//                    listX.add(Double.parseDouble(lineX.next()));
//                }
//                ArrayList<Double> listY = new ArrayList<>();
//                while (lineY.hasNext()) {
//                    listY.add(Double.parseDouble(lineY.next()));
//                }
//
//                System.out.println(pathX);
//                System.out.println(pathY);
//
//
//                // add a item of random data to queue
//
//                for (int i=0; i<listX.size();i++) {
//                    pointY.add(listY.get(i)); // add robot coordinates here only y-values being updated here??
//                    pointX.add(listX.get(i)); // robot coordinates x-values updating
//                }

                int randomX = (int) (Math.random() * 10);
                int randomY = (int) (Math.random() * 10);

                pointX.add(randomX);
                pointY.add(randomY);


                Thread.sleep(1000);
                executor.execute(this);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
