package SPARQLHelper;

use strict;
# Use long names for builtin variables.
use English; 
# Get absolute path of a given filename.
use Cwd 'abs_path';

use LWP::UserAgent;
use URI;
use Term::ReadKey;
use XML::LibXML;

my $HTTP = LWP::UserAgent->new();
$HTTP->timeout(30);  # in seconds

# for HTTP basic authentication
my $username;

use constant MAX_AUTH_ATTEMPTS => 3;
use constant HTTP_STATUS_UNAUTHORIZED => 401;
use constant SPARQL_XML_NS => "http://www.w3.org/2005/sparql-results#";

#----------------------------------------------------------------------
# Routines
#----------------------------------------------------------------------

sub setHTTPBasicAuthCredentials
{
    my ($host, $realm, $username, $password) = @_;
    $HTTP->credentials($host, $realm, $username, $password);
}

sub setUsername
{
    my $usernameArg = shift(@_);
    $username = $usernameArg;
}

sub readInput
{
    my $prompt = shift(@_);
    print $prompt;
    my $input = <STDIN>;
    chomp($input);
    return $input;
}

sub readHiddenInput
{
    my $prompt = shift(@_);
    
    print $prompt;

    ReadMode('noecho');
    my $input = '';

    while (1) {
 	my $c;
 	1 until defined($c = ReadKey(-1));
 	last if $c eq "\n";
 	$input .= $c;
    }
    print "\n";

    ReadMode('restore');
#    print "pass: $input\n";
    return $input;
}

sub authenticate
{
    my ($url, $realm) = @_;

    my $user;
    if(defined($username)) {
	$user = $username;
    }
    else {
	$user = readInput("username: ");
    }

    my $password = readHiddenInput("password: ");
    my $URL = URI->new($url);
    
    setHTTPBasicAuthCredentials(
	$URL->host() . ":" . $URL->port(),
	$realm,
	$user,
	$password);
    
}

sub updateQuery
{
    my ($endpoint, $query) = @_;
 
    my %postData;
    $postData{'query'} = $query;
    
    my $response;
    for(my $i = 0; $i < MAX_AUTH_ATTEMPTS; $i++) {
	$response = $HTTP->post($endpoint, \%postData);
	if($response->code() == HTTP_STATUS_UNAUTHORIZED) {
	    authenticate($endpoint, getAuthRealmFromResponse($response));
	} 
	else {
	    last;
	}
    }
    return $response;
}

sub selectQuery
{
    my ($endpoint, $query) = @_;
    
    my $getURI = URI->new($endpoint);
    $getURI->query_form("query" => $query);

    my $response;
    for(my $i = 0; $i < MAX_AUTH_ATTEMPTS; $i++) {
	$response = $HTTP->get($getURI->as_string());
	if($response->code() == HTTP_STATUS_UNAUTHORIZED) {
	    authenticate($endpoint, getAuthRealmFromResponse($response));
	} 
	else {
	    last;
	}
    }

    if(!$response->is_success()) {
	warn $response->status_line();
	return (); # empty array
    }

    return SPARQLResultsXMLToArray($response->content());
}

sub getAuthRealmFromResponse
{
    my $response = shift(@_);

    my $realm;
    my @authFields = split(",", $response->header("WWW-Authenticate"));
    foreach my $field (@authFields) {
	if($field =~ /realm\s*=/i) {
	    my @parts = split("=", $field);
	    $realm = $parts[1];
	    # remove any surrounding quotes
	    if($realm =~ /^"(.*)"$/ || $realm =~ /^'(.*)'$/) {
		$realm = $1;
	    }
	}
    }
    
    return $realm;
}


sub SPARQLResultsXMLToArray
{
    my $resultsXML = shift(@_);
    
    my $parser = XML::LibXML->new();
    my $dom = $parser->parse_string($resultsXML);
    my $root = $dom->documentElement();

    my $xc = XML::LibXML::XPathContext->new( $root );
    $xc->registerNs( sparql => SPARQL_XML_NS );

    my @resultNodes = $root->getElementsByTagName('result');
    my @resultsAsArray = ();

    foreach my $result (@resultNodes) {

	my %resultHash = ();

	foreach my $binding ($result->getElementsByTagName('binding')) {

	    my $varName = $binding->getAttribute('name'); 
	    my $value = $xc->findvalue('./sparql:uri|./sparql:literal|./sparql:bnode', $binding);
	    $resultHash{$varName} = $value;
	}
	
	push(@resultsAsArray, \%resultHash);
    }
    
    return @resultsAsArray;
}

sub format_query_for_display
{
    my ($query, $indentLevel) = @_;

    my @newlineBefore = (
	'PREFIX',
	'SELECT',
	'FROM',
	'WHERE',
	'CONSTRUCT',
	'INSERT INTO',
	);
    
    my @newlineAfter = ( '{', '\s+\.' );
    
    # Remove any leading/trailing spaces.
    $query =~ s/^\s*//g;
    $query =~ s/\s*$//g;
    # Replace any pre-existing newlines with spaces.
    $query =~ s/\n/ /g;
    # Replace multiple spaces with a single space.
    $query =~ s/ +/ /g;

    foreach my $marker1 (@newlineBefore) {
        $query =~ s/(\S+\s*)($marker1)/$1\n$2/g;
    }

    foreach my $marker2 (@newlineAfter) {
	$query =~ s/($marker2)/$1\n/g;
    }

    my @lines = split(/\n/, $query);

    my $formattedQuery = "";
    
    foreach my $line (@lines) {
        $formattedQuery .= "\t" x $indentLevel;
	$formattedQuery .= $line . "\n";
    }
    
    return $formattedQuery;
}

#--------------------------------------------------
# Expand abbreviated URIs within a text file.
# The expansions are performed based on SPARQL PREFIX
# lines that occur within the file.
# 
# The result is returned as a single multi-line
# string.
#--------------------------------------------------

sub get_prefix_expanded_text
{
    my $text = shift(@_);
    my @lines = split(/\n/, $text);

    my %prefixHash;
    my $output = "";
    
    foreach my $line (@lines)
    {
	# Strip comments. 
	#
	# To prevent conflicts with hashes appearing in URIs,
	# we don't allow comments at the ends of lines.
	
	$line =~ s/^\s*#.*//g;
	
	# Ignore blank lines (or lines that are just comments).
	
	if($line =~ /^\s*$/)
	{
	    next;
	}
	elsif($line =~ /\s*PREFIX\s*(\S+:)\s*<(\S+)>/)
	{
	    # Extract any prefix mappings, so we can do the
	    # appropriate substitutions.
	    $prefixHash{"$1"} = "$2";
	}
	else
	{
	    # A non-PREFIX, non-blank line.
	    # Do expansions according the PREFIX lines
	    # we've seen so far.

	    foreach my $key (keys %prefixHash)
	    {
		my $expanded = $prefixHash{$key};
		$line =~ s/$key/$expanded/g;
	    }
	
	    $output .= $line . "\n";
	}
    }

    return $output;
}

1;
