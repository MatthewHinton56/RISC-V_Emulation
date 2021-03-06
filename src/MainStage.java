import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainStage extends Application implements EventHandler<ActionEvent>{
	
	public YSTab ystab;
	public YOTab yotab;
	public TabPane pane;
	@Override
	public void start(Stage primaryStage) throws Exception {

		pane = new TabPane();
		EmulatorMenuBar emb = new EmulatorMenuBar(this);
		BorderPane border = new BorderPane();
		border.setTop(emb);
		border.setCenter(pane);
		Scene scene = new Scene(border);
		primaryStage.setScene(scene);
		primaryStage.setMaximized(true);
		primaryStage.setMinHeight(500);
		primaryStage.setMinWidth(500);
		primaryStage.show();
	}

	@Override
	public void handle(ActionEvent arg0) {
		String input = ystab.area.getText();
		ystab.output.setText("Compiler Output:\n");
		String output;
		try {
		output = Compiler.compile(input, ystab.output);
		ystab.output.setText(ystab.output.getText() + "\n Assembly compiled and ready for emulation in yotab" );
		
		Processor.clear();
		pane.getTabs().remove(yotab);
		yotab = new YOTab(pane,ystab.fileName.substring(0,ystab.fileName.indexOf(".")) +".o", output);
		yotab.refresh();
		pane.getTabs().add(yotab);
		}
		catch(IllegalArgumentException e) {
			ystab.output.setText(ystab.output.getText() + "Compiler Output:\n" + e.getMessage());
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
