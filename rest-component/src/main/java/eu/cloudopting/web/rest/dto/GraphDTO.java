package eu.cloudopting.web.rest.dto;

import java.io.Serializable;

/**
 * @author Xavier Cases Camats (xavier.cases@worldline.com)
 */
public class GraphDTO implements Serializable
{
    private String xkey;
    private String[] labels;
    private Data[] data;
    private String[] lineColors;
    private String[] ykeys;

    public String getXkey (){
        return xkey;
    }

    public void setXkey (String xkey){
        this.xkey = xkey;
    }

    public String[] getLabels (){
        return labels.clone();
    }

    public void setLabels (String[] labels){
        this.labels = labels.clone();
    }

    public Data[] getData (){
        return data.clone();
    }

    public void setData (Data[] data){
        this.data = data.clone();
    }

    public String[] getLineColors () {
        return lineColors.clone();
    }

    public void setLineColors (String[] lineColors){
        this.lineColors = lineColors.clone();
    }

    public String[] getYkeys (){
        return ykeys.clone();
    }

    public void setYkeys (String[] ykeys) {
        this.ykeys = ykeys.clone();
    }

    @Override
    public String toString() {
        return "ClassPojo [xkey = "+xkey+", labels = "+labels+", data = "+data+", lineColors = "+lineColors+", ykeys = "+ykeys+"]";
    }
}
