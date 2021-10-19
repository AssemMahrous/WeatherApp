package com.example.weatherapp.basemodule.base.platform

abstract class BaseUseCase<Repository : IBaseRepository>(val repository: Repository)