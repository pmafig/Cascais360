package nsop.neds.cascais360.Manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.view.Menu;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

import nsop.neds.cascais360.R;

public class DownloadImage extends AsyncTask<String, Void, Bitmap> {
    ImageView bmImage;
    Menu menu;
    Context context;

    public DownloadImage(Context context, ImageView bmImage) {
        this.bmImage = bmImage;
        this.context = context;
    }

    public DownloadImage(Context context, Menu menu) {
        this.menu = menu;
        this.context = context;
    }

    protected Bitmap doInBackground(String... urls) {
        try {
            return BitmapFactory.decodeStream(new URL(urls[0]).openStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void onPostExecute(Bitmap result) {
        if(bmImage != null) {
            bmImage.setImageBitmap(result);
        }
        if(menu != null){
            BitmapDrawable b = new BitmapDrawable(context.getResources(), result);
            b.setTint(context.getResources().getColor(R.color.colorBlack));
            menu.getItem(0).setIcon(b);

            //menu.getItem(0).getIcon().setTint(context.getResources().getColor(R.color.colorBlack));
            //menu.getItem(0).setIcon(context.getDrawable(R.drawable.ic_menu_camera));
        }
    }
}
