package com.r212.pokemon.dialogue;

import java.util.HashMap;
import java.util.Map;

/**
 * @author r212
 */
public class Dialogue {
	
	private Map<Integer, DialogueNode> nodes = new HashMap<Integer, DialogueNode>();
	
	public DialogueNode getNode(int id) {
		return nodes.get(id);
	}
	
	public void addNode(DialogueNode node) {
		this.nodes.put(node.getID(), node);
	}
	
	public int getStart() {
		return 0;
	}
	
	/**
	 * @return Number of nodes in this dialogue
	 */
	public int size() {
		return nodes.size();
	}
}
