package nsop.neds.cascais360.Entities.Json;

import java.util.List;

public class ResponseData {
    public boolean IsAuthenticated;
    //"IdentityUser": null,
    public boolean SendEmail;
    public String Email;
    public String Token;
    public boolean TransferToMyCascais;
    public boolean ForceReset;
    public boolean Blocked;
    //"Disclaimers": null
    public String RefreshToken;
    public List<PhoneContacts> PhoneContacts;
    // SessionExpirationDate":
    // "AppList":
    public String SSK;
    public String UserID;
    public String DisplayName;
    public String DisplayValidation;
}
