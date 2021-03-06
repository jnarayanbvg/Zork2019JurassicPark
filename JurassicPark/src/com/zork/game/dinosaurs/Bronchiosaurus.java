package com.zork.game.dinosaurs;

import java.util.ArrayList;
import java.util.HashMap;

import com.zork.game.Formatter;
import com.zork.game.Game;
import com.zork.game.Room;

public class Bronchiosaurus extends Dinosaur {
	private final double MOBILITY = 0.25;
	private final double AWARENESS = 0;
	private final int TURN_TO_KILL = -1;
	
	public Bronchiosaurus(Room startRoom, String name) {
		super(startRoom);
		roomsInRange = new ArrayList<Room>();
		roomsInRange.add(getMap().get("BRONCHIOSAURUS_NW"));	
		roomsInRange.add(getMap().get("BRONCHIOSAURUS_SW"));	
		roomsInRange.add(getMap().get("BRONCHIOSAURUS_NE"));	
		roomsInRange.add(getMap().get("BRONCHIOSAURUS_SE"));	
		roomsInRange.add(getMap().get("HALLWAY_3"));	
		roomsInRange.add(getMap().get("HALLWAY_4"));
		
		this.currentRoom = startRoom;
		this.name = name;
		mobility = MOBILITY;
		awareness = AWARENESS;
		turnToKill = TURN_TO_KILL;
		invincible = true;
		canAttackInTree = true;
		carnivore = false;
	}
	
	public void killPlayer() {
		System.out.println(Formatter.blockText("The Bronchiosaurus has become upset with your attempted attacks and has knocked you to the ground "
				+ "and stomped on your flailing corpse. You have died.", Formatter.getCutoff(),""));
		super.killPlayer();
	}
	
	public String toString() {
		return super.toString("Bronchiosaurus");
	}
}
