package org.nardhar

import spock.lang.Specification
import grails.test.mixin.TestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestMixin(GroovyPageUnitTestMixin)
class TbIconTagLibSpec extends Specification {

    def setup() {
        given:
        mockTagLib(TbHtmlTagLib)
        mockTagLib(TbElementTagLib)
        mockTagLib(TbIconTagLib)
    }

    def cleanup() {
    }

    void "test icon"() {
        expect:
        applyTemplate('<tb:icon name="plus" />') == '<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>'
    }

    void "test icon other"() {
        expect:
        applyTemplate('<tb:icon name="other" />') == '<span class="glyphicon glyphicon-other" aria-hidden="true"></span>'
    }
}
