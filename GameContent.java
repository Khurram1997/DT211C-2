package com.PlatformerGame;
//IMPORTS
//Provides a resizable array... 
//Source: tutorialspoint.com: "java.util package classes" 
import java.util.ArrayList; 

//To create background (bg) of game
//REF: https://www.tutorialspoint.com/javafx/2dshapes_rectangle.htm
import javafx.scene.shape.Rectangle;

//To add in game layout (player and paltforms)
//REF: http://zetcode.com/gui/javafx/layoutpanes/
import javafx.scene.layout.Pane;

//Used for animation (movement of player), main loop/flow of game, classes included are used to animate nodes
//REF: https://www.tutorialspoint.com/javafx/javafx_animations.htm
import javafx.animation.AnimationTimer;

//Used as an entry point of the application itslef (the game itself)
//REF: https://www.tutorialspoint.com/javafx/javafx_application.htm
import javafx.application.Application;
//A model object to provide using of objects such as the player (box)
//REF: https://www.tutorialspoint.com/javafx/javafx_application.htm
import javafx.scene.Node;
//A window that contains all the objects of java application (in my case the game itself)
//REF: https://www.tutorialspoint.com/javafx/javafx_application.htm
import javafx.stage.Stage;
//Data structure representing contents of a scene
//REF: https://www.tutorialspoint.com/javafx/javafx_application.htm
import javafx.scene.Scene;

//Provides all the optional map operations... 
//REF: https://www.tutorialspoint.com/java/util/index.htm
import java.util.HashMap;

//Provides classes for keyboard input event handling...
//REF: https://docs.oracle.com/javafx/2/api/javafx/scene/input/class-use/KeyCode.html
import javafx.scene.input.KeyCode;

//To apply colour to player (box) and platforms and to distinguish between them
//REF: https://docs.oracle.com/cd/E17802_01/javafx/javafx/1.1/docs/api/javafx.scene.paint/javafx.scene.paint.Color.html
import javafx.scene.paint.Color;

//Geometry provides the set of 2D classes for defining and performing operations on objects related to two-dimensional geometry.
//REF: https://docs.oracle.com/javase/8/javafx/api/javafx/geometry/class-use/Point2D.html
import javafx.geometry.Point2D;

public class GameContent extends Application 
{
	//To launch the application (game) itself.
    public static void main(String[] args) 
    {
        launch(args);
    }
    
	//Evrything within the game, player, platforms, game entities...positioning off everything
    private Pane GamesRoot = new Pane();
	//The application root (main root), contains other roots/nodes
    private Pane ApplicationRoot = new Pane();
   
	//Taps a key code (key on keyboard) to a boolean to verify if a key is pressed or not, keycode
    //This framework allows you to make use of pre-existing collections (effectively, data structures) such as sets, lists, maps, queues etc AND 
    //to make use of common algorithms such as sorting and searching. /
    //The purpose is to maximise code reuse so that you concentrate on the main part of your program, not on manipulating data structures.
    //REF: https://bukkit.org/threads/toggle-boolean-with-hashmap.258059/
    private HashMap<KeyCode, Boolean> keys = new HashMap<KeyCode, Boolean>();
    
    //Width of level 
    private int WidthofLevel;
    
    //To store/use different entities and lists that can be overwritten
    private ArrayList<Node> multiplatforms = new ArrayList<Node>();
    
    //The player a user controls (box)
    private Node firstplayer;
    
    //Storing speed of player... 
    //Returns a point with the specified coordinates added to the coordinates of this point.
    private Point2D firstplayerVelocity = new Point2D(0,0);
    
    //Boolean seeing if player (box) can jump or not
    private boolean ableJump = true;
    
    //Initial content of game
    private void initialContent() 
    {
    		//To create background of game with following dimesnsions
        Rectangle bg = new Rectangle(1300, 750, Color.WHITE); //Dimensions for height and width of application
        
        //To obtain levelWidth form CreateLevel class
        //Level[0] to return first row from class, length() returns # of chars within the string
        WidthofLevel = CreateLevel.LEVEL[0].length() * 60; //To create paltforms 60 pixels wide 
         
        //Loop will grow through each element within CreateLevel class  
        for (int i = 0; i < CreateLevel.LEVEL.length; i++) 
        {
        		//To obtain each row: eg - 0000000000.....
            String line = CreateLevel.LEVEL[i];
            //To go through each char within row
            for (int j = 0; j < line.length(); j++) 
            {
            		//To get each character from the row 
                switch (line.charAt(j)) 
                {
                		//If char is 0 then do nothing
                    case '0':
                        break;
                     //if char is 1 add part of platform
                    case '1':
                        Node platform = CreateEntity(j*60, i*60, 60, 60, Color.GREEN);
                        multiplatforms.add(platform);
                        break;
                }
            }
        }
        //Creating player (box): 40 pixels wide and in height
        //X position at 0 to start off game
        firstplayer = CreateEntity(0, 600, 40, 40, Color.RED);
        
        //To move across screen (follow player)
        //"translateXproperty" node player (box), addlistener because player is a property that may change, obs: observes/records changes
        firstplayer.translateXProperty().addListener((obs, old, newValue) -> 
        {
            int offset = newValue.intValue();
            	//As player moves right, everything within game will move left so that it looks like camera is following the player
            if (offset > 100 && offset < WidthofLevel + 1000) 
            {
                GamesRoot.setLayoutX(-(offset - 100));
            }
        });
        			//To add everything to application: the background and game entities
        			//bg comes first because we want all game objects to be over the background (be visible)
                ApplicationRoot.getChildren().addAll(bg, GamesRoot);
    }
    
    //All game logic, move left and right and jump
    private void GUpdate() 
    {
    		//To see if key is pressed and then if true make player jump i.e. move up y-cordinate
    		//5 is speed of player
        if (isPressed(KeyCode.UP) && firstplayer.getTranslateY() >= 5) 
        {
            jumpPlayer();
        }
        //To move player to the left
        if (isPressed(KeyCode.LEFT) && firstplayer.getTranslateX() >= 5) 
        {
            PosmovePlayerX(-5);
        }
        //To move player to the right
        if (isPressed(KeyCode.RIGHT) && firstplayer.getTranslateX() + 40 <= WidthofLevel - 5) 
        {
            PosmovePlayerX(5);
        }
        //10 as max value for gravity (as high as the player can jump.. distance on y-axis)
        if (firstplayerVelocity.getY() < 10) 
        {
        	//Acceleration in y-axis (how high player can jump)
        	firstplayerVelocity = firstplayerVelocity.add(0, 2);
        }
        //So you can't determine the y-cordinate of player only have power to change x-cordinate
        PosmovePlayerY((int)firstplayerVelocity.getY());
    }
    
    //To move player and collision detection to avoid player moving through the platforms
    //Moving 1 unit at a time "value" amount of times
    private void PosmovePlayerX(int value) 
    {
        boolean movingRight = value > 0;
        //Value may be negative so use the absolute value
        for (int i = 0; i < Math.abs(value); i++) 
        {
        		//For every platform perform...
            for (Node platform : multiplatforms) 
            {	
            		//Get bounds for player and for platform:
            		//if they intersect... there is a collision
                if (firstplayer.getBoundsInParent().intersects(platform.getBoundsInParent())) 
                {
                		//If moving right...checking if the right side of player is touching the platform, 
                		//thus stopping the player moving when it hits a platform
                    if (movingRight)
                    {
                    		//"getTranslateX" is position of player, 40 = width of player
                        if (firstplayer.getTranslateX() + 40 == platform.getTranslateX()) 
                        {
                            return;
                        }
                    }
                    //For players moving left
                    else 
                    {
                        if (firstplayer.getTranslateX() == platform.getTranslateX() + 60) 
                        {
                            return;
                        }
                    }
                }
            }
            //Then if no collision, player moves right by one unit if left then by minus one unit (based on x-axis)
            //? is basically an if-else statement 
            firstplayer.setTranslateX(firstplayer.getTranslateX() + (movingRight ? 1 : -1));
        }
    }

    private void PosmovePlayerY(int value) 
    {
        boolean movingDown = value > 0;
        for (int i = 0; i < Math.abs(value); i++) 
        {
            for (Node platform : multiplatforms) 
            {
                if (firstplayer.getBoundsInParent().intersects(platform.getBoundsInParent())) 
                {
                    if (movingDown) 
                    {
                        if (firstplayer.getTranslateY() + 40 == platform.getTranslateY()) 
                        {
                        		//To avoid player from constantly colliding (sticking) with the platforms
                        		firstplayer.setTranslateY(firstplayer.getTranslateY() - 1);
                            ableJump = true;
                            return;
                        }
                    }
                    else 
                    {
                        if (firstplayer.getTranslateY() == platform.getTranslateY() + 60) 
                        {
                            return;
                        }
                    }
                }
            }
            firstplayer.setTranslateY(firstplayer.getTranslateY() + (movingDown ? 1 : -1));
        }
    }

    private void jumpPlayer() 
    {
    		//To enable player to jump over gaps in the platform
        if (ableJump) 
        {
        		//y-cordinate is opposite in java effects screen, thus the minus value for 35
        		//Which determines how fast and how high player jumps during gameplay
         	firstplayerVelocity = firstplayerVelocity.add(0, -35);
            //Set following to false so that there is no double jump
            ableJump = false;
        }
    }
    
    //Entity returns node (model object...player (box))
    private Node CreateEntity(int x, int y, int w, int h, Color color) 
    {
    		//Create rectangle with following variables...dimensions specified in method "private void initContent()"
        Rectangle entity = new Rectangle(w, h);
        //Set x and y of player and color of player
        entity.setTranslateX(x);
        entity.setTranslateY(y);
        entity.setFill(color);
        //Adding children such as player and platforms to game
        GamesRoot.getChildren().add(entity);
        return entity;
    }
    
    //Getting keycode form defined map to see if key is pressed or not
    //If not pressed returns "false" by default
    //If pressed it will return the boolean value of that key
    private boolean isPressed(KeyCode key) 
    {
        return keys.getOrDefault(key, false);
    }

    @Override
    public void start(Stage primaryStage) throws Exception 
    {
        initialContent(); //method from above 
        //Create application (game itself)
        Scene scene = new Scene(ApplicationRoot); 
        //Adding callbacks to scene
        //Getting code from event to see what key is pressed, this is put into map of keys and... 
        //sets value of key to true, to allow us to track what key is pressed 
        scene.setOnKeyPressed(event -> keys.put(event.getCode(), true));
        //Opposite of above, to see when key is released
        scene.setOnKeyReleased(event -> keys.put(event.getCode(), false));
        //Setting title of application
        primaryStage.setTitle("Game: Welcome to Platforms");
        //To show overall application
        primaryStage.setScene(scene);
        primaryStage.show();
        
        //To control movement of player (box): 60 fps.
        AnimationTimer timer = new AnimationTimer() 
        {
        		@Override
        		//The class AnimationTimer allows to create a timer, that is called in each frame while it is active. 
        		//An extending class has to override the method handle(long) which will be called in every frame. 
        		//Source: docs.oracle.com: "Class Animation Timer"
            	public void handle(long now) 
        		{
        			//Forwards to method from above: "private void update()"
        			GUpdate();        		
        		}
        };
        //The methods start() and stop() allow to start and stop the timer.
        timer.start();
    }
}