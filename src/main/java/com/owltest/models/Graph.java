package com.owltest.models;

import java.util.ArrayList;

public class Graph {

    private ArrayList<Vertex> V;
    private ArrayList<Edge> E;

    public Graph(ArrayList<Vertex> V, ArrayList<Edge> E) {
        this.V = V;
        this.E = E;
    }

    public ArrayList<Vertex> getV() {
        return this.V;
    }

    public ArrayList<Edge> getE() {
        return this.E;
    }

}