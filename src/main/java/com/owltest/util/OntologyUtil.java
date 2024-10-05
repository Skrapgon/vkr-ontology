package com.owltest.util;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import openllet.owlapi.OpenlletReasoner;
import openllet.owlapi.OpenlletReasonerFactory;
import openllet.owlapi.SWRL;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.EntityType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassAssertionAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.SWRLAtom;
import org.semanticweb.owlapi.model.SWRLDArgument;
import org.semanticweb.owlapi.model.SWRLLiteralArgument;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.model.SWRLVariable;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.vocab.SWRLBuiltInsVocabulary;

import com.owltest.models.Edge;
import com.owltest.models.Factor;
import com.owltest.models.FactorsInfo;
import com.owltest.models.Graph;
import com.owltest.models.Route;
import com.owltest.models.RuleM;
import com.owltest.models.Rules;
import com.owltest.models.Vertex;

public class OntologyUtil {

    public static OWLOntology loadOntology(String path) throws OWLOntologyCreationException {
        File owlPath = new File(path);
        OWLOntologyManager om = OWLManager.createOWLOntologyManager();
        OWLOntology o = om.loadOntologyFromOntologyDocument(owlPath);
        return o;
    }

    public static void saveOntology(String path, OWLOntology o) throws OWLOntologyStorageException {
        File saveFile = new File(path);
        OWLOntologyManager om = o.getOWLOntologyManager();
        om.saveOntology(o, IRI.create(saveFile.toURI()));
    }

    public static void addGraph(OWLOntology o, Graph G) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();

        OWLClass Road = factory.getOWLClass(IRI.create(ontologyIRI + "#Дорога"));
        OWLClass Weather = factory.getOWLClass(IRI.create(ontologyIRI + "#Погода"));

        OWLDataProperty roadLength = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Протяженность_дорожного_покрытия"));
        OWLDataProperty surfaceType = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Тип_дорожного_покрытия"));

        OWLDataProperty weather = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Текущая_погода"));

        OWLObjectProperty weatherIs = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Стоит"));

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(o);

        ArrayList<Edge> E = G.getE();

        for (Edge e: E) {
            OWLNamedIndividual road = factory.getOWLNamedIndividual(ontologyIRI + "#Дорога" + e.getNumber());
            OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(Road, road);
            OWLDataPropertyAssertionAxiom roadLengthAssertion = factory.getOWLDataPropertyAssertionAxiom(roadLength, road, e.getLenght());
            OWLDataPropertyAssertionAxiom surfaceTypeAssertion = factory.getOWLDataPropertyAssertionAxiom(surfaceType, road, e.getSurfaceType());
            
            OWLNamedIndividual weatherT = null;
            NodeSet<OWLNamedIndividual> weatherInstances = reasoner.getInstances(Weather, false);
            for (OWLNamedIndividual i : weatherInstances.getFlattened()) {
                Set<OWLLiteral> weatherNames = reasoner.getDataPropertyValues(i, weather);
                String weatherName = weatherNames.iterator().next().getLiteral();
                if (e.getWeather().equals(weatherName)) weatherT = factory.getOWLNamedIndividual(ontologyIRI + "#" + i.getIRI().getFragment());
            }
            OWLObjectPropertyAssertionAxiom weatherAssetion = factory.getOWLObjectPropertyAssertionAxiom(weatherIs, road, weatherT);
            
            om.addAxiom(o, classAssertion);
            om.addAxiom(o, roadLengthAssertion);
            om.addAxiom(o, surfaceTypeAssertion);
            om.addAxiom(o, weatherAssetion);
        }
    }

    public static void addFactorsInfo(OWLOntology o, ArrayList<FactorsInfo> factorsInfo) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();

        for (FactorsInfo fi : factorsInfo) {
            OWLNamedIndividual road = factory.getOWLNamedIndividual(ontologyIRI + "#Дорога" + fi.getEdgeNumber());
            for (Factor f : fi.geFactors()) {
                OWLEntity entity = factory.getOWLEntity(EntityType.DATA_PROPERTY, IRI.create(ontologyIRI + "#" + f.getFactorName()));
                OWLAxiom declare = factory.getOWLDeclarationAxiom(entity);
                om.addAxiom(o, declare);
                OWLDataProperty fName = factory.getOWLDataProperty(ontologyIRI + "#" + f.getFactorName());
                OWLDataPropertyAssertionAxiom factorAssertion = factory.getOWLDataPropertyAssertionAxiom(fName, road, f.getFactorProbability());
                om.addAxiom(o, factorAssertion);
            }
        }
    }

    public static void addRoute(OWLOntology o, Route r, int i) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();

        OWLClass Car = factory.getOWLClass(IRI.create(ontologyIRI + "#Транспорт"));
        OWLClass Route = factory.getOWLClass(IRI.create(ontologyIRI + "#Маршрут"));
        OWLClass RouteInfo = factory.getOWLClass(IRI.create(ontologyIRI + "#Итоговая_информация_о_маршруте"));

        OWLDataProperty attached = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Прикреплен_к_маршруту"));

        OWLObjectProperty goes = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Едет"));
        OWLObjectProperty routeInfo = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Информация_о_маршруте"));
        OWLObjectProperty consists = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Состоит_из"));

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(o);

        OWLNamedIndividual route = factory.getOWLNamedIndividual(ontologyIRI + "#Маршрут" + i);
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(Route, route);
        om.addAxiom(o, classAssertion);
        for (Edge en : r.getEdges()) {
            OWLNamedIndividual road = factory.getOWLNamedIndividual(ontologyIRI + "#Дорога" + en.getNumber());
            OWLObjectPropertyAssertionAxiom consistsAxiom = factory.getOWLObjectPropertyAssertionAxiom(consists, route, road);
            om.addAxiom(o, consistsAxiom);
        }
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(Car, false);
        for (OWLNamedIndividual in : instances.getFlattened()) {
            Set<OWLLiteral> routesT = reasoner.getDataPropertyValues(in, attached);
            if (routesT.isEmpty()) {
                OWLNamedIndividual ri = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#Информация_о_маршруте" + i + in.getIRI().getFragment())); 
                OWLClassAssertionAxiom riClassAssertion = factory.getOWLClassAssertionAxiom(RouteInfo, ri);
                om.addAxiom(o, riClassAssertion);
                OWLObjectPropertyAssertionAxiom goesAssertion = factory.getOWLObjectPropertyAssertionAxiom(goes, ri, in);
                om.addAxiom(o, goesAssertion);
                OWLObjectPropertyAssertionAxiom infoAssertion = factory.getOWLObjectPropertyAssertionAxiom(routeInfo, route, ri);
                om.addAxiom(o, infoAssertion);
            }
            else {
                for (OWLLiteral rn : routesT) {
                    if (rn.getLiteral().equals(String.valueOf(i))) {
                        OWLNamedIndividual ri = factory.getOWLNamedIndividual(IRI.create(ontologyIRI + "#Информация_о_маршруте" + i + in.getIRI().getFragment())); 
                        OWLClassAssertionAxiom riClassAssertion = factory.getOWLClassAssertionAxiom(RouteInfo, ri);
                        om.addAxiom(o, riClassAssertion);
                        OWLObjectPropertyAssertionAxiom goesAssertion = factory.getOWLObjectPropertyAssertionAxiom(goes, ri, in);
                        om.addAxiom(o, goesAssertion);
                        OWLObjectPropertyAssertionAxiom infoAssertion = factory.getOWLObjectPropertyAssertionAxiom(routeInfo, route, ri);
                        om.addAxiom(o, infoAssertion);
                    }
                }
            }
        }
    }

    public static void addBrigade(OWLOntology o, Vertex b) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();

        OWLClass Consumer = factory.getOWLClass(IRI.create(ontologyIRI + "#Потребитель"));

        OWLDataProperty xCoordCons = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Координата_x_бригады"));
        OWLDataProperty yCoordCons = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Координата_y_бригады"));

        OWLNamedIndividual brigade = factory.getOWLNamedIndividual(ontologyIRI + "#" + b.getName());
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(Consumer, brigade);
        om.addAxiom(o, classAssertion);
        OWLDataPropertyAssertionAxiom xAssertion = factory.getOWLDataPropertyAssertionAxiom(xCoordCons, brigade, b.getX());
        om.addAxiom(o, xAssertion);
        OWLDataPropertyAssertionAxiom yAssertion = factory.getOWLDataPropertyAssertionAxiom(yCoordCons, brigade, b.getY());
        om.addAxiom(o, yAssertion);
    }

    public static void addWarehouse(OWLOntology o, Vertex w) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();

        OWLClass Warehouse = factory.getOWLClass(IRI.create(ontologyIRI + "#Склад"));
        OWLClass Route = factory.getOWLClass(IRI.create(ontologyIRI + "#Маршрут"));

        OWLDataProperty xCoordWare = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Координата_x_склада"));
        OWLDataProperty yCoordWare = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Координата_y_склада"));

        OWLObjectProperty foundsOnRoute = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Встречается_на_маршрутах"));

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(o);

        OWLNamedIndividual warehouse = factory.getOWLNamedIndividual(ontologyIRI + "#" + w.getName());
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(Warehouse, warehouse);
        om.addAxiom(o, classAssertion);
        OWLDataPropertyAssertionAxiom xAssertion = factory.getOWLDataPropertyAssertionAxiom(xCoordWare, warehouse, w.getX());
        om.addAxiom(o, xAssertion);
        OWLDataPropertyAssertionAxiom yAssertion = factory.getOWLDataPropertyAssertionAxiom(yCoordWare, warehouse, w.getY());
        om.addAxiom(o, yAssertion);
        NodeSet<OWLNamedIndividual> instances = reasoner.getInstances(Route, false);
        for (OWLNamedIndividual in : instances.getFlattened()) {
            for (int i = 0; i < w.getRoutes().size(); i++) {
                if (in.getIRI().getFragment().contains("Маршрут"+w.getRoutes().get(i))) {
                    OWLObjectPropertyAssertionAxiom foundsOnAxiom = factory.getOWLObjectPropertyAssertionAxiom(foundsOnRoute, warehouse, in);
                    om.addAxiom(o, foundsOnAxiom);
                    break;
                }
            }
        }
    }

    public static void addRailstation(OWLOntology o, Vertex r) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();

        OWLClass Railstation = factory.getOWLClass(IRI.create(ontologyIRI + "#Железнодорожная_станция"));

        OWLDataProperty xCoordRail = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Координата_x_ЖДстанции"));
        OWLDataProperty yCoordRail = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Координата_y_ЖДстанции"));

        OWLNamedIndividual railstation = factory.getOWLNamedIndividual(ontologyIRI + "#" + r.getName());
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(Railstation, railstation);
        om.addAxiom(o, classAssertion);
        OWLDataPropertyAssertionAxiom xAssertion = factory.getOWLDataPropertyAssertionAxiom(xCoordRail, railstation, r.getX());
        om.addAxiom(o, xAssertion);
        OWLDataPropertyAssertionAxiom yAssertion = factory.getOWLDataPropertyAssertionAxiom(yCoordRail, railstation, r.getY());
        om.addAxiom(o, yAssertion);
    }

    public static void addAllToOntology(OWLOntology o, Graph G, ArrayList<FactorsInfo> factorsInfo, ArrayList<Route> routes, ArrayList<Vertex> brigades, ArrayList<Vertex> warehouses, ArrayList<Vertex> railstations) {
        addGraph(o, G);
        addFactorsInfo(o, factorsInfo);
        for (int i = 0; i < routes.size(); i++) addRoute(o, routes.get(i), i);
        for (Vertex b : brigades) addBrigade(o, b);
        for (Vertex w : warehouses) addWarehouse(o, w);
        for (Vertex r : railstations) addRailstation(o, r);
    }

    public static void clearOntology(OWLOntology o) {

    }

    public static SWRLRule createRule(RuleM pr, OWLOntology o) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();
        ArrayList<OWLClass> owlclasses = new ArrayList<OWLClass>();
        o.classesInSignature().forEach(owlclasses::add);
        ArrayList<String> classes = new ArrayList<String>();
        for (OWLClass c : owlclasses) classes.add(c.getIRI().getFragment());
        
        ArrayList<OWLDataProperty> owldataproperties = new ArrayList<OWLDataProperty>();
        o.dataPropertiesInSignature().forEach(owldataproperties::add);
        ArrayList<String> dataproperties = new ArrayList<String>();
        for (OWLDataProperty d : owldataproperties) dataproperties.add(d.getIRI().getFragment());
        
        ArrayList<OWLObjectProperty> owlobjproperties = new ArrayList<OWLObjectProperty>();
        o.objectPropertiesInSignature().forEach(owlobjproperties::add);
        ArrayList<String> objproperties = new ArrayList<String>();
        for (OWLObjectProperty ob : owlobjproperties) objproperties.add(ob.getIRI().getFragment());

        String[] bodyAtoms = pr.getAntecedent().substring(0, pr.getAntecedent().length()-1).split("\\)\\,\\ ");
        String headAtom = pr.getConsequent().substring(0, pr.getConsequent().length()-1).split("\\(")[0];
        String[] headVarib = pr.getConsequent().split("\\(")[1].split("\\)")[0].split("\\,\\ ");
        Set<SWRLAtom> swrlbodyatoms = new HashSet<>();
        Set<SWRLAtom> swrlheadatoms = new HashSet<>();
        for (String ba : bodyAtoms) {
            String[] expr = ba.split("\\(");
            String atom = expr[0];
            String[] varib = expr[1].split("\\,\\ ");
            SWRLAtom a;
            if (classes.contains(atom)) {
                SWRLVariable r = SWRL.variable(IRI.create(ontologyIRI + "#" + varib[0].substring(1)));
                a = SWRL.classAtom(factory.getOWLClass(IRI.create(ontologyIRI + "#" + atom)), r);
            }
            else if (dataproperties.contains(atom)) {
                OWLDataProperty dataProperty = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + atom));
                SWRLVariable r = SWRL.variable(IRI.create(ontologyIRI + "#" + varib[0].substring(1)));
                SWRLVariable p = SWRL.variable(IRI.create(ontologyIRI + "#" + varib[1].substring(1)));
                a = SWRL.propertyAtom(dataProperty, r, p);
            }
            else if (objproperties.contains(atom)) {
                OWLObjectProperty objectProperty = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#" + atom));
                SWRLVariable r = SWRL.variable(IRI.create(ontologyIRI + "#" + varib[0].substring(1)));
                SWRLVariable p = SWRL.variable(IRI.create(ontologyIRI + "#" + varib[1].substring(1)));
                a = SWRL.propertyAtom(objectProperty, r, p);
            }
            else {
                ArrayList<SWRLDArgument> arg = new ArrayList<SWRLDArgument>();
                for (String var : varib) {
                    if (var.contains("?")) {
                        SWRLVariable v = SWRL.variable(IRI.create(ontologyIRI + "#" + var.substring(1)));
                        arg.add(v);
                    }
                    else {
                        String temp;
                        if (var.contains("\"")) temp = var.split("\\\"")[1];
                        else temp = var;
                        try {
                            double t = Double.parseDouble(temp);
                            arg.add(SWRL.constant(t));
                          } catch(NumberFormatException e){
                            arg.add(SWRL.constant(temp));
                          }                
                    }
                }
                a = SWRL.builtIn(SWRLBuiltInsVocabulary.getBuiltIn(IRI.create("http://www.w3.org/2003/11/swrlb#" + atom)), arg); 
            }
            swrlbodyatoms.add(a);
        }
        OWLDataProperty headProp = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#" + headAtom));
        SWRLVariable a1;
        SWRLAtom a;
        a1 = SWRL.variable(IRI.create(ontologyIRI + "#" + headVarib[0].substring(1)));
        if (headVarib[1].contains("?")) {
            SWRLVariable a2 = SWRL.variable(IRI.create(ontologyIRI + "#" + headVarib[1].substring(1)));
            a = SWRL.propertyAtom(headProp, a1, a2);
        }
        else {
            if (headVarib[1].contains("?")) {
                SWRLVariable a2 = SWRL.variable(IRI.create(ontologyIRI + "#" + headVarib[1].substring(1)));
                a = SWRL.propertyAtom(headProp, a1, a2);
            }
            else{
                String var;
                if (headVarib[1].contains("\"")) var = headVarib[1].split("\\\"")[1];
                else var = headVarib[1];
                SWRLLiteralArgument a2 = SWRL.constant(var);
                a = SWRL.propertyAtom(headProp, a1, a2);
            }
        }
        swrlheadatoms.add(a);

        SWRLRule rule = SWRL.rule(swrlbodyatoms, swrlheadatoms);
        return rule;
    }

    public static ArrayList<SWRLRule> createRules(ArrayList<RuleM> rules, OWLOntology o) {
        ArrayList<SWRLRule> rulesA = new ArrayList<SWRLRule>();
        for (RuleM rule : rules) rulesA.add(createRule(rule, o));
        return rulesA;
    }

    public static void excecuteRules(OWLOntology o) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(o);

        reasoner.flush(); // Очищаем все, что хранилось у ризонера в буфере, дабы он использовал новые правила
        reasoner.prepareReasoner(); // Подготавливаем ризонер для исполнения
        reasoner.getKB().realize(); // Запускаем ризонер

        InferredOntologyGenerator generator = new InferredOntologyGenerator(reasoner); // Сохрянем все новые аксиомы, которые вывели ризонер
        generator.fillOntology(factory, o); // Добавляем выведенные аксиомы в онтологию

    }
    
    public static void processOntology(OWLOntology o, Rules rules) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();
        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(o);

        OWLClass Route = factory.getOWLClass(IRI.create(ontologyIRI + "#Маршрут"));
        OWLClass Warehouse = factory.getOWLClass(IRI.create(ontologyIRI + "#Склад"));
        OWLClass Recomendation = factory.getOWLClass(ontologyIRI + "#Рекомендация");

        OWLDataProperty routePassageTime  = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Время_прохождения_маршрута"));
        OWLDataProperty routeSafety = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Безопасность_маршрута"));
        OWLDataProperty routePriority = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Приоритет_маршрута"));

        OWLDataProperty roadPassageTime = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Время_прохождения_участка"));
        OWLDataProperty roadSafety = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Вероятность_безопасного_проезда"));

        OWLDataProperty warehousePriority = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Приоритет_склада"));

        OWLObjectProperty routeInfo = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Информация_о_маршруте"));
        OWLObjectProperty consists = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Состоит_из"));
        OWLObjectProperty foundsOnRoute = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Встречается_на_маршрутах"));
        OWLObjectProperty bestWarehouse = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Лучшее_расположение_склада"));

        ArrayList<SWRLRule> rulesProb = OntologyUtil.createRules(rules.getProbabilityRules(), o);
        for (SWRLRule r : rulesProb) o.addAxiom(r); // Добавляем правила в онтологию

        ArrayList<SWRLRule> rulesT = OntologyUtil.createRules(rules.getTimeRules(), o);
        for (SWRLRule r : rulesT) o.addAxiom(r); // Добавляем правила в онтологию

        excecuteRules(o); // Выполняем добавленные в онтологию правила

        reasoner.flush(); // обновляем ризонер

        for (SWRLRule r : rulesProb) o.remove(r); // Удаляем из онтологии правила
        for (SWRLRule r : rulesT) o.remove(r); // Удаляем из онтологии правила

        NodeSet<OWLNamedIndividual> routes = reasoner.getInstances(Route);
        for (OWLNamedIndividual route : routes.getFlattened()) {
            NodeSet<OWLNamedIndividual> routeInfos = reasoner.getObjectPropertyValues(route, routeInfo);
            for (OWLNamedIndividual ri : routeInfos.getFlattened()) {
                double totalTime = 0;
                Set<OWLLiteral> rtimes = reasoner.getDataPropertyValues(ri, roadPassageTime);
                for (OWLLiteral rtime : rtimes) totalTime += Double.valueOf(rtime.getLiteral()); // Вычисляем общее время прохождения маршрута
                OWLDataPropertyAssertionAxiom routeTimeAssertion = factory.getOWLDataPropertyAssertionAxiom(routePassageTime, ri, totalTime);
                om.addAxiom(o, routeTimeAssertion);
            }
            NodeSet<OWLNamedIndividual> roads = reasoner.getObjectPropertyValues(route, consists);
            String[] safety = new String[] {"Очень низкая", "Низкая", "Средняя", "Высокая", "Очень высокая"};
            int totalCount = 0;
            int summSafety = 0;
            for (OWLNamedIndividual road : roads.getFlattened()) {
                String roadRisk = reasoner.getDataPropertyValues(road, roadSafety).iterator().next().getLiteral();
                for (int i = 0; i < safety.length; i++) {
                    if (safety[i].equals(roadRisk)) {
                        totalCount++;
                        summSafety += i;
                    }
                }
            }
            int routeRisk = summSafety/totalCount; // Определяем среднюю безопасность маршрута
            OWLDataPropertyAssertionAxiom routeSafetyAssertion = factory.getOWLDataPropertyAssertionAxiom(routeSafety, route, safety[routeRisk]);
            om.addAxiom(o, routeSafetyAssertion);
        }

        ArrayList<SWRLRule> rulesPrior = OntologyUtil.createRules(rules.getPriorityRules(), o);

        for (SWRLRule r : rulesPrior) o.addAxiom(r); // Добавляем правила в онтологию

        excecuteRules(o); // Выполняем добавленные в онтологию правила

        for (SWRLRule r : rulesPrior) o.remove(r); // Удаляем из онтологии правила

        reasoner.flush(); // Очищаем все, что хранилось у ризонера в буфере, чтобы  получать выведенные факты о приоритетах маршрутов

        NodeSet<OWLNamedIndividual> warehouses = reasoner.getInstances(Warehouse);
        for (OWLNamedIndividual warehouse : warehouses.getFlattened()) {
            String[] priority = new String[] {"Очень низкий", "Низкий", "Средний", "Высокий", "Очень высокий"};
            int totalCount = 0;
            int summPriority = 0;
            NodeSet<OWLNamedIndividual> routesT = reasoner.getObjectPropertyValues(warehouse, foundsOnRoute);
            for (OWLNamedIndividual rt : routesT.getFlattened()) {
                NodeSet<OWLNamedIndividual> routesInfoT = reasoner.getObjectPropertyValues(rt, routeInfo);
                for (OWLNamedIndividual rit : routesInfoT.getFlattened()) {
                    Set<OWLLiteral> routePriorityT = reasoner.getDataPropertyValues(rit, routePriority);
                    String rpt = routePriorityT.iterator().next().getLiteral();
                    for (int i = 0; i < priority.length; i++) {
                        if (priority[i].equals(rpt)) {
                            totalCount++;
                            summPriority += i;
                        }
                    }
                }
            }
            int warehousePrior = summPriority/totalCount; // Определяе приоритетность склада
            OWLDataPropertyAssertionAxiom warehouseSafetyAssertion = factory.getOWLDataPropertyAssertionAxiom(warehousePriority, warehouse, priority[warehousePrior]);
            om.addAxiom(o, warehouseSafetyAssertion);
        }

        reasoner.flush(); // Очищаем все, что хранилось у ризонера в буфере, чтобы  получать информацию о приоритетах складов

        ArrayList<OWLNamedIndividual> resW = new ArrayList<OWLNamedIndividual>();
        int bestPrioir = 0;
        for (OWLNamedIndividual warehouse : warehouses.getFlattened()) { // Выбираем из всех складов те, у которых наилучший приоритет для упрощения поиска рекомендаций в онтологии
            ArrayList<String> priority = new ArrayList<String>();
            priority.add("Очень низкий");
            priority.add("Низкий");
            priority.add("Средний");
            priority.add("Высокий");
            priority.add("Очень Высокий");
            Set<OWLLiteral> warehousePriorityT = reasoner.getDataPropertyValues(warehouse, warehousePriority);
            String wpt = warehousePriorityT.iterator().next().getLiteral();
            if (priority.indexOf(wpt) > bestPrioir) {
                bestPrioir = priority.indexOf(wpt);
                while (resW.size() != 0) resW.remove(0);
            }
            if (priority.indexOf(wpt) == bestPrioir) resW.add(warehouse);
        }

        OWLNamedIndividual recomendation = factory.getOWLNamedIndividual(ontologyIRI + "#Рекомендация1");
        OWLClassAssertionAxiom classAssertion = factory.getOWLClassAssertionAxiom(Recomendation, recomendation);
        om.addAxiom(o, classAssertion);
        for (OWLNamedIndividual w : resW) {
            OWLObjectPropertyAssertionAxiom bestAssertion = factory.getOWLObjectPropertyAssertionAxiom(bestWarehouse, recomendation, w);
            om.addAxiom(o, bestAssertion);
        }
    }

    public static String[] getBestWarehouses(OWLOntology o) {
        OWLOntologyManager om = o.getOWLOntologyManager();
        OWLDataFactory factory = om.getOWLDataFactory();
        String ontologyIRI = o.getOntologyID().getOntologyIRI().get().toString();
        OWLClass Recomendation = factory.getOWLClass(ontologyIRI + "#Рекомендация");
        OWLDataProperty warehousePriority = factory.getOWLDataProperty(IRI.create(ontologyIRI + "#Приоритет_склада"));
        OWLObjectProperty bestWarehouse = factory.getOWLObjectProperty(IRI.create(ontologyIRI + "#Лучшее_расположение_склада"));

        OpenlletReasoner reasoner = OpenlletReasonerFactory.getInstance().createReasoner(o);

        reasoner.flush();

        String[] res = new String[] {"", ""};

        NodeSet<OWLNamedIndividual> recomendations = reasoner.getInstances(Recomendation);
        for (OWLNamedIndividual rec : recomendations.getFlattened()) {
            NodeSet<OWLNamedIndividual> warehouses = reasoner.getObjectPropertyValues(rec, bestWarehouse);
            for (OWLNamedIndividual w : warehouses.getFlattened()) {
                Set<OWLLiteral> priorT = reasoner.getDataPropertyValues(w, warehousePriority);
                String p = priorT.iterator().next().getLiteral();
                res[0] = p; // Запоминаем приоритет рекомендуемых для выбора складов
                res[1] = res[1] + ", " + w.getIRI().getFragment(); // Запоминаем названия рекомендуемых складов
            }
        }

        return res;
    }
}