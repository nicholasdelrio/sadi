if (typeof(PHL) === 'undefined') {
    var PHL = {};
}
(function($) {
	PHL.erProxyURL = "${deploy.root}/er-proxy";
    PHL.get_entities = function(callback) {
        /* if we're not deployed to localhost, but the page we're looking at
         * is on localhost, we need to send the text (we are limited by URL
         * length in how much text we can send, so we can't just do this all
         * the time...)
         */
        if (("${deploy.root}".indexOf("localhost") === -1) &&
            (document.URL.indexOf("localhost") >= 0)) {
            $.get(PHL.erProxyURL, { inputText: extract_text($this) },
                  parse_RDF.bind(this, callback), 'jsonp');
        } else {
            $.get(PHL.erProxyURL, { inputURL: document.URL },
                  parse_RDF.bind(this, callback), 'jsonp');
        }
    };
    PHL.extract_text = function() {
        // TODO send only content, not navigational elements, etc.
        return $("body").text();
    };
    var parse_XML,
        parse_RDF = function(callback, outputRDF, textStatus, jqXHR) {
        // TODO find the appropriate test to do this in the opposite direction
        if (typeof outputRDF === 'string') {
            if (typeof parse_XML !== 'undefined') {
                outputRDF = parse_XML(outputRDF);
            } else {
                //throw new Error("No XML parser found");
                outputRDF = "";
            }
        }
        var rdf = $.rdf()
            .load(outputRDF, {})
            .prefix('rdfs', 'http://www.w3.org/2000/01/rdf-schema#')
            .prefix('foaf', 'http://xmlns.com/foaf/0.1/')
            .prefix('sio', 'http://semanticscience.org/resource/')
            .prefix('lsrn', 'http://purl.oclc.org/SADI/LSRN/')
            .prefix('med', 'http://sadiframework.org/ontologies/personalized-medicine/prescription.owl#')
            .prefix('ddi', 'http://sadi-ontology.semanticscience.org/ddiv2.owl#');
        var results = rdf
            .where("?page foaf:topic ?drug")
            .where("?drug sio:SIO_000008 ?nameNode") // 'has attribute'
            .where("?nameNode a sio:SIO_000116")     // 'name'
            .where("?nameNode sio:SIO_000300 ?name") // 'has value'
            .where("?drug sio:SIO_000008 ?idNode")   // 'has attribute'
            .where("?idNode a sio:SIO_000728")       // 'chemical identifier'
            .where("?idNode sio:SIO_000300 ?id")     // 'has value'
            .optional("?drug rdfs:label ?label");
        var entities = results.map(function() {
           return {
               match : this.name.value,
               label : this.label.value, 
                 url : this.drug.toString().slice(1,-1), // trim <>
                  id : this.id.value
           }; 
        });
        callback(entities);
    };
    if (typeof $.parseXML !== 'undefined') {
        parse_XML = $.parseXML;
    } else if (typeof window.DOMParser != "undefined") {
        parse_XML = function(xmlStr) {
            return (new window.DOMParser()).parseFromString(xmlStr, "text/xml");
        };
    } else if (typeof window.ActiveXObject != "undefined" &&
               new window.ActiveXObject("Microsoft.XMLDOM")) {
        parseXml = function(xmlStr) {
            var xmlDoc = new window.ActiveXObject("Microsoft.XMLDOM");
            xmlDoc.async = "false";
            xmlDoc.loadXML(xmlStr);
            return xmlDoc;
        };
    }
})(jQuery);
