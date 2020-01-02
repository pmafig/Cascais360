package nsop.neds.cascais360.Entities;

import java.util.ArrayList;
import java.util.List;

public class CategoryEntity {
    private String title;
    private int id;
    private List<CategoryNodeEntity> nodeList;

    public CategoryEntity(String title, int id){
        this.title = title;
        this.id = id;
    }

    public String Title(){ return this.title; }

    public int Id(){ return this.id; }

    public List<CategoryNodeEntity> Nodes(){
        if(this.nodeList == null){
            this.nodeList = new ArrayList<>();
        }
        return this.nodeList;
    }
}
