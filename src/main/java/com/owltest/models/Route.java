package com.owltest.models;

import java.util.ArrayList;

public class Route {
    private ArrayList<Edge> edges;
    private double routeLength;

    public Route(ArrayList<Edge> edges, double routeLength) {
        this.edges = edges;
        this.routeLength = routeLength;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
    }

    public double getRouteLength() {
        return this.routeLength;
    }
}
