#!/usr/bin/perl

use strict;
use warnings;
use POSIX qw(strftime);


my $birthday = strftime('%A, %d %B %Y', 0, 0, 0, 11, 0, 101);

print "$birthday\n";
