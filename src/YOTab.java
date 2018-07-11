import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;

public class YOTab extends Tab {
	
	String fileName;
	Button step,run,initialize;
	TextArea area;
	ScrollPane pane, displayPane;
	BorderPane border;
	GridPane registerDisplay;
	GridPane memDisplay;
	ToggleGroup group;
	RadioButton byteButton, halfWordButton, wordButton, doubleWordButton; 
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
		registerDisplay = new GridPane();
		memDisplay = new GridPane();
		byteButton = new RadioButton("Byte");
		halfWordButton = new RadioButton("Half Word");
		wordButton = new RadioButton("Word");
		doubleWordButton = new RadioButton("Double Word");
		group = new ToggleGroup();
		byteButton.setToggleGroup(group);
		byteButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				YOTab.this.refresh();
			}
			
		});
		halfWordButton.setToggleGroup(group);
		halfWordButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				YOTab.this.refresh();
			}
			
		});
		wordButton.setToggleGroup(group);
		wordButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				YOTab.this.refresh();
			}
			
		});
		doubleWordButton.setToggleGroup(group);
		doubleWordButton.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				YOTab.this.refresh();
			}
			
		});
		doubleWordButton.setSelected(true);
		memDisplay.add(byteButton, 0, 0);
		memDisplay.add(halfWordButton, 1, 0);
		memDisplay.add(wordButton, 0, 1);
		memDisplay.add(doubleWordButton, 1, 1);
		refresh();
		displayPane = new ScrollPane(registerDisplay);
		displayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		box = new HBox();
		box.setPrefHeight(100);
		initialize = new Button("Initialize");
		initialize.setPrefHeight(100);
		initialize.setPrefWidth(100);
		initialize.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.initialize();
				refresh();
			}
		});
		step = new Button("Step");
		step.setPrefHeight(100);
		step.setPrefWidth(100);
		step.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.step();
				refresh();
			}
		});
		run = new Button("Run");
		run.setPrefHeight(100);
		run.setPrefWidth(100);
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.run();
				refresh();
			}
		});
		box.getChildren().addAll(initialize,step,run);
		border.setBottom(box);
		border.setLeft(pane);
		border.setRight(displayPane);
		border.setCenter(memDisplay);
		this.setContent(border);
		this.setText(fileName);
	}
	
	public void refresh() {
		registerDisplay.getChildren().clear();
		int row = 0;
		for(String register: Processor.registerFile.keySet()) {
			registerDisplay.add(new TextField(register), 0, row);
			registerDisplay.add(new TextField("0x"+Processor.registerFile.get(register).displayToString()), 1, row);
			row++;
		}
		
		memDisplay.getChildren().clear();
		memDisplay.add(byteButton, 0, 0);
		memDisplay.add(halfWordButton, 1, 0);
		memDisplay.add(wordButton, 0, 1);
		memDisplay.add(doubleWordButton, 1, 1);
		int offset = 0;
		switch(((RadioButton)group.getSelectedToggle()).getText()) {
		case "Byte":
			offset = 1;
			break;
		case "Half Word":
			offset = 2;
			break;
		case "Word":
			offset = 4;
			break;
		case "Double Word":
			offset = 8;
			break;
		}
		row = 2;
		for(long address: Memory.memory.keySet()) {
			if(address % offset == 0) {
				LittleEndian value = Memory.load(address, offset);
				memDisplay.add(new TextField("0x"+Long.toHexString(address)), 0, row);
				memDisplay.add(new TextField("0x"+value.displayToString()), 1, row);
				row++;
			}
		}
	}
	
	
	
}
