package nsop.neds.mycascais.Manager.ControlsManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;

public class DownloadImageAsync extends AsyncTask<String, Void, Bitmap> {

    @Override
    protected Bitmap doInBackground(String... params) {

        try {
            return BitmapFactory.decodeStream(new java.net.URL(params[0]).openStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}