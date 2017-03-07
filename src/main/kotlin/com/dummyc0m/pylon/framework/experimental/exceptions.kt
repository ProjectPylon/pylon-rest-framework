package com.dummyc0m.pylon.framework.experimental

/**
 * Created by dummy on 3/7/17.
 */
class ServiceNotFoundExceotion(message: String?) : RuntimeException(message)

class ServiceNotInitializedException(message: String?) : RuntimeException(message)