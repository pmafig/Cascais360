package nsop.neds.cascais360.Entities.Json;

import android.graphics.Bitmap;

import java.util.List;

import nsop.neds.cascais360.Entities.Base;

public class MapMarker extends Base {
    public int ID;
    public String Title;
    public List<SubTitle> SubTitle;
    public List<Coordinates> Coordinates;
    public List<String> Images;

    public Bitmap Image(){
        return decodeImage(Images.get(0));
    }
}
