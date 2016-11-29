package assignment7;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ClientMain extends Application{
    private ListView<String> conversations;
    private Map<String, List<Text>> conversationText;
    private ObservableList<String> list;
    private TextFlow chatbox;
    private ClientConnect connection;

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage primaryStage){
		while(connection == null){
			TextInputDialog ip = new TextInputDialog("127.0.0.1");
			ip.setTitle("Connect");
			ip.setContentText("IP of the server: ");
			ip.showAndWait().ifPresent(val ->  connection = new ClientConnect(this, val));
		}
        conversationText = new HashMap<>();

        primaryStage.setOnCloseRequest(event -> connection.close());
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(createScene());
        primaryStage.show();
    }

    public Scene createScene(){
        VBox outer = new VBox();
		outer.setStyle("-fx-font-family: 'Roboto', 'Arial', sans-serif; -fx-font-size: 10pt;");
        MenuBar toolbar = new MenuBar();
        Menu chat = new Menu("Chat");
        MenuItem newChat = new MenuItem("Create new chat");
        newChat.setOnAction(actionEvent->{
                TextInputDialog input = new TextInputDialog();
				input.setTitle("New Chat");
				input.setContentText("Enter new chat name: ");
                input.showAndWait().ifPresent(val -> connection.sendToServer("createChat " + val));
         });


        MenuItem addPerson = new MenuItem("Add person to chat");
        addPerson.setOnAction(actionEvent->{
                if(!getCurrentChat().equals("server")){
                    TextInputDialog input = new TextInputDialog();
                    input.showAndWait().ifPresent(val -> connection.sendToServer("addToChat " + getCurrentChat() + " " + val));
                }
            });
        chat.getItems().addAll(newChat, addPerson);
        toolbar.getMenus().addAll(chat);
        
        HBox clientPane = new HBox();
        conversations = new ListView<String>();
        list = FXCollections.observableArrayList();
        list.add("server");
        conversations.setItems(list);
        conversationText.put("server", new ArrayList<>());
        conversations.setEditable(false);
        conversations.getSelectionModel().select(0);

        //System.out.println(conversations.getCellFactory().call(conversations));

        conversations.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) ->{
					chatbox.getChildren().clear();
					chatbox.getChildren().addAll(conversationText.get(newValue));
				});

        VBox panel = new VBox();
        chatbox = new TextFlow();

        TextField input = new TextField();
        input.setOnAction(actionEvent ->{
                connection.send(input.getText());
                input.clear();
            });
		ScrollPane wrapper = new ScrollPane(chatbox);
		wrapper.setFitToHeight(true);
		wrapper.setFitToWidth(true);
        panel.getChildren().addAll(wrapper, input);
        panel.setVgrow(chatbox, Priority.ALWAYS);

        clientPane.setPadding(new Insets(10, 10, 10, 10));
        clientPane.getChildren().addAll(conversations, panel);
        clientPane.setHgrow(panel, Priority.ALWAYS);

        outer.getChildren().addAll(toolbar, clientPane);
        outer.setVgrow(clientPane, Priority.ALWAYS);

        Scene scene = new Scene(outer);
        return scene;
    }

    public String getCurrentChat(){
        return conversations.getSelectionModel().getSelectedItem();
    }

    public void addConversation(String newChat){
        list.add(newChat);
        conversationText.put(newChat, new ArrayList<>());
    }

    public void appendToConversation(String chatId, String newMsg){
		Text toAdd = new Text(newMsg);

        conversationText.get(chatId).add(new Text(newMsg + "\n"));

        if(getCurrentChat().equals(chatId)){
			chatbox.getChildren().add(conversationText.get(chatId).get(conversationText.get(chatId).size() - 1));
        }
    }
}
