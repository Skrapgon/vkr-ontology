package com.owltest.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

import com.owltest.models.Edge;
import com.owltest.models.Factor;
import com.owltest.models.FactorsInfo;
import com.owltest.models.Graph;
import com.owltest.models.RuleM;
import com.owltest.models.Rules;
import com.owltest.models.Vertex;

public class JSON {
    public static Graph graphFromJSON(String filePath) throws FileNotFoundException, IOException, ParseException {
        Object o = new JSONParser().parse(new FileReader(filePath, StandardCharsets.UTF_8));
        JSONObject j = (JSONObject) o;
        JSONArray jsonV = (JSONArray) j.get("V");
        JSONArray jsonE = (JSONArray) j.get("E");
        ArrayList<Vertex> V = new ArrayList<Vertex>();
        ArrayList<Edge> E = new ArrayList<Edge>();
        for (int i = 0; i < jsonV.size(); i++) {
            JSONObject tempV = (JSONObject) jsonV.get(i);
            Vertex v = new Vertex(Long.valueOf((long) tempV.get("X")).intValue(), Long.valueOf((long) tempV.get("Y")).intValue(), Long.valueOf((long) tempV.get("number")).intValue());
            V.add(v);
        }
        for (int i = 0; i < jsonE.size(); i++) {
            JSONObject tempE = (JSONObject) jsonE.get(i);
            Vertex startV = null, endV = null;
            double length = 0;
            for (Vertex v : V) {
                if (v.getNumber() == Long.valueOf((long) tempE.get("v1")).intValue()) startV = v;
                if (v.getNumber() == Long.valueOf((long) tempE.get("v2")).intValue()) endV = v;
            }
            if ((boolean) tempE.get("flag")) length = CalculationUtil.mod(startV, endV);
            else length = (double) tempE.get("length");
            Edge e = new Edge(startV, endV, (boolean) tempE.get("flag"), length, (String) tempE.get("weather"), (String) tempE.get("surface_type"), i);
            E.add(e);
        }
        Graph G = new Graph(V, E);
        return G;
    }

    @SuppressWarnings("unchecked")
    public static void graphToJSON(String filePath, Graph G) {
        JSONObject j = new JSONObject();
        JSONArray jsonV = new JSONArray();
        JSONArray jsonE = new JSONArray();
        for (Vertex v : G.getV()) {
            JSONObject tempV = new JSONObject();
            tempV.put("X", v.getX());
            tempV.put("Y", v.getY());
            tempV.put("number", v.getNumber());
            jsonV.add(tempV);
        }
        for (Edge e : G.getE()) {
            JSONObject tempE = new JSONObject();
            int startNumber = 0, endNumber = 0;
            for (Vertex v : G.getV()) {
                if (v == e.getStartV()) startNumber = v.getNumber();
                if (v == e.getEndV()) endNumber = v.getNumber();
            }
            tempE.put("v1", startNumber);
            tempE.put("v2", endNumber);
            tempE.put("flag", e.getFlag());
            tempE.put("length", e.getLenght());
			tempE.put("weather", e.getWeather());
            tempE.put("surface_type", e.getSurfaceType());
            tempE.put("number", e.getNumber());
            jsonE.add(tempE);
        }
        j.put("V", jsonV);
        j.put("E", jsonE);
        try (FileWriter file = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            file.write(j.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<FactorsInfo> factorsInfoFromJSON(String filePath) throws FileNotFoundException, IOException, ParseException {
        Object o = new JSONParser().parse(new FileReader(filePath, StandardCharsets.UTF_8));
        JSONObject j = (JSONObject) o;
        JSONArray jsonInfo = (JSONArray) j.get("info");
        ArrayList<FactorsInfo> factorsInfo = new ArrayList<FactorsInfo>();
        for (int i = 0; i < jsonInfo.size(); i++) {
            JSONObject tempE = (JSONObject) jsonInfo.get(i);
            JSONObject tempEdgeInfo = (JSONObject) tempE.get("factors");
            @SuppressWarnings("unchecked")
            Set<String> keySet = tempEdgeInfo.keySet();
            Iterator<String> keys = keySet.iterator();
            ArrayList<Factor> tempFactors = new ArrayList<Factor>();
            while(keys.hasNext()) {
                String factorName = keys.next();
                String factorProbability = (String) tempEdgeInfo.get(factorName);
                Factor tFactor = new Factor(factorName, factorProbability);
                tempFactors.add(tFactor);
            }
            FactorsInfo edgeFactorsInfo = new FactorsInfo(Long.valueOf((long)tempE.get("number")).intValue(), tempFactors);
            factorsInfo.add(edgeFactorsInfo);
        }
        return factorsInfo;
    }

    @SuppressWarnings("unchecked")
    public static void factorsInfoToJSON(String filePath, ArrayList<FactorsInfo> info) {
        JSONObject j = new JSONObject();
        JSONArray jsonFactorsInfo = new JSONArray();
        for (FactorsInfo i : info) {
            JSONObject tempI = new JSONObject();
            tempI.put("number", i.getEdgeNumber());
            ArrayList<Factor> tempFactors = i.geFactors();
            JSONObject tempF = new JSONObject();
            for (Factor factor: tempFactors) {
                tempF.put(factor.getFactorName(), factor.getFactorProbability());
            }
            tempI.put("factors", tempF);
            jsonFactorsInfo.add(tempI);
        }
        j.put("info", jsonFactorsInfo);
        try (FileWriter file = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            file.write(j.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Rules rulesFromJSON(String filePath) throws FileNotFoundException, IOException, ParseException {
        Object o = new JSONParser().parse(new FileReader(filePath, StandardCharsets.UTF_8));
        JSONObject j = (JSONObject) o;
        JSONObject jsonRules = (JSONObject) j.get("rules");
        JSONArray jsonTimeRules = (JSONArray) jsonRules.get("time");
        JSONArray jsonProbabilityRules = (JSONArray) jsonRules.get("probability");
        JSONArray jsonPriorityRules = (JSONArray) jsonRules.get("priority");
        ArrayList<RuleM> timeRules = new ArrayList<RuleM>();
        ArrayList<RuleM> probabilityRules = new ArrayList<RuleM>();
        ArrayList<RuleM> priorityRules = new ArrayList<RuleM>();
        for (int i = 0; i < jsonTimeRules.size(); i++) {
            JSONObject jsonTempRule = (JSONObject) jsonTimeRules.get(i);
            RuleM tempRule = new RuleM((String) jsonTempRule.get("antecedent"), (String) jsonTempRule.get("consequent"));
            timeRules.add(tempRule);
        }
        for (int i = 0; i < jsonProbabilityRules.size(); i++) {
            JSONObject jsonTempRule = (JSONObject) jsonProbabilityRules.get(i);
            RuleM tempRule = new RuleM((String) jsonTempRule.get("antecedent"), (String) jsonTempRule.get("consequent"));
            probabilityRules.add(tempRule);
        }
        for (int i = 0; i < jsonPriorityRules.size(); i++) {
            JSONObject jsonTempRule = (JSONObject) jsonPriorityRules.get(i);
            RuleM tempRule = new RuleM((String) jsonTempRule.get("antecedent"), (String) jsonTempRule.get("consequent"));
            priorityRules.add(tempRule);
        }
        Rules rules = new Rules(timeRules, probabilityRules, priorityRules);
        return rules;
    }

    @SuppressWarnings("unchecked")
    public static void rulesToJSON(String filePath, Rules rules) {
        JSONObject j = new JSONObject();
        JSONObject jsonRules = new JSONObject();
        JSONArray jsonProbabilityRules = new JSONArray();
        JSONArray jsonTimeRules = new JSONArray();
        JSONArray jsonPriorityRules = new JSONArray();
        for (RuleM rule : rules.getProbabilityRules()) {
            JSONObject tempRule = new JSONObject();
            tempRule.put("antecedent", rule.getAntecedent());
            tempRule.put("consequent", rule.getConsequent());
            jsonProbabilityRules.add(tempRule);
        }
        for (RuleM rule : rules.getTimeRules()) {
            JSONObject tempRule = new JSONObject();
            tempRule.put("antecedent", rule.getAntecedent());
            tempRule.put("consequent", rule.getConsequent());
            jsonTimeRules.add(tempRule);
        }
        for (RuleM rule : rules.getPriorityRules()) {
            JSONObject tempRule = new JSONObject();
            tempRule.put("antecedent", rule.getAntecedent());
            tempRule.put("consequent", rule.getConsequent());
            jsonPriorityRules.add(tempRule);
        }
        jsonRules.put("probability", jsonProbabilityRules);
        jsonRules.put("time", jsonTimeRules);
        jsonRules.put("priority", jsonPriorityRules);
        j.put("rules", jsonRules);
        try (FileWriter file = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            file.write(j.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
