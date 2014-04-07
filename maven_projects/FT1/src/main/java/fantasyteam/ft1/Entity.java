package fantasyteam.ft1;

/**
 * Abstract base class for all entities
 * @author jamessemple
 */
public abstract class Entity {
    
    //needs physics based attributes
    
    
    //abstract methods for event handlers .....could be more could be less, to be decided
    public abstract void create();
    public abstract void step();
    public abstract void draw();
    public abstract void destroy();
}
