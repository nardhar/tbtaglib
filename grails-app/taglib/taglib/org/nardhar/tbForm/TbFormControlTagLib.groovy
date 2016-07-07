package org.nardhar.tbForm

class TbFormControlTagLib {
    static namespace = 'tbForm'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def label = { attrs, body ->
        def required = attrs.remove('required')
        out << tb.elem(tag: 'label', attrs: attrs) {
            if (required) {
                out << tb.elem(tag: 'em', class: 'required') { '*' }
            }
            out << body()
        }
    }

    def hiddenField = { attrs ->
        out << tbForm.render(attrs) {
            g.hiddenField(it)
        }
    }

    def textField = { attrs ->
        out << tbForm.generate(attrs + [type: 'textField'])
    }

    def textArea = { attrs ->
        out << tbForm.generate(attrs + [type: 'textArea'])
    }

    def passwordField = { attrs ->
        out << tbForm.generate(attrs + [type: 'passwordField'])
    }

    def select = { attrs ->
        out << tbForm.generate(attrs + [type: 'select'])
    }

    def datePicker = { attrs ->
        if (!attrs.years && !attrs.relativeYears) {
            attrs.relativeYears = -10..30
        }
        if (!attrs.precision) {
            attrs.precision = 'day'
        }
        out << tbForm.render(attrs) {
            tbForm.datePickerRender(it)
        }
    }

    def radio = { attrs ->
        out << tbForm.render(attrs + [isRadioButton: true]) {
            g.radio(it)
        }
    }

    def checkBox = { attrs ->
        def rightLabel = attrs.containsKey('rightLabel') ? attrs.remove('rightLabel') : false
        if (rightLabel) {
            // quita la etiqueta de la izquierda
            attrs.remove('label')
        }
        out << tbForm.render(attrs) {
            out << g.checkBox(it)
            if (rightLabel) {
                //out << '&nbsp;'
                out << tb.elem(tag: 'label', class: 'for-radio') {
                    rightLabel
                }
            }
        }
    }

    def submit = { attrs ->
        //if (pageScope.formStyle == 'horizontal') {
        //    mandatory << [width: [sm: 8, md: 9], offset: [sm: 4, md: 3]]
        //}
        out << tb.elem(tag: 'div', class: 'form-group') {
            tb.col(width: [sm: 8, md: 10], offset: [sm: 4, md: 2]) {
                g.submitButton(tb.merge(current: attrs, additional: [class: 'btn btn-primary']))
            }
        }
    }

    def autoComplete = { attrs ->
        def optionKey = attrs.optionKey ?: 'id'
        def optionValue = attrs.optionValue ?: 'id'
        attrs.divClass = (attrs.fillForm ? '' : 'select-autocomplete-parent') + (attrs.divClass ? ' ' + attrs.divClass : '')
        def inputClass = attrs.class ?: ''
        def changeForSpan = false
        if (attrs.containsKey('changeForSpan')) {
            changeForSpan = attrs.remove('changeForSpan')
        }
        out << yfield(attrs + [inputRender: {
            def keyValue = attrs.value != null ? attrs.value.getAt(optionKey) : ''
            def textValue = attrs.value != null ? optionValue instanceof Closure ? optionValue(attrs.value).toString() : attrs.value.getAt(optionValue)  : ''
            g.hiddenField(name: "${it.name}-id", value: keyValue) +
            g.textField(name: it.name, value: textValue, class: "select-autocomplete ${inputClass}", placeholder: attrs.placeholder ?: '', 'data-toggle': 'tooltip', title: attrs.title ?: 'Escriba para buscar y seleccione del listado emergente') +
            g.hiddenField(name: "${it.name}-url", value: it.url, class: 'url')
        }])
    }

    def fileInput = { attrs, body ->
        def archivoTipo = attrs.remove('tipo')
        def archivoDominio = attrs.remove('dominio')
        def multipleClass = 'ypfb-file-upload-multiple'
        if (attrs.containsKey('multiple')) {
            def multiple = attrs.remove('multiple')
            if (!multiple) {
                multipleClass = ''
            }
        }
        def archivoInstance = attrs.value
        def withDescription = true
        if (attrs.containsKey('withDescription')) {
            withDescription = attrs.withDescription
        }
        def descriptionRequired = true
        if (attrs.containsKey('descriptionRequired')) {
            descriptionRequired = attrs.descriptionRequired
        }
        def showMaxSize = false
        if (attrs.containsKey('showMaxSize')) {
            showMaxSize = attrs.remove('showMaxSize')
        }
        //attrs.divClass = 'ypfb-file-upload-group'
        attrs.divControlsClass = 'ypfb-file-upload-group'
        //attrs.showDiv = false
        if (archivoInstance) {
            attrs.value = ypfbArchivo.downloadInfo(attrs + [archivo: archivoInstance])
            //attrs.value = ypfbAcl.link(controller: 'archivo', action: 'download', id: archivoInstance?.id, title: archivoInstance.fileName, 'data-toggle': 'tooltip') {
            //    '<i class="icon-download"></i> ' + archivoInstance?.prettyName()
            //}
            //attrs.value += ' (' + archivoInstance?.prettySize() + ')<br/>' + (archivoInstance?.descripcion ?: '')
        }
        // NOTE: agregando la muestra de los errores solo del campo "descripción"
        //       habría que hacer que se pueda mostrar todos los errores del dominio
        attrs.bean = archivoInstance
        attrs.field = 'descripcion'
        attrs.label = attrs.label ?: '&nbsp;'
        if (attrs.noLabel) {
            attrs.label = null
        }
        def plantillaLink
        if (attrs.containsKey('plantillaLink')) {
            plantillaLink = attrs.plantillaLink
        }
        attrs.name = attrs.name ?: 'archivo'
        out << yfield(attrs + [isFileInput: true, inputRender: {
            ypfbArchivo.puedeSubir(dominio: archivoDominio, tipo: archivoTipo) {
                //"<div class=\"controls ypfb-file-upload-group\">" +
                "<div class=\"ypfb-file-upload ${attrs.class ?: ''} ${multipleClass} ${archivoInstance ? 'for-edit' : ''}\">" +
                    g.link(url: '#', class: 'select') { 'Agregar Archivo' } +
                    (
                        plantillaLink ? '&nbsp;' + g.link(plantillaLink.url + [class: 'plantilla-tooltip']) {
                            def strElems = []
                            if (plantillaLink.icon || plantillaLink.label) {
                                if (plantillaLink.icon) {
                                    strElems << "<i class=\"${plantillaLink.icon}\"></i>"
                                }
                                if (plantillaLink.label) {
                                    strElems << plantillaLink.label
                                }
                            } else {
                                strElems << 'Descargar plantilla'
                            }
                            strElems.join(' ')
                        } :
                        ''
                    ) +
                    (archivoInstance ? '' : '<input type="file" class="file" />') +
                    g.hiddenField(name: "${attrs.name}Id", value: archivoInstance?.id ?: '') +
                    g.hiddenField(name: "${attrs.name}Url", value: g.createLink(controller: 'archivo', action: 'upload')) +
                    g.hiddenField(name: "${attrs.name}Dominio", value: archivoDominio ?: '') +
                    g.hiddenField(name: "${attrs.name}Tipo", value: archivoTipo ?: '') +
                    "<span class=\"file-name\"${archivoInstance ? ' title="' + archivoInstance.fileName + '"' : ''}>" +
                        (archivoInstance?.prettyName() ?: '') +
                    '</span>' +
                    '<span class="file-size">'+(archivoInstance ? ('(' + archivoInstance?.prettySize() + ')') : '')+'</span>' +
                    '<div class="progress-outer"><div class="progress"></div></div>' +
                    g.link(url: '#', class: 'cancel') { 'Cancelar' } +
                    g.link(url: '#', class: 'remove') { 'Quitar' } +
                    g.link(url: '#', class: 'change') { 'Cambiar' } +
                    '<div class="clearfix"></div>' +
                    (
                        showMaxSize ?
                        '<em class="muted archivo-error-maxsize">' + message(code: 'archivo.maxSizeUpload.message', args: [10]) + '</em>' + // consultar el maximo tamaño de archivos del servidor
                        '<div class="clearfix"></div>' : ''
                    ) +
                    '<div class="file-description">' +
                        (withDescription ? g.textArea(
                            name: "${attrs.name}Descripcion",
                            value: archivoInstance?.descripcion ?: '',
                            placeholder: "${descriptionRequired ? '*' : ''}Descripción${descriptionRequired ? ' (Requerida)'  : ' (Opcional)'}",
                            class: 'span12', maxlength: 2000
                        ) : '') +
                    '</div>' +
                '</div>' +
                '<div class="clearfix"></div>' +
                body()
                //+ '</div>'
            }
        }])
    }
}
