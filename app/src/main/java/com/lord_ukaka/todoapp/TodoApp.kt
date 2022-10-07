package com.lord_ukaka.todoapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

// This application class is created to setup DaggerHilt. After this, go to the manifest to
// add the name of the app the in the application block. Next, create the AppModule.
@HiltAndroidApp
class TodoApp: Application()