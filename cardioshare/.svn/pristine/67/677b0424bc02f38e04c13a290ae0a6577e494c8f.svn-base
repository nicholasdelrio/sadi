#! /usr/bin/perl -w

use strict;
# Use long names for builtin variables.
use English; 
# Contains helper routines for loading data, issuing queries, etc.
use VirtuosoUtil;

my $usage = <<USAGE;

USAGE: 

(1) add_pred_def.pl <[host:]port> <filename1> [<filename2>] ...
(2) add_pred_def.pl <[host:]port> 
(3) add_pred_def.pl <-h|--help>

Add a definition for a predicate to the registry.

NOTE: Be warned, the definition you provide will completely replace
any definition information that already exists in the registry for
the predicate in question.  (This is necessary due the use of blank
nodes in the definition structure; simply adding the new definition
will create a duplicate structure.)

Under usage (1):

    <filenameN> contains a definition for the predicate
    in the form on an anonymous PERL hash.  Multiple filenames
    may be given.  

    This definition file (PERL hash) includes:

    - the URI for the predicate, and any number of synonyms
    - the URI for the inverse predicate, and any number of synonyms
    - any SPARQL PREFIX lines that must be used to expand the above URIs
    - whether the predicate is a datatype property or an object property
    - any CONSTRUCT queries that are associated with the predicate 

Under usage (2):

    Read the list of filenames from STDIN, one filename per line.

Under usage (3):

    Display this help message.

USAGE

#------------------------------------------------------------
# Determine the usage case.
#------------------------------------------------------------

# Tells whether the user has invoked the script under usage 
# case 1, 2, 3, or 4 (see above).

use constant USAGE_CMDLINE => 1;
use constant USAGE_STDIN => 2;
use constant USAGE_HELP => 3;

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
elsif($usageCase == USAGE_HELP)
{
    print $usage;
    exit 0;
}

#------------------------------------------------------------
# Load the definitions.
#------------------------------------------------------------

my $port = $ARGV[0];

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

foreach my $file (@args)
{

    my $predDefStr = VirtuosoUtil::get_file_as_string($file);
    my $pPredDef = eval $predDefStr;
    
    if(!defined($pPredDef)) {
	die "There is a syntax error in the file: $file";
    }

#     foreach my $key (keys %$pPredDef) {
# 	print "Key: $key\n";
#     }

#     print "predDefStr: " . $predDefStr . "\n";
#     print "predicate: " . $pPredDef->{'predicate'} . "\n";

    # If a definition already exists for the predicate,
    # we must remove it from the registry first.

    VirtuosoUtil::remove_pred_def($port, $pPredDef, $file);
    VirtuosoUtil::add_pred_def2($port, $pPredDef, $file);
    
#    print "Press any key to continue...";
#    my $line = <STDIN>;
}
