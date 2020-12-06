// IMyAidlInterface.aidl
package com.example.custom_sdk;

// Declare any non-default types here with import statements

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
        String getMessage(String name);
         int getPid();
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
}
