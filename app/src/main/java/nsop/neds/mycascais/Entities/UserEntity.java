package nsop.neds.mycascais.Entities;

public class UserEntity {

    private String ssk;
    private String userid;

    public UserEntity(String ssk, String userid){
        this.ssk = ssk;
        this.userid = userid;
    }

    public String getSsk(){
        return this.ssk;
    }

    public String getUserId(){
        return this.userid;
    }
}
