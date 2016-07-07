package org.nardhar

import spock.lang.Specification
import grails.test.mixin.TestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestMixin(GroovyPageUnitTestMixin)
class TbElementTagLibSpec extends Specification {

    def setup() {
        given:
        mockTagLib(TbHtmlTagLib)
        mockTagLib(TbElementTagLib)
    }

    def cleanup() {
    }

    void "test simpleElement with attrs and extra attributes for merging"() {
        expect:
        applyTemplate('<tb:elem tag="span" class="sampleClass" id="sampleId" attrs="[class: \'otherClass\']">text</tb:elem>') == '<span class="sampleClass otherClass" id="sampleId">text</span>'
    }

    void "test singletonElement with attrs and extra attributes for merging"() {
        expect:
        applyTemplate('<tb:elem tag="input" class="sampleClass" name="sampleName" attrs="[class: \'otherClass\']" />') == '<input class="sampleClass otherClass" name="sampleName"/>'
    }
}
