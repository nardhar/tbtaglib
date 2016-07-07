package org.nardhar

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestFor(TbHtmlTagLib)
class TbHtmlTagLibSpec extends Specification {

    def setup() {
        // tagLib is available by default of above annotation is used
        //tagLib.htmlUtilService = Mock(HtmlUtilService) {
        //    // Mock the service and stub the response
        //    1 * attributes(_) >> { ' class="sampleClass otherClass" id="sampleId"' }
        //}
    }

    def cleanup() {
    }

    void "test something"() {
    }
}
