package nsop.neds.mycascais.Entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

public class Base {
    public Bitmap decodeImage(String highlightImage){
        try {
            return BitmapFactory.decodeStream(new java.net.URL(highlightImage).openStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
