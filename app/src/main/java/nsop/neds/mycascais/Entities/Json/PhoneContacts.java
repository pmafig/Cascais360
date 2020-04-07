package nsop.neds.mycascais.Entities.Json;

import java.util.List;

public class PhoneContacts {
    public int ID;
    public String CustomerID;
    public int PhoneContactTypeID;
    public String CountryCode;
    public String Number;
    public boolean Main;
    public boolean Login;
    public boolean SendSMS;
    public List<ValidationStates> ValidationStates;
}
