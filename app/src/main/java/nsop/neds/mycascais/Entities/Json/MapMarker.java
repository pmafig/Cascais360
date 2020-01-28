package nsop.neds.mycascais.Entities.Json;

import android.graphics.Bitmap;

import java.util.List;

import nsop.neds.mycascais.Entities.Base;

public class MapMarker extends Base {
    public int ID;
    public String Title;
    public List<SubTitle> SubTitle;
    public List<Coordinates> Coordinates;
    public List<String> Images;
    public Bitmap Image;

    public Bitmap Image(){
        return decodeImage(Images.get(0));
    }
}
