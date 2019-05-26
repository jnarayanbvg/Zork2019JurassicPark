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
	
	//Percentage chance that dino will move each term
	protected double mobility;
	
	//Chance each turn they become aware of player
	protected double awareness;
	
	//If they are aware player is in room
	protected boolean aware;
	
	
	
	public Dinosaur(Room startRoom) {
		setStartRoom(startRoom);
	}
	
	
	
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
	public Room moveToNewRoom(DinosaurController c) {
		if(!aware) {
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
						
						//Check to see if you left a room and have to tell the player
						if(c.getLastDinosaur()==this && c.getLastStatus().equals("unaware")) {
							c.setStatus(this, "left");
						}
					}
					return currentRoom;
				}
			}
		} else {
			double followChance = awareness + (1-awareness)/2;
			
			//Lost the trail
			if(Math.random()>followChance) {
				aware = false;
				c.setStatus(this, "lost");
			} else {
				//Follow player
				if(roomsInRange.contains(Game.getCurrentRoom()) && !Game.getCurrentRoom().getRoomInventory().hasDinosaurs()) {
					currentRoom.getRoomInventory().removeDinosaur(this);
					currentRoom = Game.getCurrentRoom();
					currentRoom.getRoomInventory().addDinosaur(this);		
					c.setStatus(this, "follow");
				} else {
					//Lost the trail
					aware = false;
					c.setStatus(this, "lost");
				}
			}
		}
		return currentRoom;
	}
	
	public void determineAwareness(DinosaurController c) {
		if(currentRoom == Game.getCurrentRoom()) {
			if(awareness==0.0) {
				c.setStatus(this, "peace");
			} else {
				if(!aware) {
					if(Math.random()<awareness && c.isAware()==false) {
						aware = true;
						c.setStatus(this, "aware");
					} else {
						c.setStatus(this, "unaware");
					}
				}
			}
		} else {
			aware = false;
		}
	}
	
	public boolean isAware() {
		return aware;
	}
	
	
	public String toString(String s) {
		//return(s + " named " + name);
		return s;
	}
	
	//For testing purposes
	public void printCurrentRoom() {
		System.out.println("Dinosaur " + toString() + " is currently in Room " + getCurrentRoom().getRoomName());
	}
}
