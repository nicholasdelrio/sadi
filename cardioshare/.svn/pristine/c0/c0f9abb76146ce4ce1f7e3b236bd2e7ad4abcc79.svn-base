package VirtuosoHelper;

use strict;
# Use long names for builtin variables.
use English; 
# Get absolute path of a given filename.
use Cwd 'abs_path';

use LWP::UserAgent;
use URI;

use XML::LibXML;
use SPARQLHelper;
use IOHelper;

#--------------------------------------------------
# Graph URIs.
#--------------------------------------------------

#our $SERVICE_GRAPH = "http://mobyreg/services/";
#our $SERVICETYPE_GRAPH = "http://mobyreg/servicetypes/";
#our $OBJECTTYPE_GRAPH = "http://mobyreg/objecttypes/";
#our $NAMESPACE_GRAPH = "http://mobyreg/namespaces/";
# Stores CONSTRUCT queries for predicate/datatype pairs.
#our $PREDICATE_GRAPH = "http://mobyreg/servicepredicates/";

our $SERVICE_GRAPH = "http://sadiframework.org/mobywrappers/";
our $SERVICETYPE_GRAPH = "http://sadiframework.org/mobywrappers/";
our $OBJECTTYPE_GRAPH = "http://sadiframework.org/mobywrappers/";
our $NAMESPACE_GRAPH = "http://sadiframework.org/mobywrappers/";
our $PREDICATE_GRAPH = "http://sadiframework.org/mobywrappers/";

#--------------------------------------------------
# SPARQL PREFIXes
#--------------------------------------------------

our $RDF_PREFIX = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
our $OWL_PREFIX = "http://www.w3.org/2002/07/owl#";
our $MOBY_OBJECT_PREFIX = "http://biomoby.org/RESOURCES/MOBY-S/Objects/";
# Used in properties  that are part of service predicate definitions.
our  $MOBY_PREFIX = "http://moby/";

#--------------------------------------------------
# This method is necessary because the Virtuoso 
# 'isql' always returns zero, even when the last
# executed SQL/SPARQL statement failed.
#
# Instead output "SQLSTATE" to the console, and
# extract it from the output. 
#
# So... this method extracts the SQLSTATE value from
# a multi-line block of Virtuoso output.  (If there is
# one, it will be on its own line.) If the last 
# SQL/SPARQL statement completed successfully, the 
# value of SQLSTATE is "OK", otherwise it is some number.
#
# If there are multiple SQLSTATE values in the 
# output block, returns the first occurence. If there
# are no SQLSTATE values, returns an empty string.
#--------------------------------------------------

sub extract_sqlstate
{
    my $virt_output = shift @_;
    my $sqlstate = '';

    if($virt_output =~ /SQLSTATE\s+is\s+(\S+)/) {
	$sqlstate = $1;
    }
    else {
	if($virt_output =~ /Connect failed to/) {
	    die "Unable to connect to Virtuoso.  Please ensure that " .
		"Virtuoso is running and that you have specified the correct port number " .
		"(the default is 1111).";
	}
	die "Unable able to determine SQLSTATE from isql output. Output: $virt_output";
    }
    return $sqlstate; 
}

#--------------------------------------------------
# Clear a named graph from the Virtuoso triple
# store.
#
# $port - the port that the Virtuoso server is
#         running on.
#
# $graph - the URI of the RDF graph to delete.
#          (Each triple in Virtuoso has
#           an associated graph URI, that's 
#           why they call a 'quad store')
#--------------------------------------------------

sub clear_graph
{
    my ($port, $graph) = @_;
    IOHelper::say "Clearing graph <$graph>... ";
    isql($port, "SPARQL CLEAR GRAPH <$graph>");
}

#--------------------------------------------------
# Load an RDF/XML into the Virtuoso triple store.
#
# $port - the port that the Virtuoso server is
#         running on.
#
#--------------------------------------------------

sub load_rdfxml_file
{
    my ($port, $rdf_file, $target_graph) = @_;
    IOHelper::say "Loading rdf file '$rdf_file' into graph <$target_graph>... ";
    # Expand the given filename to an absolute path.
    my $full_path = abs_path($rdf_file);
    my $command = "DB.DBA.RDF_LOAD_RDFXML_MT(file_to_string_output('$full_path'), '', '$target_graph')";
    isql($port, $command);
}

sub isql
{
    my ($port, $command) = @_;
    my $output = qx(/opt/virtuoso/bin/isql $port dba Rx7y2N exec="$command" exec="SHOW SQLSTATE" 2>&1);
    print $output;
    my $sqlstate = extract_sqlstate($output);
}

#--------------------------------------------------
# Runs the SPARUL query stored in the specified
# text file.
#--------------------------------------------------

sub runUpdateQueryFromFile
{
    my ($port, $query_file) = @_;
    my $query = IOHelper::read_file_into_string($query_file);
    updateQuery($port, $query);
}

#--------------------------------------------------
# Runs a SPARQL query given as a string.
#--------------------------------------------------

sub updateQuery
{
    my ($port, $query) = @_;
    isql($port, "SPARQL $query");
}

#----------------------------------------
# Drop indices created for full-text 
# searching.
#----------------------------------------

sub drop_fulltext_index
{
    my ($port, $graph) = @_;
    isql("DB.DBA.RDF_OBJ_FT_RULE_DEL('$graph', null, 'mobyreg')");
}

#----------------------------------------
# Build index for full-text searching.
#----------------------------------------

sub build_fulltext_index
{
    my ($port, $graph) = @_;
    isql("DB.DBA.RDF_OBJ_FT_RULE_ADD('$graph', null, 'mobyreg')");
}

#------------------------------------------------------------
# Add a predicate mapping to the registry.
#
# A predicate mapping connects one service input to one
# service output.
#------------------------------------------------------------ 

sub add_mapping
{
    my ($port, 
	$predicate,
	$serviceName,
	$serviceAuth,
	$inArticleName,
	$outArticleName,
	@constructQueries) = @_;
         
    IOHelper::say "Adding mapping for predicate '" . $predicate . "':";
    IOHelper::say "   Service: " . $serviceName;
    IOHelper::say "   Authority: " . $serviceAuth;
    IOHelper::say "   Input article name: " . $inArticleName;
    IOHelper::say "   Output article name: " . $outArticleName;
    
    foreach my $constructQuery (@constructQueries) {
        my ($mobyDatatype, $constructQueryFile) = split(/:/, $constructQuery);
	IOHelper::say "   Datatype/CONSTRUCT query: " . $mobyDatatype . 
	    " / " . $constructQueryFile;
    }

    add_basic_mapping($port, 
		      $predicate, 
		      $serviceName, 
		      $serviceAuth, 
		      $inArticleName, 
		      $outArticleName);

    foreach my $constructQuery (@constructQueries) {
        my ($mobyDatatype, $constructQueryFile) = split(/:/, $constructQuery);
	add_construct_query($port, $predicate, $MOBY_OBJECT_PREFIX . $mobyDatatype, $constructQueryFile);
    }
    
}

sub add_basic_mapping
{
    my ($port, 
	$predicate, 
	$serviceName, 
	$serviceAuth,
	$inArticleName,
	$outArticleName) = @_;
    
    my $query = 
	"PREFIX svc: <http://www.mygrid.org.uk/mygrid-moby-service#>\n" .
	"PREFIX dc: <http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl#>\n" .
	"\n" .
	"INSERT INTO GRAPH <$SERVICE_GRAPH> {\n" .
	"   ?in <$predicate> ?out .\n" .
	"}\n" .
	"WHERE {\n" .
	"   ?svc svc:hasServiceNameText '$serviceName' .\n" .
	"   ?svc svc:providedBy ?provider .\n" .
	"   ?provider dc:publisher '$serviceAuth' .\n" .
	"   ?svc svc:hasOperation ?op .\n" .
	"   ?op svc:inputParameter ?in .\n" .
	"   ?in svc:hasParameterNameText '$inArticleName' .\n" .
	"   ?op svc:outputParameter ?out .\n" .
	"   ?out svc:hasParameterNameText '$outArticleName' .\n" .
	"}";

    updateQuery($port, $query);
}

sub add_construct_query
{
    my ($port, $predicate, $mobyDatatype, $constructQueryFile) = @_;

    # If there is already a CONSTRUCT query for this predicate/datatype pair, 
    # we need to remove it first.
    remove_construct_query($port, $predicate, $mobyDatatype);

    my $constructQuery = IOHelper::read_file_into_string($constructQueryFile);
    $constructQuery = escape_construct_query_for_registry($constructQuery);
    
    my $query =
	"PREFIX rdf: <$RDF_PREFIX>\n" .
	"PREFIX owl: <$OWL_PREFIX>\n" .
	"PREFIX moby: <$MOBY_PREFIX>\n" .
	"\n" .
	"INSERT INTO GRAPH <$PREDICATE_GRAPH> {\n" .
	"    <$predicate> moby:hasOutputType _:a0 .\n" .
	"    _:a0 rdf:type <$mobyDatatype> .\n" .
	"    _:a0 moby:hasConstructQuery '$constructQuery' .\n" .
	"}";
    
    updateQuery($port, $query);
}


sub remove_construct_query
{
    my ($port, $predicate, $mobyDatatype) = @_;
    
    my $datatypeInQuery;
    if(defined($mobyDatatype)) {
	$datatypeInQuery = "<$mobyDatatype>";
    }
    else {
	$datatypeInQuery = "?datatype";
    }
    
    my $query =
	"PREFIX rdf: <$RDF_PREFIX>\n" .
	"PREFIX moby: <$MOBY_PREFIX>\n" .
	"DELETE FROM GRAPH <$PREDICATE_GRAPH> {\n" .
	"    <$predicate> moby:hasOutputType ?blank .\n" .
	"    ?blank rdf:type $datatypeInQuery .\n" .
	"    ?blank moby:hasConstructQuery ?constructQuery .\n" .
	"}\n" .
	"WHERE {\n" .
	"    <$predicate> moby:hasOutputType ?blank .\n" .
	"    ?blank rdf:type $datatypeInQuery .\n" .
	"    ?blank moby:hasConstructQuery ?constructQuery .\n" .
	"}";

    updateQuery($port, $query);
}

sub escape_construct_query_for_registry
{
    my $query = shift(@_);

    # Strings aren't allowed to span multiple lines in a SPARQL query, 
    # so remove newlines.
    $query =~ s/\n/ /g;
    # Escape quotes.
    $query =~ s/"/\\"/g;
    $query =~ s/'/\\'/g;

    # When I run 'isql' through bash, it converts %u% to '='.  I can't 
    # figure out why it's doing this.  But changing '%u%' to '%%u%%' solves
    # the problem.
    $query =~ s/%/%%/g;

    return $query;
}

sub add_mappings_from_file
{
    my ($port, $filename) = @_;
    my $filehandle;

    if(!open($filehandle, "<", $filename)) {
	die "Unable to open file $filename";
    }
    
    add_mappings_from_filehandle($port, $filehandle);
    close($filehandle);
}

sub add_mappings_from_filehandle
{
    my ($port, $filehandle) = @_;
    my $mappingsText = IOHelper::read_filehandle_into_string($filehandle);
    my $prefixExpandedText = SPARQLHelper::get_prefix_expanded_text($mappingsText);
    my @mappings = split(/\n/, $prefixExpandedText);
    
    foreach my $mapping (@mappings) {
        my @mappingAsArray = split(/\s+/, $mapping);
	if((@mappingAsArray < 5) || (@mappingAsArray > 6)) {
	    warn "Skipped mapping, incorrect number of fields: $mapping";
	    next;
	}
	add_mapping($port, @mappingAsArray);
    }
}
    
#------------------------------------------------------------
# Remove one or more predicate mappings from the registry.
#
# A predicate mapping connects one service input to one
# service output.
#------------------------------------------------------------ 

sub remove_mappings
{
    my ($port,
	$endpoint,
	$predicate,
	$serviceName,
	$serviceAuth) = @_;
    
    my $criteriaIsSet = 0;
    my $criteriaStr = '';

    if(defined($predicate)) {
	$criteriaStr .= "   Predicate: $predicate\n";
	$criteriaIsSet = 1;
    }

    if(defined($serviceName)) {
	$criteriaStr .= "   Service name: $serviceName\n";
	$criteriaIsSet = 1;
    }

    if(defined($serviceAuth)) {
	$criteriaStr .= "   Service authority: $serviceAuth\n";
	$criteriaIsSet = 1;
    }
    
    if(!$criteriaIsSet) {
	IOHelper::say "Removing all mappings";
    }
    else {
	IOHelper::say "Removing mappings that match (all of) the following criteria:";
	IOHelper::say $criteriaStr;
    }
    
    remove_basic_mappings($port, $predicate, $serviceName, $serviceAuth);
    remove_orphan_construct_queries($port, $endpoint);
}

sub remove_basic_mappings
{
    my ($port, 
	$predicate,
	$serviceName,
	$serviceAuth) = @_;

    my $predicateInQuery;

    if(defined($predicate)) {
	$predicateInQuery = "<$predicate>";
    }
    else {
	$predicateInQuery = "?predicate";
    }
    
    my $query =
	"PREFIX svc: <http://www.mygrid.org.uk/mygrid-moby-service#>\n" .
	"PREFIX dc: <http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl#>\n" .
	"DELETE FROM GRAPH <$SERVICE_GRAPH> {\n" .
	"   ?in $predicateInQuery ?out .\n" .
	"}\n" .
	"WHERE {\n" .
	"   ?svc svc:hasOperation ?op .\n" .
	"   ?op svc:inputParameter ?in .\n" .
	"   ?op svc:outputParameter ?out .\n" .
	"   ?in $predicateInQuery ?out .\n";

    if(defined($serviceName)) {
	$query .= "   ?svc svc:hasServiceNameText '$serviceName' .\n";
    }

    if(defined($serviceAuth)) {
	$query .= 
	    "   ?svc svc:providedBy ?provider .\n" .
	    "   ?provider dc:publisher '$serviceAuth' .\n";
    }

    $query .= "}";
    
    updateQuery($port, $query);
}

sub remove_orphan_construct_queries
{
    my ($port, $endpoint) = @_;

    my @currentMappings = get_all_mappings($endpoint);
    my %predicateIsMapped = ();

    foreach my $mappingRef (@currentMappings) {
	my %mapping = %$mappingRef;
	$predicateIsMapped{ $mapping{'predicate'} } = 1;
    }

    my $query = 
	"PREFIX rdf: <$RDF_PREFIX>\n" .
	"PREFIX moby: <$MOBY_PREFIX>\n" .
	"SELECT DISTINCT ?predicate \n" .
	"FROM <$PREDICATE_GRAPH>\n" .
	"WHERE {\n" .
	"   ?predicate moby:hasOutputType ?blank .\n" .
	"}";

    my @predicatesWithConstructQueries = SPARQLHelper::selectQuery($endpoint, $query);

    foreach my $resultRow (@predicatesWithConstructQueries) {

	my $predicate = $resultRow->{'predicate'};
	if(!$predicateIsMapped{$predicate}) {
	    remove_construct_query($port, $predicate);
	}
    }
}

sub get_all_mappings 
{
    my $endpoint = shift(@_);

    my $query = 

	"PREFIX svc: <http://www.mygrid.org.uk/mygrid-moby-service#>\n" .
	"PREFIX dc: <http://protege.stanford.edu/plugins/owl/dc/protege-dc.owl#>\n" .
	"\n" .
	"SELECT ?serviceName ?inputName ?predicate ?outputName \n" .
	"FROM <$SERVICE_GRAPH>\n" .
	"WHERE {\n" .
	"   ?svc svc:hasServiceNameText ?serviceName .\n" .
	"   ?svc svc:hasOperation ?op .\n" .
	"   ?op svc:inputParameter ?in .\n" .
	"   ?in svc:hasParameterNameText ?inputName .\n" .
	"   ?op svc:outputParameter ?out .\n" .
	"   ?out svc:hasParameterNameText ?outputName .\n" .
	"   ?in ?predicate ?out .\n" .
	"}";
    
    return SPARQLHelper::selectQuery($endpoint, $query);
}

sub get_construct_queries
{
    my ($endpoint, $predicate) = @_;

    my $query =
	"PREFIX moby: <http://moby/>\n" .
	"PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" .
	"\n" .
	"SELECT ?mobyDatatype ?constructQuery\n" .
	"FROM <$PREDICATE_GRAPH>\n" .
	"WHERE {\n" .
	"   <$predicate> moby:hasOutputType ?blankNode .\n" .
	"   ?blankNode rdf:type ?mobyDatatype .\n" .
	"   ?blankNode moby:hasConstructQuery ?constructQuery .\n" .
	"}";

    return SPARQLHelper::selectQuery($endpoint, $query);
}


#--------------------------------------------------
# NOTE: This method works, but is not currently being
# used.
#
# Delete a subgraph of a named graph.  The method
# deletes edges that are connected to a
# given root URI, up to a specified depth.
# 
# A depth must be specified because it is not
# possible to delete a graph of arbitrary depth
# with a SPARUL query.
#
# $rootURI -     the root node of the subgraph to delete.
# $prefixLines - any prefix lines that should be
#                included at the top of the query.
#                (For example, to expand the root URI).
# $deleteDepth - delete all triples up to a distance
#                of this many edges from the root URI
# $graphURI -    the named graph to delete the 
#                triples from.
#--------------------------------------------------

sub delete_subgraph
{
    my ($port, $rootURI, $pPrefixList, $deleteDepth, $graphURI) = @_;

    $rootURI = format_uri($rootURI);

    for(my $i = $deleteDepth; $i >= 1; $i--)
    {
	
	my $deleteQuery;

	if(defined($pPrefixList))
	{
	    $deleteQuery .= join("\n", @{$pPrefixList}) . "\n";
	}
	
	$deleteQuery .= "DELETE FROM GRAPH <" . $graphURI . ">\n{\n";
	
	if($i == 1)
	{
	    
	    $deleteQuery .= "\t$rootURI ?p1 ?o1 .\n";
	}
	else
	{
	    $deleteQuery .= "\t?o" . ($i - 1) . " ?p" . $i . " ?o" . $i . " .\n";
	}
	
	$deleteQuery .= "}\nWHERE\n{\n";
	
	for(my $j = 1; $j <= $i; $j++)
	{
	    if($j == 1)
	    {
		$deleteQuery .= "\t$rootURI ?p1 ?o1 .\n";
	    }
	    else
	    {
		$deleteQuery .= "\t?o" . ($j - 1) . " ?p" . $j . " ?o" . $j . " .\n";
	    }
	}
	
	$deleteQuery .= "\n}";
	
	updateQuery($port, $deleteQuery);
    }
    
}

1;  # packages must return true at the end of the file
