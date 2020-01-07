package nsop.neds.cascais360.Authenticator;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Build;

import nsop.neds.cascais360.Entities.UserEntity;

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
