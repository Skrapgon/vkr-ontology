package com.owltest.util;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class DrawerUtil {
    private Canvas canvas;
    private GraphicsContext gc;

    public DrawerUtil(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        clearCanvas();
    }

    public void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawCircle(double[] coord) {
        gc.setFill(Color.RED);
        gc.setStroke(Color.RED);
        gc.fillOval(coord[0]-5, coord[1]-5, 10, 10);
    }

    public void drawRhomb(double[] coord) {
        gc.setFill(Color.BLUE);
        gc.setStroke(Color.BLUE);
        gc.fillPolygon(new double[] {coord[0]-5, coord[0], coord[0]+5, coord[0]}, new double[] {coord[1], coord[1]-5, coord[1], coord[1]+5}, 4);
    }

    public void drawTriangle(double[] coord) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.GREEN);
        gc.fillPolygon(new double[] {coord[0]-5, coord[0]+5, coord[0]-5}, new double[] {coord[1]-5, coord[1], coord[1]+5}, 3);
    }

    public void drawVertex(double[] coord) {
        gc.setFill(Color.BLACK);
        gc.setStroke(Color.BLACK);
        gc.fillOval(coord[0]-2.5, coord[1]-2.5, 5, 5);
    }

    public void drawEdge(double[] coordStart, double[] coordEnd, Paint p) {
        gc.setFill(p);
        gc.setStroke(p);
        gc.setLineWidth(1.0);
        gc.strokeLine(coordStart[0], coordStart[1], coordEnd[0], coordEnd[1]);
    }

    public void drawNode(double[] coord) {
        gc.setFill(Color.rgb(12, 39, 44, 0.25));
        gc.setStroke(Color.rgb(12, 39, 44, 0.25));
        gc.fillOval(coord[0]-3.5, coord[1]-3.5, 7, 7);
    }

    public void drawNode(double x, double y) {
        gc.setFill(Color.rgb(12, 39, 44, 0.25));
        gc.setStroke(Color.rgb(12, 39, 44, 0.25));
        gc.fillOval(x-3.5, y-3.5, 7, 7);
    }
}
