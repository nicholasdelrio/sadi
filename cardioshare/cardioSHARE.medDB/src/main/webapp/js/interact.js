// TODO figure out how to make this more jQuery-like...
if (typeof(PHL) === 'undefined') {
    var PHL = {};
}
(function() {
    var query_open =
"PREFIX med:  <http://sadiframework.org/ontologies/personalized-medicine/prescription.owl#>\n" + 
"PREFIX sio:  <http://semanticscience.org/resource/>\n" + 
"PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" + 
"PREFIX lsrn: <http://purl.oclc.org/SADI/LSRN/>\n" + 
"PREFIX ddi:  <http://sadi-ontology.semanticscience.org/ddiv2.owl#>\n" + 
"SELECT *\n" + 
"WHERE {\n" + 
"  ?prescribedDrug sio:SIO_000062 ?interaction .  \n" + // 'is participant in'
"  ?interaction\n" + 
"    a ddi:DDI_00062; \n" +                 // 'directed drug drug interaction'
"    sio:SIO_000355 [ \n" +                 // 'realizes' (a role)
"      a ddi:DDI_00008; \n" +               // 'actor'
"      sio:SIO_000227 ?dangerousDrug \n" +  // 'is role of'
"    ];\n" + 
"    sio:SIO_000355 [ \n" +                 // 'realized' (a role)
"      a ddi:DDI_00010; \n" +               // 'target'
"      sio:SIO_000227 ?prescribedDrug \n" + // 'is role of'
"    ];\n" + 
"    sio:SIO_000253 ?publication; \n" +            // 'has source'
"    sio:SIO_000554 ?interactionDescriptor . \n" + // 'results in'
"  ?publication\n" + 
"    a sio:SIO_000087; \n" +          // 'publication'
"    sio:SIO_000008 [ \n" +           // 'has attribute'
"      a lsrn:PMID_Identifier;\n" + 
"      sio:SIO_000300 ?pubmedId \n" + // 'has value'
"    ] .\n" + 
"  ?interactionDescriptor rdfs:label ?interactionDescription .\n" + 
"  ?prescribedDrug\n" + 
"    a med:PrescribedDrug;\n" + 
"    rdfs:label ?prescribedDrugName;\n" + 
"    sio:SIO_000008 [ \n" +                   // 'has attribute'
"      a lsrn:DrugBank_Identifier;\n" + 
"      sio:SIO_000300 ?prescribedDrugId \n" + // 'has value'
"    ] .\n" + 
"  ?dangerousDrug\n" + 
"    rdfs:label ?dangerousDrugName;\n" + 
"    sio:SIO_000008 [ \n" +                   // 'has attribute'
"      a lsrn:DrugBank_Identifier;\n" + 
"      sio:SIO_000300 ?dangerousDrugId \n" +  // 'has value'
"    ] .\n",
        query_close = "}",
        get_query = function(ids) {
            var i, seen = {}, filter = '';
            // a large FILTER is overflowing Jetty's header somehow;
            // I don't think just grabbing all of the interactions
            // will slow us down too much, though...
//            for (i=0; i<ids.length; ++i) {
//            	if (seen[ids[i]]) {
//            		continue;
//            	} else {
//            		seen[ids[i]] = true; 
//            	}
//                if (filter.length > 0) {
//                    filter += ' || ';
//                }
//                filter += 
//'?prescribedDrugId = "' + ids[i] + '" || ?dangerousDrugId = "' + ids[i] + '"';
//            }
//            if (filter.length > 0) {
//                filter = "  FILTER( " + filter + " )\n";
//            }
            return query_open + filter + query_close;
        };
    PHL.convert_bindings = function(bindings) {
        var i, binding, key, hash = {}, interactions = [];

        // collapse references into list...
        for (i=0; i<bindings.length; ++i) {
            binding = bindings[i];
            key = binding.interaction.value;
            if (!hash[key]) {
                hash[key] = {
                     prescribedDrugURI : binding.prescribedDrug.value,
                    prescribedDrugName : binding.prescribedDrugName.value,
                      prescribedDrugID : binding.prescribedDrugId.value,
                        prescribedDrug : {
                                   uri : binding.prescribedDrug.value,
                                  name : binding.prescribedDrugName.value,
                                    id : binding.prescribedDrugId.value,
                        },
                      dangerousDrugURI : binding.dangerousDrug.value,
                     dangerousDrugName : binding.dangerousDrugName.value,
                       dangerousDrugID : binding.dangerousDrugId.value,
                         dangerousDrug : {
                                   uri : binding.dangerousDrug.value,
                                  name : binding.dangerousDrugName.value,
                                    id : binding.dangerousDrugId.value,
                        },
                           description : binding.interactionDescription.value,
                            references : []
                };
            }
            hash[key].references.push(
                "http://lsrn.org/PMID:" + binding.pubmedId.value);
        }
        for (key in hash) {
            interactions.push(hash[key]);
        }
        return interactions;
    };
    PHL.sparql = function(endpoint, query, callback) {
        $.ajax({
                url : endpoint,
            accepts : 'application/sparql-results+json',
           dataType : 'jsonp',
// accepts doesn't seem to be working, so...
//               data : { query : query },
               data : {
                        query : query,
                        accept : 'application/sparql-results+json'
                      },
            success : function(data, textStatus, jqXHR) {
                callback(PHL.convert_bindings(data.results.bindings));
            }
        });
    };
    PHL.find_interactions = function(drugs, callback) {
        var i, drug_ids = [];
        for (i=0; i<drugs.length; ++i) {
            if (typeof(drugs[i]) === 'string') {
                drug_ids.push(i);
            } else if (drugs[i].id) {
                drug_ids.push(drugs[i].id);
            }
        }
        PHL.sparql("${deploy.root}/sparql/medDB",
            get_query(drug_ids), callback);
    };
})();
