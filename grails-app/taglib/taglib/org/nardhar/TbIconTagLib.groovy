package org.nardhar

class TbIconTagLib {
    static namespace = 'tb'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    
    def icon = { attrs ->
        def name = "glyphicon glyphicon-${attrs.remove('name')}"
        out << tb.elem(tag: 'span', attrs: attrs, class: name, 'aria-hidden': 'true')
    }
    
}
