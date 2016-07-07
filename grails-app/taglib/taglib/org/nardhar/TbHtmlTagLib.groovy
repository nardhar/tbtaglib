package org.nardhar

class TbHtmlTagLib {

    static namespace = 'tb'
    static returnObjectForTags = ['merge', 'defaultMap']
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def attributes = { attrs ->
        def result = tb.merge(current: attrs.current, additional: attrs.additional).collect {
            "${it.key}=\"${it.value}\""
        }?.join(' ')
        
        if (result) {
            out << " $result"
        }
    }

    def merge = { attrs ->
        def current = attrs.current
        
        def addToCurrent
        addToCurrent = {
            if (it.key == 'attrs') {
                it.value.each(addToCurrent)
            } else {
                current << [(it.key): (
                    (current.containsKey(it.key) ? current[it.key]?.split(' ')?.toList() : []) +
                    it.value.split(' ').toList()
                ).unique().join(' ')]
            }
        }
        attrs.additional.each(addToCurrent)
        current
    }

    // width values = xs, sm, md, lg
    def width = { attrs ->
        def str = []
        if (attrs?.xs) {
            str << 'col-xs-' + attrs.xs
        }
        if (attrs?.sm) {
            str << 'col-sm-' + attrs.sm
        }
        if (attrs?.md) {
            str << 'col-md-' + attrs.md
        }
        if (attrs?.lg) {
            str << 'col-lg-' + attrs.lg
        }
        out << str.join(' ')
    }

    // width values = xs, sm, md, lg
    def offset = { attrs ->
        def str = []
        if (attrs?.xs) {
            str << 'col-xs-offset-' + attrs.xs
        }
        if (attrs?.sm) {
            str << 'col-sm-offset-' + attrs.sm
        }
        if (attrs?.md) {
            str << 'col-md-offset-' + attrs.md
        }
        if (attrs?.lg) {
            str << 'col-lg-offset-' + attrs.lg
        }
        out << str.join(' ')
    }

    def defaultMap = { attrs ->
        def map = attrs.map
        def var = attrs.var
        if (!map.containsKey(var)) {
            return attrs.defaultValue
        }
        def value = map.remove(var)
        value == false ? [:] : attrs.defaultValue + value
    }

}
