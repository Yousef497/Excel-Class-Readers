package Advanced;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;


public class MainController implements Initializable {

    private Stage stage;
    private Scene scene;
    private Parent root;
    public String path;
    public static String pathName;

    @FXML
    private Label choosetxt;

    @FXML
    private Text APIMethod;
    @FXML
    private Text APIMethodTitle;
    @FXML
    private Text APIURLTitle;
    @FXML
    private Text APIURL;
    @FXML
    private Text APIDetailsTitle;

    @FXML
    private ListView<String> APIsList;

    @FXML
    private ListView<String> allowedList;

    @FXML
    private ListView<String> componentsList;

    @FXML
    private ListView<String> mandatoryList;

    @FXML
    private ListView<String> requestList;

    @FXML
    private ListView<String> responseList;

    @FXML
    private Button btnBrowse;

    @FXML
    private Button btnStart;

    @FXML
    private Button btnProcess;

    @FXML
    private Button btnExit;

    @FXML
    private Button btnBack;

    @FXML
    private TextField txtfDirectory;

    @FXML
    public void btnBrowseAction(ActionEvent event) {

        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("EXCEL files (*.xlsx)", "*.xlsx"),
                new FileChooser.ExtensionFilter("EXCEL files (*.xls)", "*.xls"));
        File selectedFile = fc.showOpenDialog(null);

        if(selectedFile != null){
            txtfDirectory.setText(selectedFile.getAbsolutePath());
            path = txtfDirectory.getText();
            pathName = new String(path);
            btnStart.setDisable(false);
        }
        else {
            System.out.println("File not Valid");
        }

    }

    @FXML
    public void btnExitAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void btnBackAction(ActionEvent event) throws IOException {

        root = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setResizable(false);
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();

    }

    @FXML
    public void btnStartAction(ActionEvent event) throws IOException{

        root = FXMLLoader.load(getClass().getResource("Scene2.fxml"));
        stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root, 1160,600);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.resizableProperty().setValue(Boolean.FALSE);
        stage.centerOnScreen();
        stage.show();
    }

    void getObjects(boolean request, ArrayList<Field> result, ObjectField o){
        for(Field f : o.getChildren()){
            if(f instanceof ObjectField && f.isRequest() == request){
                result.add(f);
                getObjects(request, result, (ObjectField)f);
            }
        }
    }


    void display(Field f){
        componentsList.getItems().clear();
        mandatoryList.getItems().clear();
        allowedList.getItems().clear();

        mandatoryList.getItems().add(f.isMandatory() ? "YES":"NO");
        componentsList.getItems().add("Field : " + f.getName());
        //allowedList.getItems().add(((StringField) f).getAllowedValues() == null ? "(ALL)" : ((StringField) f).getAllowedValues());
        if(f instanceof ObjectField){
            allowedList.getItems().add("");
            for(Field child : ((ObjectField) f).getChildren()){
                componentsList.getItems().add(child.getName());
                mandatoryList.getItems().add(child.isMandatory() ? "YES":"NO");
                if(child instanceof StringField)
                    allowedList.getItems().add(((StringField) child).getAllowedValues() == null ? "(ALL)" : ((StringField) child).getAllowedValues());
                else
                    allowedList.getItems().add("");
            }
        }
        else{
            StringField sf = (StringField)f;
            String allowedValues = sf.getAllowedValues() == null ? "(ALL)" : sf.getAllowedValues();
            allowedList.getItems().add(allowedValues);
        }
    }

    void updateComponentList(ObjectField parent){
        componentsList.setOnMouseClicked(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent click) {
                String selectedComp = componentsList.getSelectionModel().getSelectedItem();
                if(componentsList.getItems().size() > 1)
                    for(Field f : parent.getChildren())
                        if(f.getName().equals(selectedComp)) display(f);
            }
        });
    }

    @FXML
    public void addList() throws IOException, InvalidFormatException {
           
        APIsList.getItems().clear();
        
        requestList.getItems().clear();
        responseList.getItems().clear();
        componentsList.getItems().clear();
        mandatoryList.getItems().clear();
        allowedList.getItems().clear();
        
        APIDetailsTitle.setVisible(false);
        APIMethodTitle.setVisible(false);
        APIMethod.setVisible(false);
        APIURLTitle.setVisible(false);
        APIURL.setVisible(false);
        
        
        Utility utility = new Utility(pathName);
        Service s = utility.extractData();

        for(Operation op : s.getOperations()){
            APIsList.getItems().add(op.getName());

            //APIsList view handling mouse click
            APIsList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent click) {

                    if (click.getClickCount() == 1) {

                        ArrayList<Field> requests = new ArrayList<>();
                        ArrayList<Field> responses = new ArrayList<>();

                        requestList.getItems().clear();
                        responseList.getItems().clear();
                        
                        
                        APIDetailsTitle.setVisible(true);
                        APIMethodTitle.setVisible(true);
                        APIMethod.setVisible(true);
                        APIURLTitle.setVisible(true);
                        APIURL.setVisible(true);


                        APIDetailsTitle.setText("API Details:");
                        APIMethodTitle.setText("API Method:");
                        APIMethod.setText(op.getMethod());
                        APIURLTitle.setText("API URL:");
                        APIURL.setText(op.getUrl());


                        for(Field f : op.getFields()){
                            if(f.isRequest()){
                                requests.add(f);
                                if(f instanceof ObjectField)
                                    getObjects(true, requests, (ObjectField)f);
                            }else{
                                responses.add(f);
                                if(f instanceof ObjectField)
                                    getObjects(false, responses, (ObjectField)f);
                            }
                        }

                        requests.forEach(request -> requestList.getItems().add(request.getName()));
                        responses.forEach(response -> responseList.getItems().add(response.getName()));

                        requestList.setOnMouseClicked((MouseEvent inclick) -> {
                            if (inclick.getClickCount() == 1) {
                                String currentItemSelected = requestList.getSelectionModel().getSelectedItem();
                                for(Field r : requests) {
                                    if(r.getName().equals(currentItemSelected)){
                                        display(r);
                                        updateComponentList((ObjectField)r);
                                        break;
                                    }
                                }
                                allowedList.setStyle("-fx-control-inner-background: #76b5c5;");
                                allowedList.setMouseTransparent(true);
                                allowedList.setFocusTraversable(false);
                                mandatoryList.setStyle("-fx-control-inner-background: #76b5c5;");
                                mandatoryList.setMouseTransparent(true);
                                mandatoryList.setFocusTraversable(false);


                            }
                        });

                        responseList.setOnMouseClicked((MouseEvent inclick) -> {
                            if (inclick.getClickCount() == 1) {
                                String currentItemSelected = responseList.getSelectionModel().getSelectedItem();
                                for(Field r : responses) {
                                    if(r.getName().equals(currentItemSelected)){
                                        display(r);
                                        updateComponentList((ObjectField)r);
                                        break;
                                    }
                                }

                                allowedList.setStyle("-fx-control-inner-background: #76b5c5;");
                                allowedList.setMouseTransparent(true);
                                allowedList.setFocusTraversable(false);
                                mandatoryList.setStyle("-fx-control-inner-background: #76b5c5;");
                                mandatoryList.setMouseTransparent(true);
                                mandatoryList.setFocusTraversable(false);

                            }
                        });

                    }
                }
            });


        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
