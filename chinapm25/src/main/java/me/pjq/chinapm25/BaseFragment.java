package me.pjq.chinapm25;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    private String TAG;

    private View mFragmentView;
    private boolean reuseFragmentView = false;
    ViewGroup container;
    private boolean isFragmentAlive = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = this.getClass().getSimpleName();
        EFLogger.i(TAG, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EFLogger.i(TAG, "onCreateView");
        this.container = container;

        if (reuseFragmentView) {
            if (null == mFragmentView) {
                mFragmentView = onGetFragmentView(inflater);
                ensureUi();
            }
        } else {
            mFragmentView = onGetFragmentView(inflater);
            ensureUi();
        }

        return mFragmentView;
    }


    protected View getContainerView() {
        return mFragmentView;
    }

    /**
     * Return the FragmentView created by
     * {@link #onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)}
     *
     * @return
     */
    protected abstract View onGetFragmentView(LayoutInflater inflater);

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    /**
     * Init the ui,such as retriev the view id.
     */
    protected abstract void ensureUi();

    @Override
    public void onPause() {
        super.onPause();

        EFLogger.i(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();

        EFLogger.i(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();

        EFLogger.i(TAG, "onStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        EFLogger.i(TAG, "onDestroy");
        isFragmentAlive = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        EFLogger.i(TAG, "onDestroyView");

        isFragmentAlive = false;
        Utils.unBindDrawables(mFragmentView);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        isFragmentAlive = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        isFragmentAlive = false;
        EFLogger.i(TAG, "onDestroyView");
    }

    protected Context getApplicationContext() {
        return BaseApplication.getContext().getApplicationContext();
    }

    protected boolean isFragmentStillAlive() {
        return isFragmentAlive && null != getActivity() && !getActivity().isFinishing() && !isDetached();
    }

}
