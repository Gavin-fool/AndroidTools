package com.alier.com.commons.utils;

import java.util.HashSet;

import android.annotation.SuppressLint;
import android.database.sqlite.SQLiteDatabase;

import com.alier.com.commons.Config;

public class DBHelper {

    private static SQLiteDatabase db = null;

    private static Object lock = new Object();
    /**
     * 当前数据库是否被锁
     */
    private static boolean isdbLocked = false;
    /**
     * 存放当前访问数据库线程名
     */
    private static HashSet<String> setNames = new HashSet<String>();

    public DBHelper() {
        super();
        OpenDB();
    }

    public DBHelper(String threadName) {
        super();
        setNames.add(threadName);
        OpenDB();

    }

    @SuppressLint("NewApi")
    public void OpenDB() {
        if (db == null) {
            synchronized (lock) {
                if (null == db) {
                    try {
                        db = SQLiteDatabase
                                .openDatabase(Config.ANDROID_TEST_DBPATH,
                                        null, SQLiteDatabase.OPEN_READWRITE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public SQLiteDatabase getDb() {
        if (db == null) {
            OpenDB();
        }
        return db;
    }

    public void CloseDB(String threadName) {
        if (setNames.size() == 1) {
            if (db != null && db.isOpen()) {
                db.close();
                db = null;
            }
        }
        setNames.remove(threadName);

    }

    public void beginTransaction() {
        isDBUse();
        if (null == db || db.inTransaction()) {
            return;
        } else if (db.isOpen()) {
            isdbLocked = true;
            db.beginTransaction();
        }

    }

    public void commitTransaction() {
        if (null == db || !db.inTransaction()) {
            isdbLocked = false;
            return;
        }
        if (db.isOpen()) {
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        isdbLocked = false;
    }

    public void rollbackTransaction() {
        if (null == db || !db.inTransaction()) {
            isdbLocked = false;
            return;
        }
        if (db.isOpen()) {
            db.endTransaction();
        }
        isdbLocked = false;
    }

    public boolean isUse() {
        if (db != null) {
            return true;
        } else {
            return false;
        }
    }

    public void isDBUse() {
        synchronized (lock) {
            if (DBHelper.isdbLocked) {
                while (isdbLocked) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
