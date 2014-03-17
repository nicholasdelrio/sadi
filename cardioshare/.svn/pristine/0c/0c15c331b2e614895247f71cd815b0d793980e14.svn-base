package IOHelper;

# Currently: 1 = print everything, 0 = print nothing. 
my $VERBOSE_LEVEL = 1;
my $INDENT_LEVEL = 0;

sub say
{
    if(getVerboseLevel() >= 1) {
	print "\t" x getIndentLevel();
	print "@_\n";
    }
}

sub setIndentLevel
{
    my $newLevel = shift(@_);
    my $oldLevel = $VERBOSE_LEVEL;
    $INDENT_LEVEL = $newLevel;
    return $oldLevel;
}

sub getIndentLevel
{
    return $INDENT_LEVEL;
}

sub setVerboseLevel
{
    my $newLevel = shift(@_);
    my $oldLevel = $VERBOSE_LEVEL;
    $VERBOSE_LEVEL = $newLevel;
    return $oldLevel;
}

sub getVerboseLevel
{
    return $VERBOSE_LEVEL;
}

sub read_file_into_string 
{
    my $filename = shift(@_);
    my $filehandle;
    
    if(!open($filehandle, "<$filename")) {
	die "Unable to open file $filename \n";
    }

    my $content = read_filehandle_into_string($filehandle);
    close($filehandle);
    return $content;
}

sub read_filehandle_into_string 
{
    my $filehandle = shift(@_);
    my $content = "";

    while(<$filehandle>) {
	$content .= $_;
    }
    
    return $content;
}

1;
