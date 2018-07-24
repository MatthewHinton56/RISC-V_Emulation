import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class EmulatorMenuBar extends MenuBar implements EventHandler<ActionEvent> {
	public static final String HEX = "Hex";
	public static final String UNSIGNED = "Unsigned";
	public static final String SIGNED = "Signed";

	public MainStage mainStage;
	public Menu file, options, baseInteger, multiplyExtension, instructions;
	public MenuItem newButton, saveButton, loadButton;
	public RadioMenuItem hex, unsigned, signed;
	public ToggleGroup group;
	public static String displaySetting = HEX;

	public EmulatorMenuBar(MainStage mainStage) {
		this.mainStage = mainStage;
		file = new Menu("File");
		newButton = new MenuItem("New");
		newButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				mainStage.pane.getTabs().clear();
				mainStage.yotab = null;
				TextInputDialog dialog = new TextInputDialog("file");
				dialog.setTitle("File Name");
				dialog.setHeaderText(".s appended for you");
				dialog.setContentText("Please enter File Name:");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (result.isPresent()){
					mainStage.ystab = new YSTab(mainStage.pane,result.get() + ".s","",mainStage);
					mainStage.pane.getTabs().add(mainStage.ystab);
				}
			}
		});
		saveButton = new MenuItem("Save");
		saveButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				try {
					if(mainStage.ystab != null) {
						FileChooser fileChooser = new FileChooser();
						fileChooser.setTitle("Save file");
						fileChooser.setInitialFileName(mainStage.ystab.fileName);
						File file = fileChooser.showSaveDialog(null);
						FileWriter writer = new FileWriter(file);
						writer.write(mainStage.ystab.area.getText());
						writer.close();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
		loadButton = new MenuItem("Load");
		loadButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent e) {
				FileChooser fileChooser = new FileChooser();
				fileChooser.setTitle("Open Resource File");
				fileChooser.getExtensionFilters().addAll(
						new ExtensionFilter("RISCV - Assembler", "*.s"));
				File selectedFile = fileChooser.showOpenDialog(null);

				// Traditional way to get the response value.
				try {
					Scanner scan = new Scanner(selectedFile);
					String input = "";
					while(scan.hasNextLine()) {
						input+=scan.nextLine()+"\n";
					}
					scan.close();
					mainStage.ystab = new YSTab(mainStage.pane,selectedFile.getName(),input,mainStage);
					mainStage.pane.getTabs().add(mainStage.ystab);
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		options = new Menu("Options");
		hex = new RadioMenuItem(HEX);
		unsigned = new RadioMenuItem(UNSIGNED);
		signed = new RadioMenuItem(SIGNED);
		group = new ToggleGroup();
		hex.setToggleGroup(group);
		unsigned.setToggleGroup(group);
		signed.setToggleGroup(group);
		hex.setOnAction(this);
		signed.setOnAction(this);
		unsigned.setOnAction(this);
		hex.setSelected(true);

		baseInteger = new Menu("Base Integer");
		for(String instruction: Instruction.INSTRUCTION_TO_ARCHETYPE.keySet()) {
			MenuItem item = new MenuItem(instruction);
			item.setOnAction(this);
			baseInteger.getItems().add(item);
		}

		multiplyExtension = new Menu("Multiplication Extension");
		for(String instruction: Instruction.INSTRUCTION_TO_ARCHETYPE_M.keySet()) {
			MenuItem item = new MenuItem(instruction);
			item.setOnAction(this);
			multiplyExtension.getItems().add(item);
		}
		instructions = new Menu("Instructions");
		instructions.getItems().addAll(baseInteger, multiplyExtension);
		file.getItems().addAll(newButton, saveButton, loadButton);
		options.getItems().addAll(hex, signed, unsigned);
		this.getMenus().addAll(file, options, instructions);
	}

	//options listener
	@Override
	public void handle(ActionEvent arg0) {
		displaySetting = ((RadioMenuItem)group.getSelectedToggle()).getText();
		if(mainStage.yotab != null) 
			mainStage.yotab.refresh();
		if(mainStage.ystab != null && arg0.getSource() instanceof MenuItem && !(arg0.getSource() instanceof RadioMenuItem)) {
			MenuItem item = (MenuItem)arg0.getSource();
			if(item.getParentMenu().getText().equals("Base Integer")) {
				String archetype = Instruction.INSTRUCTION_TO_ARCHETYPE.get(item.getText());
				mainStage.ystab.area.setText(mainStage.ystab.area.getText()+archetype+"\n");
			} else if(item.getParentMenu().getText().equals("Multiplication Extension")) {
				String archetype = Instruction.INSTRUCTION_TO_ARCHETYPE_M.get(item.getText());
				mainStage.ystab.area.setText(mainStage.ystab.area.getText()+archetype+"\n");
			}
		}
	}



}
