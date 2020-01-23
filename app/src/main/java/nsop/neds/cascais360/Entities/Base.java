package nsop.neds.cascais360.Entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;

import nsop.neds.cascais360.Manager.ControlsManager.DownloadImageAsync;

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
