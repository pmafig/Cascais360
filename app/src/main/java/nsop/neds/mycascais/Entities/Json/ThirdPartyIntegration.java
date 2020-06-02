package nsop.neds.mycascais.Entities.Json;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ThirdPartyIntegration {
    public String SSK;
    public String SessionExpirationDate;
    public String AuthID;
    public String MyCascaisID;
    public String DisplayName;

    public final List<ThirdPartyIntegrationField> Fields;

    public ThirdPartyIntegration(){
        Fields = new ArrayList<>();
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
