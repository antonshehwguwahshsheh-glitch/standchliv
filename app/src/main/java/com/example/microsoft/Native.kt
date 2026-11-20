package com.example.microsoft

class Native {
    companion object { init { System.loadLibrary("microsoft") } }
    external fun writeInt(pkg: String, addr: Long, v: Int): Boolean
    external fun writeFloat(pkg: String, addr: Long, v: Float): Boolean
    external fun setAntiBan(pkg: String, enable: Boolean): Boolean
    external fun scanInt(pkg: String, target: Int): Long // Новая функция
}
