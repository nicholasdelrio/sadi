#! /usr/bin/perl -w

use strict;
# Use long names for builtin variables.
use English; 
# Contains helper routines for loading data, issuing queries, etc.
use VirtuosoUtil;

my $usage = <<USAGE;

USAGE: 

(1) remove_pred_def.pl <[host:]port> <filename1>|<predicateURI1> [<filename2>|<predicateURI2>] ...
(2) remove_pred_def.pl <[host:]port>
(3) remove_pred_def.pl <[host:]port> -a
(4) remove_pred_def.pl <-h|--help>
 
Remove the definition for a service predicate from the registry. 

Under usage (1):

   <filenameN> contains definition for the predicate
   in the form on an anonymous PERL hash.  This is handy
   because you can use the same definition file you 
   for loading predicate as for deleting it.  The file(s) 
   given on the commandline are only used to determine 
   the URI for predicates; the other information in the
   .defn file (CONSTRUCT queries and so on) need not match
   the actual definition in the registry.

   Just FYI, the definition file contains a PERL hash, which 
   includes:

   - the URI for the predicate, and any number of synonyms
   - the URI for the inverse predicate, and any synonyms
   - any SPARQL PREFIX lines that must be used to expand the 
   above URIs
   - whether the predicate is a datatype property or an object
   property
   - any CONSTRUCT queries that are associated with the predicate 

   You may also specify the predicates directly by URIs. URIs
   are distinguished from filename by the fact that they start
   with "lsid:" or "http://".  

Under usage (2): 
     
   The filenames/URIs may be specified, one per line, via
   standard input. SPARQL PREFIX lines may be used to abbreviate
   URIs.

Under usage (3):
    
   All predicate definitions are removed from the registry.

Under usage (4):

   Display this help message.

USAGE

#------------------------------------------------------------
# Determine the usage case.
#------------------------------------------------------------

# Tells whether the user has invoked the script under usage 
# case 1, 2, 3, or 4 (see above).

use constant USAGE_CMDLINE => 1;
use constant USAGE_STDIN => 2;
use constant USAGE_ALL => 3;
use constant USAGE_HELP => 4;

my $usageCase = USAGE_CMDLINE;

if(@ARGV == 1)
{
    $usageCase = USAGE_STDIN;
}

foreach my $arg (@ARGV)
{
    if($arg eq '-h' || $arg eq '--help')
    {
	$usageCase = USAGE_HELP;
    }
    elsif($arg eq '-a' && $usageCase != USAGE_HELP)
    {
	$usageCase = USAGE_ALL;
    }

}

#------------------------------------------------------------
# Check that the right number of arguments was given for the 
# given usage case.
#------------------------------------------------------------

if($usageCase == USAGE_CMDLINE && @ARGV < 2)
{
    print $usage;
    exit 1;
}
elsif($usageCase == USAGE_STDIN && @ARGV != 1)
{
    print $usage;
    exit 1;
}
elsif($usageCase == USAGE_ALL && @ARGV != 2)
{
    print $usage;
    exit 1;
}
elsif($usageCase == USAGE_HELP)
{
    print $usage;
    exit 0;
}

#------------------------------------------------------------
# Delete the definitions.
#------------------------------------------------------------

my $port = $ARGV[0];

if($usageCase == USAGE_ALL)
{
    print "Clearing all predicate definitions from the registry...";
    VirtuosoUtil::SPARQL($port, "CLEAR GRAPH <" . $VirtuosoUtil::PREDICATE_GRAPH . ">");
    exit 0;
}

my @args;

if($usageCase == USAGE_STDIN)
{
    my @input = <STDIN>;
    @args = split(/\n/, VirtuosoUtil::get_prefix_expanded_text(@input));
}
else
{
    @args = @ARGV[1..$#ARGV];
}

foreach my $arg (@args)
{

    if(!isURI($arg))
    {
	my $predDefStr = VirtuosoUtil::get_file_as_string($arg);
	my $pPredDef = eval $predDefStr;
	VirtuosoUtil::remove_pred_def($port, $pPredDef, $arg);
    }
    else
    {
	VirtuosoUtil::remove_pred_def_by_uri($port, $arg);
    }
    
}


#////////////////////////////////////////////////////////////
# SUBROUTINES
#////////////////////////////////////////////////////////////

#------------------------------------------------------------
# Test if a command-line argument is a URI or 
# a filename.
#------------------------------------------------------------

sub isURI
{
    my $arg = shift @_;
    
    if($arg =~ /http:\/\//)
    {
	return 1;
    }
    else
    {
	return 0;
    }
}
