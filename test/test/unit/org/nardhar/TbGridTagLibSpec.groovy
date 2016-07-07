package org.nardhar

import spock.lang.Specification
import grails.test.mixin.TestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestMixin(GroovyPageUnitTestMixin)
class TbGridTagLibSpec extends Specification {

    def setup() {
        given:
        mockTagLib(TbHtmlTagLib)
        mockTagLib(TbElementTagLib)
        mockTagLib(TbGridTagLib)
    }

    def cleanup() {
    }

    void "test container"() {
        expect:
        applyTemplate('<tb:container>text</tb:container>') == '<div class="container-fluid">text</div>'
    }

    void "test row"() {
        expect:
        applyTemplate('<tb:row>text</tb:row>') == '<div class="row">text</div>'
    }

    void "test col"() {
        expect:
        applyTemplate('<tb:col width="[md: 2]">text</tb:col>') == '<div class="col-md-2">text</div>'
    }

    void "test width"() {
        expect:
        applyTemplate('<tb:width md="2" />') == 'col-md-2'
    }
}
