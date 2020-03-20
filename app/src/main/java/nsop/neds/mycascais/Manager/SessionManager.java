package nsop.neds.mycascais.Manager;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import nsop.neds.mycascais.Authenticator.AccountGeneral;
import nsop.neds.mycascais.Entities.Json.Dashboard;
import nsop.neds.mycascais.Entities.Json.Resources;

public class SessionManager {

    private static final String PREFERENCES = "MYCASCAISPREFERENCES";
    private static SharedPreferences sharedpreferences;
    private static SharedPreferences.Editor editor;

    private static final String ssk = "ssk";
    private static final String userId = "userId";

    //Notifications
    private static final String events = "Events";
    private static final String places = "Places";
    private static final String routes = "Routes";
    private static final String townCouncil = "TownCouncil";

    private static final String firebasetoken = "FirebaseToken";

    private static final String langcode = "LangCode";
    private static final String langcodeposition = "LangCodePosition";

    private static final String countryList = "countryList";
    private static final String onBoarding = "onboarding";
    private static final String recover = "recover";
    private static final String newaccount = "newaccount";
    private static final String recoveremail = "recoveremail";
    private static final String user = "User";
    private static final String fullname = "Nome";
    private static final String username = "username";
    private static final String userstatus = "userstatus";
    private static final String refreshtoken = "refreshtoken";
    private static final String mobilenumber = "mobilenumber";
    private static final String mobilenumbermain = "mobilenumbermain";
    private static final String email = "email";
    private static final String address = "Morada";

    private static final String appid = "externalappid";
    private static final String packagename = "packagename";

    private static final String appkeys = "appkeys";
    private static final String disclaimers = "disclaimers";

    private static final String fulldisclaimer = "fulldisclaimer";

    private static final String appInfo = "appinfo";
    private static final String packageName = "packageName";
    private static final String externalAppId = "externalAppId";

    private static final String isAuth = "isAuth";
    private static final String emailID = "emailId";

    private Context context;

    public SessionManager(Context context){
        this.context = context;

        if(sharedpreferences == null){
            sharedpreferences = context.getSharedPreferences(PREFERENCES, context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
        }
    }

    private enum keys{
        resources("resources"),
        dashboard("dashboard");

        private String key;

        keys(String key) {
            this.key = key;
        }
    }

    public Resources getResources(){
        if(sharedpreferences.contains(keys.resources.key)){
            return new Gson().fromJson(sharedpreferences.getString(keys.resources.key, ""), Resources.class);
        }
        return null;
    }

    public void setResources(Resources resources){
        editor.putString(keys.resources.key, new Gson().toJson(resources)).commit();
    }


    public Dashboard getDashboard(){
        if(sharedpreferences.contains(keys.dashboard.key)){
            return new Gson().fromJson(sharedpreferences.getString(keys.dashboard.key, ""), Dashboard.class);
        }
        return null;
    }

    public void setDashboard(Dashboard dashboard){
        editor.putString(keys.dashboard.key, new Gson().toJson(dashboard)).commit();
    }

    public Boolean getOnboarding(){ return sharedpreferences.getBoolean(onBoarding, false);    }

    public void setOnboarding(){ editor.putBoolean(onBoarding, true).commit(); }

    public String getCountryList(){
        if(sharedpreferences.contains(countryList)) {
            return sharedpreferences.getString(countryList, "");
        }else{
            return null;
        }
    }

    public void setSetCountryList(String list){ editor.putString(countryList, list).commit(); }

    public void setEvents(String e){ editor.putString(events, e).commit(); }

    public String getEvents(){
        return sharedpreferences.getString(events, "");
    }

    public void setNewAccount(){
        editor.putBoolean(newaccount, true).commit();
    }

    public boolean getNewAccount(){ return sharedpreferences.getBoolean(newaccount, false); }

    //public void setRecoverEmail(String email){ editor.putString(routesNotifications, email).commit(); }

    //public String getRecoverEmail(){ return sharedpreferences.getString(routesNotifications, "");    }

    public String getLangCode(){ return sharedpreferences.getString(langcode, "pt");    }

    public void setFirebaseToken(String token){ editor.putString(firebasetoken, token).commit(); }

    public String getFirebaseToken(){ return sharedpreferences.getString(firebasetoken, "");    }

    public void setLangCode(String langCode){ editor.putString(langcode, langCode).commit(); }

    public int getLangCodePosition(){ return sharedpreferences.getInt(langcodeposition, 0);    }

    public void setLangCodePosition(int langCodePosition){ editor.putInt(langcodeposition, langCodePosition).commit(); }

    public String getDisplayname(){ return sharedpreferences.getString(username, "--");    }

    public void setDisplayname(String name){ editor.putString(username, name).commit(); }

    public String getFullName(){ return sharedpreferences.getString(fullname, " ");    }

    public void setFullName(String name){ editor.putString(fullname, name).commit(); }

    public String getDisplaystatus(){ return sharedpreferences.getString(userstatus, "--");    }

    public void setDisplaystatus(String status){ editor.putString(userstatus, status).commit(); }

    public void clear(){
        editor.remove(username).commit();
        editor.remove(fullname).commit();
        editor.remove(address).commit();
        editor.remove(email).commit();

        editor.remove(mobilenumber).commit();
        editor.remove(packageName).commit();
        editor.remove(externalAppId).commit();

        editor.remove(recover).commit();
        editor.remove(newaccount).commit();

        editor.remove(appInfo).commit();
        editor.remove(packageName).commit();
        editor.remove(externalAppId).commit();

        editor.remove(mobilenumbermain).commit();
    }

    public void setRecover(){ editor.putBoolean(recover, true).commit(); }

    /*public void setSsk(String _ssk){ editor.putString(ssk, _ssk).commit();}

    public String getSsk(){ return sharedpreferences.getString(ssk, ""); }

    public void setUserId(String _userId){ editor.putString(userId, _userId).commit();}

    public String getUserId(){ return sharedpreferences.getString(userId, ""); }*/

    public void setAddress(String a){ editor.putString(address, a).commit(); }

    public String getAddress(){ return sharedpreferences.getString(address, " ");    }

    public void setEmail(String e){ editor.putString(email, e).commit(); }

    public String getEmail(){ return sharedpreferences.getString(email, " ");    }

    public String getRefreshToken(){ return sharedpreferences.getString(refreshtoken, "");    }

    public void setRefreshToken(String refreshtoken){ editor.putString(refreshtoken, refreshtoken).commit(); }

    public void setMobileNumber(String status){ editor.putString(mobilenumber, status).commit(); }

    public String getMobileNumber(){ return sharedpreferences.getString(mobilenumber, "");    }

    public void setMobileNumberMain(boolean status){ editor.putBoolean(mobilenumbermain, status).commit(); }

    public boolean getMobileNumberMain(){ return sharedpreferences.getBoolean(mobilenumbermain, false);    }

    public void setDisclaimers(String json){ editor.putString(disclaimers, json).commit(); }

    public void setAppKeys(String json){ editor.putString(appkeys, json).commit(); }

    public String getFullDisclaimer(){ return sharedpreferences.getString(fulldisclaimer, "");    }

    public void setFullDisclaimer(String json){ editor.putString(fulldisclaimer, json).commit(); }

    public String getUser(){ return sharedpreferences.getString(user, " ");    }

    public void setUser(String json){ editor.putString(user, json).commit(); }

    public String getExternalAppInfo(){ return sharedpreferences.getString(appInfo, "");    }

    public void setExternalAppInfo(String info){ editor.putString(appInfo, info).commit(); }

    public String getExternalAppPackageName(){ return sharedpreferences.getString(packageName, "");    }

    public void setExternalAppPackageName(String name){ editor.putString(packageName, name).commit(); }

    public int getExternalAppExternalId(){ return sharedpreferences.getInt(externalAppId, 0);    }

    public void setExternalAppPackageExternalId(int id){ editor.putInt(externalAppId, id).commit(); }


    public void isAuth(){ editor.putBoolean(isAuth, true).commit(); }

    public boolean getIsAuth(){ return sharedpreferences.getBoolean(isAuth, false);    }

    public void setEmailId(int id){ editor.putInt(emailID, id).commit(); }

    public int getEmailId(){ return sharedpreferences.getInt(emailID, 0);    }

    public boolean isLoggedOn(){
        AccountManager mAccountManager = AccountManager.get(context);
        Account[] availableAccounts  = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);

        return availableAccounts.length > 0;
    }

    public boolean asUserLoggedOn(){
        AccountManager mAccountManager = AccountManager.get(this.context);

        final Account availableAccounts[] = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        boolean asAccount = availableAccounts.length != 0;

        try {
            return asAccount;
        }catch (Exception ex){
            System.out.println(ex.getMessage());
            return false;
        }
    }
}
