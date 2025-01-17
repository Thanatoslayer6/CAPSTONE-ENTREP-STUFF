package mainPackage;

import scenes.introScene;
import scenes.sceneOnePartOne;
import scenes.sceneOnePartTwo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.Timer;
import javax.swing.event.MenuKeyEvent;
//import javax.swing.event.MenuKeyListener;
import javax.swing.event.MouseInputListener;

import mainPackage.storyLines.dialogues;
import mainPackage.storyLines.questions;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;

public class Game implements java.io.Serializable{

	Dimension gameWindowSize = Toolkit.getDefaultToolkit().getScreenSize();
		int screenWidth = (int)gameWindowSize.getWidth();
		int screenHeight = (int)gameWindowSize.getHeight();
		
	public Font narrationFont = new Font("Times New Roman", Font.ITALIC, 35), normalFont = new Font ("Arial", Font.PLAIN, 35),
			hyperboleFont = new Font ("Papyrus", Font.BOLD, 40);
		
	//Game Mechanics/Essentials
	UserInterface ui = new UserInterface();
	soundManager sm = new soundManager();
	storyLines lines = new storyLines();
	playerStats player = new playerStats();
	TransitionClass tc = new TransitionClass(ui);
	ImageManager imgManage = new ImageManager(this, screenWidth, screenHeight);
		
	//Scene Management
		introScene intro = new introScene(this, ui, tc, sm, player, lines, imgManage, screenWidth, screenHeight);
		sceneOnePartOne homeOne = new sceneOnePartOne(this, ui, tc, sm, player, lines, imgManage, screenWidth, screenHeight);
		sceneOnePartTwo homeTwo = new sceneOnePartTwo(this, ui, tc, sm, player, lines, imgManage, screenWidth, screenHeight);
	
	gameStory Story = new gameStory(this, ui, tc, sm, player, imgManage, intro, homeOne, homeTwo);
	
	//Input Handlers
	ChoiceHandler cHandler = new ChoiceHandler();
	MouseHandler mHandler = new MouseHandler();
	KeyboardHandler kbHandler = new KeyboardHandler();
	NameHandler nHandler = new NameHandler();
	AnswerHandler InputHandler = new AnswerHandler();

	//Save-Load Button (VERY IMPORTANT)
	SaveLoadHandler saveloadHandler = new SaveLoadHandler();
	
		int click = 0;
	
	public String currentDialogue, currentQuestion, nextDialogue, nextMove, nextPosition1, nextPosition2, nextPosition3, nextPosition4;
	public static String playerName, playerAnswer = null, gender, selected; 
	public static int numberAnswer = 0;

	public int diatextTracker = 0, questiontextTracker = 0, enableKeys = 0, letterTracker = 0, arrayNumber,
				normalSpeed = 30, fastSpeed = 5;

	public char DiaGen[], choiceGen[], nameGen[];
	
	public Timer DiaTimer, choiceTimer, calculateTimer;

	public static void main(String[] args) {
		new Game();
	}
	
	public Game() {
		ui.makeUI(cHandler, mHandler, nHandler, kbHandler, saveloadHandler, InputHandler, screenWidth, screenHeight, lines, this);
		// // tc.introSequence();
		 	sm.bgsMusic.setFile(sm.titleScreenMusic);
		// 	sm.bgsMusic.playMusic();
		// 	sm.bgsMusic.loopMusic();
		// 		//tc.introSequence();
		// 		//Story.pauseTime = 19000;
		// 		//Story.pause();
				Story.startStats();
		tc.showTitleScreen();
	}
	
	public class ChoiceHandler implements ActionListener{
		public void actionPerformed(ActionEvent event) {
				
			String theChoice = event.getActionCommand();
			sm.se.setFile(sm.buttonsfx);
			sm.se.playButtonSFX(); // STANDARD BUTTON NOISE
			
				switch(theChoice) {
					case "start":
						textGeneration();
						ui.bgPanel.remove(ui.bgPic);
						tc.showDialogue();
						intro.intro0Game();
						//Story.goodbedroomExit12();
						tc.showName();
						saveAction();
						break;
					case "continue":
						textGeneration();
						ui.bgPanel.remove(ui.bgPic);
						sm.bgsMusic.stopMusic();
						tc.showDialogue();
						loadAction();
						break;
						
					case "dialogue":
						sm.se.setFile(sm.buttonsfx);
						sm.se.playButtonSFX();
						Story.dialogueTracker(nextDialogue);
						break;
						
					case "male":
						selected = "HE";
						gender = "she";
						storyLines.subInGender = gender;
						Story.dialogueTracker(nextDialogue);
						saveAction();
						break;
					case "female":
						selected = "SHE";
						gender = "he";
						storyLines.subInGender = gender;
						Story.dialogueTracker(nextDialogue);
						saveAction();
						break;					
					case "inclusive":
						selected = "THEY";
						gender = "they";
						storyLines.subInGender = gender;
						Story.dialogueTracker(nextDialogue);
						saveAction();
						break;
						
					case "c1":
						Story.progressTracker(nextPosition1); break;
					case "c2":
						Story.progressTracker(nextPosition2); break;
					case "c3":
						Story.progressTracker(nextPosition3); break;
					case "c4":
						Story.progressTracker(nextPosition4); break;	
			}
		}
	}

	public void textGeneration() {
		//INTRO GENERATION
		DiaTimer = new Timer(normalSpeed, new ActionListener(){
			public void actionPerformed(ActionEvent ie) {
				enableKeys = 0;
				ui.dialoguePanel.setVisible(false);
				DiaGen = dialogues.diaText[diatextTracker].toCharArray();
				arrayNumber = DiaGen.length;
				
					if((letterTracker%3) == 0){
						sm.se.setFile1(sm.typesfx);
						sm.se.playTypeSFX();
					}
					
				String letterGen = ""; String space = "";
			
				letterGen = space + DiaGen[letterTracker]; 
				ui.mainTextArea.append(letterGen);
				
				letterTracker++;
				if(letterTracker == arrayNumber) {
					letterTracker = 0;
					DiaTimer.stop();
					diatextTracker++;
					ui.dialoguePanel.setVisible(true);
					enableKeys = 1;
				}
			}
		});

		//CHOICE MOMENT ANIMATION
		choiceTimer = new Timer(normalSpeed, new ActionListener(){
			public void actionPerformed(ActionEvent c) {
				ui.dialoguePanel.setVisible(false);
				choiceGen = questions.questionText[questiontextTracker].toCharArray();
				arrayNumber = choiceGen.length;
				
					if((letterTracker%2) == 0){
						sm.se.setFile1(sm.typesfx);
						sm.se.playTypeSFX();
					}
					
				String letterGen = ""; String space = "";
				
				letterGen = space + choiceGen[letterTracker]; 
				ui.mainTextArea.append(letterGen);
				
				letterTracker++;
				if(letterTracker == arrayNumber) {
					letterTracker = 0;
					choiceTimer.stop();
					questiontextTracker++;
					tc.showChoices();
				}
			}
		});

		//CALCULATION MOMENT ANIMATION
		calculateTimer = new Timer(normalSpeed, new ActionListener(){
			public void actionPerformed(ActionEvent cal) {
				ui.dialoguePanel.setVisible(false);
				choiceGen = questions.questionText[questiontextTracker].toCharArray();
				arrayNumber = choiceGen.length;
				
					if((letterTracker%2) == 0){
						sm.se.setFile1(sm.typesfx);
						sm.se.playTypeSFX();
					}
					
				String letterGen = ""; String space = "";
				
				letterGen = space + choiceGen[letterTracker]; 
				ui.mainTextArea.append(letterGen);
				
				letterTracker++;
				if(letterTracker == arrayNumber) {
					letterTracker = 0;
					calculateTimer.stop();
					questiontextTracker++;
					tc.inputAnswer();
				}
			}
		});
	}
	
	public void startDialogue(){
		Story.number++;
		gameStory.increaseCP = 0; gameStory.decreaseCP = 0;
		ui.mainTextArea.setText("");
		ui.dialogueBox.setText(">");
		DiaTimer.start();
		enableKeys = 0;
		System.out.println("DIALOGUE SUCCESS: " + Story.number);
	}
	
	public class MouseHandler implements MouseInputListener{
		@Override
		public void mouseClicked(MouseEvent e) {	
		}
		@Override
		public void mousePressed(MouseEvent e) {
			DiaTimer.setDelay(fastSpeed);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			DiaTimer.setDelay(normalSpeed);
		}
		@Override
		public void mouseEntered(MouseEvent e) {
		}
		@Override
		public void mouseExited(MouseEvent e) {
		}
		@Override
		public void mouseDragged(MouseEvent e) {
		}
		@Override
		public void mouseMoved(MouseEvent e) {
		}
	}
	
	public class NameHandler implements ActionListener{
		public void actionPerformed(ActionEvent in) {
			if(in.getSource() == ui.nameInputBTN) {
				sm.se.setFile(sm.buttonsfx);
				sm.se.playButtonSFX(); // STANDARD BUTTON NOISE
					playerName = ui.nameInput.getText();
					if(playerName== null) {
						Story.dialogueTracker("intro0");
						diatextTracker = 0;
						tc.showName();
						intro.intro0Game();
					}
					else {
						gameStory.name = playerName;
						Story.dialogueTracker(nextDialogue);
						saveAction();
					}
			}
		}
	}

	public class AnswerHandler implements ActionListener{
		public void actionPerformed(ActionEvent an) {
			if(an.getSource() == ui.submitInputBTN) {
				sm.se.setFile(sm.buttonsfx);
				sm.se.playButtonSFX(); // STANDARD BUTTON NOISE
					Game.playerAnswer = ui.answerInput.getText();
					Story.dialogueTracker(nextDialogue);
				}
		}
	}
	
	public class KeyboardHandler implements KeyListener{

		@Override
		public void keyTyped(KeyEvent e) {
		}
		@Override
		public void keyPressed(KeyEvent e) {
				if(e.getKeyCode()==MenuKeyEvent.VK_SPACE) {
					DiaTimer.setDelay(fastSpeed);
				}
				if(enableKeys == 1) {
					if(e.getKeyCode() == 'z' || e.getKeyCode() == 'Z') {
						click++;
						sm.se.setFile(sm.buttonsfx);
						sm.se.playButtonSFX();
						DiaTimer.stop();
						ui.mainTextArea.setText("");
						Story.dialogueTracker(nextDialogue);
				}	
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {
			if(e.getKeyCode()==MenuKeyEvent.VK_SPACE) {
				DiaTimer.setDelay(normalSpeed);
			}			
		}

	}

	public class SaveLoadHandler implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent SaveOrLoad) {
			String loadSLMenu = SaveOrLoad.getActionCommand();
			sm.se.setFile(sm.buttonsfx);
			sm.se.playButtonSFX();

				switch(loadSLMenu){
					case "save":
						saveAction();
						break;
					case "load":
						loadAction();
						break;
				}	
		}
	}
	public void saveAction(){
		try{
			FileOutputStream fos = new FileOutputStream("save.dat");
			BufferedOutputStream bos = new BufferedOutputStream(fos);
			ObjectOutputStream oos = new ObjectOutputStream(bos);

			DataStorage dStorage = new DataStorage();

			dStorage.playerName = playerName;
			dStorage.selectedGender = selected;

			dStorage.playerCP = playerStats.CP;
			dStorage.playerXP = playerStats.XP;
			dStorage.playermaxCP = playerStats.maxCP;

			dStorage.dialogueTracker = diatextTracker;
			dStorage.playerDialogue = currentDialogue;
			dStorage.nextStoryDialogue = nextDialogue;

			dStorage.questionTracker = questiontextTracker;
			dStorage.playerQuestion = currentQuestion;

			oos.writeObject(dStorage);
			oos.close();
			System.out.println();
			System.out.println("SAVE COMPLETE:");
			System.out.println("Player Name: " + dStorage.playerName);
			System.out.println("CP: " + dStorage.playerCP + " | " + "XP: " + dStorage.playerXP + " | " + "GENDER: " + dStorage.selectedGender + " | ");
			System.out.println("Current Position: " + dStorage.playerDialogue + " | " 
								+ "Dialogue Tracker Number: " + dStorage.dialogueTracker + " | "
								+ "Next Position: " + dStorage.nextStoryDialogue);
			System.out.println();
		}
		catch(Exception saveError){
			System.out.println("SAVE ERROR");
		}
	}
	public void loadAction(){
		try{
			FileInputStream fis = new FileInputStream("save.dat");
			BufferedInputStream bis = new BufferedInputStream(fis);
			ObjectInputStream ois = new ObjectInputStream(bis);

			DataStorage dStorage = (DataStorage)ois.readObject();

			playerName = dStorage.playerName;
			
			selected = dStorage.selectedGender;
			playerStats.CP = dStorage.playerCP;
			playerStats.XP = dStorage.playerXP;
			playerStats.maxCP = dStorage.playermaxCP;

			diatextTracker = dStorage.dialogueTracker;
			currentDialogue = dStorage.playerDialogue;
			nextDialogue = dStorage.nextStoryDialogue;

			questiontextTracker = dStorage.questionTracker;
			currentQuestion = dStorage.playerQuestion;

			ois.close();
			System.out.println();
			System.out.println("LOAD COMPLETE:");
			System.out.println("Player Name: " + playerName);
			System.out.println("CP: " + playerStats.CP + " | " + "XP: " + playerStats.XP + " | " + "GENDER: " + selected + " | ");
			System.out.println("Current Position: " + currentDialogue + " | " 
								+ "Dialogue Tracker Number: " + diatextTracker 
								+ " | " + "Next Position: " + nextDialogue);
			System.out.println();
		
			update();

		}
		catch(IOException LoadError){
			System.out.println("LOAD ERROR");
		}
		catch(ClassNotFoundException missing){
			System.out.println("MISSING FILE");
		}
	}
	
	public void update(){
		gameStory.name = playerName;
		UserInterface.XPNumberLabel.setText("<html><center>" + playerStats.XP + "<center><html>");
		UserInterface.ChancePointsNumberLabel.setText("<html><center>" + playerStats.CP + "<center><html>");
		Story.dialogueTracker(currentDialogue);
	}
}
