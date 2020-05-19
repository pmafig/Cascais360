package nsop.neds.mycascais.Entities.Json;

import java.util.ArrayList;
import java.util.List;

public class Disclaimer {
    public boolean HasDisclaimer;
    public boolean HasAccepted;
    public boolean NeedUpdate;
    public int SiteID;
    public final List<DisclaimerField> DisclaimerFields;

    public Disclaimer() {
        DisclaimerFields = new ArrayList<>();
    }
}
