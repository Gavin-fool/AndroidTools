package com.alier.com.commons.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

public class MemoryUtil {

	public static void logMemoryStats(Context context) {
        String text = "";
        //Get the number of loaded classes
        text += "\nLoadedClassCount="               + Debug.getLoadedClassCount();
        //Returns the global size, in bytes, of objects allocated by the runtime between a start and stop.
        text += "\nGlobalAllocSize="                + Debug.getGlobalAllocSize();
        //Returns the global size, in bytes, of objects freed by the runtime between a start and stop.
        text += "\nGlobalFreedSize="                + Debug.getGlobalFreedSize();
        
        text += "\nGlobalExternalAllocSize="        + Debug.getGlobalExternalAllocSize();
        
        text += "\nGlobalExternalFreedSize="        + Debug.getGlobalExternalFreedSize();
        //text += "\nEpicPixels="                     + EpicBitmap.getGlobalPixelCount()*4;
        
        text += "\nNativeHeapSize="                 + Debug.getNativeHeapSize();
        
        text += "\nNativeHeapFree="                 + Debug.getNativeHeapFreeSize();
        
        text += "\nNativeHeapAllocSize="            + Debug.getNativeHeapAllocatedSize();
        
        text += "\nThreadAllocSize="                + Debug.getThreadAllocSize();
   
        text += "\ntotalMemory()="                  + Runtime.getRuntime().totalMemory();
        
        text += "\nmaxMemory()="                    + Runtime.getRuntime().maxMemory();
        
        text += "\nfreeMemory()="                   + Runtime.getRuntime().freeMemory();
   
        ActivityManager.MemoryInfo mi1 = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(mi1);
        text += "\napp.mi.availMem="                + mi1.availMem;
        text += "\napp.mi.threshold="               + mi1.threshold;
        text += "\napp.mi.lowMemory="               + mi1.lowMemory;
   
        Debug.MemoryInfo mi2 = new Debug.MemoryInfo();
        Debug.getMemoryInfo(mi2);
        text += "\ndbg.mi.dalvikPrivateDirty="      + mi2.dalvikPrivateDirty;
        
        text += "\ndbg.mi.dalvikPss="               + mi2.dalvikPss;
        
        text += "\ndbg.mi.dalvikSharedDirty="       + mi2.dalvikSharedDirty;
        
        text += "\ndbg.mi.nativePrivateDirty="      + mi2.nativePrivateDirty;
        
        text += "\ndbg.mi.nativePss="               + mi2.nativePss;
        
        text += "\ndbg.mi.nativeSharedDirty="       + mi2.nativeSharedDirty;
        
        text += "\ndbg.mi.otherPrivateDirty="       + mi2.otherPrivateDirty;
        
        text += "\ndbg.mi.otherPss"                 + mi2.otherPss;
        
        text += "\ndbg.mi.otherSharedDirty="        + mi2.otherSharedDirty;
   
    }

}
