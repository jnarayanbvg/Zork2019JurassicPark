package com.zork.game;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import com.zork.game.dinosaurs.DinosaurController;

import javazoom.jl.player.Player;

/**
 * Class Game - the main class of the "Zork" game.
 *
 * Author: Michael Kolling Version: 1.1 Date: March 2000
 * 
 * This class is the main class of the "Zork" application. Zork is a very
 * simple, text based adventure game. Users can walk around some scenery. That's
 * all. It should really be extended to make it more interesting!
 * 
 * To play this game, create an instance of this class and call the "play"
 * routine.
 * 
 * This main class creates and initialises all the others: it creates all rooms,
 * creates the parser and starts the game. It also evaluates the commands that
 * the parser returns.
 */
public class Game {
	private Parser parser;
	private Room currentRoom;
	private int timeLeft = -1; // -1 means time has not been initialized; 1440 minutes when really initialized
	private final int MAX_TIME = 1440;
	private final int TIME_IN_HOUR = 60;
	private final String SIREN_POSITION = "Supply Shed";

	private DinosaurController dinosaurController;	

	// This is a MASTER object that contains all of the rooms and is easily
	// accessible.
	// The key will be the name of the room -> no spaces (Use all caps and
	// underscore -> Great Room would have a key of GREAT_ROOM
	// In a hashmap keys are case sensitive.
	// masterRoomMap.get("GREAT_ROOM") will return the Room Object that is the Great
	// Room (assuming you have one).
	private static HashMap<String, Room> masterRoomMap;

	public static HashMap<String, Room> getMasterRoomMap() {
		return masterRoomMap;
	}

	private void initRooms(String fileName) throws Exception {
		masterRoomMap = new HashMap<String, Room>();
		Scanner roomScanner;
		try {
			HashMap<String, HashMap<String, String>> exits = new HashMap<String, HashMap<String, String>>();
			roomScanner = new Scanner(new File(fileName));
			while (roomScanner.hasNext()) {
				Room room = new Room();
				// Read the Name
				String roomName = roomScanner.nextLine();
				room.setRoomName(roomName.split(":")[1].trim());
				// Read the Description
				String roomDescription = roomScanner.nextLine();
				room.setDescription(roomDescription.split(":")[1].replaceAll("<br>", "\n").trim());
				// Read the Exits
				String roomExits = roomScanner.nextLine();
				// An array of strings in the format E-RoomName
				String[] rooms = roomExits.split(":")[1].split(",");
				HashMap<String, String> temp = new HashMap<String, String>();
				for (String s : rooms) {
					temp.put(s.split("-")[0].trim(), s.split("-")[1]);
				}

				exits.put(roomName.substring(10).trim().toUpperCase().replaceAll(" ", "_"), temp);

				// This puts the room we created (Without the exits in the masterMap)
				masterRoomMap.put(roomName.toUpperCase().substring(10).trim().replaceAll(" ", "_"), room);

				// Now we better set the exits.
			}

			for (String key : masterRoomMap.keySet()) {
				Room roomTemp = masterRoomMap.get(key);
				HashMap<String, String> tempExits = exits.get(key);
				for (String s : tempExits.keySet()) {
					// s = direction
					// value is the room.

					String roomName2 = tempExits.get(s.trim());
					Room exitRoom = masterRoomMap.get(roomName2.toUpperCase().replaceAll(" ", "_"));
					roomTemp.setExit(s.trim().charAt(0), exitRoom);

				}

			}

			roomScanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the game and initialise its internal map.
	 */
	public Game() {
		try {
			initRooms("data/rooms.dat");
			currentRoom = masterRoomMap.get("BOAT_LANDING_A");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		parser = new Parser();
	}

	/**
	 * Main play routine. Loops until end of play.
	 */
	public void play() {
		printWelcome();
		// Enter the main command loop. Here we repeatedly read commands and execute
		// them until the game is over.

		dinosaurController = new DinosaurController();
		RoomItemInit.initRooms();

		boolean finished = false;
		while (!finished) {
			Command command = parser.getCommand();
			finished = processCommand(command, false); // FALSE is for inFight variable, not yet implemented
		}
		System.out.println("Thank you for playing.");
	}

	/**
	 * Print out the opening message for the player.
	 */
	private void printWelcome() {
		System.out.println("-------------------------------------------------------------------");
		System.out.print("Welcome to Isla Nublar, the site of Jurassic Park! \nYou are a reporter who is looking "
				+ "to write an article to \nexpose the dangerous experiments being conducted on the island. "
				+ "\nType '?' or 'help' if you need help. \n");
		System.out.println("-------------------------------------------------------------------");
		System.out.println(currentRoom.longDescription());
	}

	/*
	 * Plays a sound from a specific directory
	 */
	public static void play(String file) {
		String filename = file;
		try {

			FileInputStream fis = new FileInputStream(filename);
			BufferedInputStream bis = new BufferedInputStream(fis);
			Player player = new Player(bis);
			player.play();
		} catch (Exception e) {
			System.out.println("Problem playing file " + filename);
			System.out.println(e);
		}
	}

	/**
	 * Given a command, process (that is: execute) the command. If this command ends
	 * the game, true is returned, otherwise false is returned.
	 * 
	 * @param inFight
	 */
	private boolean processCommand(Command command, boolean inFight) {
		if (command.isUnknown()) {
			System.out.println("I don't know what you mean...");
			return false;
		}
		String commandWord = command.getCommandWord();
		switch (commandWord) {
		case "test":
			dinosaurController.printAllDinosaurs();
			break;
		case "help":
			printHelp();
			break;
		case "?":
			printHelp();
			break;
		case "go":
			goRoom(command);
			break;
		case "quit":
			if (command.hasSecondWord())
				System.out.println("Quit what?");
			else
				return true; // signal that we want to quit
			break;
		case "use":
			use(command);
			break;
		case "climb":
			climb(command);
			break;
		case "inventory":
			checkInventory(command);
			break;
		case "ammo":
			checkAmmo(command);
			break;
		case "drop":
			 drop(command);
			break;
		case "grab":
			grab(command);
			break;
		case "attack":
			attack(command);
		case "eqiup":
			equip(command);
		case "unequip":
			unequip(command);

		default:
			if (!inFight) { // the following commands are for when you are not in battle
				switch (commandWord) {
				case "look":
					look(command);
				case "search":
					search(command);
				case "heal":
					heal(command);
					break;
				case "time":
					checkTime(command);
					break;
				}
			} else {
				System.out.println("You must do a battle command!");

			}

		}

		return false;

	}

	private void equip(Command command) {
		if(!command.hasSecondWord()){
			System.out.println("what do you want to equip?");
		//}else if(!player.)
	}

	private void unequip(Command command) {
	}

	private void checkTime(Command command) {
	}

	private void heal(Command command) {
	}

	private void search(Command command) {
	}

	private void grab(Command command) {
	}

	private void attack(Command command) {
	}

	private void drop(Command command) {
	}

	private void checkAmmo(Command command) {
	}

	private void checkInventory(Command command) {
	}

	private void climb(Command command) {
	}

	private void use(Command command) {

	}

	// implementations of user commands:
	/**
	 * Print out some help information. Here we print some stupid, cryptic message
	 * and a list of the command words.
	 */
	private void printHelp() {
		System.out.println("You are a reporter who is looking to write an article");
		System.out.println("to expose the dangerous experiments being conducted on");
		System.out.println("Isla Nublar, site of Jurassic Park.");
		System.out.println();
		System.out.println("Your command words are:");
		parser.showCommands();
	}

	/**
	 * Try to go to one direction. If there is an exit, enter the new room,
	 * otherwise print an error message.
	 */
	private void goRoom(Command command) {
		if (!command.hasSecondWord()) {
			// if there is no second word, we don't know where to go...
			System.out.println("Go where?");
			return;
		}
		String direction = command.getSecondWord();
// Try to leave current room.
		Room nextRoom = currentRoom.nextRoom(direction);
		if (nextRoom == null)
			System.out.println("You cannot go that way!");
		else {
			currentRoom = nextRoom;
			System.out.println(currentRoom.longDescription());

			// Print out the siren message in-story to open the facilities
			if (currentRoom.getRoomName().equals(SIREN_POSITION)) {
				if (getTimeLeft() == -1)
					setTimeLeft(MAX_TIME);

				System.out.println(Formatter.blockText("Inside the shed, you hear sirens begin to blare and an alert "
						+ "message sounds through the speakers:", Formatter.getCutoff(), "") + "\n");
				System.out.println(Formatter.blockText("\"Attention! Attention everyone on Jurassic Park! Worsening "
						+ "conditions have made it unsafe to continue work here. All staff personnel must evacuate "
						+ "the island immediately. Approaching storms from the south of the island "
						+ "are forcing all personnel to make their way to the northeast shipyard. "
						+ "I repeat, all personnel to the northeast shipyard. Control centers are losing power, "
						+ "meaning enclosure doors may be starting to open due to technical malfunctions. "
						+ "The last personnel ship will evacuate in " + getTimeLeft() / TIME_IN_HOUR
						+ " hours. I repeat, you have " + getTimeLeft() / TIME_IN_HOUR
						+ " hours to get off the island. Over and out.\"", Formatter.getCutoff(), "\t") + "\n");
				System.out.println(Formatter.blockText("You hear clanging of metal outside of the shed - the security "
						+ "doors have opened. You have limited time to gather information on the island before you "
						+ "need to escape. You'll need to evade the creaters unleashed on the island, and if not, face death.",
						Formatter.getCutoff(), " ") + "\n");

			}
		}
	}

	private void look(Command command) {
		System.out.println(Phrases.getLook().get((int) (Math.random() * Phrases.getLook().size())));

		// Look at environment
		ArrayList<EnvironmentItem> env = currentRoom.getRoomInventory().getEnvironment();
		//List all the environmentItems in the room
		if (env.size() == 1) {
			System.out.print(Phrases.getLookEnv().get((int) (Math.random() * Phrases.getLookEnv().size())) + "only ");
			for (EnvironmentItem obj : env)
				System.out.print(obj.toString());
			System.out.println(". ");
		} else if (env.size() > 1) {
			System.out.print(Phrases.getLookEnv().get((int) (Math.random() * Phrases.getLookEnv().size())));
			for (int i = 0; i < env.size(); i++) {
				if (i < env.size() - 1) {
					System.out.print(env.get(i).toString());
					if(env.size()>2) System.out.print(", ");
					else System.out.print(" ");
				} else
					System.out.print("and " + env.get(i).toString());
			}
			System.out.println(". ");
		}
		String seeInEnvironment = "";
		for(EnvironmentItem obj : env) {
			if(obj.getItems().size()>0) {
				if(seeInEnvironment.equals("")) {
					seeInEnvironment += Phrases.getLookInEnv().get((int) (Math.random() * Phrases.getLookInEnv().size()));
				} else seeInEnvironment += "and ";
				seeInEnvironment += obj.toString() + " ";
			}
		}
		if(!seeInEnvironment.equals("")) System.out.println(seeInEnvironment.substring(0,seeInEnvironment.length()-1)+".");
		
		
		
		//Look at roomItems
		ArrayList<UsableItem> items = currentRoom.getRoomInventory().getItems();
		
		
		//Check both
		if(env.size()==0 && items.size()==0) {
			System.out.println(Phrases.getLookNothing().get((int) (Math.random() * Phrases.getLookNothing().size())));
		}
		
	}

	private int getTimeLeft() {
		return timeLeft;
	}

	private void setTimeLeft(int newTime) {
		timeLeft = newTime;
	}

}
