package org.nardhar

import spock.lang.Specification
import grails.test.mixin.TestMixin
import grails.test.mixin.web.GroovyPageUnitTestMixin

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@TestMixin(GroovyPageUnitTestMixin)
class TbLinkTagLibSpec extends Specification {

    def setup() {
        given:
        mockTagLib(TbHtmlTagLib)
        mockTagLib(TbElementTagLib)
        mockTagLib(TbLinkTagLib)
        mockTagLib(TbIconTagLib)
    }

    def cleanup() {
    }

    void "test link"() {
        expect:
        applyTemplate('<tbLink:link controller="a" action="b" label="text" />') == '<a href="/a/b">text</a>'
    }

    void "test button"() {
        expect:
        applyTemplate('<tbLink:button controller="a" action="b" label="text" />') == '<a href="/a/b" class="btn btn-default">text</a>'
    }

    void "test linkSubmit"() {
        expect:
        applyTemplate('<tbLink:linkSubmit controller="a" action="b" label="text" />') ==
            '<form action="/a/b" method="post" class="display-inline form-confirm" data-confirm-message="default.confirm.message" ><button class="btn btn-link" type="submit">text</button></form>'
    }

    void "test buttonSubmit"() {
        expect:
        applyTemplate('<tbLink:buttonSubmit controller="a" action="b" label="text" />') ==
            '<form action="/a/b" method="post" class="display-inline form-confirm" data-confirm-message="default.confirm.message" ><button class="btn btn-default" type="submit">text</button></form>'

    }

    void "test buttonSubmit with id"() {
        expect:
        applyTemplate('<tbLink:buttonSubmit controller="a" action="b" id="15" label="text" />') ==
            '<form action="/a/b" method="post" class="display-inline form-confirm" data-confirm-message="default.confirm.message" >' +
                '<input type="hidden" name="id" value="15" id="id" />' +
                '<button class="btn btn-default" type="submit">text</button>' +
            '</form>'

    }

    void "test buttonSubmit with id and params"() {
        expect:
        applyTemplate('<tbLink:buttonSubmit controller="a" action="b" id="15" params="[other: 1, another: 2]" label="text" />') ==
            '<form action="/a/b" method="post" class="display-inline form-confirm" data-confirm-message="default.confirm.message" >' +
                '<input type="hidden" name="id" value="15" id="id" />' +
                '<input type="hidden" name="other" value="1" id="other" />' +
                '<input type="hidden" name="another" value="2" id="another" />' +
                '<button class="btn btn-default" type="submit">text</button>' +
            '</form>'

    }

    void "test create"() {
        expect:
        applyTemplate('<tbLink:create controller="a" />') == '<a href="/a/create">'+applyTemplate('<tb:icon name="plus" />')+' default.button.create.label</a>'
    }

    void "test edit"() {
        expect:
        applyTemplate('<tbLink:edit controller="a" id="15" />') == '<a href="/a/edit/15">'+applyTemplate('<tb:icon name="pencil" />')+' default.button.edit.label</a>'
    }

    void "test show"() {
        expect:
        applyTemplate('<tbLink:show controller="a" id="15" />') == '<a href="/a/show/15">'+applyTemplate('<tb:icon name="folder-open" />')+' default.button.show.label</a>'
    }

    void "test delete"() {
        expect:
        applyTemplate('<tbLink:delete controller="a" id="15" />') ==
            '<form action="/a/delete" method="post" class="display-inline form-confirm" data-confirm-message="default.button.delete.confirm.message" >' +
                '<input type="hidden" name="id" value="15" id="id" />' +
                '<button class="btn btn-link" type="submit">' +
                    applyTemplate('<tb:icon name="remove" />') + ' default.button.delete.label' +
                '</button>' +
            '</form>'
    }
}
