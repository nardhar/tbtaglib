package org.nardhar

class TbLinkTagLib {
    static namespace = 'tbLink'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def link = { attrs ->
        def type   = attrs.remove('type') ?: 'link'   // puede ser 'link' o 'button'
        def method = attrs.remove('method') ?: 'link' // puede ser 'link' o 'submit'
        
        // construye el texto del enlace
        def textArray = []
        if (attrs.containsKey('icon')) {
            textArray << tb.icon(name: attrs.remove('icon'))
        }
        if (attrs.containsKey('label')) {
            textArray << attrs.remove('label')
        }
        if (attrs.containsKey('iconAfter') && attrs.remove('iconAfter')) {
            textArray = textArray.reverse()
        }
        def text = textArray.join(' ')
        
        def additional = [:]
        
        if (method == 'link') {
            if (type == 'button') {
                additional << [class: 'btn']
                if (!attrs.class?.contains('btn-')) {
                    additional.class += ' btn-default'
                }
            }
            // TODO: verificar permiso
            if (attrs.remove('showText') && !true) {
                out << text
            } else if (true) {
                out << g.link(tb.merge(current: attrs, additional: additional)) { text }
            }
        } else if (method == 'submit') {
            // prepara los atributos del boton
            additional = [
                class: 'btn',
                type: 'submit',
            ]
            if (type == 'link') {
                additional.class += ' btn-link'
            } else if (!attrs.class?.contains('btn-')) {
                additional.class += ' btn-default'
            }
            if (attrs.containsKey('elementId')) {
                additional << ['id': attrs.remove('elementId')]
            }
            
            // prepara los atributos del form
            def formAttrs = [class: 'display-inline']
            def formConfirm = attrs.containsKey('formConfirm') ? attrs.remove('formConfirm') : true
            if (formConfirm) {
                formAttrs.class += ' form-confirm'
                formAttrs << ['data-confirm-message': attrs.containsKey('data-confirm-message') ? attrs.remove('data-confirm-message') : message(code: 'default.confirm.message')]
            }
            if (attrs.containsKey('controller')) {
                formAttrs << [controller: attrs.remove('controller')]
            }
            if (attrs.containsKey('action')) {
                formAttrs << [action: attrs.remove('action')]
            }
            
            // prepara el form
            // TODO: verificar permisos
            out << g.form(formAttrs) {
                if (attrs.containsKey('id')) {
                    out << g.hiddenField(name: "id", value: attrs.remove('id'))
                }
                attrs.remove('params').each {
                    out << g.hiddenField(name: it.key, value: it.value)
                }
                out << tb.elem([tag: 'button', attrs: attrs] + additional) {
                    text
                }
            }
            
        }
    }

    // short methods
    def button = { attrs ->
        out << link(attrs + [type: 'button'])
    }

    def linkSubmit = { attrs ->
        out << link(attrs + [method: 'submit'])
    }

    def buttonSubmit = { attrs ->
        out << link(attrs + [type: 'button', method: 'submit'])
    }

    def create = { attrs ->
        out << link(attrs + [action: 'create', icon: 'plus', label: attrs?.remove('label') ?: message(code: 'default.button.create.label')])
    }
    
    def edit = { attrs ->
        out << link(attrs + [action: 'edit', icon: 'pencil', label: attrs?.remove('label') ?: message(code: 'default.button.edit.label')])
    }
    
    def delete = { attrs ->
        out << link(attrs + [
            action: 'delete',
            icon: 'remove',
            label: attrs?.remove('label') ?: message(code: 'default.button.delete.label'),
            'data-confirm-message': attrs?.remove('data-confirm-message') ?:
                                    message(code: 'default.button.delete.confirm.message'),
            method: 'submit',
        ])
    }
    
    def show = { attrs ->
        out << link(attrs + [action: 'show', icon: 'folder-open', label: attrs?.remove('label') ?: message(code: 'default.button.show.label')])
    }
    
}
