package com.r212.pokemon.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.r212.pokemon.PokemonGame;
import com.r212.pokemon.dialogue.ChoiceDialogueNode;
import com.r212.pokemon.dialogue.Dialogue;
import com.r212.pokemon.dialogue.DialogueNode;
import com.r212.pokemon.dialogue.DialogueTraverser;
import com.r212.pokemon.dialogue.LinearDialogueNode;
import com.r212.pokemon.screen.AbstractScreen;
import com.r212.pokemon.screen.AngadBattleScreen;
import com.r212.pokemon.screen.transition.FadeInTransition;
import com.r212.pokemon.screen.transition.FadeOutTransition;
import com.r212.pokemon.ui.DialogueBox;
import com.r212.pokemon.ui.OptionBox;
import com.r212.pokemon.util.Action;

import static com.r212.pokemon.screen.AngadBattleScreen.angad_defeated;


/**
 * Controller for the game's dialogue system.
 * 
 * @author r212
 */
public class DialogueController extends InputAdapter {
	
	private DialogueTraverser traverser;
	private DialogueBox dialogueBox;
	private OptionBox optionBox;
	PokemonGame game;

	public DialogueController(DialogueBox box, OptionBox optionBox) {
		this.dialogueBox = box;
		this.optionBox = optionBox;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		if (dialogueBox.isVisible()) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		if (optionBox.isVisible()) {
			if (keycode == Keys.UP || keycode == Keys.W) {
				optionBox.moveUp();
				return true;
			} else if (keycode == Keys.DOWN || keycode == Keys.S) {
				optionBox.moveDown();
				return true;
			}
		}
		if (dialogueBox.isVisible() && !dialogueBox.isFinished()) {
			return false;
		}
		if (traverser != null && keycode == Keys.ENTER) { // continue through tree
			DialogueNode thisNode = traverser.getNode();
			
			if (thisNode instanceof LinearDialogueNode)  {
				LinearDialogueNode node = (LinearDialogueNode)thisNode;
				if (node.getPointers().isEmpty()) { // dead end, since no pointers
					traverser = null;				// end dialogue
					dialogueBox.setVisible(false);
				} else {
					progress(0); // progress through first pointer
				}
			}
			if (thisNode instanceof ChoiceDialogueNode)  {
				ChoiceDialogueNode node = (ChoiceDialogueNode)thisNode;
				progress(optionBox.getIndex());
			}
			
			return true;
		}
		if (dialogueBox.isVisible()) {
			return true;
		}
		return false;
	}
	
	public void update(float delta) {
		if (dialogueBox.isFinished() && traverser != null) {
			DialogueNode nextNode = traverser.getNode();
			if (nextNode instanceof ChoiceDialogueNode) {
				optionBox.setVisible(true);
			}

		}
		if (!angad_defeated && dialogueBox.isFinished() && traverser != null && Gdx.input.isKeyJustPressed(Keys.ENTER)){
			game.startTransition(
					game.getGameScreen(),
					new AngadBattleScreen(game),
					new FadeOutTransition(0.5f, Color.BLACK, game.getTweenManager(), game.getAssetManager()),
					new FadeInTransition(0.5f, Color.BLACK, game.getTweenManager(), game.getAssetManager()),
					new Action(){
						@Override
						public void action() {
							System.out.println("STATUS UPDATE: A fight with Angad has started");
						}
					});
		}
	}
	
	public void startDialogue(Dialogue dialogue, PokemonGame game) {
		this.game = game;
		traverser = new DialogueTraverser(dialogue);
		dialogueBox.setVisible(true);
		
		DialogueNode nextNode = traverser.getNode();
		if (nextNode instanceof LinearDialogueNode) {
			LinearDialogueNode node = (LinearDialogueNode)nextNode;
			dialogueBox.animateText(node.getText());



		}
		if (nextNode instanceof ChoiceDialogueNode) {
			ChoiceDialogueNode node = (ChoiceDialogueNode)nextNode;
			dialogueBox.animateText(node.getText());
			optionBox.clear();
			for (String s : node.getLabels()) {
				optionBox.addOption(s);
			}
		}

	}
	
	private void progress(int index) {
		optionBox.setVisible(false);
		DialogueNode nextNode = traverser.getNextNode(index);
		if (nextNode instanceof LinearDialogueNode) {
			LinearDialogueNode node = (LinearDialogueNode)nextNode;
			dialogueBox.animateText(node.getText());
		}
		if (nextNode instanceof ChoiceDialogueNode) {
			ChoiceDialogueNode node = (ChoiceDialogueNode)nextNode;
			dialogueBox.animateText(node.getText());
			optionBox.clearChoices();
			for (String s : node.getLabels()) {
				optionBox.addOption(s);
			}
		}
	}
	
	public boolean isDialogueShowing() {
		return dialogueBox.isVisible();
	}
}
