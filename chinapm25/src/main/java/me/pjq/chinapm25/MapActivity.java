package me.pjq.chinapm25;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.Overlay;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    public static final String ME_PJQ_CHINAPM_25_PM_25 = "me.pjq.chinapm25.pm25";

    public static Intent newItent(Context context, PM25Object data) {
        Intent intent = new Intent(context, MapActivity.class);
        intent.putExtra(ME_PJQ_CHINAPM_25_PM_25, data);
        return intent;
    }

    private MapView mMapView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        mMapView = findViewById(R.id.bmapView);
        PM25Appliction pm25Appliction = (PM25Appliction)getApplication();
        ArrayList<PM25Object> pmList = pm25Appliction.getPM25Objects();

        PM25Object pm25Object = (PM25Object)getIntent().getSerializableExtra(ME_PJQ_CHINAPM_25_PM_25);
        if (pm25Object == null) {
            pm25Object = pmList.get(0);
        }

        LatLng currentGeo = new LatLng(Double.parseDouble(pm25Object.lat), Double.parseDouble(pm25Object.lng));
        BaiduMap map = mMapView.getMap();
        MapStatusUpdate status = MapStatusUpdateFactory.newLatLng(currentGeo);
        map.setMapStatus(status);
        for (PM25Object pmObj : pmList) {
            LatLng geo = new LatLng(Double.parseDouble(pmObj.lat), Double.parseDouble(pmObj.lng));
            OverlayOptions mTextOptions = new TextOptions()
                    .text(String.format("%s %s", pmObj.cityChinese,pmObj.getPm25()))
                    .bgColor(Color.parseColor(pmObj.getColor()))
                    .fontSize(48)
                    .fontColor(Integer.parseInt(pmObj.getPm25()) > 50 && Integer.parseInt(pmObj.getPm25()) <= 100 ? Color.BLACK : Color.WHITE)
                    .position(geo);
            map.addOverlay(mTextOptions);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
