package com.example.microsoft

class Native {
    companion object { init { System.loadLibrary("microsoft") } }
    external fun inject(pkg: String, soPath: String): Boolean
    external fun moduleBase(pkg: String): Long
    external fun writeFloat(pkg: String, addr: Long, v: Float): Boolean
    external fun writeInt(pkg: String, addr: Long, v: Int): Boolean
    external fun scanFloat(pkg: String, target: Float): LongArray?
}
