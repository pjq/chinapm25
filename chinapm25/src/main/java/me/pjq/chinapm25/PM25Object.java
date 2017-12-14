package me.pjq.chinapm25;


/**
 * var myCompOverlay = new ComplexCustomOverlay(cityPoints["秦皇岛"], "秦皇岛", "秦皇岛", "秦皇岛 105 - 较不健康", "#eb8a14", "");
 * <p>
 * Created by pjq on 1/21/15.
 */
public class PM25Object {
    String cityPingyin;
    String cityChinese;
    String pm25;
    //    #eb8a14"
    String color;
    //较不健康"
    String levelDescription;
    String lat;
    String lng;

    public PM25Object() {

    }

    public String getColor() {
        return color;
    }

    public int getColorInt() {
        return  Utils.HexToInt(color.replace("#", "0x"));
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getLevelDescription() {
        return levelDescription;
    }

    public void setLevelDescription(String levelDescription) {
        this.levelDescription = levelDescription;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public int getIndeOfAll() {
        return indeOfAll;
    }

    public void setIndeOfAll(int indeOfAll) {
        this.indeOfAll = indeOfAll;
    }

    int indeOfAll;

    public PM25Object(String cityPingyin, String cityChinese, String pm25) {
        this.cityPingyin = cityPingyin;
        this.cityChinese = cityChinese;
        this.pm25 = pm25;
    }

    public String getCityPingyin() {
        return cityPingyin;
    }

    public void setCityPingyin(String cityPingyin) {
        this.cityPingyin = cityPingyin;
    }

    public String getCityChinese() {
        return cityChinese;
    }

    public void setCityChinese(String cityChinese) {
        this.cityChinese = cityChinese;
    }

    public String getPm25() {
        return pm25;
    }

    public int getPm25Int() {
        try {
            return Integer.valueOf(pm25);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }


}
