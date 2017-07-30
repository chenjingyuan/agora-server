package com.melot.recorder.inferface;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * Created by mn on 2017/7/17.
 */
public interface Kernel32 extends Library {
    public static Kernel32 INSTANCE = (Kernel32) Native.loadLibrary("jna",Kernel32.class);

    public int GetProcessId(Long hProcess);
}
