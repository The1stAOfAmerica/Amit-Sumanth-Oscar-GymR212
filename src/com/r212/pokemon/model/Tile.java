package com.r212.pokemon.model;

import com.r212.pokemon.model.actor.Actor;
import com.r212.pokemon.model.world.WorldObject;
import com.r212.pokemon.worldloader.LTerrain;

/**
 * @author r212
 */
public class Tile {
	
	private LTerrain terrain;
	private WorldObject object;
	private Actor actor;
	
	private boolean walkable = true;

	public Tile(LTerrain terrain) {
		this.terrain = terrain;
	}
	
	public void setTerrain(LTerrain terrain) {
		this.terrain = terrain;
	}
	
	public void setWalkable(boolean walkable) {
		this.walkable = walkable;
	}

	public LTerrain getTerrain() {
		return terrain;
	}

	public Actor getActor() {
		return actor;
	}


	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public WorldObject getObject() {
		return object;
	}

	public void setObject(WorldObject object) {
		this.object = object;
	}
	
	public boolean walkable() {
		return walkable;
	}
	
	/**
	 * Fires when an Actor steps on the Tile. Called when the Actor is just about finished with his/her step.
	 */
	public void actorStep(Actor a) {
		
	}
	
	public boolean actorBeforeStep(Actor a) {
		return true;
	}

}


