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
# COMMANDLINE ARGS
#------------------------------------------------------------

use constant DEFAULT_PORT => 1111;
use constant DEFAULT_ENDPOINT => 'http://localhost:8890/sparql';

my %optionValues = ();
$optionValues{'port'} = DEFAULT_PORT;
$optionValues{'endpoint'} = DEFAULT_ENDPOINT;

my @commandLineOptions = (
    "port=s" => \$optionValues{'port'},
    "R!" => \$optionValues{'removeAll'},
    "uri=s" => \$optionValues{'predicate'},
    "servicename=s" => \$optionValues{'serviceName'},
    "authority=s" => \$optionValues{'serviceAuth'},
    "help!" => \$optionValues{'help'},
    );

if(!GetOptions(@commandLineOptions) || $optionValues{'help'}) {
    HelpMessage();
    exit;
}

if(!defined($optionValues{'removeAll'}) && 
   !defined($optionValues{'predicate'}) &&
   !defined($optionValues{'serviceName'}) &&
   !defined($optionValues{'serviceAuth'})) {
    HelpMessage();
    exit;
}
    
#------------------------------------------------------------
# REMOVE THE MAPPINGS
#------------------------------------------------------------

VirtuosoHelper::remove_mappings($optionValues{'port'}, 
				$optionValues{'endpoint'},
				$optionValues{'predicate'},
				$optionValues{'serviceName'},
				$optionValues{'serviceAuth'});


__END__

=head1 NAME

remove_mappings.pl - remove predicate annotations from Moby services

=head1 SYNOPSIS

 # Remove all mappings of a given predicate
 remove_mappings.pl [-p <Virtuoso port>] [--uri <predicate URI>]

 # Remove all mappings for services from a given authority
 remove_mappings.pl [-p <Virtuoso port>] [--authority <authority URL>]

 # Remove all mappings for a given service 
 remove_mappings.pl [-p <Virtuoso port>] [--servicename <Moby service name>]

 # Remove all mappings
 remove_mappings.pl -R

=head1 DESCRIPTION

Remove one or more predicate mappings from the local (Virtuoso) registry. 

(Please note that this script must be run from the same machine on which 
the Virtuoso/Moby service registry is setup. It cannot be run remotely 
from another machine. Also, the Virtuoso 'isql' binary must come before 
the mySQL 'isql' on your PATH.)

You may indicate the mapping(s) to be deleted using any combination
of the criteria above.  However, unless -R is used ("remove all"),
at least one of --predicate, --authority, or --servicename must be
specified.

=over 4

=item -p <Virtuoso port>

The port number that the Virtuoso installation is running on.  This argument 
is optional, and defaults to 1111.

=item --uri <predicate URI>

The full (absolute) URI of the predicate.    

=item --authority <authority URL> 

Identifies the party that hosts the service, by DNS 
domain name.  

=item --servicename <Moby service name> 

Identifies the name of a BioMoby service.

=item -R

Remove all mappings from all services.

=back

