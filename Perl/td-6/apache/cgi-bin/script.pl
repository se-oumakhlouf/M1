#!/usr/bin/perl

use strict;
use warnings;
use CGI;

my $query = CGI->new();
print "Content-Type: text/html\n";
print "\n";
print $query->param('nom') . "\n";