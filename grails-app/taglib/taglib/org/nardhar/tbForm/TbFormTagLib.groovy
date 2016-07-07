package org.nardhar.tbForm

class TbFormTagLib {
    static namespace = 'tbForm'
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    def form = { attrs, body ->
        // parametros del form
        def container = tb.defaultMap(map: attrs, var: 'container', defaultValue: [width: [md: 10], offset: [md: 1]])
        def submit = tb.defaultMap(map: attrs, var: 'submit', defaultValue: [name: 'create', value: g.message(code: 'default.save.button.label')])
        def upload = attrs.containsKey('upload') ? attrs.remove('upload') : false
        def formStyle = attrs.containsKey('formStyle') ? attrs.remove('formStyle') : 'horizontal'
        def readOnly = attrs.containsKey('readOnly') ? attrs.remove('readOnly') : false
        // variables a usar en los controles
        pageScope.bean = attrs.remove('bean')
        pageScope.beanClass = attrs?.containsKey('beanClass') ? attrs.remove('beanClass') : pageScope.bean ? pageScope.bean?.class?.name : ''
        pageScope.formStyle = formStyle
        def tempReadOnly = pageScope.readOnly
        pageScope.readOnly = readOnly
        // parametros adicionales del form
        attrs = tb.merge(current: attrs, additional: [class: 'form-' + formStyle])
        if (readOnly) {
            submit = false
        } else {
            if (upload) {
                attrs['enctype'] = 'multipart/form-data'
            }
        }
        def formContent = {
            body() + (submit ? tbForm.submit(submit) : '')
        }
        // genera el form en attrs deberian quedarse solo elementos validos de g.form()
        out << tbForm.formContainer(container) {
            readOnly ? tb.elem(attrs + [tag: 'div'], formContent) : g.form(attrs, formContent)
        }
        // recupera valores temporales
        pageScope.readOnly = tempReadOnly
    }

    def formInline = { attrs, body ->
        out << tbForm.form(attrs + [formStyle: 'inline']) {
            body()
        }
    }

    def formHorizontal = { attrs, body ->
        out << tbForm.form(attrs + [formStyle: 'horizontal']) {
            body()
        }
    }

    def create = { attrs, body ->
        out << tbForm.form(attrs + [submit: [name: 'create'], action: 'save']) {
            body()
        }
    }

    def edit = { attrs, body ->
        out << tbForm.form(attrs + [submit: [name: 'edit'], action: 'update']) {
            body()
        }
    }

    def show = { attrs, body ->
        out << tbForm.form(attrs + [readOnly: true]) {
            body()
        }
    }

    def fieldset = { attrs, body ->
        def legend = attrs.containsKey('legend') ? attrs.remove('legend') : ''
        out << tb.elem(tag: 'fieldset', attrs: attrs) {
            (legend ? tb.elem(tag: 'legend') { legend } : '') + body()
        }
    }

    // para un formulario de busqueda
    def searchContainer = { attrs, body ->
        out << tb.row() {
            body()
        }
        // prepara un div para indicar lo que se esta buscando
        // todos los parametros deben empezar por 'qs' (Filtros) o ser 'q' (Texto a buscar)
        if (pageScope.formSearch) {
            def filters = pageScope.formSearch.qsParams?.findAll { (it.name.startsWith('qs') || it.name == 'q') && it.value } ?: []
            if (filters) {
                def q = filters.find { it.name == 'q' }?.value
                def filtersText = filters.findAll { it.name != 'q' && it.value }.collect { "${it.label}: ${it.value ?: 'Todos'}" }.join(', ')
                out << tb.row() {
                    tb.col(width: [sm: 12, md: 8]) {
                        out << tb.elem(tag: 'small') {
                            out << 'Buscando: '
                            out << tb.elem(tag: 'strong') {
                                "${q ?: ''}" +
                                (filtersText ? ' (' : '') + (filtersText ?: '') + (filtersText ? ')' : '')
                            }
                            out << ' ' + g.link() { 'Limpiar' }
                        }
                    }
                }
            }
        }
        pageScope.formSearch = null
    }

    def search = { attrs, body ->
        pageScope.formSearch = [icon: false, qsParams: []]
        // la accion por defecto es 'search'
        if (!attrs.action) {
            attrs.action = 'search'
        }
        // el formulario es inline
        out << tb.col(width: attrs.remove('width')) {
            formInline(tb.merge(current: attrs, additional: [class: 'form-search']) + [withSubmit: false, withContainer: false]) {
                tb.row() {
                    body()
                }
            }
        }
    }

    // para un listado de varios inputs en forma de tabla (master-detail)
    def list = { attrs, body ->
        // para que no genera el div contenedor
        def tempShowDiv = pageScope.showDiv
        def tempTableDiv = pageScope.tableDiv
        def tempBeanClass = pageScope.beanClass
        
        pageScope.showDiv = false
        pageScope.tableDiv = true
        if (attrs?.beanClass) {
            pageScope.beanClass = attrs.remove('beanClass')
        }
        
        if (attrs?.var) {
            pageScope.var = attrs.var
        }
        pageScope.name = attrs.remove('name') ?: ''
        
        def itemList = attrs.collection ?: []
        def min = attrs.min ?: 1
        def label = attrs.containsKey('label') ? attrs.remove('label') : null
        def withTitle = attrs.containsKey('withTitle') ? attrs.remove('withTitle') : true
        def divRemove = attrs.containsKey('divRemove') ? attrs.remove('divRemove') : false
        def showAddLink = attrs.containsKey('showAddLink') ? attrs.remove('showAddLink') : true
        
        def contentGenerator = {
            // crea los titulos
            if (withTitle) {
                pageScope.fieldRenderOption = 'title'
                out << '<div class="row table title">'
                out << body()
                out << '</div>'
            }
            
            // renderea cada item
            pageScope.fieldRenderOption = 'value'
            def rowAttrs = [class: 'row table addable']
            def itemRender = { item, val, removable ->
                pageScope.item = item
                pageScope.iterator = val
                def removeClass = []
                if (!removable) {
                    removeClass << 'not-removable'
                }
                if (divRemove) {
                    removeClass << 'addable-div-remove'
                }
                out << tb.elem(tag: 'div', attrs: rowAttrs, class: removeClass.join(' ')) {
                    def bodyParams = [:]
                    if (pageScope.var) {
                        bodyParams << [(pageScope.var): item]
                    }
                    body(bodyParams)
                }
            }
            def totalMostrar = [min, itemList.size()].max() - 1
            (0..totalMostrar).toList().each {
                def item = itemList.size() > 0 ? itemList.first() : null
                itemList = itemList.drop(1)
                itemRender.call(item, it, it > min - 1)
            }
            
            // quita el fieldRenderOption
            pageScope.fieldRenderOption = null
            
            // datos de suma y del link para agregar filas
            def addRowOptions = [width: [md: 2]] + (attrs.remove('addRowOptions') ?: [:])
            def sumableTotalLabel = [label: 'Total', width: [md: 1]] + (attrs.remove('sumableTotalLabel') ?: [:])
            if ((!pageScope.readOnly && showAddLink) || pageScope.dataSumable) {
                out << tb.row(attrs: pageScope.readOnly ? [:] : [class: 'addable-link'], class: 'table') {
                    out << tb.col(width: addRowOptions.width) {
                        if (!pageScope.readOnly && showAddLink) {
                            out << g.link(url: '#', class: 'addRow') {
                                attrs.remove('addRowLabel') ?:
                                g.message(code: (pageScope.name ? pageScope.name + '.' : '') + 'addRow.label')
                            }
                        }
                    }
                    if (pageScope.dataSumable) {
                        out << tb.col(width: sumableTotalLabel.width) {
                            tb.elem(tag: 'strong') {
                                out << tb.elem(tag: 'span', class: 'display-input') {
                                    sumableTotalLabel.label
                                }
                            }
                        }
                        pageScope.dataSumableFields.each {
                            out << tbForm.textField(
                                name: pageScope.name + '.' + it.key,
                                value: it.value.sum,
                                // si estuviera en modo lectura haria que el total este en negrilla agregando un strong
                                valueBold: pageScope.readOnly,
                                class: it.value.class,
                                disabled: true,
                                divClass: it.value.divClass,
                                dataNumber: [decimals: it.value.decimals]
                            )
                        }
                    }
                }
            }
            // TODO: renderea los errores del listado
            
        }
        
        def showDiv = attrs.containsKey('showDiv') ? attrs.remove('showDiv') : true
        def fieldContainerAttrs = [width: [sm: 12]]
        if (pageScope.formStyle == 'horizontal') {
            fieldContainerAttrs += [label: '', labelAttrs: [class: 'control-label ' + tb.width(sm: 3)]]
            fieldContainerAttrs << [width: [sm: 9]]
        }
        if (label) {
            fieldContainerAttrs.label = label
        }
        if (showDiv != null) {
            fieldContainerAttrs += [showDiv: showDiv]
        }
        fieldContainer.call(fieldContainerAttrs, contentGenerator)
        
        // quita el sumable para renderear otras tablas
        if (pageScope.dataSumable) {
            pageScope.dataSumable = null
            pageScope.dataSumableFields = null
        }
        // recupera otros valores
        pageScope.showDiv = tempShowDiv
        pageScope.tableDiv = tempTableDiv
        pageScope.beanClass = tempBeanClass
    }

    // otros utiles
    def formContainer = { attrs, body ->
        if (!attrs.offset && attrs.containsKey('offset')) {
            attrs.remove('offset')
        }
        if (!attrs.width && attrs.containsKey('width')) {
            attrs.remove('width')
        }
        if (!attrs.width) {
            attrs.width = [xs: 12, sm: 12, md: 12, lg: 12]
            if (attrs.offset) {
                attrs.width.inject([:]) { acc, val ->
                    attrs.width[val.key] -= attrs.offset.getAt(val.key) ?: 0
                }
            }
        }
        out << tb.row() {
            tb.col(attrs) {
                body()
            }
        }
    }

    def controlsContainer = { attrs, body ->
        def showDiv = attrs.containsKey('showDiv') ? attrs.remove('showDiv') :
                      pageScope.showDiv != null ? pageScope.showDiv : true

        def label = attrs.containsKey('label') ? attrs.remove('label') : null
        def labelAttrs = attrs.remove('labelAttrs') ?: [:]
        
        def contentGenerator = {
            if (label != null) {
                out << tbForm.label(labelAttrs) {
                    label
                }
            }
            out << body()
        }
        if (showDiv) {
            out << tb.elem([tag: 'div', attrs: attrs, class: 'form-group'], contentGenerator)
        } else {
            contentGenerator.call()
        }
    }

    def fieldContainer = { attrs, body ->
        def showDiv = attrs.containsKey('showDiv') ? attrs.remove('showDiv') :
                      pageScope.showDiv != null ? pageScope.showDiv : true
        def width = [sm: 6] + (attrs.remove('width') ?: [:])
        def offset = [:] + (attrs.remove('offset') ?: [:])
        if (showDiv) {
            out << tbForm.controlsContainer(attrs + [showDiv: true]) {
                if (pageScope.formStyle == 'horizontal') {
                    out << tb.col(width: width, offset: offset) {
                        body()
                    }
                } else {
                    body()
                }
            }
        } else {
            out << body()
        }
    }

    def render = { attrs, body ->
        // recopila parametros enviados
        def bean = attrs.containsKey('bean') ? attrs.remove('bean') : pageScope?.bean
        def beanClass = attrs.remove('beanClass') ?: pageScope?.beanClass
        def field = attrs.remove('field')
        
        def label = attrs.containsKey('label') ? attrs.remove('label') :
                    field ? message(code: "${beanClass}.${field}.label") : ''
        def readOnly = attrs.containsKey('readOnly') ? attrs.remove('readOnly') : (pageScope?.readOnly ?: false)
        def required = attrs.containsKey('required') ? attrs.remove('required') : false
        def renderErrors = attrs.containsKey('renderErrors') ? attrs.remove('renderErrors') : true
        def width = attrs.remove('width')
        def offset = attrs.remove('offset')
        def isRadioButton = attrs.remove('isRadioButton')
        def isFileInput = attrs.remove('isFileInput')
        def isControl = attrs.containsKey('isControl') ? attrs.remove('isControl') : true
        // add-ons
        def prependText = attrs.remove('prepend') ?: ''
        def appendText = attrs.remove('append') ?: ''
        def appendSubmit = attrs.containsKey('appendSubmit') ? attrs.remove('appendSubmit') : false
        
        def showDiv = attrs.containsKey('showDiv') ? attrs.remove('showDiv') :
                      pageScope.showDiv != null ? pageScope.showDiv : true
        // para las tablas
        def divWidth = attrs.containsKey('divClass') ? attrs.remove('divClass') : [:]
        // si no se hubiera enviado un nombre utiliza el field
        if (!attrs.name) {
            attrs.name = field
        }
        // valor actual
        if (!attrs.value) {
            if (bean && field) {
                def beanFieldValue = bean instanceof LinkedHashMap ? bean.getAt(field) : bean.properties[field]
                if (beanFieldValue) {
                    attrs.value = beanFieldValue
                }
            }
        }
        if (attrs.value instanceof Closure) {
            attrs.value = attrs.value(bean)
        }
        def value = attrs.value instanceof Date ? attrs.value.fd(attrs.precision) : attrs.value
        
        // clase por defecto del control
        if (isControl) {
            attrs = tb.merge(current: attrs, additional: [class: 'form-control'])
        }
        
        // Closure para generar el control
        def contentGenerator
        
        // verifica si es un numero (data-number)
        // lo hace antes de generar la tabla pero despues de verificar si se envio 'name' o 'field'
        // para que use el parametro enviado originalmente y no el que tiene el iterador
        def dataNumber = [:]
        if (attrs.containsKey('dataNumber')) {
            dataNumber = attrs.remove('dataNumber')
            def decimals = dataNumber.containsKey('decimals') ? dataNumber.decimals : 2
            if (value != null) {
                value = value.rd(decimals)
            }
            attrs.put('data-number-decimals', decimals)
            if (dataNumber.containsKey('sumable')) {
                def numberInputName = attrs.name
                if (pageScope.fieldRenderOption == 'value') {
                    numberInputName = pageScope.name + '.' + attrs.name
                }
                attrs.put('data-number-sumable', numberInputName)
                pageScope.dataSumable = true
                if (!pageScope.dataSumableFields) {
                    pageScope.dataSumableFields = [:]
                }
                if (!pageScope.dataSumableFields[attrs.name]) {
                    pageScope.dataSumableFields[attrs.name] = [divClass: tableDivClass, sum: 0.0, class: attrs.class ?: '', decimals: decimals]
                }
                pageScope.dataSumableFields[attrs.name].sum += (value ?: 0.0).rd(decimals)
            }
            if (value != null) {
                value = value.fd(decimals)
            }
        }
        
        // si estuviera generando una tabla
        if (pageScope['fieldRenderOption'] == 'title') {
            out << tbTable.title(width: divWidth) {
                if (required) {
                    out << tb.elem(tag: 'em', class: 'required') { '*' }
                }
                out << label
            }
        } else {
            if (pageScope.fieldRenderOption == 'value') {
                // para que genere el nombre con un numero correlativo utilizando pageScope.name del listado
                // excepto si es radio
                if (isRadioButton) {
                    attrs.name = pageScope.name + '.' + attrs.name
                } else {
                    attrs.name = pageScope.name + '.' + pageScope.iterator + '.' + attrs.name
                }
            }
            
            if (readOnly) {
                // muestra un div.controls con span.display-input
                if (attrs.optionValue) {
                    value = value != null ? (attrs.optionValue instanceof Closure ? attrs.optionValue(value).toString().encodeAsHTML() : value.getAt(attrs.optionValue)) :
                            attrs.noSelection ? attrs.noSelection.find { true }.value :
                            ''
                }
                contentGenerator = {
                    value = (isRadioButton ? (attrs.checked ? 'SI' : 'NO') : ((isFileInput ? (value ?: '') : "${(value ?: '').encodeAsHTML()}") ?: '&nbsp;'))
                       
                    out << tb.elem(tag: 'span', class: 'form-control-static') {
                        if (prependText) {
                            out << prependText + ' '
                        }
                        
                        out << value
                        
                        if (appendText) {
                            out << ' ' + appendText
                        }
                    }
                }
            } else {
                if (attrs.optionKey) {
                    attrs.value = value?.getAt(attrs.optionKey)
                }
                // utiles para la busqueda
                if (pageScope.formSearch) {
                    pageScope.formSearch.qsParams << [name: attrs.name, label: label, value: attrs.optionValue ? (value != null ? value.getAt(attrs.optionValue) : '') : value]
                }
                // crea el contenido
                contentGenerator = {
                    def inputTemplate = ''
                    def inputTemplateAttrs = [
                        prependText: prependText,
                        appendText: appendText,
                        appendButton: appendSubmit,
                    ]
                    if (prependText || appendText || appendSubmit || pageScope.formSearch) {
                        inputTemplate = 'Group'
                        if (pageScope.formSearch && pageScope.formSearch?.icon == false) {
                            inputTemplateAttrs.prependText = tb.icon(name: 'search')
                            pageScope.formSearch.icon = true
                        }
                    }
                    out << render(template: '/tb/input' + inputTemplate, model: inputTemplateAttrs) {
                        body(attrs)
                    }
                    // genera los errores
                    if (renderErrors && bean && field && !(bean instanceof LinkedHashMap)) {
                        if (bean?.hasErrors()) {
                            out << tb.elem(tag: 'ul', class: 'error') {
                                def str = ''
                                g.eachError(bean: bean, field: field) { err ->
                                    str += tb.elem(tag: 'li') {
                                        g.message(error: err)
                                    }
                                }
                                out << str
                            }
                        }
                    }
                }
            }
            
            if (pageScope.tableDiv || pageScope.formSearch) {
                out << tb.col(width: divWidth, class: 'form-group') {
                    contentGenerator.call()
                }
            } else {
                // formFieldContainer
                def fieldContainerAttrs = [:]
                if (width) {
                    fieldContainerAttrs << [width: width]
                }
                if (offset) {
                    fieldContainerAttrs << [offset: offset]
                }
                if (pageScope.formStyle == 'horizontal' && isControl) {
                    fieldContainerAttrs += [label: '', labelAttrs: [class: 'control-label ' + tb.width(sm: 3)]]
                }
                if (label) {
                    fieldContainerAttrs.label = label
                    fieldContainerAttrs.labelAttrs = (fieldContainerAttrs.labelAttrs ?: [:]) + ([required: required && !readOnly] + (readOnly ? [:] : ['for': attrs.name]))
                }
                if (showDiv != null) {
                    fieldContainerAttrs += [showDiv: showDiv]
                }
                fieldContainer.call(fieldContainerAttrs, contentGenerator)
            }
        }
    }
    
    def generate = { attrs ->
        def type = attrs.remove('type')
        
        def bean = attrs.containsKey('bean') ? attrs.remove('bean') : pageScope?.bean
        def beanClass = attrs.remove('beanClass') ?: pageScope?.beanClass
        def field = attrs.remove('field')
        
        def isMultiple = attrs.remove('isMultiple') ?: pageScope?.isMultiple
        
        // verifica si colocar en modo lectura
        def readOnly = attrs.containsKey('readOnly') ? attrs.remove('readOnly') : false
        
        // el valor (es un objeto segun el tipo indicado)
        if (!attrs.value && bean && field) {
            def beanFieldValue = bean instanceof LinkedHashMap ? bean.getAt(field) : bean.properties[field]
            if (beanFieldValue != null) {
                attrs.value = beanFieldValue
            }
        }
        if (attrs.value instanceof Closure) {
            attrs.value = attrs.value(bean)
        }
        
        // prepara los valores a enviar al control
        if (!readOnly && attrs.optionKey) {
            attrs.value = attrs.value?.getAt(attrs.optionKey)
        }
        if (readOnly && attrs.optionValue) {
            attrs.value = attrs.value?.getAt(attrs.optionValue)
        }
        if (!attrs.name) {
            attrs.name = field
        }
        
        // prepara el template y sus valores
        def templateAttrs = [
            bean: bean,
            field: field,
            type: type,
            labelFor: attrs.name,
            required: attrs.remove('required') ?: false,
            showLabel: attrs.containsKey('showLabel') ? attrs.remove('showLabel') : true,
            readOnly: readOnly,
            width: attrs.remove('width') ?: [xs: 12],
            withAddon: attrs.containsKey('prepend') || attrs.containsKey('append') || attrs.containsKey('appendSubmit'),
            fieldDataList: [],
        ]
        if (templateAttrs.showLabel) {
            templateAttrs.label = attrs.containsKey('label') ? attrs.remove('label') :
                                  message(code: "${beanClass}.${field}.label")
        }
        if (!isMultiple) {
            templateAttrs.fieldDataList << [
                type: type,
                field: field,
                width: templateAttrs.width,
                withAddon: templateAttrs.withAddon,
                attrs: attrs,
            ]
        }
        
        def fieldTemplate = isMultiple ? 'input' : 'fieldGroup'
        out << tbForm."${fieldTemplate}"(templateAttrs) {
            g."${type}"(tb.merge(current: attrs, additional: [class: 'form-control']))
        }
    }

    def fieldGroup = { attrs, body ->
        out << tb.elem(tag: 'div', class: 'form-group' + (pageScope.formStyle == 'inline' ? ' ' + tb.width(attrs.width) : '')) {
            def str = ''
            if (attrs.showLabel) {
                str += tbForm.label(
                    for: attrs.labelFor ?: '',
                    required: attrs.required,
                    class: 'control-label' + (pageScope.formStyle == 'horizontal' ? ' ' + tb.width(sm: 4, md: 2) : '')
                ) {
                    attrs.label
                }
            }
            if (pageScope.formStyle == 'horizontal') {
                str += tb.col(width: [sm: 8, md: 10]) {
                    tb.row {
                        attrs.fieldDataList.collect { fieldData ->
                            tbForm.input(width: fieldData.width) {
                                g."${fieldData.type}"(tb.merge(current: fieldData.attrs, additional: [class: 'form-control']))
                            }
                        }.join('')
                    } +
                    attrs.fieldDataList.collect { fieldData ->
                        !fieldData.renderErrors ? '' : tbForm.fieldErrors(bean: attrs.bean, field: fieldData.field)
                    }.join('')
                }
            } else if (pageScope.formStyle == 'inline') {
                str += attrs.fieldDataList.collect { fieldData ->
                    g."${fieldData.type}"(tb.merge(current: fieldData.attrs, additional: [class: 'form-control']))
                }.join('')
            }
            str
        }
    }

    // solo usar con form-horizontal
    def formGroup = { attrs, body ->
        pageScope.isMultiple = true
        
        
        out << tb.elem(tag: 'div', class: 'form-group' + (pageScope.formStyle == 'inline' ? ' ' + tb.width(attrs.width) : '')) {
            def str = ''
            if (attrs.showLabel) {
                str += tbForm.label(
                    for: attrs.labelFor ?: '',
                    required: attrs.required,
                    class: 'control-label' + (pageScope.formStyle == 'horizontal' ? ' ' + tb.width(sm: 4, md: 2) : '')
                ) {
                    attrs.label
                }
            }
            if (pageScope.formStyle == 'horizontal') {
                str += tb.col(width: [sm: 8, md: 10]) {
                    tb.row {
                        attrs.fieldDataList.collect { fieldData ->
                            tbForm.input(width: fieldData.width) {
                                g."${fieldData.type}"(tb.merge(current: fieldData.attrs, additional: [class: 'form-control']))
                            }
                        }.join('')
                    } +
                    attrs.fieldDataList.collect { fieldData ->
                        !fieldData.renderErrors ? '' : tbForm.fieldErrors(bean: attrs.bean, field: fieldData.field)
                    }.join('')
                }
            } else if (pageScope.formStyle == 'inline') {
                str += attrs.fieldDataList.collect { fieldData ->
                    g."${fieldData.type}"(tb.merge(current: fieldData.attrs, additional: [class: 'form-control']))
                }.join('')
            }
            str
        }
        
        pageScope.isMultiple = false
    }

    def input = { attrs, body ->
        out << tb.col(width: attrs.width) {
            body()
        }
    }

    def fieldErrors = { attrs ->
        def errorList = attrs?.bean?.errors?.getFieldErrors(attrs?.field)
        if (errorList) {
            out << tb.elem(tag: 'ul', class: 'error list-unstyled') {
                errorList.collect { err ->
                    tb.elem(tag: 'li', class: 'text-danger') {
                        message(error: err)
                    }
                }.join('')
            }
        }
    }

}
