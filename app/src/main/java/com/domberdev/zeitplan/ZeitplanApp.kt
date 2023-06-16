package com.domberdev.zeitplan

import android.app.Application
import com.google.android.gms.ads.MobileAds

class ZeitplanApp: Application() {

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
    }
}