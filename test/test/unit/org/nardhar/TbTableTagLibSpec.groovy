package org.nardhar

import spock.lang.Specification
import grails.test.mixin.TestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestMixin(GroovyPageUnitTestMixin)
class TbTableTagLibSpec extends Specification {

    def setup() {
        given:
        mockTagLib(TbHtmlTagLib)
        mockTagLib(TbElementTagLib)
        mockTagLib(TbGridTagLib)
        mockTagLib(TbLinkTagLib)
        mockTagLib(TbIconTagLib)
        mockTagLib(TbTableTagLib)
    }

    def cleanup() {
    }

    void "test simpleTable"() {
        def tableTemp = '<tbTable:list collection="[[a:1,b:2],[a:3,b:4]]">' +
            '<tbTable:field field="a" width="[md:4]" label="A" />' +
            '<tbTable:field field="b" width="[md:8]" label="B" />' +
            '</tbTable:list>'
        
        expect:
        applyTemplate(tableTemp) ==
            '<div class="row table title">' +
                '<div class="col-md-4"><strong><span>A</span></strong></div>' +
                '<div class="col-md-8"><strong><span>B</span></strong></div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-4">1</div>' +
                '<div class="col-md-8">2</div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-4">3</div>' +
                '<div class="col-md-8">4</div>' +
            '</div>'
    }

    void "test complexTable"() {
        def tableTemp = '<tbTable:list collection="[[a:1,b:2],[a:3,b:4]]" var="item">' +
            '<tbTable:field field="a" width="[md:3]" label="A" />' +
            '<tbTable:field field="b" width="[md:4]" label="B" />' +
            '<tbTable:field field="b" width="[md:5]" label="C" value="${{it.b*1000}}" />' +
            '</tbTable:list>'
        
        expect:
        applyTemplate(tableTemp) ==
            '<div class="row table title">' +
                '<div class="col-md-3"><strong><span>A</span></strong></div>' +
                '<div class="col-md-4"><strong><span>B</span></strong></div>' +
                '<div class="col-md-5"><strong><span>C</span></strong></div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-3">1</div>' +
                '<div class="col-md-4">2</div>' +
                '<div class="col-md-5">2000</div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-3">3</div>' +
                '<div class="col-md-4">4</div>' +
                '<div class="col-md-5">4000</div>' +
            '</div>'
    }

    void "test complexTable with body"() {
        def tableTemp = '<tbTable:list collection="[[a:1,b:2],[a:3,b:4]]" var="item">' +
            '<tbTable:field field="a" width="[md:3]" label="A" />' +
            '<tbTable:field field="b" width="[md:4]" label="B" value="${{it.b*1000}}" />' +
            '<tbTable:field field="b" width="[md:5]" label="C">${item.b + 100}</tbTable:field>' +
            '</tbTable:list>'
        
        expect:
        applyTemplate(tableTemp) ==
            '<div class="row table title">' +
                '<div class="col-md-3"><strong><span>A</span></strong></div>' +
                '<div class="col-md-4"><strong><span>B</span></strong></div>' +
                '<div class="col-md-5"><strong><span>C</span></strong></div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-3">1</div>' +
                '<div class="col-md-4">2000</div>' +
                '<div class="col-md-5">102</div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-3">3</div>' +
                '<div class="col-md-4">4000</div>' +
                '<div class="col-md-5">104</div>' +
            '</div>'
    }

    void "test complexTable with actions"() {
        def tableTemp = '<tbTable:list collection="[[id:1,a:1,b:2],[id:2,a:3,b:4]]" var="item">' +
            '<tbTable:field field="a" width="[md:3]" label="A" />' +
            '<tbTable:field field="b" width="[md:4]" label="B" value="${{it.b*1000}}" />' +
            '<tbTable:actions width="[md:5]">' +
                '<tbLink:show controller="a" id="${item.id}" />' +
                '<tbLink:edit controller="a" id="${item.id}" />' +
                '<tbLink:delete controller="a" id="${item.id}" />' +
            '</tbTable:actions>' +
            '</tbTable:list>'
        
        expect:
        applyTemplate(tableTemp) ==
            '<div class="row table title">' +
                '<div class="col-md-3"><strong><span>A</span></strong></div>' +
                '<div class="col-md-4"><strong><span>B</span></strong></div>' +
                '<div class="col-md-5 actions"><strong><span>Acciones</span></strong></div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-3">1</div>' +
                '<div class="col-md-4">2000</div>' +
                '<div class="col-md-5 actions">' +
                    applyTemplate('<tbLink:show controller="a" id="1" />') +
                    applyTemplate('<tbLink:edit controller="a" id="1" />') +
                    applyTemplate('<tbLink:delete controller="a" id="1" />') +
                '</div>' +
            '</div>' +
            '<div class="row table table-hover">' +
                '<div class="col-md-3">3</div>' +
                '<div class="col-md-4">4000</div>' +
                '<div class="col-md-5 actions">' +
                    applyTemplate('<tbLink:show controller="a" id="2" />') +
                    applyTemplate('<tbLink:edit controller="a" id="2" />') +
                    applyTemplate('<tbLink:delete controller="a" id="2" />') +
                '</div>' +
            '</div>'
    }

    void "test title of table"() {
        def tableTemp = '<tbTable:list>' +
            '<tbTable:title field="a" width="[md:3]" label="A" />' +
            '<tbTable:title field="b" width="[md:4]">' +
                '<tbLink:link label="Ejemplo" />' +
            '</tbTable:title>' +
            '</tbTable:list>'
        
        expect:
        applyTemplate(tableTemp) ==
            '<div class="row table title">' +
                '<div class="col-md-3"><strong><span>A</span></strong></div>' +
                '<div class="col-md-4"><strong>'+
                applyTemplate('<tbLink:link label="Ejemplo" />') +
                '</strong></div>' +
            '</div>'
    }

}
