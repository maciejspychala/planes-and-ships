package interfaces;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

/**
 * Created by Maciej on 2015-11-06.
 */
public interface Draw {
    /**add object to the pane
     * @param pane
     */
    void draw(Pane pane);

    /**
     * relocate object to the current Coords
     */
    void relocate();

    /**get Image to display
     * @return
     */
    Image getImage();
}
