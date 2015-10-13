package danaapp.danaapp;

import android.app.Application;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.logger.LoggerFactory;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import danaapp.danaapp.db.DatabaseHelper;


public class MainApp  extends Application {

    private static Bus sBus;
    private static MainApp sInstance;


    private static DatabaseHelper databaseHelper = null;
    private static DanaConnection sDanaConnection = null;

    @Override
    public void onCreate() {
        super.onCreate();

        sBus = new Bus(ThreadEnforcer.ANY);
        sInstance = this;

    }

    public static Bus bus() {
        return sBus;
    }
    public static MainApp instance() {
        return sInstance;
    }

	public static DatabaseHelper getDbHelper() {
		if (databaseHelper == null) {
//            System.setProperty(LoggerFactory.LOG_TYPE_SYSTEM_PROPERTY,LoggerFactory.LogType.SLF4J.name());
			databaseHelper = OpenHelperManager.getHelper(sInstance, DatabaseHelper.class);
		}
		return databaseHelper;
	}

	public static void closeDbHelper() {
		if (databaseHelper != null) {
		    databaseHelper.close();
		    databaseHelper = null;
        }
	}

    @Override
    public void onTerminate() {
        super.onTerminate();
        databaseHelper.close();
    }

    public static DanaConnection getDanaConnection() {
        return sDanaConnection;
    }

    public static void setDanaConnection(DanaConnection con) {
        sDanaConnection = con;
    }


}