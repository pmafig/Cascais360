package nsop.neds.mycascais.Entities;

import java.util.ArrayList;
import java.util.List;

public class EventEntity {
    private String title;
    private List<FrameEntity> frameList;

    public EventEntity(String title){
        this.title = title;
        this.frameList = new ArrayList<>();
    }

    public List<FrameEntity> List(){ return this.frameList; }

    public String Title(){ return this.title; }

}
