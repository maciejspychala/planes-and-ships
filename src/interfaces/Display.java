package interfaces;

import javafx.scene.layout.HBox;

import java.util.ArrayList;

/**
 * Created by Maciej on 08.01.2016.
 */
public interface Display {

    /**get info about object which should be refreshed every frame
     * @return
     */
    ArrayList<HBox> getInfo();

    /**get info about object which shouldn't be refreshed every frame
     * @return
     */
    ArrayList<HBox> getNoRefresh();

    /**check if no refresh info box should be refreshed
     * @return
     */
    boolean shouldRefreshInfoBox();

    void setShouldRefreshInfoBox(boolean shouldRefreshInfoBox);
}
