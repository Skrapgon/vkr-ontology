package com.owltest.util;

import java.util.ArrayList;

import com.owltest.models.Vertex;

public class CalculationUtil {

    private static int scale = 25;

    public static Vertex isInVertexes(ArrayList<Vertex> V, Vertex u) {
        for (Vertex v : V) if (equals(v.getX(), v.getY(), u.getX(), u.getY())) return v;
        return null;
    }

    public static int transferDoubleToInteger(double coord) {
        int res = (int) Math.round(coord)/scale;
        return res;
    }

    public static double transferIntegerToDouble(int coord) {
        double res = coord*scale+scale/2;
        return res;
    }

    public static boolean equals(int x1, int y1, int x2, int y2) {
        if (x1 == x2 && y1 == y2) return true;
        return false;
    }
    
    public static double mod(Vertex v1, Vertex v2) {
        return Math.pow(Math.pow(v1.getX()-v2.getX(), 2) + Math.pow(v1.getY()-v2.getY(), 2), 0.5);
    }

    public static double mod(Vertex v1, Vertex v2, double s) {
        return Math.pow(Math.pow(v1.getX()-v2.getX(), 2) + Math.pow(v1.getY()-v2.getY(), 2), 0.5)*s;
    }
}
