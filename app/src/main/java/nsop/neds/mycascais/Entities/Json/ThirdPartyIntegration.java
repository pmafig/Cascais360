package nsop.neds.mycascais.Entities.Json;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ThirdPartyIntegration extends SessionHeader {
    public final List<DisclaimerField> DisclaimerFields;

    public ThirdPartyIntegration(){
        DisclaimerFields = new ArrayList<>();
    }

    public String toJson(){
        return new Gson().toJson(this);
    }
}
