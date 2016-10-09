package proj4ChanceLinRemondiSolis;

import javafx.scene.shape.Rectangle;

/**
 * Created by mremondi on 10/9/16.
 */
public class DragBox {

    private Rectangle dragBox;

    public DragBox(Rectangle dragBox){
        this.dragBox = dragBox;
    }

    public Rectangle getBox(){
        return this.dragBox;
    }
}
