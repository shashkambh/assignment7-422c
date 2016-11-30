/* Chat Client - Project 7
 * Shashank Kambhampati
 * skk834
 * 16445
 * Slip days used: 0
 * Fall 2016
 */
package assignment7;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.Priority;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.control.TextArea;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
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
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ClientMain extends Application{
    private ListView<String> conversations;
    private Map<String, StringBuilder> conversationText;
    private ObservableList<String> list;
    private TextArea chatbox;
    private ClientConnect connection;
	private Map<String, ListCell<String>> cells;

    public static void main(String[] args){
        launch(args);
    }

    public void start(Stage primaryStage){
		TextInputDialog ip = new TextInputDialog("127.0.0.1");
		ip.setTitle("Connect");
		ip.setContentText("IP of the server: ");
		ip.showAndWait().ifPresent(val ->  connection = new ClientConnect(this, val));
		if(connection == null) System.exit(0);
		
        conversationText = new HashMap<>();

        primaryStage.setOnCloseRequest(event -> connection.close());
        primaryStage.setTitle("Chat Client");
        primaryStage.setScene(createScene());
        primaryStage.show();
    }

    private Scene createScene(){
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
					input.setTitle("Add to chat");
					input.setContentText("Enter username to add: ");
                    input.showAndWait().ifPresent(val -> connection.sendToServer("addToChat " + getCurrentChat() + " " + val));
                }
            });

		MenuItem save = new MenuItem("Save");
		save.setOnAction(actionEvent->{
				conversationText.get(getCurrentChat()).toString();
				try(PrintWriter out = new PrintWriter(getCurrentChat() + ".txt")){
					out.println(conversationText.get(getCurrentChat()).toString());
				} catch(IOException e){
					
				}
			});

		chat.getItems().addAll(newChat, addPerson, save);

		Menu login = new Menu("Login");
		MenuItem log = new MenuItem("Log in");
		log.setOnAction(actionEvent->{
				TextInputDialog username = new TextInputDialog();
				username.setTitle("Login");
				username.setContentText("Enter your new username: ");
				username.showAndWait().ifPresent(val -> connection.sendToServer("createLogin " + val));
			});

		MenuItem username = new MenuItem("Get username");
		username.setOnAction(actionEvent->{
				connection.sendToServer("whoami");
			});
		login.getItems().addAll(log, username);

		
        toolbar.getMenus().addAll(chat, login);
        
        HBox clientPane = new HBox();
        conversations = new ListView<String>();
        list = FXCollections.observableArrayList();

        conversations.getSelectionModel().selectedItemProperty()
            .addListener((observable, oldValue, newValue) ->{
					chatbox.setText(conversationText.get(newValue).toString());
				});
		cells = new HashMap<>();
		conversations.setCellFactory(list->{
				ListCell<String> toAdd = new ListCell<String>(){
						@Override
						protected void updateItem(String item, boolean empty){
							super.updateItem(item, empty);
							if(item != null){
								cells.put(item, this);
								setText(item);
							}
						}
					};
				return toAdd;
			});

        VBox panel = new VBox();
        chatbox = new TextArea();
		chatbox.setEditable(false);
        conversations.setItems(list);
        list.add("server");
		conversations.setEditable(false);
        conversationText.put("server", new StringBuilder());
        conversations.getSelectionModel().select(0);

        TextField input = new TextField();
        input.setOnAction(actionEvent ->{
                connection.send(input.getText());
                input.clear();
            });

        panel.getChildren().addAll(chatbox, input);
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
		if(!conversationText.containsKey(newChat)){
			list.add(newChat);
			conversationText.put(newChat, new StringBuilder());
		}
    }

    public void appendToConversation(String chatId, String newMsg){
		conversationText.get(chatId).append(newMsg + "\n");

		if(getCurrentChat().equals(chatId)){
			chatbox.appendText(newMsg + "\n");
		} else {
			blink(chatId);
		}
    }

	private void blink(String notified){
		Runnable blinkThread = ()->{
			Platform.runLater(()->cells.get(notified).setStyle("-fx-control-inner-background: orange;"));

			try{
				Thread.sleep(1000);
			} catch(InterruptedException e){
				
			}

			Platform.runLater(()->cells.get(notified).setStyle("-fx-control-inner-background: white;"));
		};

		new Thread(blinkThread).start();
	}
}
