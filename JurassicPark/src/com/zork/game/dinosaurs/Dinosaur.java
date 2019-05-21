package com.zork.game.dinosaurs;

import java.util.ArrayList;
import java.util.HashMap;

import com.zork.game.Game;
import com.zork.game.Room;

public abstract class Dinosaur {
	//The room in which the dinosaur currently is
	protected Room currentRoom;
	
	//East name to identify
	protected String name;
	
	//The room in which the dinosaur's range moves out from
	protected Room startRoom;
	
	//The arraylist of rooms that the dinosaur can move to
	//Hand-created by game designers
	protected ArrayList<Room> roomsInRange;
	
	
	protected HashMap<String, Room> getMap() {
		return Game.getMasterRoomMap();
	}
	
	public Room getCurrentRoom() {
		return currentRoom;
	}

	public void setCurrentRoom(Room currentRoom) {
		this.currentRoom = currentRoom;
	}

	public Room getStartRoom() {
		return startRoom;
	}

	public void setStartRoom(Room startRoom) {
		this.startRoom = startRoom;
		this.startRoom.getRoomInventory().addDinosaur(this);
	}
	
	

	//Picks a random direction until finds a direction it can move to
	public Room moveToNewRoom() {
		while(true) {
			int random = (int)(Math.random()*4);
			
			String direction;
			switch(random) {
			case 1:
				direction="north";
				break;
			case 2:
				direction="west";
				break;
			case 3:
				direction="south";
				break;
			default:
				direction="east";
				break;
			}
			Room nextRoom = currentRoom.nextRoom(direction);
			if(nextRoom==null) {
				//The dinosaur cannot move in this direction because there is no room
				//It will go in the loop until it moves into a valid position
			} else if(!roomsInRange.contains(nextRoom)){
				//The dinosaur cannot move in this direction because it's out of range
				//It will go in the loop until it moves into a valid position
			} else {
				//This is a valid room to move to
				
				if(!nextRoom.getRoomInventory().hasDinosaurs()) {
					currentRoom.getRoomInventory().removeDinosaur(this);
					currentRoom = nextRoom;
					currentRoom.getRoomInventory().addDinosaur(this);				
				} else {
					System.out.println(nextRoom.getRoomName()+" has dinos.");
				}
				return currentRoom;
			}
		}
	}
	
	public String toString(String s) {
		return(s + " named " + name);
	}
	
	//For testing purposes
	public void printCurrentRoom() {
		System.out.println("Dinosaur " + toString() + " is currently in Room " + getCurrentRoom().getRoomName());
	}
}