package com.zak.sidilan.util

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.zak.sidilan.data.repositories.bookRepositoryModule
import com.zak.sidilan.data.repositories.userRepositoryModule
import com.zak.sidilan.ui.addbook.addBookActivityModule
import com.zak.sidilan.ui.addbook.addBookViewModelModule
import com.zak.sidilan.ui.auth.authActivityModule
import com.zak.sidilan.ui.auth.authViewModelModule
import com.zak.sidilan.ui.bookdetail.bookDetailActivityModule
import com.zak.sidilan.ui.bookdetail.bookDetailViewModelModule
import com.zak.sidilan.ui.books.booksFragmentModule
import com.zak.sidilan.ui.books.booksViewModelModule
import com.zak.sidilan.ui.bottomsheets.modalBottomSheetActionModule
import com.zak.sidilan.ui.dashboard.dashboardFragmentModule
import com.zak.sidilan.ui.dashboard.dashboardViewModelModule
import com.zak.sidilan.ui.scan.scanActivityModule
import com.zak.sidilan.ui.scan.scanViewModelModule
import com.zak.sidilan.ui.users.userDetailActivityModule
import com.zak.sidilan.ui.users.userDetailViewModelModule
import com.zak.sidilan.ui.users.userListActivityModule
import com.zak.sidilan.ui.users.userListViewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AuthManager.init(this)
        AndroidThreeTen.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(
                authActivityModule,
                authViewModelModule,
                booksFragmentModule,
                booksViewModelModule,
                dashboardViewModelModule,
                dashboardFragmentModule,
                addBookActivityModule,
                addBookViewModelModule,
                scanActivityModule,
                scanViewModelModule,
                bookDetailActivityModule,
                bookDetailViewModelModule,
                bookRepositoryModule,
                userRepositoryModule,
                userListViewModelModule,
                userListActivityModule,
                userDetailActivityModule,
                userDetailViewModelModule
                )
        }
    }
}