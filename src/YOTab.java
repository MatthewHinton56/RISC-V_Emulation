import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;

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
	Button step,run,initialize, clockPulse;
	TextArea area, outputDisplay;
	ScrollPane pane, displayPane,memDisplayScrollPane, outputDisplayPane;
	BorderPane border, textBorder;
	GridPane registerDisplay;
	GridPane memDisplay;
	ToggleGroup group;
	RadioButton byteButton, halfWordButton, wordButton, doubleWordButton; 
	TabPane parent;
	HBox box;
	String inputText;
	public YOTab(TabPane parent, String fileName, String inputText) {
		this.parent = parent;
		border = new BorderPane();
		textBorder = new BorderPane();
		area = new TextArea(inputText);
		this.inputText = inputText;
		Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

		area.setPrefHeight((bounds.getHeight()-175)/2);
		area.setPrefWidth(bounds.getWidth()/2);
		pane = new ScrollPane(area);
		pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		outputDisplay = new TextArea("Processor output:\n");
		outputDisplay.setPrefHeight((bounds.getHeight()-175)/2);
		outputDisplay.setPrefWidth(bounds.getWidth()/2);
		outputDisplayPane = new ScrollPane(outputDisplay);
		outputDisplayPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

		textBorder.setBottom(outputDisplayPane);
		textBorder.setTop(pane);

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
		memDisplayScrollPane = new ScrollPane(memDisplay);
		memDisplayScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
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
				initializeDisplay();
			}
		});
		step = new Button("Step");
		step.setPrefHeight(100);
		step.setPrefWidth(100);
		step.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.step();
				refresh();
				stepDisplay();
			}
		});
		run = new Button("Run");
		run.setPrefHeight(100);
		run.setPrefWidth(100);
		run.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.run();
				refresh();
				runDisplay();
			}
		});
		clockPulse = new Button("Clock Pulse");
		clockPulse.setPrefHeight(100);
		clockPulse.setPrefWidth(100);
		clockPulse.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent arg0) {
				Processor.clockPulse();
				refresh();
			}
		});
		box.getChildren().addAll(initialize,step,run,clockPulse);
		border.setBottom(box);
		border.setLeft(textBorder);
		border.setRight(displayPane);
		border.setCenter(memDisplayScrollPane);

		this.setContent(border);
		this.setText(fileName);
	}

	protected void stepDisplay() {
		if(Processor.initialized) {
			outputDisplay.setText(outputDisplay.getText() + "STEP:\n");
			if(Processor.status.equals("HLT")) {
				if(Processor.exceptionGenerated)
					outputDisplay.setText(outputDisplay.getText() + "The processor exited with:\n" + Processor.exception+"\n");
				else {
					outputDisplay.setText(outputDisplay.getText() + "The program has completed its execution:\n");
					outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.registerFile.get("pc")) + "\n");
					outputDisplay.setText(outputDisplay.getText() + "Completed Instruction: " + Processor.completedInstruction.buildDisplayInstruction() +"\n");
					outputDisplay.setText(outputDisplay.getText() + registerDisplay());
					outputDisplay.setText(outputDisplay.getText() + memoryDisplay());
					outputDisplay.setText(outputDisplay.getText() + registerDifference(Processor.initialRegisterFile, Processor.finalRegisterFile, "FINAL"));
					outputDisplay.setText(outputDisplay.getText() + memoryDifference(Processor.initialMemory, Processor.finalMemory, "FINAL"));
				}
			} else {
				outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.completedInstruction.address) + "\n");
				outputDisplay.setText(outputDisplay.getText() + "Completed Instruction: " + Processor.completedInstruction.buildDisplayInstruction() +"\n");
				outputDisplay.setText(outputDisplay.getText() + registerDifference(Processor.stepBeforeReg, Processor.stepAfterReg, "STEP"));
				outputDisplay.setText(outputDisplay.getText() + memoryDifference(Processor.stepBeforeMem, Processor.stepAfterMem, "STEP"));
			}
		}
	}

	protected void runDisplay() {
		if(Processor.initialized) {
			outputDisplay.setText(outputDisplay.getText() + "RUN:\n");
			outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.registerFile.get("pc")) + "\n");
			if(Processor.exceptionGenerated)
				outputDisplay.setText(outputDisplay.getText() + "The processor exited with: " + Processor.exception+"\n");
			outputDisplay.setText(outputDisplay.getText() + registerDisplay());
			outputDisplay.setText(outputDisplay.getText() + memoryDisplay());
			outputDisplay.setText(outputDisplay.getText() + registerDifference(Processor.initialRegisterFile, Processor.finalRegisterFile, "FINAL"));
			outputDisplay.setText(outputDisplay.getText() + memoryDifference(Processor.initialMemory, Processor.finalMemory, "FINAL"));
		}
	}

	public void initializeDisplay() {
		outputDisplay.setText("Processor output:\n\n Initialize:\n");
		if(Processor.status.equals("HLT")) {
			outputDisplay.setText(outputDisplay.getText() + "Program failed to initialize, check that all memory locations are valid");
		} else {
			outputDisplay.setText(outputDisplay.getText() + "PC: " + displayText(Processor.registerFile.get("pc")) + "\n\n");
			outputDisplay.setText(outputDisplay.getText() + registerDisplay() +"\n");
			outputDisplay.setText(outputDisplay.getText() + memoryDisplay()+ "\n");
		}
	}

	private String registerDisplay() {
		String output = "Register File:\n";
		for(String reg: Processor.registerFile.keySet()) {
			output += String.format("%3s", reg) + " = " + displayText(Processor.registerFile.get(reg))+ "\n";
		}
		return output+"\n";
	}

	private String registerDifference(TreeMap<String, DoubleWord> before, TreeMap<String, DoubleWord> after, String text) {
		String output = "Register File Differences: " + text + ":\n";
		ArrayList<String> dif = RegisterFile.getDif(before, after);
		for(String s: dif) {
			output += String.format("%3s", s) +  ": " + displayText(before.get(s)) + "====>" + displayText(after.get(s)) +"\n";
		}
		return output+"\n";
	}

	private String memoryDisplay() {
		String output = "Memory:\n";
		for(Long address: Memory.memory.keySet()) {
			output +=  "0x" + Long.toString(address, 16)+ " = " + displayText(Memory.memory.get(address))+ "\n";
		}
		return output+"\n";
	}

	private String memoryDifference(HashMap<Long, BYTE> before, HashMap<Long, BYTE> after, String text) {
		String output = "Memory Differences: " + text + ":\n";
		ArrayList<Long> dif = Memory.getDif(before, after);
		for(Long l: dif) {
			if(before.containsKey(l))
				output += "0x" +Long.toString(l, 16) + ": " + displayText(before.get(l)) + "====>" + displayText(after.get(l)) +"\n";
			else 
				output += "0x" +Long.toString(l, 16) + ": " + displayText(new BYTE()) + "====>" + displayText(after.get(l)) +"\n";
		}
		return output+"\n";
	}


	public void refresh() {
		registerDisplay.getChildren().clear();
		int row = 0;
		registerDisplay.add(new TextField("Pipeline info"), 0, row);
		row++;
		registerDisplay.add(new TextField("Status"), 0, row);
		registerDisplay.add(new TextField(Processor.status), 1, row);
		row++;
		row = pipeLineStages(row);
		registerDisplay.add(new TextField("Register info"), 0, row);
		row++;
		for(String register: Processor.registerFile.keySet()) {
			registerDisplay.add(new TextField(register), 0, row);
			if(Processor.registerFile.get(register) != null)
				registerDisplay.add(new TextField(displayText(Processor.registerFile.get(register))), 1, row);
			else
				registerDisplay.add(new TextField("N/A"), 1, row);
			row++;
		}
		String outputDisplay = modifiedDisplay();
		area.setText(outputDisplay);
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
				memDisplay.add(new TextField(displayText(value)), 1, row);
				row++;
			}
		}
	}

	private int pipeLineStages(int row) {
		DoubleWord fetchAddress = Processor.pcAddresses[0];
		System.out.println(fetchAddress);
		registerDisplay.add(new TextField("Fetch"), 0, row);
		if(fetchAddress == null || !validAddress(fetchAddress)) {
			registerDisplay.add(new TextField("BUBBLE"), 1, row);
		} else {
			registerDisplay.add(new TextField("0x"+fetchAddress.displayToString()), 1, row);
		}
		row++;

		DoubleWord decodeAddress = Processor.pcAddresses[1];
		registerDisplay.add(new TextField("Decode"), 0, row);
		if(decodeAddress == null) {
			registerDisplay.add(new TextField("BUBBLE"), 1, row);
		} else {
			registerDisplay.add(new TextField("0x"+decodeAddress.displayToString()), 1, row);
		}
		row++;

		DoubleWord executeAddress = Processor.pcAddresses[2];
		registerDisplay.add(new TextField("Execute"), 0, row);
		if(executeAddress == null) {
			registerDisplay.add(new TextField("BUBBLE"), 1, row);
		} else {
			registerDisplay.add(new TextField("0x"+executeAddress.displayToString()), 1, row);
		}
		row++;

		DoubleWord memoryAddress = Processor.pcAddresses[3];
		registerDisplay.add(new TextField("Memory"), 0, row);
		if(memoryAddress == null) {
			registerDisplay.add(new TextField("BUBBLE"), 1, row);
		} else {
			registerDisplay.add(new TextField("0x"+memoryAddress.displayToString()), 1, row);
		}
		row++;

		DoubleWord writeBackAddress = Processor.pcAddresses[4];
		registerDisplay.add(new TextField("Write Back"), 0, row);
		if(writeBackAddress == null) {
			registerDisplay.add(new TextField("BUBBLE"), 1, row);
		} else {
			registerDisplay.add(new TextField("0x"+writeBackAddress.displayToString()), 1, row);
		}
		row++;
		return row;
	}

	private boolean validAddress(DoubleWord address) {
		Scanner scan = new Scanner(inputText);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x")+1, line.indexOf(":"));
			String restOfLine = line.substring(line.indexOf(":")+1);
			DoubleWord addressLine = new DoubleWord(Long.parseLong(addressString, 16));
			System.out.println(addressLine.displayToString());
			if(addressLine.equals(address) && !restOfLine.contains(":") && !restOfLine.contains(".")) {
				scan.close();
				return true;
			}
		}
		scan.close();
		return false;
	}

	private String modifiedDisplay() {
		String output = "";
		Scanner scan = new Scanner(inputText);
		while(scan.hasNextLine()) {
			String line = scan.nextLine();
			String addressString = line.substring(line.indexOf("x")+1, line.indexOf(":"));
			DoubleWord address = new DoubleWord(Long.parseLong(addressString, 16));
			String restOfLine = line.substring(line.indexOf(":")+1);
			if(!restOfLine.contains(":") && !restOfLine.contains(".")) {
				if(Processor.pcAddresses[Processor.FETCH_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.FETCH_ADDRESS_POSITION].equals(address))
					output += "F";
				else 
					output += "\u2002";
				if(Processor.pcAddresses[Processor.DECODE_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.DECODE_ADDRESS_POSITION].equals(address))
					output += "D";
				else 
					output += "\u2002";
				if(Processor.pcAddresses[Processor.EXECUTE_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.EXECUTE_ADDRESS_POSITION].equals(address))
					output += "E";
				else 
					output += "\u2002";
				if(Processor.pcAddresses[Processor.MEMORY_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.MEMORY_ADDRESS_POSITION].equals(address))
					output += "M";
				else 
					output += "\u2002";
				if(Processor.pcAddresses[Processor.WRITE_BACK_ADDRESS_POSITION] != null && Processor.pcAddresses[Processor.WRITE_BACK_ADDRESS_POSITION].equals(address))
					output+= "W";
				else 
					output += "\u2002";
			} else {
				output += "\u2002\u2002\u2002\u2002\u2002";
			}

			output+=" "+line+"\n";
		}
		scan.close();
		return output;
	}

	public static String displayText(LittleEndian val) {
		switch(EmulatorMenuBar.displaySetting) {
		case EmulatorMenuBar.SIGNED:
			return (val.calculateValueSigned()+" ");
		case EmulatorMenuBar.UNSIGNED:
			return val.calculateValueUnSigned();
		case EmulatorMenuBar.HEXLE:
			return val.generateHexLE();
		default:
			return "0x"+val.displayToString();
		}
	}




}
