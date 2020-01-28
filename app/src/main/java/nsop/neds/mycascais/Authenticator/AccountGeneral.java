package nsop.neds.mycascais.Authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;
import android.widget.Toast;

import nsop.neds.mycascais.Entities.UserEntity;
import nsop.neds.mycascais.Settings.Settings;

public class AccountGeneral {
    public static final String ACCOUNT_TYPE = "com.MyCascais.authenticator";

    /**
     * Account name
     */
    public static final String ACCOUNT_NAME = "MyCascais";

    /**
     * Auth token types
     */
    public static final String AUTHTOKEN_TYPE_READ_ONLY = "Read only";
    public static final String AUTHTOKEN_TYPE_READ_ONLY_LABEL = "Read only access to an MyCascais account";

    public static final String AUTHTOKEN_TYPE_FULL_ACCESS = "Full access";
    public static final String AUTHTOKEN_TYPE_FULL_ACCESS_LABEL = "Full access to an MyCascais account";

    public static final ServerAuthenticate sServerAuthenticate = new ParseComServerAuthenticate();

    public static boolean logout(Context context){
        AccountManager mAccountManager = AccountManager.get(context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        if(availableAccounts.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                mAccountManager.removeAccountExplicitly(availableAccounts[0]);
            }

            Toast.makeText(context, Settings.labels.LogoutSuccess, Toast.LENGTH_LONG).show();

            return true;
        }else{
            return false;
        }
    }

    public static UserEntity getUser(Context context){
        AccountManager mAccountManager = AccountManager.get(context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        String ssk = mAccountManager.getUserData(availableAccounts[0], "SSK");
        String userid = mAccountManager.getUserData(availableAccounts[0], "UserId");

        return new UserEntity(ssk, userid);
    }
}
