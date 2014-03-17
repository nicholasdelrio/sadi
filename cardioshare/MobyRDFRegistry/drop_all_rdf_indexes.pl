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
# Get the list of existing indexes on the
# RDF_QUAD table.
#--------------------------------------------------

my $stats_output = qx(isql $port exec="statistics rdf_quad");

my @lines = split(/\n/, $stats_output);
my %indexes;


foreach(@lines)
{
    # The 6th column of the output contains the index names.
    if(/^DB\s+(\S+\s+){4}(\S+)/)
    {

	# NULL means the current row describes a statistic instead of an index.
	# RDF_QUAD means the current row describes the index for the primary key,
	# which you can't delete.

	if(!($2 =~ /NULL|RDF_QUAD/))
	{
	    $indexes{$2} = 1;
	}
    }

}

foreach(keys(%indexes))
{
    print "Dropping index " . "$_...\n";
    qx(isql $port exec="drop index $_");
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


