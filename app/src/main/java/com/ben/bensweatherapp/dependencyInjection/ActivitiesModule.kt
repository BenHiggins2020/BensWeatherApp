package com.ben.bensweatherapp.dependencyInjection

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(ActivityComponent::class)
class ActivitiesModule {
}