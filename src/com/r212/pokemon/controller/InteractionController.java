package com.r212.pokemon.controller;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.r212.pokemon.PokemonGame;
import com.r212.pokemon.dialogue.Dialogue;
import com.r212.pokemon.model.DIRECTION;
import com.r212.pokemon.model.Tile;
import com.r212.pokemon.model.actor.Actor;
import com.r212.pokemon.screen.AbstractScreen;
import com.r212.pokemon.screen.AngadBattleScreen;

/**
 * Controller that interacts with what is in front of the player Actor.
 * 
 * @author r212
 */
public class InteractionController extends InputAdapter {
	
	private Actor a;
	private DialogueController dialogueController;
	private PokemonGame game;

	public InteractionController(Actor a, DialogueController dialogueController, PokemonGame game) {
		this.a = a;
		this.dialogueController = dialogueController;
		this.game = game;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.ENTER) {
			Tile target = a.getWorld().getMap().getTile(a.getX()+a.getFacing().getDX(), a.getY()+a.getFacing().getDY());
			if (target.getActor() != null) {
				Actor targetActor = target.getActor();
				if (targetActor.getDialogue() != null) {
					if (targetActor.refaceWithoutAnimation(DIRECTION.getOpposite(a.getFacing()))){
						Dialogue dialogue = targetActor.getDialogue();
						dialogueController.startDialogue(dialogue, game);
					}
				}
			}

			return false;
		}
		return false;
	}

}
