package com.owltest.models;

import java.util.ArrayList;

public class Vertex {

    private int x,  y;
    private int number;
    private String type, name;
    private ArrayList<Integer> routes;

    public Vertex(int x, int y, int number) {
        this.x = x;
        this.y = y;
        this.number = number;
    }

    public Vertex(Vertex u) {
        this.x = u.getX();
        this.y = u.getY();
        this.number = u.getNumber();
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRoutes(int s, int offset) {
        this.routes = new ArrayList<Integer>();
        for (int i = 0; i < s; i++) this.routes.add(i+offset);
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getNumber() {
        return this.number;
    }

    public String getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }

    public ArrayList<Integer> getRoutes() {
        return this.routes;
    }
}
