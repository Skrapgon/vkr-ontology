package com.owltest.models;

public class Factor {

    private String factorName;
    private String factorProbability;

    public Factor(String factorName, String factorProbability) {
        this.factorName = factorName;
        this.factorProbability = factorProbability;
    }

    public String getFactorName() {
        return this.factorName;
    }

    public String getFactorProbability() {
        return this.factorProbability;
    }

    public void setFactorName(String factorName) {
        this.factorName = factorName;
    }
    
    public void setFactorProbability(String factorProbability) {
        this.factorProbability = factorProbability;
    }
}