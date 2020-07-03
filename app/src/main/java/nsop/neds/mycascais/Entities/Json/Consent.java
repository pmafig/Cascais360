package nsop.neds.mycascais.Entities.Json;

public class Consent {
    public int ID;
    public int ConsentID;
    public String Description;
    public boolean IsRequired;
    public boolean HasAccepted;
    public boolean Active;

    public Consent(){

    }

    public Consent(int id, boolean state, boolean isRequired){
        ID = id;
        ConsentID = id;
        HasAccepted = state;
        IsRequired = isRequired;
    }
}
