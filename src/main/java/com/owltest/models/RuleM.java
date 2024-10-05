package com.owltest.models;

public class RuleM {
    private String antecedent;
    private String consequent;

    public RuleM(String antecedent, String consequent) {
        this.antecedent = antecedent;
        this.consequent = consequent;
    }

    public void setAntecedent(String antecedent) {
        this.antecedent = antecedent;
    }

    public void setConsequent(String consequent) {
        this.consequent = consequent;
    }

    public String getAntecedent() {
        return this.antecedent;
    }

    public String getConsequent() {
        return this.consequent;
    }
    
}
