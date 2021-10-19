package com.example.weatherapp.basemodule.base.platform

import com.example.weatherapp.basemodule.utils.Loading

class RetryOperation(val showLoading: Loading, val function: suspend () -> Any)