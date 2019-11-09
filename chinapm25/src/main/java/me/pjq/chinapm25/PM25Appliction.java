package me.pjq.chinapm25;

import java.util.ArrayList;

public class PM25Appliction extends BaseApplication {
    private ArrayList<PM25Object> mPM25Objects;

    public ArrayList<PM25Object> getPM25Objects() {
        return mPM25Objects;
    }

    public void setPM25Objects(ArrayList<PM25Object> PM25Objects) {
        mPM25Objects = PM25Objects;
    }
}
