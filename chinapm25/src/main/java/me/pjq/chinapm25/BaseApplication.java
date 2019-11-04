package me.pjq.chinapm25;

import android.app.Application;
import android.content.Context;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

//public class AppApplication extends Application implements ActivityLifecycleCallbacks {
public class BaseApplication extends Application {

    private static final String TAG = BaseApplication.class.getName();

    protected static Context context;

    private Timer mActivityTransitionTimer;
    private TimerTask mActivityTransitionTimerTask;
    private final long MAX_ACTIVITY_TRANSITION_TIME_MS = 2000;

    private int resumed = 0;
    private int paused = 0;
    private int started = 0;
    private int stopped = 0;

    @Override
    public void onCreate() {
        context = getApplicationContext();
        super.onCreate();

        VolleyManger.init(context);

        ExecutorService backgroundExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r, "BackgroundExecutorThread");
                thread.setPriority(Thread.MIN_PRIORITY);
                return thread;
            }
        });

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        EFLogger.w(TAG, "onLowMemory");
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        EFLogger.i(TAG, "onTerminate");
    }

    public static Context getContext() {
        return context;
    }

}
