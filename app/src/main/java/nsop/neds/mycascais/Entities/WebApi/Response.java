package nsop.neds.mycascais.Entities.WebApi;

import java.util.List;

import nsop.neds.mycascais.Entities.Json.ReportList;

public class Response<T>{
    public List<ReportList> ReportList;
    public ReportSettings ReportSettings;
    public T ResponseData;
}
