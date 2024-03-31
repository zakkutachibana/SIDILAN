package com.zak.sidilan.util

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.zak.sidilan.data.repositories.bookRepositoryModule
import com.zak.sidilan.ui.addbook.addBookActivityModule
import com.zak.sidilan.ui.addbook.addBookViewModelModule
import com.zak.sidilan.ui.bookdetail.bookDetailActivityModule
import com.zak.sidilan.ui.bookdetail.bookDetailViewModelModule
import com.zak.sidilan.ui.scan.scanActivityModule
import com.zak.sidilan.ui.scan.scanViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                addBookActivityModule,
                addBookViewModelModule,
                bookRepositoryModule,
                scanActivityModule,
                scanViewModelModule,
                bookDetailActivityModule,
                bookDetailViewModelModule,
                modalBottomSheetActionModule,
                )
        }
    }
}