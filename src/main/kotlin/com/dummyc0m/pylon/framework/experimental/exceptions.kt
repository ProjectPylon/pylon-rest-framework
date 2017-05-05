package com.dummyc0m.pylon.framework.experimental

/**
 * buncho exceptions, probably not useful at all.
 * Created by dummy on 3/7/17.
 */
class ServiceNotFoundException(message: String?) : RuntimeException(message)

class ServiceNotInitializedException(message: String?) : RuntimeException(message)

class InvalidRequest(message: String?): RuntimeException(message)