package model;

import java.util.LinkedList;
import java.util.List;

public class SearchPhonesRequest {

    private List<String> areaCodes = new LinkedList();

    public SearchPhonesRequest() {}

    public List<String> getAreaCodes() {
        return areaCodes;
    }

    public void setAreaCodes(List<String> areaCodes) {
        this.areaCodes = areaCodes;
    }

}
