package com.owltest.models;

public class Edge {

    private Vertex v1, v2;
    private boolean flag;
    private double length;
	private String weather;
    private String surfaceType;
	private int number;

    public Edge(Vertex v1, Vertex v2, boolean flag, double length, String weather, String surfaceType, int number) {
        this.v1 = v1;
        this.v2 = v2;
        this.flag = flag;
        this.length = length;
		this.weather = weather;
        this.surfaceType = surfaceType;
		this.number = number;
    }

    public Vertex getStartV() {
        return this.v1;
    }

    public Vertex getEndV() {
        return this.v2;
    }

    public boolean getFlag() {
        return this.flag;
    }

    public double getLenght() {
        return this.length;
    }
	
	public String getWeather() {
		return this.weather;
	}

    public String getSurfaceType() {
        return this.surfaceType;
    }
	
	public int getNumber() {
		return this.number;
	}
}
