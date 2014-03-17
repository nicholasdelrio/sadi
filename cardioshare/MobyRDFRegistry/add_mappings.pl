#! /usr/bin/perl -w

use strict;
# Use long names for builtin variables.
use English; 
# Include the directory of the script on the @INC path
use FindBin;
use lib "$FindBin::Bin";
# Contains helper routines for loading data, issuing queries, etc.
use VirtuosoHelper;
use Getopt::Long qw(HelpMessage);

#------------------------------------------------------------
# Parse command line options
#------------------------------------------------------------

use constant DEFAULT_PORT => 1111;

my %optionValues = ();
$optionValues{'port'} = DEFAULT_PORT;

my @commandLineOptions = (
    "port=s" => \$optionValues{'port'},
    "file=s" => \$optionValues{'mappingFile'},
    "help!" => \$optionValues{'help'},
    );

if(!GetOptions(@commandLineOptions) || $optionValues{'help'}) {
    HelpMessage();
    exit;
}

#------------------------------------------------------------
# Insert the mappings into the triple store.
#------------------------------------------------------------

my $filehandle;

if(defined($optionValues{'mappingFile'})) {
    if(!open($filehandle, $optionValues{'mappingFile'})) {
	die "Couldn't open file $optionValues{'mappingFile'}"; 
    }
}

if(@ARGV == 0) {
    if(!defined($filehandle)) {
	$filehandle = *STDIN{IO};
    }
    VirtuosoHelper::add_mappings_from_filehandle($optionValues{'port'}, $filehandle);
}
else {
    if(@ARGV < 5) {
	HelpMessage();
	exit;
    }
    VirtuosoHelper::add_mapping($optionValues{'port'}, @ARGV);
}

__END__

=head1 NAME

add_mappings.pl - add predicate annotations to Moby services

=head1 SYNOPSIS

  # Specify a single mapping on the commandline
  add_mappings.pl [-p <Virtuoso port>] <predicate URI> <service name> <service authority> <input article name> <output article name> [<moby datatype>:<CONSTRUCT query filename>]

  # Read in a list of mappings from a file
  add_mappings.pl [-p <Virtuoso port>] -f <mappings filename>

  # Read mappings from STDIN
  add_mappings.pl [-p <Virtuoso port>]

=head1 DESCRIPTION

Add one or more predicate mappings to Moby services in the local registry. 

(Please note that this script must be run from the same machine on which 
the Virtuoso/Moby service registry is setup. It cannot be run remotely 
from another machine. Also, the Virtuoso 'isql' binary must come before 
the mySQL 'isql' on your PATH.)

Each predicate mapping serves describe the semantic relationship between an input and 
an output of a service. The annotations are used by the SHARE query engine for 
discovery of Moby services, when resolving a SPARQL query.

=over 4

=item -p <Virtuoso port>

The port number that the Virtuoso installation is running on.  This argument 
is optional, and defaults to 1111.

=item input article name / output article name 

are the names used to identify the input and output arguments to be connected. These are given in the 
Moby service description (use "Dashboard" to obtain these descriptions).

=item service authority 

identifies the party that hosts the service, by DNS domain name.  

=back

A list of mappings may be specified via standard input, in which case the script
should be given no arguments (except possibly the port number of the registry).
The format of each mapping line from STDIN is the same as for mappings stored in a file, and
as for specifying a single mapping on the command line. In addition, SPARQL "PREFIX" lines 
may be specified at the head of the file (prior to any mapping lines) in order
to make typing/editing predicate URIs easier.

=cut
