package nsop.neds.mycascais.Entities.Json;

import java.util.List;

public class Email {
    public int ID;
    public String CustomerID;
    public String EmailAddress;
    public boolean Main;
    public boolean Login;
    public List<ValidationStates> ValidationStates;
}
