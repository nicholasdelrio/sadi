#!/usr/bin/perl -w

use strict;
# Use long names for builtin variables.
use English; 
# Include the script directory on the @INC path
use FindBin;
use lib "${FindBin::Bin}/";
# Contains helper routines for loading data, issuing queries, etc.
use VirtuosoHelper;
use IOHelper;
use Getopt::Long qw(HelpMessage);

#------------------------------------------------------------
# GLOBAL VARIABLES
#------------------------------------------------------------

my $base_url = "moby.ucalgary.ca/RESOURCES/MOBY-S";
my $services_file = "moby.services.rdf";
my $servicetypes_file = "moby.servicetypes.rdf";
my $objecttypes_file = "moby.objecttypes.rdf";
my $namespaces_file = "moby.namespaces.rdf";

#------------------------------------------------------------
# Parse command line options
#------------------------------------------------------------

use constant DEFAULT_VIRTUOSO_PORT => 1111;

my $port = DEFAULT_VIRTUOSO_PORT;
my $verbose = 0;

# Download the latest Moby RDF service/ontology files from
# Moby Central, even if there are already copies of those files
# in the current directory.
my $forceDownload = 0;
my $help;

my @commandLineOptions = (
    "forceDownload!" => \$forceDownload,
    "port=i" => \$port,
    "verbose!" => \$verbose,
    "help!" => \$help,
    );

if(!GetOptions(@commandLineOptions) || $help) {
    HelpMessage();
    exit;
}

IOHelper::setVerboseLevel($verbose);

#--------------------------------------------------
# Setup the registry
#--------------------------------------------------

&check_for_required_programs();
&download_moby_rdf_files($forceDownload);
&clear_registry($port);
&load_moby_rdf_files_into_registry($port);
&load_predicate_mappings($port);
&set_default_secondary_parameters_for_services($port);

print "DONE!\n";

#------------------------------------------------------------
# SUBROUTINES
#------------------------------------------------------------

sub download_moby_rdf_files
{
    my $forceDownload = shift(@_);

    my $allFilesInDir = 
	(-e $services_file &&
	 -e $servicetypes_file &&
	 -e $objecttypes_file &&
	 -e $namespaces_file);

    my $download = ($forceDownload || !$allFilesInDir);

    IOHelper::say "";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "DOWNLOADING SOURCE RDF FROM MOBY CENTRAL";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "";

    
    if(!$verbose) {
	if($download) {
	    print "Downloading RDF files from Moby Central...\n";
	}
    }
    
    if(!$download) {
	print "Using Moby RDF files in current directory, instead of " .
	    "downloading the latest versions.  To change this behaviour, use " .
	    "the -f switch.\n";
    }

    if($forceDownload || !$allFilesInDir) {
	download_file("$base_url/ServiceInstances", $services_file);
	download_file("$base_url/Services", $servicetypes_file);
	download_file("$base_url/Objects", $objecttypes_file);
	download_file("$base_url/Namespaces", $namespaces_file);
    }

}

sub clear_registry
{
    my $port = shift(@_);
    
    IOHelper::say "";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "CLEARING EXISTING RDF GRAPHS";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "";

    if(!$verbose) {
	print "Clearing existing registry data...\n";
    }

    VirtuosoHelper::clear_graph($port, $VirtuosoHelper::SERVICE_GRAPH);
    VirtuosoHelper::clear_graph($port, $VirtuosoHelper::SERVICETYPE_GRAPH);
    VirtuosoHelper::clear_graph($port, $VirtuosoHelper::OBJECTTYPE_GRAPH);
    VirtuosoHelper::clear_graph($port, $VirtuosoHelper::NAMESPACE_GRAPH);
    VirtuosoHelper::clear_graph($port, $VirtuosoHelper::PREDICATE_GRAPH);
}

sub load_moby_rdf_files_into_registry
{
    my $port = shift(@_);

    IOHelper::say "";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "LOADING DOWNLOADED RDF FILES INTO VIRTUOSO";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "";

    if(!$verbose) {
	print "Loading Moby RDF files into registry...\n";
    }

    VirtuosoHelper::load_rdfxml_file($port, $services_file, $VirtuosoHelper::SERVICE_GRAPH);
    VirtuosoHelper::load_rdfxml_file($port, $servicetypes_file, $VirtuosoHelper::SERVICETYPE_GRAPH);
    VirtuosoHelper::load_rdfxml_file($port, $objecttypes_file, $VirtuosoHelper::OBJECTTYPE_GRAPH);
    VirtuosoHelper::load_rdfxml_file($port, $namespaces_file, $VirtuosoHelper::NAMESPACE_GRAPH);
}

sub load_predicate_mappings
{
    my $port = shift(@_);

    IOHelper::say "";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "LOADING PREDICATE MAPPINGS";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "";

    if(!$verbose) {
	print "Loading Moby predicate annotations...\n";
    }

    my @mappingFiles  = <*.mappings>;

    IOHelper::setIndentLevel(1);

    foreach my $file (@mappingFiles) {
	IOHelper::say "Loading predicate mappings from '$file':\n";
	VirtuosoHelper::add_mappings_from_file($port, $file);
    }

    IOHelper::setIndentLevel(0);
}

sub set_default_secondary_parameters_for_services
{
    my $port = shift(@_);

    IOHelper::say "";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "SETTING DEFAULT SECONDARY PARAMETERS FOR SERVICES";
    IOHelper::say "--------------------------------------------------";
    IOHelper::say "";

    print "Setting default secondary parameters for services...\n";

    VirtuosoHelper::runUpdateQueryFromFile($port, "update.param.cutoff.annotatesProtein.sparql");
}

sub download_file
{
    my ($url, $file) = @_;
    IOHelper::say "Downloading $url..."; 
    my $silent = $verbose ? '' : '-s';
    qx|curl $silent $url > $file|;
    if(($CHILD_ERROR >> 8) > 0) {
	die "Sorry, unable to retrieve '$url' from the existing Moby registry!";
    }
}

sub check_for_required_programs
{
    check_for_prog('curl');
    my $isql_path = check_for_prog('isql');

    # Additionally, check that the user is using the right 'isql'.

    my $help_output = qx|$isql_path --help|;

    if($help_output =~ /unixODBC/) {
	die "'$isql_path' is not the Virtuoso isql binary.  Please" .
	    " note that this script requires a local installation of Virtuoso. " .
	    " Also note that once Virtuoso has been installed, you must " .
	    " ensure that the Virtuoso version of 'isql' comes before any other " .
	    " 'isql' in the path!";
    }
}

sub check_for_prog
{
    my $progname = shift @_;
    my $bin_path = qx|which $progname|;
    chomp($bin_path);
    if(($CHILD_ERROR >> 8) > 0) {
	die "Please ensure that the '$progname' program is installed and is on your path.";
    }
    return $bin_path;
}

__END__

=head1 NAME

setup_rdf_registry.pl - load a RDF version of the Moby registry into a *local* 
installation of Virtuoso

=head1 SYNOPSIS
    
  setup_rdf_registry [-p <Virtuoso port>] [--verbose] [--forceDownload]
    
=head1 DESCRIPTION

Setup up a RDF version of the BioMoby registry in a local Virtuoso
triple store.  

Please note that this script must be run from the same machine as
the Virtuoso triple store. It cannot be run remotely from another machine. 
Also, the Virtuoso 'isql' binary must come before the mySQL 'isql' on your PATH.

Please also note that this script requires the 'curl' commandline HTTP
client to be installed.

=over 4

=item -p <Virtuoso port>

The port that Virtuoso is running on.  This argument is optional and defaults
to 1111.

=item --forceDownload

Force downloading of fresh RDF files from Moby Central. When setting up the 
registry, the script must first download the source registry data from Moby central.
These files are then loaded into Virtuoso.  By default, the RDF files will
not be re-downloaded if there are already copies in the current directory
(from a previous execution of setup_rdf_registry.pl).
    
=back
