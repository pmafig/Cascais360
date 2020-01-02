package nsop.neds.cascais360.Entities;

import java.util.ArrayList;
import java.util.List;

public class DashboardEntity {

    private String crc;
    private String sort;

    public String Sort(){
        return sort;
    }

    public String CRC(){
        return crc;
    }

    public void setSort(String _sort){
        sort = _sort;
    }

    public void setCRC(String _crc){
        crc = _crc;
    }

    private List<BlockEntity> blockList;

    public DashboardEntity(){

    }

    public List<BlockEntity> getBlockList(){
        if(this.blockList == null){
            this.blockList = new ArrayList<>();
        }

        return this.blockList;
    }
}
