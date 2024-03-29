package nsop.neds.mycascais.Entities.Json;

import java.net.URI;
import java.net.URISyntaxException;

public class Weather {

    public int Current;
    public int Min;
    public int Max;
    public String Icon;
    public String Text;

    public String getIcon() throws URISyntaxException {
        try {
            URI uri = new URI(this.Icon);
            String path = uri.getPath();
            String idStr = path.substring(path.lastIndexOf('/') + 1);

            String s = idStr.replace(".svg", "");

            return "ic_" + s;
        }catch (Exception ex){
            return "ic_1";
        }
    }
}
