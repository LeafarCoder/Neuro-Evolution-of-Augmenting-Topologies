package rafa.Main.Simulations.Created.FlappyBird;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Random;

import ddf.minim.AudioSample;
import ddf.minim.Minim;
// import ddf.minim.*;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class FlappyBird_GUI extends PApplet implements Serializable{

	private static final long serialVersionUID = 1L;
	
	// Parent simulator
	private FlappyBird_Sim parent_simulator;
	private boolean enableDraw;
	private boolean soundOn;
	private double[] results;
	private double[] inputs;
	private int singleSimTime;

	// BIRD
	Bird bird;
	ArrayList<Color> bird_colors;
	
	// SPRITE IMAGES:
	private PImage ground_img;
	private PImage[] background_img = new PImage[2];
	private PImage down_pipe_img;
	private PImage up_pipe_img;
	private Digits digits;
	private PImage tapToStart_img;
	private PImage gameOver_img;
	private PImage getReady_img;
	
	// SOUNDS
	private AudioSample wing_flap_sound;
	private AudioSample hit_sound;
	private AudioSample point_sound;
	
	// *************************************************** GAME VARIABLES ***************************************************
	
	private Pipes pipes;
	private int pipeOpenSize = 100;
	private int distToNextPipe;
	private int distToNextPipe2;
	private int pipe_x_spacing;
	private int pipe_center_offset = 50;
	
	private int background_index;
	private int points;	// pipes crossed
	
	private int gameFrameRate = 60;
	private float boost_vel = -5;
	
	private int game_width;
	private int game_height;
	private int game_ground_height;
	private float game_x_speed;
	private float game_ground_x_pos;
	private float scale_factor = 0.6f;

	private char lastKey;	// keeps the last key character pressed
	private boolean collisionOccured;
	private int gameState; // 0: game didnt start yer; 1: game in process; 2: game finished
	
	// *************************************************** GAME VARIABLES (end) ***********************************************
	
	// OTHER VARIABLES
	private boolean collisionTest = false;	// used to display boxes for collision detection for programming purposes
	String rsc_path;
	String img_path;
	String sound_path;
	
	public FlappyBird_GUI(FlappyBird_Sim parent){
		this.parent_simulator = parent;
	}
	

	// ***************************************** SETUP *******************************************
	public void setup(){
		results = new double[4];
		inputs = new double[2];
		singleSimTime = millis();
		
		// get Resources path
		getResourcesPath();

		// GET SOUNDS
		loadSounds(sound_path);
				
		// GET IMAGES
		loadSprites(img_path);
		
		// MAKE BIRD
		defineColors();
		bird = new Bird(img_path, bird_colors.get(new Random().nextInt(bird_colors.size())));

		resizeSpritesSetup();

		// use landscape after resize to set the game width and height:
		resizeImage(background_img[0], scale_factor);
		resizeImage(background_img[1], scale_factor);
		game_width = background_img[0].width;
		game_height = background_img[0].height;
		
		// SET WINDOW SIZE
		size((int)(game_width), (int)(game_height));

		// GAME PROPERTIES
		gameState = 0;
		game_ground_height = ground_img.height;
		distToNextPipe = game_width * 10;
		game_x_speed = -2f;
		game_ground_x_pos = 0;
		points = 0;
		background_index = new Random().nextInt(2);
		collisionOccured = false;
		
		// System.out.println("\t" + single_game_time_start);
		// define pipes
		PGraphics pipe = createGraphics(down_pipe_img.width, down_pipe_img.height + up_pipe_img.height + pipeOpenSize, JAVA2D);
		pipe.beginDraw();
		pipe.image(down_pipe_img, 0, 0);
		pipe.image(up_pipe_img, 0, up_pipe_img.height + pipeOpenSize);
		pipe.endDraw();
		PImage pipe_img = pipe.get();
		pipe_x_spacing = 150;
		pipes = new Pipes(pipe_img, 4, pipeOpenSize, game_width + 50, pipe_x_spacing, game_height - game_ground_height, game_ground_height);
		//pipes = new Pipes(pipe_img, 4, pipeOpenSize, game_width - 100, 150, game_height - game_ground_height, game_ground_height);
		distToNextPipe = pipes.getDistToClosestPipe((int)bird.getPosition().x);
		distToNextPipe2 = distToNextPipe;
		
		
		if(enableDraw){
			frameRate(gameFrameRate);
		}else{
			frameRate(500);
		}
		// frameRate(20);
	}
	
	public void draw(){

		if(enableDraw){
			background(255);
			image(background_img[background_index], 0, 0);		// background image
		}

		// ********************************* DRAW SPRITES **************************************

		if(enableDraw){
			drawSprites();

			digits.displayNumber(points, game_width/2, game_height/10);
		}

		// ******************************* DRAW SPRITES (end) ************************************
				
		
		if(gameState == 0){		// if game has not started (waiting for the first flap)
			gameState0();
		}else if(gameState == 1){	// if game has started:
			gameState1();
		}else if(gameState == 2){	// game has finished (bird has crashed into the ground or into pipes)
			gameState2();
			// erase these 2 lines after collision test:
			// game_x_speed = 0;
			// bird.setYVelocity(0f);
		}

		
		
		// Give inputs and read output
		inputs = new double[2];
		inputs[0] = Math.min(1, map(distToNextPipe, 0, pipe_x_spacing, -1, 1));
		
		int vertDistOfNextPipe = pipes.getVertDistOfNextPipe();
		int pipe_max = game_height - game_ground_height - pipeOpenSize/2;
		int pipe_min =  pipeOpenSize/2;
		int diff = pipe_max - pipe_min;
		inputs[1] = map(vertDistOfNextPipe, -diff, diff, -1, 1);
		
		double[] move = parent_simulator.getNetwork().fireNet(inputs);
		boolean jumpQ = parent_simulator.outputToMove(move[0]);
		if(jumpQ){
			if(gameState == 0){
				gameState = 1;
				bird.setYPosition(game_height/2 + bird.bird_height/2);
			}
			if(gameState == 1){
				bird.setYVelocity(boost_vel);
				if(enableDraw && soundOn)wing_flap_sound.trigger();
			}
		}

		double fitness = parent_simulator.getFitness(results);
		parent_simulator.game_JFrame.setFitnessText(fitness);
		parent_simulator.getNetwork().setNetFitness(fitness);
		
	}

	// ******************************************* DRAW (end) *************************************************

	public void enableDraw(boolean enable){
		enableDraw = enable;
	}
	
	public void enableSound(boolean enable){
		soundOn = enable;
	}
	
	private void loadSprites(String img_path){
		background_img[0] = loadImage(img_path + "skyline_1.png");
		background_img[1] = loadImage(img_path + "skyline_2.png");

		ground_img = loadImage(img_path + "ground.png");
		down_pipe_img = loadImage(img_path + "pipe_down.png");
		up_pipe_img = loadImage(img_path + "pipe_up.png");
		tapToStart_img = loadImage(img_path + "tap_to_start.png");
		gameOver_img = loadImage(img_path + "game_over.png");
		getReady_img = loadImage(img_path + "get_ready.png");
		
		//get digits sprites:
		String[] files = new String[10];
		for(int i = 0; i <= 9; i++)files[i] = "digit_" + i + ".png";
		digits = new Digits(img_path, files, 0.5f);
	}
	
	private void loadSounds(String sound_path){
		Minim minim = new Minim(new PApplet());
		wing_flap_sound = minim.loadSample(sound_path + "sfx_wing.wav");
		hit_sound = minim.loadSample(sound_path + "sfx_hit.wav");
		point_sound = minim.loadSample(sound_path + "sfx_point.wav");
	}
	
	private void resizeSpritesSetup(){
		resizeImage(ground_img, scale_factor);
		resizeImage(up_pipe_img, scale_factor);
		resizeImage(down_pipe_img, scale_factor);
		resizeImage(gameOver_img, 1.5f);
		resizeImage(getReady_img, 1.3f);
		resizeImage(tapToStart_img, 1f);
		bird.resize(1f);
	}
	
	public void getResourcesPath(){
		URI uri = null;
		try {
			uri = new URI(FlappyBird_GUI.class.getResource("/rafa/Main/Simulations/Created/FlappyBird/resources/").toString());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		rsc_path = uri.getPath();
		img_path = rsc_path + "images/";
		sound_path = rsc_path + "sounds/";
	}
	
	private void drawSprites(){
		pipes.display();

		game_ground_x_pos = (game_ground_x_pos + game_x_speed) % (ground_img.width - game_width - 10);
		image(ground_img, game_ground_x_pos , game_height - game_ground_height);

		// draw bird:
		bird.display();
		
		
	}

	private void checkKeyPress(){
		
		if(keyPressed){


			if(gameState == 2 && key == ENTER){
				gameState = 0;
				setup();
			}

			if(lastKey != key){	// use this block for PRESSED keys (not continuous)
				switch (key) {
				// TAP CONTROL
				case ' ':
					if(gameState == 0){
						gameState = 1;
						bird.setYPosition(game_height/2 + bird.bird_height/2);
					}
					if(gameState == 1){
						bird.setYVelocity(boost_vel);
						// wing_flap_sound.trigger();
					}
					break;

					// restart game
				case 'R':
				case 'r':
					gameState = 0;
					setup();
					break;

				case 'C':
				case 'c':
					collisionTest = !collisionTest;
					break;

				case 'P':
				case 'p':
					gameState = 0;
					setup();
					break;
				}
			}


			lastKey = key;

		}else{
			// set lastKey to unused key
			lastKey = '.';
		}

	}
	
	private void gameState0(){
		
		bird.setYPosition(game_height/2 + 7 * sin(frameCount/10f));	// small oscillations
		if(enableDraw){
			image(tapToStart_img, game_width/2 - tapToStart_img.width/2, game_height*3/5 - tapToStart_img.height/2);
			image(getReady_img, game_width/2 - getReady_img.width/2, game_height/3 - getReady_img.height/2);
		}
		
		collisionOccured = false;
	}
	
	private void gameState1(){
		pipes.positionUpdate();
		distToNextPipe = pipes.getDistToClosestPipe((int)bird.getPosition().x);
		if(distToNextPipe > distToNextPipe2){	// new pipe crossed!
			if(enableDraw && soundOn)point_sound.trigger();
			points++;
			
			results[1] *= results[3];
			results[1] += pipes.getVertDistOfNearestPipe();
			results[3]++;
			results[1] /= results[3];
		}
		distToNextPipe2 = distToNextPipe;

		// update bird (velocity, position, direction, distance_covered, imageMode)
		bird.update();
		
		// COLLISION CHECK
		// ground collision
		if(bird.getPosition().y > game_height - game_ground_height){
			gameState = 2;
			if(enableDraw && soundOn)hit_sound.trigger();
			bird.setYPosition(game_height - game_ground_height - 5);
			bird.setEnabled(new boolean[]{false, true, false, false});
			game_x_speed = 0;
		}else if(checkBirdPipesCollision()){	// pipe collision
			gameState = 2;
			if(enableDraw && soundOn)hit_sound.trigger();
			bird.setEnabled(new boolean[]{false, true, false, false});
			game_x_speed = 0;
		}

	}

	private void gameState2(){
		if(enableDraw){
			image(gameOver_img, game_width/2 - gameOver_img.width/2, game_height/3 - gameOver_img.height/2);


			if(bird.getPosition().y < game_height - game_ground_height - 5){
				bird.addToVelocity(bird.getAcceleration());
				bird.addToPosition(bird.getVelocity());
				bird.direction = getBirdDirection();
			}else{
				bird.direction = 0;
				bird.setYPosition(game_height - game_ground_height - 5);
			}
		}else{		// if in fast simulation
			
			
			parent_simulator.game_JFrame.sketch_net.stop();
			parent_simulator.game_JFrame.sketch_game.stop();
		}

	}
	
	private void resizeImage(PImage img, float factor){
		img.resize((int)(img.width * factor), (int)(img.height * factor));
	}

	private void resizeGif(Animation gif, float factor){
		gif.width *= factor;
		gif.height *= factor;
		for(PImage img: gif.getFrames()){
			resizeImage(img, factor);
		}
	}
	
	private float getBirdDirection(){
		float ans;
		ans = map((float)bird.getVelocity().y, 10f, 2f, PI/2, -PI/8);
		if(ans < -PI/8)ans = -PI/8;
		if(ans > PI/2)ans = PI/2;
		
		return ans;
	}
	
	private void defineColors(){
		 bird_colors = new ArrayList<>();
		 bird_colors.add(new Color(255, 50, 50));	// red
		 bird_colors.add(new Color(255, 100, 255));	// pink
		 bird_colors.add(new Color(50, 150, 255));	// light blue
		 bird_colors.add(new Color(0, 255, 0));		// green
		 bird_colors.add(new Color(255, 255, 0));	// yellow
		 bird_colors.add(new Color(150, 50, 255));	// purple
		 bird_colors.add(new Color(100, 100, 100));		// black
		 bird_colors.add(new Color(255, 255, 255));	// white
	}
	
	private boolean checkBirdPipesCollision(){
		boolean ans = false;
		
		int vert_count = bird.bird_shape_vertices.length;
		PVector[] vertices = new PVector[vert_count];
		for(int i = 0; i < vert_count; i++){
			vertices[i] = PVector.add(bird.bird_shape_vertices[i], bird.getPosition());
		}
		
		float rx, ry;
		float rw = pipes.pipe_width;
		float rh = pipes.pipe_height;
		
		for(int i = 0; i < pipes.count; i++){
			float rel_y = game_height - game_ground_height - pipes.y_center_opening[i];
			rx = pipes.x_pos[i] - pipes.pipe_width/2;
			ry = rel_y + pipes.open_size/2;
			ans |= polyRect(vertices, rx, ry, rw, rh);
			
			rx = pipes.x_pos[i] - pipes.pipe_width/2;
			ry = rel_y - pipes.open_size/2 - rh;
			ans |= polyRect(vertices, rx, ry, rw, rh);
			
			if(ans)break;
		}

		return ans;
	}
	
	// POLYGON/RECTANGLE
	private boolean polyRect(PVector[] vertices, float rx, float ry, float rw, float rh) {

		int next = 0;
		for (int current=0; current<vertices.length; current++) {
			next = current+1;
			if (next == vertices.length) next = 0;

			PVector vc = vertices[current];    // c for "current"
			PVector vn = vertices[next];       // n for "next"

			boolean collision = lineRect(vc.x,vc.y,vn.x,vn.y, rx,ry,rw,rh);
			if (collision) return true;
		}
		return false;
	}

	// LINE/RECTANGLE
	private boolean lineRect(float x1, float y1, float x2, float y2, float rx, float ry, float rw, float rh) {

		boolean left =   lineLine(x1,y1,x2,y2, rx,ry,rx, ry+rh);
		boolean right =  lineLine(x1,y1,x2,y2, rx+rw,ry, rx+rw,ry+rh);
		boolean top =    lineLine(x1,y1,x2,y2, rx,ry, rx+rw,ry);
		boolean bottom = lineLine(x1,y1,x2,y2, rx,ry+rh, rx+rw,ry+rh);

		if (left || right || top || bottom) {
			return true;
		}
		return false;
	}

	// LINE/LINE
	private boolean lineLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {

		float uA = ((x4-x3)*(y1-y3) - (y4-y3)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));
		float uB = ((x2-x1)*(y1-y3) - (y2-y1)*(x1-x3)) / ((y4-y3)*(x2-x1) - (x4-x3)*(y2-y1));

		if (uA >= 0 && uA <= 1 && uB >= 0 && uB <= 1) {
			return true;
		}
		return false;
	}

	public int getGameState(){
		return gameState;
	}
	
	private class Bird implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private Animation bird_gif;
		private PVector[] bird_shape_vertices;
		
		// cinematic variables
		private PVector position;
		private PVector velocity;
		private PVector acceleration;
		private float direction;
		private float distance_covered;

		// sprite properties:
		private int bird_width;
		private int bird_height;


		public Bird(String img_path, Color c){
			// bird GIF wing flap
			// String[] bird_color_images = {"bird_1.png","bird_2.png","bird_3.png","bird_2.png"};
			String[] bird_color_images = {"bird_color_1.png","bird_color_2.png","bird_color_3.png","bird_color_2.png"};
			String[] bird_body_images = {"bird_body_1.png","bird_body_2.png","bird_body_3.png","bird_body_2.png"};
			PImage[] images = new PImage[4];
			
			for(int i = 0; i < 4; i++){
				PImage bird_body = loadImage(img_path + bird_body_images[i]);
				PImage bird_color = loadImage(img_path + bird_color_images[i]);
				
				PGraphics graphic = createGraphics(bird_body.width, bird_body.height);
				graphic.beginDraw();
				graphic.image(bird_body,0,0);
				graphic.tint(c.getRed(), c.getGreen(), c.getBlue(), 255);	// paint with the requested color
				graphic.image(bird_color,0,0);
				graphic.tint(255,255);	// back to normal
				graphic.endDraw();
				images[i] = (PImage)graphic.get();
			}
			this.bird_gif = new Animation(images, 6);
			bird_width = bird_gif.width;
			bird_height = bird_gif.height;

			//set shape:
			setShapeVertices();

			// SET CINEMATIC VARIABLES
			// set position to the center of the bird
			position = new PVector(game_width * (1/3f) + bird_width/2, game_height/2 + bird_height/2);
			velocity = new PVector(0, 0);
			acceleration = new PVector(0, 0.3f);
			direction = 0;
			distance_covered = 0;

		}
		
		public void update(){
			bird.addToVelocity(bird.getAcceleration());
			bird.addToPosition(bird.getVelocity());
			// update position and velocity
			if(position.y < bird_height/2 ){
				bird.setYVelocity(0);
				bird.setYPosition(bird_height/2);
			}

			// set enabled images (when bird is going down only one sprite; else do whole animation)
			if(bird.getVelocity().y > 4 ){
				bird.setImageMode("static");
			}else{
				bird.setImageMode("gif");
			}
			
			// update distance covered
			bird.distance_covered += abs(game_x_speed);
			results[0] = distance_covered;
			
			// CHANGE BIRD DIRETION ACCORDING TO VERTICAL SPEED
			bird.direction = getBirdDirection();
		}
		
		public PVector getPosition(){
			return position;
		}
		
		public PVector getVelocity(){
			return velocity;
		}
		
		public PVector getAcceleration(){
			return acceleration;
		}
		
		public void addToPosition(PVector vel){
		position.add(vel);
		}
		
		public void setYPosition(float y){
			position = new PVector((float)position.x, y);
		}
		
		public void addToVelocity(PVector acel){
			velocity.add(acel);
		}
		
		public void setYVelocity(float y){
			velocity = new PVector((float)velocity.x, y);
		}

		public void setImageMode(String mode){
			switch (mode) {
			case "static":
				setEnabled(new boolean[]{false,true,false,false});
				break;
			case "gif":
				setEnabled(new boolean[]{true,true,true,true});
				break;

			}
		}

		public void setEnabled(boolean e[]){
			bird_gif.setEnabled(e);
		}

		public void setShapeVertices(){
			int vertices = 11;
			bird_shape_vertices = new PVector[vertices];
			
			// around image center
			bird_shape_vertices[0] = new PVector(0, -10);
			bird_shape_vertices[1] = new PVector(7, -10);
			bird_shape_vertices[2] = new PVector(11,-7);
			bird_shape_vertices[3] = new PVector(17,6);
			bird_shape_vertices[4] = new PVector(13,12);
			bird_shape_vertices[5] = new PVector(0,14);
			bird_shape_vertices[6] = new PVector(-8,14);
			bird_shape_vertices[7] = new PVector(-16,12);
			bird_shape_vertices[8] = new PVector(-18,0);
			bird_shape_vertices[9] = new PVector(-12,-7);
			bird_shape_vertices[10] = new PVector(-6,-10);
			
			// offset (put in top_left corner):
			for(int i = 0; i < vertices; i++){
				// bird_shape_vertices[i] = bird_shape_vertices[i].addLocal(bird_width/2, bird_height/2);
				bird_shape_vertices[i].add(new PVector(1,-2));
				bird_shape_vertices[i].mult(0.9f);
			}
			
		}
		
		
		public void display(){
			// DRAW BIRD
			pushMatrix();
			translate((int)bird.getPosition().x, (int)bird.getPosition().y);
			rotate(direction);
			// translate((int)-bird_gif.width/2, (int)-bird_gif.height/2);
			bird_gif.display(-bird_gif.width/2, -bird_gif.height/2);
			popMatrix();
		}

		public void resize(float scale){
			for(PImage img : bird_gif.images){
				resizeImage(img, scale);
			}
		}
		
	}

	private class Animation implements Serializable{
		private static final long serialVersionUID = 1L;
		
		private PImage[] images;
		private boolean[] enabledImages;
		private int width;
		private int height;
		private int changePerFrames = 1;	// wait this number of frames before changing frame (works as a rate)
		private int currentWait = 1;
		private int frame;

		private Animation(PImage[] images, int changePerFrames) {
			this.frame = 0;
			this.images = images;
			this.changePerFrames = changePerFrames;
			enabledImages = new boolean[images.length];

			for (int i = 0; i < images.length; i++) {
				enabledImages[i] = true;
			}
			
			this.width = images[0].width;
			this.height = images[0].height;
		}

		public void display(int x_offset, int y_offset) {
			image(images[frame], x_offset, y_offset);
			if((currentWait++ % changePerFrames) == 0){
				// go to next enabled image:
				for(int i = 1; i <= images.length; i++){
					if(enabledImages[(frame + i)% images.length]){
						frame = (frame+i) % images.length;
						break;
					}
				}
			}
		}
		
		public void setEnabled(boolean e[]){
			this.enabledImages = e;
		}
		
		public PImage[] getFrames(){
			return images;
		}

	}
	
	private class Digits implements Serializable{
		private static final long serialVersionUID = 1L;
		
		PImage[] images;
		int digit_width;
		
		// give digits file Strings in order: 0, 1, 2, ..., 8, 9
		public Digits(String path, String[] files, float size_factor){
			images = new PImage[files.length];
			
			for(int i = 0; i < files.length; i++){
				images[i] = loadImage(path + files[i]);
				resizeImage(images[i], size_factor);
			}
			
			digit_width = images[0].width;
		}
		
		public void displayNumber(int num, int x_pos, int y_pos){
			int numberOfDigits;
			if(num == 0){
				numberOfDigits = 1;
			}else{
				numberOfDigits = (int)Math.floor(Math.log10(num) + 1);

			}
			int num_width = numberOfDigits * digit_width;
			int counter = 1;
			int temp = num;
			int digit;
			while(temp > - 1){
				digit = temp % 10;
				temp /= 10;
				int x = x_pos + num_width/2 - counter * digit_width;
				image(images[digit], x, y_pos);
				counter++;
				if(temp == 0)temp = -1;
			}
		}
		
		
	}
	
	private class Pipes implements Serializable{

		private static final long serialVersionUID = 1L;
		private PImage pipe_img;
		private int count;
		private float[] x_pos;
		private int[] y_center_opening;	// coordinate of the center of the opening (counting from the ground)
		private int open_size;
		private int pipe_width;
		private int pipe_height;
		private int x_spaces;
		private int y_spread;
		
		public Pipes(PImage pipe, int count, int open_size, int spawn_x_start, int x_spacing, int y_spread, int y_ground){
			this.pipe_img = pipe;
			this.x_pos = new float[count];
			this.count = count;
			this.y_center_opening = new int[count];
			this.open_size = open_size;
			this.pipe_width = pipe.width;
			this.pipe_height = (pipe.height - open_size)/2;
			this.x_spaces = x_spacing;
			this.y_spread = y_spread;

			for(int i = 0; i < count; i++){
				x_pos[i] = spawn_x_start + i * x_spaces;
				y_center_opening[i] = getRandomYPos();
			}
		}

	

		public void display(){
			for(int i = 0; i < count; i++){
				int rel_y = game_height - game_ground_height - y_center_opening[i];
				image(pipe_img, x_pos[i] - pipe_width/2, rel_y - pipe_img.height/2);
				
				if(collisionTest){
					int x = (int) x_pos[i];
					fill(255,100);
					rect(x - pipe_width/2, rel_y + open_size/2, pipe_width, pipe_height);
					rect(x - pipe_width/2, rel_y - open_size/2 - pipe_height, pipe_width, pipe_height);
					fill(255,255);
				}
			}
		}

		private void positionUpdate(){
			for(int i = 0; i < count; i++){
				x_pos[i] += game_x_speed;

				// if pipe goes out of sight relocate it
				if(x_pos[i] + pipe_width< 0){
					x_pos[i] = x_pos[(i + count - 1) % count] + x_spaces;	// put it behind the last one
					y_center_opening[i] = getRandomYPos();
				}
			}
		}

		private int getRandomYPos(){
			int y = (int)(y_spread/2 + new Random().nextGaussian() * y_spread * 0.1);
			
			if(y > game_height - game_ground_height - pipe_center_offset - open_size/2)
				y = game_height - game_ground_height - pipe_center_offset - open_size/2;
			
			if(y <  pipe_center_offset + open_size)
				y = pipe_center_offset + open_size/2;
			
			return y;			
		}
		
		public int getDistToClosestPipe(int bird_x){
			int ans = 2*game_width; // hypothetical maximum
			for(int i = 0; i < count; i++){
				if(bird_x < x_pos[i]){
					ans = (int)min(ans, x_pos[i] - bird_x);
				}
			}
			return ans;
		}
		
		public int getVertDistOfNextPipe(){
			int bird_x = (int)bird.getPosition().x;
			int x_dist = 2*game_width; // hypothetical maximum
			int vert = 0;
			for(int i = 0; i < count; i++){
				if(bird_x < x_pos[i]){
					x_dist = (int)min(x_dist, x_pos[i] - bird_x);
					
					int rel_y = game_height - game_ground_height - y_center_opening[i];
					vert = (int)(rel_y - bird.getPosition().y);
				}
			}
			return vert;
		}
		
		
		public int getVertDistOfNearestPipe(){
			int bird_x = (int)bird.getPosition().x;
			int x_dist = 2*game_width; // hypothetical maximum
			int vert = 0;
			for(int i = 0; i < count; i++){
				if(x_dist > Math.abs(x_pos[i] - bird_x)){
					x_dist = (int)Math.abs(x_pos[i] - bird_x);
					
					int rel_y = game_height - game_ground_height - y_center_opening[i];
					vert = (int)(rel_y - bird.getPosition().y);
				}
			}
			return vert;
		}
		
	}
	
	private class Color implements Serializable{
		private static final long serialVersionUID = 1L;
		
		int red;
		int green;
		int blue;
		
		public Color(int r, int g, int b){
			this.red = r;
			this.green = g;
			this.blue = b;
		}
		
		public int getRed(){
			return red;
		}
		
		public int getGreen(){
			return green;
		}
		
		public int getBlue(){
			return blue;
		}
	}
}
