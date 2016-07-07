package org.nardhar

class TbElementTagLib {
    
    static namespace = 'tb'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static singletonTags = [
        'area', 'base', 'br', 'col', 'command', 'embed', 'hr',
        'img', 'input', 'link', 'meta', 'param', 'source',
    ]
    
    def elem = { attrs, body ->
        def tag = attrs.remove('tag')
        def att = attrs.remove('attrs') ?: [:]
        out << "<$tag"
        out << tb.attributes(current: attrs, additional: att)
        if (tag in singletonTags) {
            out << "/>"
        } else {
            out << ">"
            out << body()
            out << "</$tag>"
        }
    }
}
