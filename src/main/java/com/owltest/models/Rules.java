package com.owltest.models;

import java.util.ArrayList;

public class Rules {
    private ArrayList<RuleM> timeRules;
    private ArrayList<RuleM> probabilityRules;
    private ArrayList<RuleM> priorityRules;

    public Rules( ArrayList<RuleM> timeRules, ArrayList<RuleM> probabilityRules, ArrayList<RuleM> priorityRules) {
        this.timeRules = timeRules;
        this.probabilityRules = probabilityRules;
        this.priorityRules = priorityRules;
    }

    public ArrayList<RuleM> getTimeRules() {
        return this.timeRules;
    }

    public ArrayList<RuleM> getProbabilityRules() {
        return this.probabilityRules;
    }

    public ArrayList<RuleM> getPriorityRules() {
        return this.priorityRules;
    }
}
