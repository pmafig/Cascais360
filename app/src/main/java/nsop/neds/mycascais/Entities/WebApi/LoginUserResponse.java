package nsop.neds.mycascais.Entities.WebApi;

import java.util.List;

import nsop.neds.mycascais.Entities.Json.App;
import nsop.neds.mycascais.Entities.Json.Disclaimer;
import nsop.neds.mycascais.Entities.Json.Consent;
import nsop.neds.mycascais.Entities.Json.Email;
import nsop.neds.mycascais.Entities.Json.PhoneContacts;

public class LoginUserResponse {
    public String SessionExpirationDate;
    public boolean IsAuthenticated;
    public String SSK;
    public String AuthID;
    public String MyCascaisID;
    public String DisplayName;
    public String DisplayValidation;
    public List<Disclaimer> Disclaimers;
    public boolean Blocked;
    public boolean ForceReset;
    public boolean SendEmail;
    public boolean HasSmsMobile;
    public String RefreshToken;
    public List<PhoneContacts> PhoneContacts;
    //public List<DisclaimerField> FullDisclaimer;
    public List<App> AppList;
    public List<Email> Emails;

    public boolean ShowConsent;
    public List<Consent> RegisteredConsents;
    public List<Consent> AvailableConsents;
}
