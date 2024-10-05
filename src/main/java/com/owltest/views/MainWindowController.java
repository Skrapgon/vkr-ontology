package com.owltest.views;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.parser.ParseException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import com.owltest.models.Edge;
import com.owltest.models.FactorsInfo;
import com.owltest.models.Graph;
import com.owltest.models.Route;
import com.owltest.models.Rules;
import com.owltest.models.Vertex;
import com.owltest.util.CalculationUtil;
import com.owltest.util.DrawerUtil;
import com.owltest.util.FindingShortestPath;
import com.owltest.util.JSON;
import com.owltest.util.OntologyUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class MainWindowController {

    @FXML
    private ComboBox<Integer> percentageCont;
    @FXML
    private Button graphLoading;
    @FXML
    private Button factorsLoading;
    @FXML
    private Button carsLoading;
    @FXML
    private Button rulesLoading;
    @FXML
    private Button routeFinding;
    @FXML
    private Button priorityProcessing;
    @FXML
    private Button ontologySaving;
    @FXML
    private AnchorPane nodeEditingPanel;
    @FXML
    private Canvas canvas;
    @FXML
    private ListView<String> brigadeList;
    @FXML
    private ListView<String> railwayList;
    @FXML
    private ListView<String> warehouseList;
    @FXML
    private ComboBox<String> nodeTypes;
    @FXML
    private ComboBox<String> nodeNames;
    @FXML
    private Button nodePropertiesSaving;
    @FXML
    private Button closeEditingPanelButton;
    @FXML
    private Label bestWarehouses;
    @FXML
    private Label bestPrioriry;

    private Stage stage;

    private String[] typesItems = new String[] {"Бригада", "Склад", "ЖД станция"};
    private String[] brigadeItems = new String[] {"Бригада1", "Бригада2", "Бригада3"};
    private String[] warehouseItems = new String[] {"Склад1", "Склад2"};
    private String[] railwayItems = new String[] {"ЖДстанция1"};
    private Graph G;
    private ArrayList<FactorsInfo> FI;
    private ArrayList<Route> routes;
    private Rules rules;
    private DrawerUtil drawer;
    private ObservableList<String> brigadeListNames = FXCollections.observableArrayList();
    private ObservableList<String> warehouseListNames = FXCollections.observableArrayList();
    private ObservableList<String> railwayListNames = FXCollections.observableArrayList();
    private ArrayList<Vertex> brigadeListArrayTemp = new ArrayList<Vertex>();
    private ArrayList<Vertex> warehouseListArrayTemp = new ArrayList<Vertex>();
    private ArrayList<Vertex> railwayListArrayTemp = new ArrayList<Vertex>();
    private Vertex vTemp;

    private OWLOntology o;

    @FXML
    private void initialize() {
        brigadeList.setItems(brigadeListNames);
        warehouseList.setItems(warehouseListNames);
        railwayList.setItems(railwayListNames);
        nodeTypes.getItems().addAll(typesItems);
        drawer = new DrawerUtil(canvas);
        for (int i = 0; i <= 100; i++) this.percentageCont.getItems().add(i);
        this.percentageCont.getSelectionModel().select(0);
        canvas.requestFocus();
        drawFigures();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    // public void setData(String[] typesItems, String[] brigadeItems, String[] warehouseItems, String[] railwayItems, Graph G, ArrayList<FactorsInfo> FI, ArrayList<Route> routes, ArrayList<Vertex> brigadeListArray, ArrayList<Vertex> railwayListArray, ArrayList<Vertex> warehouseListArray, OWLOntology o, Rules rules) {
    //     for (int i = 0; i <= 100; i++) this.percentageCont.getItems().add(i);
    //     this.percentageCont.getSelectionModel().select(0);
    //     canvas.requestFocus();
    //     drawFigures();
    // }
    
    @FXML
    public void handleShowNodeEditMenu(MouseEvent event) {

        int xTemp = CalculationUtil.transferDoubleToInteger(event.getX());
        int yTemp = CalculationUtil.transferDoubleToInteger(event.getY());

        if (G != null) {
            vTemp = CalculationUtil.isInVertexes(G.getV(), new Vertex(xTemp, yTemp, -1));
            if (vTemp != null) {
                Vertex brigadeTemp = CalculationUtil.isInVertexes(brigadeListArrayTemp, vTemp);
                Vertex railwayTemp = CalculationUtil.isInVertexes(railwayListArrayTemp, vTemp);
                Vertex warehouseTemp = CalculationUtil.isInVertexes(warehouseListArrayTemp, vTemp);

                if (brigadeTemp != null) {
                    nodeTypes.getSelectionModel().select(brigadeTemp.getType());
                    nodeNames.getSelectionModel().select(brigadeTemp.getName());
                }
                else if (railwayTemp != null) {
                    nodeTypes.getSelectionModel().select(railwayTemp.getType());
                    nodeNames.getSelectionModel().select(railwayTemp.getName());
                }
                else if (warehouseTemp != null) {
                    nodeTypes.getSelectionModel().select(warehouseTemp.getType());
                    nodeNames.getSelectionModel().select(warehouseTemp.getName());
                }
                else {
                    nodeNames.getItems().removeAll(brigadeItems);
                    nodeNames.getItems().removeAll(railwayItems);
                    nodeNames.getItems().removeAll(warehouseItems);
                    nodeTypes.getSelectionModel().select(-1);
                }
                nodeEditingPanel.setVisible(true);
            }
        }
    }

    @FXML
    public void handleChangeType() {
        if (nodeTypes.getSelectionModel().getSelectedIndex() != -1) {
            if (nodeTypes.getSelectionModel().getSelectedItem().equals(typesItems[0])) {
                nodeNames.getItems().removeAll(railwayItems);
                nodeNames.getItems().removeAll(warehouseItems);
                nodeNames.getItems().addAll(brigadeItems);
            }
            else if (nodeTypes.getSelectionModel().getSelectedItem().equals(typesItems[1])) {
                nodeNames.getItems().removeAll(brigadeItems);
                nodeNames.getItems().removeAll(railwayItems);
                nodeNames.getItems().addAll(warehouseItems);
            }
            else if (nodeTypes.getSelectionModel().getSelectedItem().equals(typesItems[2])) {
                nodeNames.getItems().removeAll(brigadeItems);
                nodeNames.getItems().removeAll(warehouseItems);
                nodeNames.getItems().addAll(railwayItems);
            }
        }
    }

    @FXML
    public void handleSaveNodeProperties() {
        if (nodeTypes.getSelectionModel().getSelectedIndex() != -1 && nodeNames.getSelectionModel().getSelectedIndex() != -1) {
            vTemp.setType(nodeTypes.getSelectionModel().getSelectedItem());
            vTemp.setName(nodeNames.getSelectionModel().getSelectedItem());

            ArrayList<Vertex> arr1, arr2, arr3;
            ObservableList<String> list1, list2, list3;
            
            if (vTemp.getType().equals(typesItems[0])) {
                arr1 = brigadeListArrayTemp;
                arr2 = warehouseListArrayTemp;
                arr3 = railwayListArrayTemp;
                list1 = brigadeListNames;
                list2 = warehouseListNames;
                list3 = railwayListNames;
            }
            else if (vTemp.getType().equals(typesItems[1])) {
                arr1 = warehouseListArrayTemp;
                arr2 = brigadeListArrayTemp;
                arr3 = railwayListArrayTemp;
                list1 = warehouseListNames;
                list2 = brigadeListNames;
                list3 = railwayListNames;
            }
            else {
                arr1 = railwayListArrayTemp;
                arr2 = brigadeListArrayTemp;
                arr3 = warehouseListArrayTemp;
                list1 = railwayListNames;
                list2 = brigadeListNames;
                list3 = warehouseListNames;
            }

            boolean exist = false;
            Vertex t = CalculationUtil.isInVertexes(G.getV(), vTemp);
            for (int i = 0; i < arr1.size(); i++) {
                if (arr1.get(i).getName().equals(vTemp.getName())) {
                    arr1.set(i, t);
                    exist = true;
                }
                else if (CalculationUtil.equals(arr1.get(i).getX(), arr1.get(i).getY(), vTemp.getX(), vTemp.getY())) {
                    arr1.remove(i);
                    list1.remove(i);
                }
            }
            for (int i = 0; i < arr2.size(); i++) {
                if (CalculationUtil.equals(arr2.get(i).getX(), arr2.get(i).getY(), vTemp.getX(), vTemp.getY())) {
                    arr2.remove(i);
                    list2.remove(i);
                    break;
                }
            }
            for (int i = 0; i < arr3.size(); i++) {
                if (CalculationUtil.equals(arr3.get(i).getX(), arr3.get(i).getY(), vTemp.getX(), vTemp.getY())) {
                    arr3.remove(i);
                    list3.remove(i);
                    break;
                }
            }
            if (!exist) {
                arr1.add(vTemp);
                list1.add(vTemp.getName());
            }

            System.gc();

            bestWarehouses.setText("");
            bestPrioriry.setText("");
            routes = null;

            canvas.requestFocus();
            drawFigures();
        }
    }

    @FXML
    public void handleCloseNodeEditMenu() {
        nodeTypes.getSelectionModel().select(-1);
        nodeNames.getItems().removeAll(railwayItems);
        nodeNames.getItems().removeAll(brigadeItems);
        nodeNames.getItems().removeAll(warehouseItems);
        nodeEditingPanel.setVisible(false);
    }

    @FXML
    public void handleLoadGraph() throws FileNotFoundException, IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        String selectedFile = fileChooser.showOpenDialog(this.stage).getAbsolutePath();
        this.G = JSON.graphFromJSON(selectedFile);
        while (brigadeListArrayTemp.size() != 0) brigadeListArrayTemp.remove(0);
        while (warehouseListArrayTemp.size() != 0) warehouseListArrayTemp.remove(0);
        while (railwayListArrayTemp.size() != 0) railwayListArrayTemp.remove(0);
        this.FI = null;
        brigadeListNames.removeAll(brigadeItems);
        warehouseListNames.removeAll(warehouseItems);
        railwayListNames.removeAll(railwayItems);
        bestWarehouses.setText("");
        bestPrioriry.setText("");
        canvas.requestFocus();
        drawFigures();
    }

    @FXML
    public void handleLoadFactors() throws FileNotFoundException, IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        String selectedFile = fileChooser.showOpenDialog(this.stage).getAbsolutePath();
        this.FI = JSON.factorsInfoFromJSON(selectedFile);
        bestWarehouses.setText("");
        bestPrioriry.setText("");
    }

    @FXML
    public void handleLoadOntology() throws OWLOntologyCreationException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Ontology Files", "*.owl", "*.owx", "*.rdf"));
        String selectedFile = fileChooser.showOpenDialog(this.stage).getAbsolutePath();
        this.o = OntologyUtil.loadOntology(selectedFile);
        bestWarehouses.setText("");
        bestPrioriry.setText("");
    }

    @FXML
    public void handleLoadRules() throws FileNotFoundException, IOException, ParseException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JSON Files", "*.json"));
        String selectedFile = fileChooser.showOpenDialog(this.stage).getAbsolutePath();
        this.rules = JSON.rulesFromJSON(selectedFile);
        bestWarehouses.setText("");
        bestPrioriry.setText("");
    }

    @FXML
    public void handleFindRoutes() {
        if (G != null && brigadeListArrayTemp.size() == 3 && warehouseListArrayTemp.size() == 2 && railwayListArrayTemp.size() == 1) {
            routes = new ArrayList<Route>();
            for (int k = 0; k < warehouseListArrayTemp.size(); k++) {
                int offset;
                if (routes == null) offset = 0;
                else offset = routes.size();
                int tempSize = 0;
                for (int j = 0; j < railwayListArrayTemp.size(); j++) {
                    for (int i = 0; i < brigadeListArrayTemp.size(); i++) {
                        ArrayList<Route> tempRoutes = FindingShortestPath.setupFindPaths(G, brigadeListArrayTemp.get(i), railwayListArrayTemp.get(j), percentageCont.getSelectionModel().getSelectedItem(), warehouseListArrayTemp.get(k).getNumber());
                        routes.addAll(tempRoutes);
                        tempSize += tempRoutes.size();
                    }
                }
                warehouseListArrayTemp.get(k).setRoutes(tempSize, offset);
            }
            
            canvas.requestFocus();
            drawFigures();
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Необходимые исходные данные отсутствуют");
            alert.setContentText("Для поиска маршрутов загрузите граф дорожной сети\nи укажите местоположения для 3 бригад, 2 складов\nи 1 ЖД станции");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleProcessOntology() {
        if (o != null && routes != null && FI != null && rules != null) {
            OntologyUtil.clearOntology(o);
            OntologyUtil.addAllToOntology(o, G, FI, routes, brigadeListArrayTemp, warehouseListArrayTemp, railwayListArrayTemp);
            OntologyUtil.processOntology(o, rules);
            String[] best = OntologyUtil.getBestWarehouses(o);
            bestWarehouses.setText(best[1]);
            bestPrioriry.setText(best[0]);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Необходимые для обработки данные отсутствуют");
            alert.setContentText("Для определения приоритетного расположения склада необходимо загрузить граф дорожной сети, указать расположение бригад, складов и жд станций, а также загрузить онтологию с транспортом и грузами, загрузить негативные факторы, которые осложняют передвижение по дорогам и загрузить правила вывода приоритетов. Также необходимо построить маршруты.");
            alert.showAndWait();
        }
    }

    @FXML
    public void handleSaveOntology() throws OWLOntologyStorageException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ontology File", "*.owl"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ontology File", "*.owx"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Ontology File", "*.rdf"));
        String selectedFile = fileChooser.showSaveDialog(this.stage).getAbsolutePath();
        if (selectedFile != null && o != null) {
            OntologyUtil.saveOntology(selectedFile, o);
        }
        else {
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(stage);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Необходимые для сохранения данные отсутствуют");
            alert.setContentText("Чтобы сохранить изменения в онтологии для начала необходимо ее загрузить");
            alert.showAndWait();
        }
    }

    public void drawFigures() {
        canvas.requestFocus();
        drawer.clearCanvas();
        double[] floatSize = new double[]{this.canvas.getWidth(), this.canvas.getHeight()};
        int[] intSize = new int[]{CalculationUtil.transferDoubleToInteger(floatSize[0]), CalculationUtil.transferDoubleToInteger(floatSize[1])};
        for (int i = 0; i < intSize[0]; i++) {
            for (int j= 0; j < intSize[1]; j++) {
                drawer.drawNode(new double[]{CalculationUtil.transferIntegerToDouble(i), CalculationUtil.transferIntegerToDouble(j)});
            }
        }
        if (G != null) {
            for (Vertex v : G.getV()) {
                double[] tempCoord = new double[]{CalculationUtil.transferIntegerToDouble(v.getX()), CalculationUtil.transferIntegerToDouble(v.getY())};
                drawer.drawVertex(tempCoord);
            }
            for (Edge e : G.getE()) {
                double[] startTempCoord = new double[]{CalculationUtil.transferIntegerToDouble(e.getStartV().getX()), CalculationUtil.transferIntegerToDouble(e.getStartV().getY())};
                double[] endtTempCoord = new double[]{CalculationUtil.transferIntegerToDouble(e.getEndV().getX()), CalculationUtil.transferIntegerToDouble(e.getEndV().getY())};
                drawer.drawEdge(startTempCoord, endtTempCoord, Color.BLACK);
            }
        }
        if (routes != null) {
            for (Route r : routes) {
                for (Edge en : r.getEdges()) {
                    double[] startTempCoord = new double[]{CalculationUtil.transferIntegerToDouble(en.getStartV().getX()), CalculationUtil.transferIntegerToDouble(en.getStartV().getY())};
                    double[] endtTempCoord = new double[]{CalculationUtil.transferIntegerToDouble(en.getEndV().getX()), CalculationUtil.transferIntegerToDouble(en.getEndV().getY())};
                    drawer.drawEdge(startTempCoord, endtTempCoord, Color.CYAN);
                }
            }
        }
        for (Vertex v : this.brigadeListArrayTemp) {
            double[] tempCoord = new double[]{CalculationUtil.transferIntegerToDouble(v.getX()), CalculationUtil.transferIntegerToDouble(v.getY())};
            drawer.drawRhomb(tempCoord);
        }
        for (Vertex v : this.warehouseListArrayTemp) {
            double[] tempCoord = new double[]{CalculationUtil.transferIntegerToDouble(v.getX()), CalculationUtil.transferIntegerToDouble(v.getY())};
            drawer.drawTriangle(tempCoord);
        }
        for (Vertex v : this.railwayListArrayTemp) {
            double[] tempCoord = new double[]{CalculationUtil.transferIntegerToDouble(v.getX()), CalculationUtil.transferIntegerToDouble(v.getY())};
            drawer.drawCircle(tempCoord);
        }
    }
}