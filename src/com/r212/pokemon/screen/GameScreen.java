package com.r212.pokemon.screen;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Animation.PlayMode;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.r212.pokemon.PokemonGame;
import com.r212.pokemon.controller.*;
import com.r212.pokemon.dialogue.Dialogue;
import com.r212.pokemon.dialogue.DialogueNode;
import com.r212.pokemon.dialogue.LinearDialogueNode;
import com.r212.pokemon.model.Camera;
import com.r212.pokemon.model.DIRECTION;
import com.r212.pokemon.model.actor.Actor;
import com.r212.pokemon.model.actor.LimitedWalkingBehavior;
import com.r212.pokemon.model.actor.PlayerActor;
import com.r212.pokemon.model.world.World;
import com.r212.pokemon.model.world.cutscene.CutsceneEvent;
import com.r212.pokemon.model.world.cutscene.CutscenePlayer;
import com.r212.pokemon.screen.renderer.EventQueueRenderer;
import com.r212.pokemon.screen.renderer.TileInfoRenderer;
import com.r212.pokemon.screen.renderer.WorldRenderer;
import com.r212.pokemon.screen.transition.FadeInTransition;
import com.r212.pokemon.screen.transition.FadeOutTransition;
import com.r212.pokemon.ui.DialogueBox;
import com.r212.pokemon.ui.OptionBox;
import com.r212.pokemon.util.Action;
import com.r212.pokemon.util.AnimationSet;

import static com.r212.pokemon.screen.AngadBattleScreen.angad_defeated;
import static com.r212.pokemon.screen.BradyBattleScreen.brady_defeated;

/**
 * @author r212
 */
public class GameScreen extends AbstractScreen implements CutscenePlayer {

	private InputMultiplexer multiplexer;
	private DialogueController dialogueController;
	private ActorMovementController playerController;
	private StorylineController storylineController;
	private InteractionController interactionController;
	private OptionBoxController debugController;
	private BattleScreen battleScreen;
	private boolean angad_done = true;
	private boolean brady_done = true;
	private Actor Angad;
	private Actor Brady;
	
	private HashMap<String, World> worlds = new HashMap<String, World>();
	private World world;
	private PlayerActor player;
	private Camera camera;
	private Dialogue dialogue;
	
	/* cutscenes */
	private Queue<CutsceneEvent> eventQueue = new ArrayDeque<CutsceneEvent>();
	private CutsceneEvent currentEvent;
	
	private SpriteBatch batch;
	
	private Viewport gameViewport;
	
	private WorldRenderer worldRenderer;
	private EventQueueRenderer queueRenderer; // renders cutscenequeue
	private TileInfoRenderer tileInfoRenderer;
	private boolean renderTileInfo = false;
	
	private int uiScale = 2;
	
	private Stage uiStage;
	private Table dialogRoot;	// root table used for dialogues
	private Table menuRoot;		// root table used for menus (i.e. debug menu)
	private DialogueBox dialogueBox;
	private OptionBox optionsBox;
	private OptionBox debugBox;

	public GameScreen(PokemonGame app) {
		super(app);
		gameViewport = new ScreenViewport();
		batch = new SpriteBatch();

		TextureAtlas atlas = app.getAssetManager().get("res/graphics_packed/tiles/tilepack.atlas", TextureAtlas.class);
		
		AnimationSet animations = new AnimationSet(
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_north"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_south"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_east"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_west"), PlayMode.LOOP_PINGPONG),
				atlas.findRegion("brendan_stand_north"),
				atlas.findRegion("brendan_stand_south"),
				atlas.findRegion("brendan_stand_east"),
				atlas.findRegion("brendan_stand_west")
		);
		animations.addBiking(
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_north"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_south"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_east"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.4f/2f, atlas.findRegions("brendan_bike_west"), PlayMode.LOOP_PINGPONG));
		animations.addRunning(
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_north"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_south"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_east"), PlayMode.LOOP_PINGPONG), 
				new Animation(0.25f/2f, atlas.findRegions("brendan_run_west"), PlayMode.LOOP_PINGPONG));
		AnimationSet angadanimation = new AnimationSet(
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_north"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_south"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_east"), PlayMode.LOOP_PINGPONG),
				new Animation(0.4f/2f, atlas.findRegions("brendan_walk_west"), PlayMode.LOOP_PINGPONG),
				atlas.findRegion("brendan_stand_north"),
				atlas.findRegion("angad_stand_south"),
				atlas.findRegion("brendan_stand_east"),
				atlas.findRegion("brendan_stand_west")
		);

		Array<World> loadedWorlds = app.getAssetManager().getAll(World.class, new Array<World>());
		for (World w : loadedWorlds) {
			worlds.put(w.getName(), w);
		}
		world = worlds.get("littleroot_town");
		
		camera = new Camera();
		player = new PlayerActor(world, world.getSafeX(), world.getSafeY(), animations, this);
		world.addActor(player);

		Angad = new Actor(world, 3, 3, angadanimation);
		Angad.setName("Angad");
		LimitedWalkingBehavior angadsbrain = new LimitedWalkingBehavior(Angad, 0, 0, 0, 0, 0.3f, 1f, new Random());
		world.addActor(Angad, angadsbrain);
		Dialogue agreeting = new Dialogue();
		DialogueNode speak = new LinearDialogueNode("You smelly person! You cannot beat me!", 0);
		agreeting.addNode(speak);
		Angad.setDialogue(agreeting);

		Brady = new Actor(world, 14, 11, angadanimation);

		Brady.setName("Brady");
		LimitedWalkingBehavior bradysbrain = new LimitedWalkingBehavior(Angad, 0, 0, 0, 0, 0.3f, 1f, new Random());
		world.addActor(Brady, bradysbrain);
		Dialogue bgreeting = new Dialogue();
		DialogueNode bspeak = new LinearDialogueNode("I am brady", 0);
		bgreeting.addNode(bspeak);
		Brady.setDialogue(bgreeting);

		initUI();

		multiplexer = new InputMultiplexer();
		
		playerController = new ActorMovementController(player);
		dialogueController = new DialogueController(dialogueBox, optionsBox);
		storylineController = new StorylineController(getApp());
		interactionController = new InteractionController(player, dialogueController, getApp(), storylineController);
		debugController = new OptionBoxController(debugBox);
		debugController.addAction(new Action() {
			@Override
			public void action() {
				renderTileInfo = !renderTileInfo;
			}
		}, "Toggle show coords");
		
		multiplexer.addProcessor(0, debugController);
		multiplexer.addProcessor(1, dialogueController);
		multiplexer.addProcessor(2, playerController);
		multiplexer.addProcessor(3, storylineController);
		multiplexer.addProcessor(4, interactionController);

		
		worldRenderer = new WorldRenderer(getApp().getAssetManager(), world);
		queueRenderer = new EventQueueRenderer(app.getSkin(), eventQueue);
		tileInfoRenderer = new TileInfoRenderer(world, camera);
	}

	@Override
	public void dispose() {
		
	}

	@Override
	public void hide() {
		
	}

	@Override
	public void pause() {
		
	}
	
	@Override
	public void update(float delta) {
		if((angad_done && angad_defeated) || Gdx.input.isKeyJustPressed(Keys.F5)){
			System.out.println("STATUS UPDATE: Angad has been defeated");
			angad_done = false;
			Dialogue nomoreangad = new Dialogue();
			nomoreangad.addNode(new LinearDialogueNode("Damn you got me!\nBut this was just a one time thing.\nIm tired now, we'll rematch later", 0));
			Angad.setDialogue(nomoreangad);
		}
		if((brady_done && brady_defeated) || Gdx.input.isKeyJustPressed(Keys.F5)){
			System.out.println("STATUS UPDATE: Brady has been defeated");
			brady_done = false;
			Dialogue nomorebrady = new Dialogue();
			nomorebrady.addNode(new LinearDialogueNode("I must've been exhausted!", 0));
			Brady.setDialogue(nomorebrady);
		}
//		if(Gdx.input.isKeyJustPressed(Keys.F5)) {
//			getApp().startTransition(
//					this,
//					getApp().getBattleScreen(),
//					new FadeOutTransition(0.5f, Color.BLACK, getApp().getTweenManager(), getApp().getAssetManager()),
//					new FadeInTransition(0.5f, Color.BLACK, getApp().getTweenManager(), getApp().getAssetManager()),
//					new Action(){
//						@Override
//						public void action() {
//						}
//					});
//		}

		while (currentEvent == null || currentEvent.isFinished()) { // no active event
			if (eventQueue.peek() == null) { // no event queued up
				currentEvent = null;
				break;
			} else {					// event queued up
				currentEvent = eventQueue.poll();
				currentEvent.begin(this);
			}
		}
		
		if (currentEvent != null) {
			currentEvent.update(delta);
		}

		if (currentEvent == null) {
			playerController.update(delta);
		}


		storylineController.update(delta);
		dialogueController.update(delta);
		
		if (!dialogueBox.isVisible()) {
			camera.update(player.getWorldX()+0.5f, player.getWorldY()+0.5f);
			world.update(delta);
		}
		uiStage.act(delta);
	}

	@Override
	public void render(float delta) {
		gameViewport.apply();
		batch.begin();
		worldRenderer.render(batch, camera);
		queueRenderer.render(batch, currentEvent);
		if (renderTileInfo) {
			tileInfoRenderer.render(batch, Gdx.input.getX(), Gdx.input.getY());
		}
		batch.end();
		
		uiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
		uiStage.getViewport().update(width/uiScale, height/uiScale, true);
		gameViewport.update(width, height);
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		if (currentEvent != null) {
			currentEvent.screenShow();
		}
	}
	
	private void initUI() {
		uiStage = new Stage(new ScreenViewport());
		uiStage.getViewport().update(Gdx.graphics.getWidth()/uiScale, Gdx.graphics.getHeight()/uiScale, true);
		//uiStage.setDebugAll(true);		// Uncomment to debug UI
		
		/*
		 * DIALOGUE SETUP
		 */
		dialogRoot = new Table();
		dialogRoot.setFillParent(true);
		uiStage.addActor(dialogRoot);
		
		dialogueBox = new DialogueBox(getApp().getSkin());
		dialogueBox.setVisible(false);
		
		optionsBox = new OptionBox(getApp().getSkin());
		optionsBox.setVisible(false);
		
		Table dialogTable = new Table();
		dialogTable.add(optionsBox)
						.expand()
						.align(Align.right)
						.space(8f)
						.row();
		dialogTable.add(dialogueBox)
						.expand()
						.align(Align.bottom)
						.space(8f)
						.row();
		
		
		dialogRoot.add(dialogTable).expand().align(Align.bottom);
		
		/*
		 * MENU SETUP
		 */
		menuRoot = new Table();
		menuRoot.setFillParent(true);
		uiStage.addActor(menuRoot);
		
		debugBox = new OptionBox(getApp().getSkin());
		debugBox.setVisible(false);
		
		Table menuTable = new Table();
		menuTable.add(debugBox).expand().align(Align.top | Align.left);
		
		menuRoot.add(menuTable).expand().fill();
	}
	
	public void changeWorld(World newWorld, int x, int y, DIRECTION face) {
		player.changeWorld(newWorld, x, y);
		this.world = newWorld;
		player.refaceWithoutAnimation(face);
		this.worldRenderer.setWorld(newWorld);
		this.camera.update(player.getWorldX()+0.5f, player.getWorldY()+0.5f);
	}

	@Override
	public void changeLocation(World newWorld, int x, int y, DIRECTION facing, Color color) {
		getApp().startTransition(
				this, 
				this, 
				new FadeOutTransition(0.8f, color, getApp().getTweenManager(), getApp().getAssetManager()), 
				new FadeInTransition(0.8f, color, getApp().getTweenManager(), getApp().getAssetManager()), 
				new Action() {
					@Override
					public void action() {
						changeWorld(newWorld, x, y, facing);
					}
				});
	}

	@Override
	public World getWorld(String worldName) {
		return worlds.get(worldName);
	}

	@Override
	public void queueEvent(CutsceneEvent event) {
		eventQueue.add(event);
	}
}
