#! /usr/bin/perl -w

use strict;

# Include the parent directory of this script on the @INC path
use FindBin;
use lib "$FindBin::Bin/..";

use IO::String;
use VirtuosoHelper;
use Getopt::Long qw(HelpMessage);
use Capture::Tiny qw(capture_merged tee_merged);

#------------------------------------------------------------
# Global variables
#------------------------------------------------------------

my $scriptDir = "${FindBin::Bin}/../";

#------------------------------------------------------------
# Parse command line options
#------------------------------------------------------------

use constant DEFAULT_VIRTUOSO_PORT => 1111;
use constant DEFAULT_VIRTUOSO_ENDPOINT => 'http://localhost:8890/sparql';

my @testCases;
my $generateOutputFiles;
my $port = DEFAULT_VIRTUOSO_PORT;
my $endpoint = DEFAULT_VIRTUOSO_ENDPOINT;

# Verbose off: Just display success/failure of each test, and any warnings.
# Verbose on:  Also show the actual output vs. expected output for each test case
my $verbose = 0;

my @commandLineOptions = (
    "port=i" => \$port,
    "endpoint=s" => \$endpoint,
    "test=s" => \@testCases,
    "output!" => \$generateOutputFiles,
    "verbose!" => \$verbose,
    );

if(!GetOptions(@commandLineOptions)) {
    HelpMessage();
    exit;
}

#------------------------------------------------------------
# Test cases
#------------------------------------------------------------

sub set_up {
    $VirtuosoHelper::SERVICE_GRAPH = "http://mobyreg/services/test/";
    $VirtuosoHelper::SERVICETYPE_GRAPH = "http://mobyreg/services/test";
    $VirtuosoHelper::OBJECTTYPE_GRAPH = "http://mobyreg/objecttypes/test";
    $VirtuosoHelper::NAMESPACE_GRAPH = "http://mobyreg/namespaces/test";
    $VirtuosoHelper::PREDICATE_GRAPH = "http://mobyreg/servicepredicates/test";
    print "Initializing test version of the registry...\n";
    system("${scriptDir}setup_rdf_registry.pl 2>&1");

}

sub tear_down {
    my $oldVerboseLevel = IOHelper::setVerboseLevel(0);
    print "Clearing temporary named graphs used for testing...";
    &VirtuosoHelper::clear_graph($port, $VirtuosoHelper::SERVICE_GRAPH);
    &VirtuosoHelper::clear_graph($port, $VirtuosoHelper::SERVICETYPE_GRAPH);
    &VirtuosoHelper::clear_graph($port, $VirtuosoHelper::OBJECTTYPE_GRAPH);
    &VirtuosoHelper::clear_graph($port, $VirtuosoHelper::NAMESPACE_GRAPH);
    &VirtuosoHelper::clear_graph($port, $VirtuosoHelper::PREDICATE_GRAPH);
    print " DONE\n";
    IOHelper::setVerboseLevel($oldVerboseLevel);
}

sub test_add_mappings
{
    VirtuosoHelper::add_mappings_from_file($port, "${FindBin::Bin}/test_add_mappings_from_file.input");
}

sub test_add_mappings_with_construct_queries
{
    VirtuosoHelper::add_mappings_from_file($port, "${FindBin::Bin}/test_add_mappings_from_file_with_construct_queries.input");
}

sub test_remove_mappings
{
    # Add a mapping
    VirtuosoHelper::add_mappings_from_file($port, "${FindBin::Bin}/test_add_mappings_from_file_with_construct_queries.input");
    # Then remove it
    VirtuosoHelper::remove_mappings($port, 
				  $endpoint, 
				  'http://es-01.chibi.ubc.ca/~benv/predicates.owl#isPaperAboutProtein',
				  'getSymbolInfo',
				  'cnio.es');
    
}

sub test_remove_mappings_by_predicate
{
    # Add a mapping
    VirtuosoHelper::add_mappings_from_file($port, "${FindBin::Bin}/test_add_mappings_from_file_with_construct_queries.input");
    # Then remove it
    VirtuosoHelper::remove_mappings($port, 
				  $endpoint, 
				  'http://es-01.chibi.ubc.ca/~benv/predicates.owl#isPaperAboutProtein');
}

sub test_remove_mappings_by_service_name
{
    # Add a mapping
    VirtuosoHelper::add_mappings_from_file($port, "${FindBin::Bin}/test_add_mappings_from_file_with_construct_queries.input");
    # Then remove it
    VirtuosoHelper::remove_mappings($port, 
				  $endpoint, 
				  undef,
				  'getSymbolInfo');
}

sub test_remove_mappings_by_service_auth
{
    # Add a mapping
    VirtuosoHelper::add_mappings_from_file($port, "${FindBin::Bin}/test_add_mappings_from_file_with_construct_queries.input");
    # Then remove it
    VirtuosoHelper::remove_mappings($port, 
				    $endpoint, 
				    undef,
				    undef,
				    'cnio.es');
}

sub test_remove_mappings_all
{
    VirtuosoHelper::remove_mappings($port, $endpoint);
}

# sub test_script_setup_rdf_registry
# {
#     system("${scriptDir}setup_rdf_registry.pl 2>&1");
# }

sub test_script_add_mappings
{
    system("${scriptDir}add_mappings.pl http://es-01.chibi.ubc.ca/~benv/predicates.owl#belongsToOrganism getSymbolInfo cnio.es search result iHOPsymbol:belongsToOrganism.iHOPsymbol.construct");
}

sub test_script_add_mappings_from_file
{
    system("${scriptDir}add_mappings.pl -f test.mappings 2>&1");
}

sub test_script_add_mappings_from_standard_input
{
    system("cat test.mappings 2>&1 | ${scriptDir}add_mappings.pl");
}

sub test_script_remove_mappings_by_predicate
{
    system("${scriptDir}add_mappings.pl http://es-01.chibi.ubc.ca/~benv/predicates.owl#belongsToOrganism getSymbolInfo cnio.es search result iHOPsymbol:belongsToOrganism.iHOPsymbol.construct");
    system("${scriptDir}remove_mappings.pl -u http://es-01.chibi.ubc.ca/~benv/predicates.owl#belongsToOrganism");
}

sub test_script_remove_mappings_by_service_name
{
    system("${scriptDir}add_mappings.pl http://es-01.chibi.ubc.ca/~benv/predicates.owl#belongsToOrganism getSymbolInfo cnio.es search result iHOPsymbol:belongsToOrganism.iHOPsymbol.construct");
    system("${scriptDir}remove_mappings.pl -s getSymbolInfo");
}

sub test_script_remove_mappings_by_service_auth
{
    system("${scriptDir}add_mappings.pl http://es-01.chibi.ubc.ca/~benv/predicates.owl#belongsToOrganism getSymbolInfo cnio.es search result iHOPsymbol:belongsToOrganism.iHOPsymbol.construct");
    system("${scriptDir}remove_mappings.pl -a cnio.es");
}

sub test_script_list_mappings
{
    # Note the output of the list_mappings.pl script is sensitive to the 
    # order in which mappings have been previously added/removed.  For 
    # this reason, we must setup the registry from scratch to get 
    # consistent output from the script.
    system("${scriptDir}setup_rdf_registry.pl 2>&1");
    system("${scriptDir}list_mappings.pl -c 2>&1");
}

#------------------------------------------------------------
# Run the tests
#------------------------------------------------------------

# If no specific test cases have been specified, run everything
# starting with "test_"

if(@testCases <= 0) {

    foreach my $symbol (keys %main::) {
	if($symbol =~ /^test_/ && defined(&{$symbol})) {
	    push(@testCases, $symbol);
	}
    }
    
}
else {

    # If test cases were specified on the commandline, check that 
    # the corresponding methods actually exist.

    my @filteredTestCases = ();

    foreach my $test (@testCases) {
        if(!defined(&{$test})) {
	    warn "Warning: Skipping test case '$test' (method does not exist)\n";
	}
	else {
	    push(@filteredTestCases, $test);
	}
    }

    @testCases = @filteredTestCases;
}

my @tests = split(/,/,join(',', @testCases));

&set_up();

foreach my $test (@tests) {

    my $outputFilename = $test . '.output';

    print "-" x 40 . "\n";
    print "TEST: $test\n";

    if($generateOutputFiles){
	&runTestCaseAndSaveOutputToFile($test, \&{$test}, $outputFilename);
    }
    else {
	if(-e $outputFilename) {
	    &runTestCaseAgainstExpectedOutput($test, \&{$test}, $outputFilename);
	}
	else {
	    &runTestCase($test, \&{$test});
	}
    }
}

&tear_down();

#------------------------------------------------------------
# Helper routines
#------------------------------------------------------------

sub runTestCaseAndSaveOutputToFile
{
    my ($testSubName, $testSubRef, $outputFilename) = @_;
    
    my $output;

    if($verbose) {
	print "TEST OUTPUT:\n";
	$output = tee_merged { &{$testSubRef}(); };
    }
    else {
	$output = capture_merged { &{$testSubRef}(); };
    }

    my $outputFilehandle;

    if(!open($outputFilehandle, ">$outputFilename")) {
	die "Unable to test output file for writing: $outputFilename";
    }

    print $outputFilehandle $output;
    close($outputFilehandle);

    print "TEST OUTPUT STORED TO: $outputFilename\n";
}

sub runTestCaseAgainstExpectedOutput
{
    my ($testSubName, $testSubRef, $expectedOutputFile) = @_;

    my $expectedOutput = IOHelper::read_file_into_string($expectedOutputFile);
    
    my $actualOutput;

    if($verbose) {
	print "EXPECTED OUTPUT: \n$expectedOutput\n";
 	print "ACTUAL OUTPUT: \n";
	$actualOutput = tee_merged { &{$testSubRef}(); };
	print "\n";
    }
    else {
	$actualOutput = capture_merged { &{$testSubRef}(); };
    }

    print "TEST RESULT FOR ";

    if($actualOutput eq $expectedOutput) {
	print "$testSubName: PASS\n";
    }
    else {
	print "$testSubName: FAIL\n";
    }
}

sub runTestCase
{
    my ($testSubName, $testSubRef) = @_;

    print "WARNING: There is no file containing expected output for the test. ";
    print "Displaying test result on console instead.\n";
    print "TEST OUTPUT: \n";

    # Run the test
    &{$testSubRef}();
	
}

