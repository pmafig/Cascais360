package nsop.neds.mycascais.Manager;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.ImageView;

public class SocialManager extends AsyncTask<String, Void, Void> {

    private Context context;
    private ImageView likeButton;
    private ImageView notificationButton;

    public SocialManager(Context context, ImageView likeButton, ImageView notificationButton){
        this.context = context;
        this.likeButton = likeButton;
        this.notificationButton = notificationButton;
    }

    @Override
    protected Void doInBackground(String... strings) {
        return null;
    }
}
