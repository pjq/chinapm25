package me.pjq.chinapm25;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class CityListFragment extends BaseFragment implements View.OnClickListener {
    private Button continueExchangeButton;
    private View view;
    private ListView listView;
    ExchangeHistoryListAdapter adapter;

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

    private void onStartGetData() {
        showProgressDialog(getString(R.string.get_data));

        VolleyManger.getInstance().getPM25(new VolleyManger.OnResponse<String>() {
            @Override
            public void onResponse(Object error, String s) {
                if (null == error) {
                    onFinishGetData(true, gbk2utf8(s));
                } else {
                    onFinishGetData(false, s);
                }
            }
        });
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
                    return rhs.getPm25Int() - lhs.getPm25Int();
                }
            });


            adapter.updateDataList(list);

        } else {
            ToastUtil.showToast(getString(R.string.fail));
        }
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
