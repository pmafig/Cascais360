package nsop.neds.mycascais.Authenticator;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;


public class CascaisAuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {

        CascaisAuthenticator authenticator = new CascaisAuthenticator(this);
        return authenticator.getIBinder();
    }
}
