package me.pjq.chinapm25;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.pjq.chinapm25.helper.UserPreference;


public class CityListFragment extends BaseFragment {
    private View view;
    private ListView listView;
    ExchangeHistoryListAdapter adapter;
    ArrayList<String> preDefineCityList;

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected View onGetFragmentView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.citylist_layout, null);

        return view;
    }

    // onCreateView
    @Override
    protected void ensureUi() {
        init();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.search_item);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQuery(UserPreference.getStoredQuery(getContext()), false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                UserPreference.setStoredQuery(getContext(), s);
                searchView.clearFocus();
                onStartGetData();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if ((s == null || s.isEmpty()) && UserPreference.getStoredQuery(getContext()) != null && !UserPreference.getStoredQuery(getContext()).isEmpty()) {
                    UserPreference.setStoredQuery(getContext(), null);
                    searchView.clearFocus();
                    onStartGetData();
                }
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_item:
                String defaultCity = UserPreference.getStoredQuery(getContext());
                PM25Appliction pm25Appliction = (PM25Appliction)getActivity().getApplication();
                PM25Object defaultObj = null;
                for (PM25Object pm25Object : pm25Appliction.getPM25Objects()) {
                    if (pm25Object.getCityChinese().equals(defaultCity) || pm25Object.getCityPingyin().equalsIgnoreCase(defaultCity)) {
                        defaultObj = pm25Object;
                    }
                }

                Intent intent = MapActivity.newItent(getActivity(), defaultObj);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

        listView = view.findViewById(R.id.list_view);
        adapter = new ExchangeHistoryListAdapter(getActivity());
        listView.setAdapter(adapter);
    }

    ProgressDialog progressDialog;

    private void showProgressDialog(String string) {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle(string);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (null != progressDialog) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private void onStartGetData() {
        showProgressDialog(getString(R.string.get_data));
        final PM25Appliction pm25Appliction = (PM25Appliction)getActivity().getApplication();
        if (pm25Appliction.getPM25Objects() != null) {
            hideProgressDialog();
            updateList(pm25Appliction.getPM25Objects());
            return;
        }

        VolleyManger.getInstance().getPM25From(new VolleyManger.OnResponse<String>() {
            @Override
            public void onResponse(Object error, String s) {
                if (getActivity().isFinishing() || isDetached()) {
                    return;
                }
                hideProgressDialog();

                try {
                    if (null == error) {
                        List<PM25Object> list = parserValues(s);
                        pm25Appliction.setPM25Objects((ArrayList<PM25Object>) list);
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
        list = sortSearchList(list);
        adapter.updateDataList(list, list.size());
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

        ArrayList<PM25Object> preDefineCityInfoList = new ArrayList<>();
        PM25Object cityInfo = null;
        for (String city : preDefineCityList) {
            cityInfo = getByCityName(list, city);
            if (null != cityInfo) {
                list.remove(cityInfo);
                preDefineCityInfoList.add(cityInfo);
            }
        }


        list.addAll(0, preDefineCityInfoList);

        return list;
    }

    private ArrayList<PM25Object> sortSearchList(ArrayList<PM25Object> list) {
        if (list == null) {
            return null;
        }

        String queryCity = UserPreference.getStoredQuery(getActivity());

        ArrayList<PM25Object> sortedList = new ArrayList<>();

        if (queryCity == null || queryCity.isEmpty()) {
            return list;
        }

        for (PM25Object cityInfo : list) {
            if (cityInfo.cityChinese.equals(queryCity) || cityInfo.cityPingyin.equalsIgnoreCase(queryCity)) {
                sortedList.add(0, cityInfo);
            } else {
                sortedList.add(cityInfo);
            }
        }

        return sortedList;
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
