package com.owltest.util;

import java.util.ArrayList;

import com.owltest.models.Edge;
import com.owltest.models.Graph;
import com.owltest.models.Route;
import com.owltest.models.Vertex;

public class FindingShortestPath {
	private static void findPaths(Graph G, Vertex root, Vertex destination, ArrayList<Route> result, ArrayList<Edge> tempPath, boolean[] check, double tempLength, double percentage, int reqVertex) {
        check[G.getV().indexOf(root)] = true; // Помечаем вершину посещенной
        for (Edge e : G.getE()) {
            Vertex v = null;
            boolean overlap = false;
            if (e.getStartV() == root) v = e.getEndV(); // определение противоположной в ребре e вершины вершине root
            else if (e.getEndV() == root) v = e.getStartV(); // определение противоположной в ребре e вершины вершине root
            if (v != null) {
                if (v == destination) overlap = true; // отметка о достижении требуемой вершины
                tempPath.add(e);
                if (!overlap && !check[G.getV().indexOf(v)]) findPaths(G, v, destination, result, tempPath, check, tempLength+e.getLenght(), percentage, reqVertex);
                else if (overlap) { // если путь построен до требуемой вершины, проверяем
                    boolean cont = false;
                    for (Edge k : tempPath) if (k.getStartV().getNumber() == reqVertex || k.getEndV().getNumber() == reqVertex) cont = true; // проверка вхождения вершины с номером reqVertex в постренный путь
                    if (cont) { // если путь содержит в себе требуемую вершину (расположение склада) проверяем данный путь дальше
                        ArrayList<Edge> path = new ArrayList<Edge>();
                        for (Edge i : tempPath) path.add(i);
                        if (result.size() == 0) { // если результирующий массив пуст, добавляем путь в массив
                            Route tempRoute = new Route(path, tempLength+e.getLenght());
                            result.add(tempRoute);
                        }
                        else {
                            if (result.get(0).getRouteLength() > tempLength+e.getLenght()) { // если найден более короткий путь помещаем его в самое начало
                                Route tempRoute = new Route(path, tempLength+e.getLenght());
                                result.add(0, tempRoute);
                            }
                            else if (result.get(0).getRouteLength()*(1+percentage/100) >= tempLength+e.getLenght()) { // если найден субпотимальный путь добавляем его в конец
                                Route tempRoute = new Route(path, tempLength+e.getLenght());
                                result.add(tempRoute);
                            }
                        }
                    }
                }
                tempPath.remove(tempPath.size()-1);
            }
        }
        check[G.getV().indexOf(root)] = false; // Забываем, что посещали вершину, т.к. может существовать другой путь через нее
    }
	

    public static ArrayList<Route> setupFindPaths(Graph G, Vertex root, Vertex destination, double percentage, int reqVertex) {
        ArrayList<Route> tempResult = new ArrayList<Route>();
        ArrayList<Edge> tempPath = new ArrayList<Edge>();
        boolean[] check = new boolean[G.getV().size()];
        for (int i = 0; i < check.length; i++) check[i] = false;
        findPaths(G, root, destination, tempResult, tempPath, check, 0, percentage, reqVertex);
        ArrayList<Route> result = new ArrayList<Route>();
        result.add(tempResult.get(0));
        for (int i = 1; i < tempResult.size(); i++) { // Отбрасываем пути, которые превосходят по длине оптимальные больше чем на percentage%
            if (tempResult.get(i).getRouteLength() <= result.get(0).getRouteLength()*(1+percentage/100)) result.add(tempResult.get(i));
        }
        return result;
    }
}