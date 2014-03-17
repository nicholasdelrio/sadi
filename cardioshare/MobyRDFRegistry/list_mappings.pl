#! /usr/bin/perl -w
use strict;

#------------------------------------------------------------
# MODULES
#------------------------------------------------------------

# Use long names for builtin variables.
use English; 
# Include the directory of the script on the @INC path
use FindBin;
use lib "$FindBin::Bin";
# Contains helper routines for loading data, issuing queries, etc.
use VirtuosoHelper;
use SPARQLHelper;
use Getopt::Long qw(HelpMessage);

#------------------------------------------------------------
# CONSTANTS / COMMANDLINE ARGS
#------------------------------------------------------------

my $DEFAULT_REGISTRY_ENDPOINT = "http://dev.biordf.net/sparql";

my $endpoint = $DEFAULT_REGISTRY_ENDPOINT;
my $constructQueriesOn;
my $help;

my $commandLineArgs = {
    "construct!" => \$constructQueriesOn,
    "endpoint=s" => \$endpoint,
    "help!" => \$help,
    };

if(!GetOptions(%$commandLineArgs) || $help) {
    &HelpMessage();
    exit;
}

#------------------------------------------------------------
# LIST THE MAPPINGS
#------------------------------------------------------------
     
my @mappings = VirtuosoHelper::get_all_mappings($endpoint);

foreach my $mapping (@mappings) {

    my %mappingHash = %$mapping;

    print join(' ', 
	       $mappingHash{'predicate'}, 
	       $mappingHash{'serviceName'},     
	       $mappingHash{'inputName'},
	       $mappingHash{'outputName'});
    print "\n";
    
    my @constructQueries = VirtuosoHelper::get_construct_queries($endpoint, $mappingHash{'predicate'});
    
    if ($constructQueriesOn) {
        
	foreach my $constructQuery (@constructQueries) {
	    
	    my %constructQueryHash = %$constructQuery;

	    # Remove the leading part of the URL for the Moby datatype.
	    my $datatype = $constructQueryHash{'mobyDatatype'};
	    $datatype =~ s|^.+/||;

	    print "\tConstruct query for moby datatype " . $datatype . ": \n";
	    my $query = $constructQueryHash{'constructQuery'};
	    print "\n" . SPARQLHelper::format_query_for_display($query, 2) . "\n";
	    
	    
	}

    }
    
}

__END__

=head1 NAME

list_mappings.pl - list all predicate mappings for Moby services

=head1 SYNOPSIS

 list_mappings.pl [--endpoint <SPARQL endpoint>] [--construct]

=head1 DESCRIPTION

List all predicate mappings which are contained in a (SHARE) Moby RDF registry.

=over 4

=item --endpoint <SPARQL endpoint>

The endpoint to get the mappings from.  Defaults to http://dev.biordf.net/sparql.

=item --construct

Also list the CONSTRUCT queries that are associated with each mapping (if any). 
The use of SPARQL CONSTRUCT queries is a temporary hack which allows RDF triples
to be generated from Moby XML output objects.  Inside the SHARE query engine, 
each Moby XML output object is converted to RDF via XSLT.  Then, a CONSTRUCT
query is run on the RDF to generate triples with the desired predicate.  
A unique CONSTRUCT query may stored for any (predicate, Moby datatype) pair.




