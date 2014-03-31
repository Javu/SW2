package fantasyteam.sw2.rendering;

import static fantasyteam.sw2.rendering.ImageExtensions.PNG;

/**
 *
 * @author jamessemple
 */
public enum SpriteResources {
    BULLET("bullet", "", PNG),
    
    MENU("menu",  "", PNG),
    MENU_CHANGE_TEAM("menu_change_team", "", PNG),
    MENU_KICK_PLAYER("menu_kick_player", "", PNG),
    MENU_USE_TEAM("menu_use_team", "", PNG),
    
    SQUARE_BLACK("square_black", "", PNG),
    SQUARE_WALL("square_wall", "", PNG);
    
    /** The base directory where sprite resources are stored */
    private static final String RESOURCE_PATH = "src/main/resources/sprites";
    
    public final String filename, path;
    
    
    /**
     * Create a SpriteResource
     * @param filename the filename of the resource
     * @param path the path of the resource
     * @param extension the file extension
     */
    private SpriteResources(String filename, String path, String extension) {
        this.filename = filename;
        this.path = RESOURCE_PATH + cleanPath(path) + filename + extension;
    }
    
    
    /**
     * Cleans a path string. Checks if the string begins and ends with '/', if not adds them.
     * @param path the path to clean
     * @return the cleaned path
     */
    private String cleanPath(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path;
    }
}
