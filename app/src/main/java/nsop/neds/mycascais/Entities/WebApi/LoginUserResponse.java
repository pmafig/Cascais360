package nsop.neds.mycascais.Entities.WebApi;

import java.util.List;

import nsop.neds.mycascais.Entities.Json.PhoneContacts;

public class LoginUserResponse {
    public boolean IsAuthenticated;
    public boolean IsCreated;
    public String Email;
    public String PhoneNumber;
    public String Token;

    //"IdentityUser": null,
    public boolean SendEmail;

    public boolean TransferToMyCascais;
    public boolean ForceReset;
    public boolean Blocked;
    //"Disclaimers": null
    public String RefreshToken;
    public List<nsop.neds.mycascais.Entities.Json.PhoneContacts> PhoneContacts;

    public String SSK;
    public String UserID;
    public String DisplayName;
    public String DisplayValidation;

    public boolean InvalidaSession;
}
