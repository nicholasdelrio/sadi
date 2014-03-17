#! /usr/bin/perl -w

use strict;
# Use long names for builtin variables.
use English;


if(@ARGV != 1)
{
    print "Usage: $0 <Virtuoso port number>\n";
    exit 1;
}

my $port = $ARGV[0];

#--------------------------------------------------
# Make that 'isql' is available, and it is the
# right isql.
#--------------------------------------------------

my $isql_path = check_for_prog('isql');

# Additionally, check they are using the right
# 'isql'.

my $help_output = qx|$isql_path --help|;

if($help_output =~ /unixODBC/)
{
    die "'$isql_path' is not the Virtuoso isql binary.  Please" .
	" change your PATH, so that the Virtuoso one comes first!";
}

#--------------------------------------------------
# Build the indexes.
#--------------------------------------------------

my $stats_output = qx(isql $port exec="statistics rdf_quad");

my @lines = split(/\n/, $stats_output);

my @indexes = ( 

"SPOG",
"SOPG",
"PSOG",
"POSG",
"OSPG",
"OPSG",

"GPOS",
"GOPS",
"PGOS",
"POGS",
"OGPS",
"OPGS",

"GSOP",
"GOSP",
"SGOP",
"SOGP",
"OGSP",
"OSGP",

);

# These are the indexes that are recommended in the Virtuoso
# documentation.

my @recommended_indexes = ( 

#"SPOG",
#"SOPG",
"PSOG",
#"POSG",
#"OSPG",
#"OPSG",

#"GPOS",
#"GOPS",
"PGOS",  # recommended for queries where only predicate and graph are specified
"POGS",
"OGPS",
#"OPGS",

#"GSOP",
#"GOSP",
#"SGOP",
#"SOGP",
#"OGSP",
#"OSGP",

);

foreach(@recommended_indexes)
{
    
    my $cmd = "isql $port exec=\"CREATE BITMAP INDEX $_ on RDF_QUAD (";
    $cmd .= substr($_,0,1) . ",";
    $cmd .= substr($_,1,1) . ",";
    $cmd .= substr($_,2,1) . ",";
    $cmd .= substr($_,3,1) . ")\"";

    print "Running: $cmd\n";
    my $output = qx($cmd);
    print $output . "\n";
    qx(isql $port exec="checkpoint");
}


#--------------------------------------------------
# Make sure the specified program is installed 
# and on the path.
#--------------------------------------------------

sub check_for_prog
{
    my $progname = shift @_;

    my $bin_path = qx|which $progname|;
    
    chomp($bin_path);

    if(($CHILD_ERROR >> 8) > 0)
    {
	die "Please ensure that the '$progname' program is installed and is on your path.";
    }
    
    return $bin_path;
}


