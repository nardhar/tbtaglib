package org.nardhar

class TbTableTagLib {
    static namespace = 'tbTable'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def list = { attrs, body ->
        def itemList = attrs.remove('collection')
        
        def status = attrs.remove('status')
        if (attrs?.var) {
            pageScope.var = attrs.remove('var')
        }
        if (attrs?.beanClass) {
            pageScope.beanClass = attrs.remove('beanClass')
        }
        
        // generacion de titulo
        def withTitle = attrs.containsKey('withTitle') ? attrs.remove('withTitle') : true
        if (withTitle) {
            pageScope.fieldRenderOption = 'title'
            out << tb.row(class: 'table title', attrs: attrs) {
                body()
            }
        }
        
        // renderea cada item
        pageScope.fieldRenderOption = 'value'
        itemList.eachWithIndex { item, k ->
            pageScope.item = item
            def bodyParams = [:]
            if (pageScope.var) {
                bodyParams << [(pageScope.var): item]
            }
            if (status) {
                bodyParams << [(status): k]
            }
            out << tb.row(class: 'table table-hover', attrs: attrs) {
                body(bodyParams)
            }
        }
        pageScope.fieldRenderOption = null
    }

    def title = { attrs, body ->
        def fieldName = attrs.remove('field')
        def title = attrs.remove('label') ?: pageScope.beanClass != null ? message(code: "${pageScope.beanClass}.${fieldName}.label") : ''
        out << tb.col(attrs) {
            tb.elem(tag: 'strong') {
                if (body) {
                    out << body()
                } else {
                    out << tb.elem(tag: 'span') {
                        title
                    }
                }
            }
        }
    }

    def field = { attrs, body ->
        def fieldName = attrs.remove('field')
        def value = attrs.remove('value')
        
        if (pageScope.fieldRenderOption.equals('title')) {
            out << tbTable.title(attrs + [field: fieldName])
        } else if (pageScope.fieldRenderOption.equals('value')) {
            attrs.remove('label')
            attrs.remove('sortable')
            out << tb.col(attrs) {
                if (body) {
                    out << body([(pageScope.var): pageScope.item])
                } else {
                    if (!value) {
                        value = pageScope.item instanceof HashMap ? pageScope.item[fieldName] : pageScope.item.properties[fieldName]
                    }
                    out << (value instanceof Closure ? value(pageScope.item) : (value ?: ''))
                }
            }
        }
    }

    def actions = { attrs, body ->
        attrs.class = ((attrs.class ?: '') + ' actions').trim()
        if (pageScope.fieldRenderOption.equals('title')) {
            out << tbTable.title(attrs + [label: attrs.remove('label') ?: 'Acciones'])
        } else if (pageScope.fieldRenderOption.equals('value')) {
            attrs.remove('label')
            out << tb.col(attrs) {
                out << body()
            }
        }
    }
}
