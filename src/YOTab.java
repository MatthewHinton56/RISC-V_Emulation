import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;

public class YOTab extends Tab {
	
	String fileName;
	Button step,run,initialize;
	TextArea area;
	ScrollPane pane;
	BorderPane border;
	TabPane parent;
	HBox box;
	
	public YOTab(TabPane parent, String fileName, String inputText) {
		this.parent = parent;
		border = new BorderPane();
		area = new TextArea(inputText);
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
		area.setPrefHeight(bounds.getHeight()-175);
		area.setPrefWidth(bounds.getWidth()/2);
		pane = new ScrollPane(area);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);
		initialize = new Button("Initialize");
		initialize.setPrefHeight(100);
		initialize.setPrefWidth(100);
		step = new Button("Step");
		step.setPrefHeight(100);
		step.setPrefWidth(100);
		run = new Button("Run");
		run.setPrefHeight(100);
		run.setPrefWidth(100);
		box.getChildren().addAll(initialize,step,run);
		border.setBottom(box);
		border.setLeft(pane);
		this.setContent(border);
		this.setText(fileName);
	}
}
