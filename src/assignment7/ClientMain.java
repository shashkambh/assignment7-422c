import javafx.application.Application;
import javafx.scene.Scene;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import java.util.HashMap;
import java.util.Map;

public class ClientMain extends Application{
	private ListView<String> conversations;
	private Map<String, String> conversationText;
	private ObservableList<String> list;
	private TextArea chatbox;
	private ClientConnect connection;

	public static void main(String[] args){
		launch(args);
	}

	public void start(Stage primaryStage){
		connection = new ClientConnect(this);
		conversationText = new HashMap<>();

		primaryStage.setOnCloseRequest(event -> connection.close());
		primaryStage.setTitle("Chat Client");
		primaryStage.setScene(createScene());
		primaryStage.show();
	}

	public Scene createScene(){
		HBox clientPane = new HBox();
		conversations = new ListView<String>();
		list = FXCollections.observableArrayList();
		list.add("server");
		conversations.setItems(list);
		conversations.setEditable(false);
		conversations.getSelectionModel().select(0);

		conversations.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> chatbox.setText(conversationText.get(newValue)));

		VBox panel = new VBox();
		chatbox = new TextArea();
		chatbox.setEditable(false);
		TextField input = new TextField();
		input.setOnAction(actionEvent ->{
				connection.sendToServer(input.getText());
				input.clear();
			});
		panel.getChildren().addAll(chatbox, input);


		clientPane.setPadding(new Insets(10, 10, 10, 10));
		clientPane.getChildren().addAll(conversations, panel);

		return new Scene(clientPane);
	}

	public String getCurrentChat(){
		return conversations.getSelectionModel().getSelectedItem();
	}

	public void addConversation(String newChat){
		list.add(newChat);
		conversationText.put(newChat, "");
	}

	public void appendToConversation(String chatId, String newMsg){
		String newStr = conversationText.get(chatId) + newMsg + "\n";
		conversationText.put(chatId, newStr);

		if(getCurrentChat().equals(chatId)){
			chatbox.appendText(newMsg + "\n");
		}
	}
}
