package com.example.custom_sdk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

public class RemoteService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    private final IMyAidlInterface.Stub binder = new IMyAidlInterface.Stub() {
        public void basicTypes(int anInt, long aLong, boolean aBoolean,
                               float aFloat, double aDouble, String aString) {
        }

        @Override
        public String getMessage(String name) throws RemoteException {
            return "Hello " + name + ", Result is:";
        }

        @Override
        public int getPid() throws RemoteException {
            return 0;
        }
    };
}
