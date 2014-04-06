package fantasyteam.ftengine.interfaces;

import fantasyteam.ftengine.Entity;
import java.util.List;

/**
 * Interface to a physics engine component
 * @author jamessemple
 */
public interface Physics {
    
    /**
     * runs the physics for a given list of entities
     * @param entities the entities to apply physics to
     */
    //might be able to implement this better so we're not just looping through the entities every time
    public void runPhysics(List<Entity> entities);
}
