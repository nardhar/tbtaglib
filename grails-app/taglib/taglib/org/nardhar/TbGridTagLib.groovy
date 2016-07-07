package org.nardhar

class TbGridTagLib {
    static namespace = 'tb'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]
    
    def container = { attrs, body ->
        out << tb.elem(tag: 'div', class: 'container-fluid', attrs: attrs) {
            body()
        }
    }
    
    def row = { attrs, body ->
        out << tb.elem(tag: 'div', class: 'row', attrs: attrs) {
            body()
        }
    }
    
    def col = { attrs, body ->
        def widthParam  = attrs.remove('width')
        def offsetParam = attrs.remove('offset')
        
        def widthAndOffset = []
        if (widthParam instanceof Map) {
            widthAndOffset << tb.width(widthParam)
        } else if (widthParam instanceof String) {
            widthAndOffset << widthParam
        }
        if (offsetParam instanceof Map) {
            widthAndOffset << tb.offset(offsetParam)
        } else if (offsetParam instanceof String) {
            widthAndOffset << offsetParam
        }
        out << tb.elem(tag: 'div', class: widthAndOffset.join(' '), attrs: attrs) {
            body()
        }
    }

}
