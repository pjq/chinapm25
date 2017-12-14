package me.pjq.chinapm25;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CityListFragment extends BaseFragment implements View.OnClickListener {
    private Button continueExchangeButton;
    private View view;
    private ListView listView;
    ExchangeHistoryListAdapter adapter;
    ArrayList<String> preDefineCityList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected View onGetFragmentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.citylist_layout, null);

        return view;
    }

    @Override
    protected void ensureUi() {
        init();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (null != progressDialog && progressDialog.isShowing()) {

        } else {
            onStartGetData();
        }
    }

    private void init() {
        preDefineCityList = getPreDefineCity();

        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ExchangeHistoryListAdapter(getApplicationContext());
        listView.setAdapter(adapter);

//        onStartGetData();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
        }
    }

    ProgressDialog progressDialog;

    private void showProgressDialog(String string) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(string);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void hideProgressDialog(){
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void onStartGetData() {
        showProgressDialog(getString(R.string.get_data));

//        VolleyManger.getInstance().getPM25(new VolleyManger.OnResponse<String>() {
//            @Override
//            public void onResponse(Object error, String s) {
//                try {
//                    if (null == error) {
//                        onFinishGetData(true, gbk2utf8(s));
//                    } else {
//                        onFinishGetData(false, s);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });

        VolleyManger.getInstance().getPM25From(new VolleyManger.OnResponse<String>() {
            @Override
            public void onResponse(Object error, String s) {
                if (getActivity().isFinishing()||isDetached()) {
                    return;
                }

                hideProgressDialog();

                try {
//                    if (null == error) {
//                        onFinishGetData(true, gbk2utf8(s));
//                    } else {
//                        onFinishGetData(false, s);
//                    }

                    if (null == error) {
                        List<PM25Object> list = parserValues(s);
                        updateList((ArrayList<PM25Object>) list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateList(ArrayList<PM25Object> list) {
        Collections.sort(list, new Comparator<PM25Object>() {
            @Override
            public int compare(PM25Object lhs, PM25Object rhs) {
                return -(rhs.getPm25Int() - lhs.getPm25Int());
            }
        });

        int total = list.size();
        for (int i = 0; i < total; i++) {
            list.get(i).setIndeOfAll(i);
        }

        list = appendPredefineList(list);
        adapter.updateDataList(list, list.size() - preDefineCityList.size());
    }

    Pattern cityPointsPattern = Pattern.compile("var cityPoints = (.*,\\});", Pattern.DOTALL);
    Pattern cityNamesPattern = Pattern.compile("var cityNames = (.*)\\}\\};", Pattern.DOTALL);
    Pattern myCompOverlayPattern = Pattern.compile("var myCompOverlay = .*\\);");

    List<PM25Object> parserValues(String html) {
        if (null == html) {
            return null;
        }

        //grep -E -i "var cityPoints|var cityNames|var myCompOverlay"
        Matcher matcher = cityPointsPattern.matcher(html);
        String cityPoints = null;
        while (matcher.find()) {
            String group = matcher.group(1);
            cityPoints = group;
            cityPoints = cityPoints.replace(",}", "}");
            int start = matcher.start();
            int end = matcher.end();
        }

        matcher = cityNamesPattern.matcher(html);
        String cityNames = null;
        while (matcher.find()) {
            String group = matcher.group(1);
            cityNames = group;
            int start = matcher.start();
            int end = matcher.end();
        }

        JSONObject cityPointsJsonObject = null;
        JSONObject cityNamesJsonObject = null;
        try {
            cityPointsJsonObject = new JSONObject(cityPoints);
            cityNamesJsonObject = new JSONObject(cityNames);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        matcher = myCompOverlayPattern.matcher(html);

        List<PM25Object> list = new ArrayList<>();
        while (matcher.find()) {
            //var myCompOverlay = new ComplexCustomOverlay(cityPoints["秦皇岛"], "秦皇岛", "秦皇岛", "秦皇岛 105 - 较不健康", "#eb8a14", "");
            String group = matcher.group();
            String cityValue = group.split(",")[3].replace("\"", "").trim();
            String color = group.split(",")[4].replace("\"", "").trim();

            PM25Object pm25Object = new PM25Object();
            pm25Object.setCityChinese(cityValue.split(" ")[0].trim());
            pm25Object.setPm25(cityValue.split(" ")[1].trim());
            pm25Object.setLevelDescription(cityValue.split(" ")[3].trim());
            pm25Object.setColor(color);

            //{"cityid":"1","pinyin":"beijing"}
            String cityNameJson = cityNamesJsonObject.optString(pm25Object.getCityChinese());
            //{"lng":116.395645,"lat":39.929986}
            String cityPointJson = cityPointsJsonObject.optString(pm25Object.getCityChinese());
            try {
                String cityNamePinyin = new JSONObject(cityNameJson).optString("pinyin");
                String lat = new JSONObject(cityPointJson).optString("lat");
                String lng = new JSONObject(cityPointJson).optString("lng");

                pm25Object.setLat(lat);
                pm25Object.setLng(lng);
                pm25Object.setCityPingyin(cityNamePinyin);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            list.add(pm25Object);
        }

        return list;
    }

    private void onFinishGetData(boolean isSuccess, String s) {
        if (!isFragmentStillAlive()) {
            return;
        }

        progressDialog.dismiss();
        progressDialog = null;

        if (isSuccess) {
            int max = 30;
            ArrayList<PM25Object> list = new ArrayList<PM25Object>();
            String[] all = s.split("\n");

            if (null == all || all.length == 0) {
                ToastUtil.showToast("List is empty");
                return;
            }

            for (String item : all) {
                String pingyin = "";
                String chinese = "";
                String pm25 = "";

                String[] pms = item.split(" ");
                if (pms != null && pms.length >= 2) {
                    String[] names = pms[0].split("_");
                    if (names != null && names.length >= 2) {
                        pingyin = names[0];
                        chinese = names[1];
                    }

                    pm25 = pms[1];
                }

                PM25Object object = new PM25Object(pingyin, chinese, pm25);
                list.add(object);
            }

            Collections.sort(list, new Comparator<PM25Object>() {
                @Override
                public int compare(PM25Object lhs, PM25Object rhs) {
                    return -(rhs.getPm25Int() - lhs.getPm25Int());
                }
            });

            int total = list.size();
            for (int i = 0; i < total; i++) {
                list.get(i).setIndeOfAll(i);
            }

            list = appendPredefineList(list);
            adapter.updateDataList(list, list.size() - preDefineCityList.size());

        } else {
            ToastUtil.showToast(getString(R.string.fail));
        }
    }

    private ArrayList<String> getPreDefineCity() {
        ArrayList<String> preDefine = new ArrayList<String>();
        preDefine.add("shanghai");
        preDefine.add("shenzhen");
        preDefine.add("beijing");
        preDefine.add("guangzhou");
        preDefine.add("nanchang");
        preDefine.add("xinyu");
        preDefine.add("suzhou");
        preDefine.add("yangzhou");
        preDefine.add("hangzhou");

        return preDefine;
    }

    private ArrayList appendPredefineList(ArrayList<PM25Object> list) {
        if (null == list) {
            return null;
        }


        ArrayList<PM25Object> preDefineCity = new ArrayList<PM25Object>();

        for (String city : preDefineCityList) {
            PM25Object object = getByCityName(list, city);
            if (null != object) {
                preDefineCity.add(object);
            }
        }

        list.addAll(0, preDefineCity);

        return list;
    }

    private PM25Object getByCityName(ArrayList<PM25Object> list, String key) {
        for (PM25Object object : list) {
            if (object.getCityChinese().equalsIgnoreCase(key) || object.getCityPingyin().equalsIgnoreCase(key)) {
                return object;
            }
        }

        return null;
    }

    public String gbk2utf8(String gbk) {
        String iso = "";
        String utf8 = gbk;
        try {
            iso = new String(gbk.getBytes("UTF-8"), "ISO-8859-1");
            utf8 = new String(iso.getBytes("ISO-8859-1"), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return utf8;
    }

}
