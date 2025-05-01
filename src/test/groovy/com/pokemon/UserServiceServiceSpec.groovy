package com.pokemon

import grails.testing.services.ServiceUnitTest
import spock.lang.Specification

class UserServiceServiceSpec extends Specification implements ServiceUnitTest<UserServiceService> {

     void "test something"() {
        expect:
        service.doSomething()
     }
}
