package com.owltest.models;

import java.util.ArrayList;

public class FactorsInfo {

    private int edgeNumber;
    private ArrayList<Factor> factors;

    public FactorsInfo(int edgeNumber, ArrayList<Factor> factors) {
        this.edgeNumber = edgeNumber;
        this.factors = factors;
    }

    public int getEdgeNumber() {
        return this.edgeNumber;
    }

    public ArrayList<Factor> geFactors() {
        return this.factors;
    }

}