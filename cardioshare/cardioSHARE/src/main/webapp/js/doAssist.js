/* this shouldn't be necessary, but because we're building the form
 * dynamically in Ext, it is; TODO fix that...
 */
Ext.onReady(function() {
	$(function() {
	    $("#queryBox").sparqlassist({
            initNamespaces : "resources/LSRN.json",
            initPredicates : "resources/props.json",
 //           remoteNamespaces : "autocomplete?category=namespaces",
 //           remotePredicates : "autocomplete?category=predicates",
 //           remoteIndividuals : "autocomplete?category=individuals"
        });
	  });
});