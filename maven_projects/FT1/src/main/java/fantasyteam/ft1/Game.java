package fantasyteam.ft1;

import fantasyteam.ft1.interfaces.Physics;
import fantasyteam.ft1.interfaces.Renderer;
import java.util.List;

/**
 * Game class. Handles game logic and combines all of the engine components
 * @author jamessemple
 */
public abstract class Game {
	
    //Engines go here
    private final Physics physics;
    private final Renderer renderer;
    private final Networking networking;

    protected List<Entity> entities;


    public Game(Physics physics, Renderer renderer, Networking networking) {
        this.physics = physics;
        this.renderer = renderer;
        this.networking = networking;
    }


    //something of the like here, could be interchanged with a seperate engine + event loop, also probably want to thread the rendering and physics stuff?
    protected void runGameLoop() {
        //need a better loop implementation here but you get the idea
        for (Entity entity : entities) {
                entity.create();
        }

        for (Entity entity : entities) {
                entity.step();
        }

        physics.runPhysics(entities);

        for (Entity entity : entities) {
                entity.draw();
        }
        //.... run other game loop steps here
    }
    
    
    /**
     * abstract method for handling messages received from the networking engine component
     * @param action
     * @param clientId 
     */
    public abstract void handleAction(List<String> action, String clientId);
}
