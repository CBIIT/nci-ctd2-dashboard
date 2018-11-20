#!/usr/bin/env perl

use strict;
use warnings;
use File::Basename qw(fileparse);
use File::Find;
use File::Spec;
use Getopt::Long qw(:config auto_help auto_version);
use List::MoreUtils qw(each_array none);
use Pod::Usage qw(pod2usage);
use Sort::Key::Natural qw(natsort);
use Term::ANSIColor;
use XML::Simple qw(:strict);
use Data::Dumper;

our $VERSION = '0.1';

# unbuffer error and output streams 
# make sure STDOUT is last so that it remains the default filehandle
select(STDERR); $| = 1;
select(STDOUT); $| = 1;

$Data::Dumper::Sortkeys = 1;
$Data::Dumper::Terse = 1;
$Data::Dumper::Deepcopy = 1;

# config
my $ok_line_length = 70;
my $submission_file_num_header_lines = 7;
my %debug_types = map { $_ => 1 } qw(
    all
    prop
    subm
    conf
);
# options
my $ctd2_home_dir = '';
my $ctd2_data_dir = '';
my $verbose = 0;
my @debug = ();
GetOptions(
    'ctd2-home-dir:s' => \$ctd2_home_dir,
    'ctd2-data-dir:s' => \$ctd2_data_dir,
    'verbose' => \$verbose,
    'debug:s' => \@debug,
) || pod2usage(-verbose => 0);
my %debug = map { $_ => 1 } split(',', join(',', map { lc($_) || 'all' } @debug));
for my $debug_type (sort keys %debug) {
    if (!$debug_types{$debug_type}) {
        my $valid_types_str = join(',', sort keys %debug_types);
        pod2usage(-message => "Valid debug types: $valid_types_str", -verbose => 0);
    }
}
$ctd2_home_dir = $ENV{CTD2_HOME} unless $ctd2_home_dir;
$ctd2_data_dir = $ENV{CTD2_DATA_HOME} unless $ctd2_data_dir;
pod2usage(-message => "Invalid --ctd2-home-dir\n") unless -d $ctd2_home_dir;
pod2usage(-message => "Invalid --ctd2-data-dir\n") unless -d $ctd2_data_dir;
my $admin_properties_file = "$ctd2_home_dir/admin/src/main/resources/META-INF/spring/admin.properties";
my $obs_data_app_context_xml_file = "$ctd2_home_dir/admin/src/main/resources/META-INF/spring/observationDataApplicationContext.xml";
my $obs_data_shared_app_context_xml_file = "$ctd2_home_dir/admin/src/main/resources/META-INF/spring/observationDataSharedApplicationContext.xml";
my $test_obs_data_app_context_xml_file = "$ctd2_home_dir/admin/src/test/resources/META-INF/spring/testObservationDataApplicationContext.xml";
my $dashboard_cv_per_template_file = "$ctd2_data_dir/dashboard-CV-per-template.txt";
my $dashboard_cv_per_column_file = "$ctd2_data_dir/dashboard-CV-per-column.txt";
for my $file (
    $admin_properties_file,
    $obs_data_app_context_xml_file,
    $obs_data_shared_app_context_xml_file,
    $dashboard_cv_per_template_file,
    $dashboard_cv_per_column_file,
) {
    die +(-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
        ": $file not found\n" unless -f $file;
}
my $admin_properties_file_name = fileparse($admin_properties_file);
my $obs_data_app_context_xml_file_name = fileparse($obs_data_app_context_xml_file);
my $obs_data_shared_app_context_xml_file_name = fileparse($obs_data_shared_app_context_xml_file);
my $test_obs_data_app_context_xml_file_name = fileparse($test_obs_data_app_context_xml_file);
my $dashboard_cv_per_template_file_name = fileparse($dashboard_cv_per_template_file);
my $dashboard_cv_per_column_file_name = fileparse($dashboard_cv_per_column_file);
# load config files
print "[Load]\n";
# load dashboard-CV-per-template.txt, dashboard-CV-per-column.txt
my (%submission_template_info, %submission_column_info);
for my $tab_file ($dashboard_cv_per_template_file, $dashboard_cv_per_column_file) {
    print "Loading $tab_file\n";
    open(my $tab_fh, '<', $tab_file)
        or die +(-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
               ": could not open $tab_file: $!\n";
    my %tab_file_col_idx_by_name;
    while (<$tab_fh>) {
        s/\s+$//;
        my @fields = split /\t/;
        if ($. == 1) {
            %tab_file_col_idx_by_name = map { $fields[$_] => $_ } 0 .. $#fields;
        }
        else {
            my $line_data_hashref = {
                map {
                    $_ => $fields[$tab_file_col_idx_by_name{$_}]
                }
                grep {
                    $_ ne 'template_name'
                }
                keys %tab_file_col_idx_by_name
            };
            if ($tab_file =~ /per-template/i) {
                $submission_template_info{
                    $fields[$tab_file_col_idx_by_name{template_name}]
                } = $line_data_hashref;
            }
            else {
                push @{
                    $submission_column_info{
                        $fields[$tab_file_col_idx_by_name{template_name}]
                    }
                }, $line_data_hashref;
            }
        }
    }
    close($tab_fh);
}
if ($debug{all} or $debug{subm}) {
    print STDERR "\%submission_template_info:\n",
                 Dumper(\%submission_template_info),
                 "\%submission_column_info:\n",
                 Dumper(\%submission_column_info);
}
# load admin.properties
my (%admin_properties, %admin_properties_files);
print "Loading $admin_properties_file\n";
open(my $admin_properties_fh, '<', $admin_properties_file)
    or die +(-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
           ": could not open $admin_properties_file: $!\n";
while (<$admin_properties_fh>) {
    next if m/^(\s*#|\s*$)/;
    s/^\s+//;
    s/\s+$//;
    my ($property, $value) = split '=', $_, 2;
    $value =~ s/\${?CTD2_HOME}?/$ctd2_home_dir/g;
    $value =~ s/\${?CTD2_DATA_HOME}?/$ctd2_data_dir/g;
    if (!exists $admin_properties{$property}) {
        $admin_properties{$property} = $value;
        if ((my $file = $value) =~ s/^file://) {
            $admin_properties_files{$file}++;
        }
    }
    else {
        die +(-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
            ": $property defined twice in $admin_properties_file\n";
    }
}
close($admin_properties_fh);
if ($debug{all} or $debug{prop}) {
    print STDERR "\%admin_propertes:\n", 
                 Dumper(\%admin_properties),
                 "\%admin_properties_files:\n", 
                 Dumper(\%admin_properties_files);
}
# load observationDataApplicationContext.xml
print "Loading $obs_data_app_context_xml_file\n";
my $obs_data_app_context_xml_data = XMLin(
    $obs_data_app_context_xml_file,
    KeyAttr => {
    },
    ForceArray => [
        'bean',
        'property',
    ],
    GroupTags => {
    },
);
# load observationDataSharedApplicationContext.xml
print "Loading $obs_data_shared_app_context_xml_file\n";
my $obs_data_shared_app_context_xml_data = XMLin(
    $obs_data_shared_app_context_xml_file,
    KeyAttr => {
    },
    ForceArray => [
        'bean',
        'property',
        'batch:job',
        'batch:step',
        'batch:tasklet',
        'batch:chunk',
        'entry',
    ],
    GroupTags => {
    },
);

if ($debug{all} or $debug{conf}) {
    print STDERR "\$obs_data_app_context_xml_data:\n", 
                 Dumper($obs_data_app_context_xml_data),
                 "\$obs_data_app_shared_context_xml_data:\n", 
                 Dumper($obs_data_shared_app_context_xml_data);
}
# check config files
print "[Check]";
# check admin.properties
print "\nChecking $admin_properties_file_name", 
      ' ' x ($ok_line_length - length("Checking $admin_properties_file_name"));
my $admin_props_errors = 0;
for my $property (natsort keys %admin_properties) {
    if ((my $file = $admin_properties{$property}) =~ s/^file://) {
        if ($file =~ /\*/) {
            if (!glob($file)) {
                print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                      ": $file not found";
                $admin_props_errors++;
            }
        }
        else {
            if (!-f $file) {
                print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                      ": $file not found";
                $admin_props_errors++;
            }
        }
    }
}
find({
    preprocess => sub {
        natsort @_;
    },
    wanted => sub {
        # directories
        if (-d) {
            my @rel_dirs = File::Spec->splitdir(
                File::Spec->abs2rel($File::Find::name, "$ctd2_data_dir/submissions")
            );
            # don't traverse down directories > 1 depth
            $File::Find::prune = 1 if scalar(@rel_dirs) > 1;
        }
        # files
        elsif (-f) {
            my $file_name = $_;
            my $file_path = $File::Find::name;
            my $file_dir  = $File::Find::dir;
            my @file_dir_parts = File::Spec->splitdir($file_dir);
            my $submission_dir_name = $file_dir_parts[$#file_dir_parts];
            my ($file_basename, undef, $file_ext) = fileparse($file_path, qr/\.[^.]*/);
            # submission txt files only
            if (
                lc($file_ext) eq '.txt' and 
                $file_basename eq $submission_dir_name and
                !exists $admin_properties_files{$file_path}
            ) {
                print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                      ": $file_path not in $admin_properties_file_name";
                $admin_props_errors++;
            }
        }
    },
}, "$ctd2_data_dir/submissions");
print '[', (-t STDOUT ? colored('OK', 'green') : 'OK'), ']' unless $admin_props_errors;
# check observationDataApplicationContext.xml
print "\nChecking $obs_data_app_context_xml_file_name", 
      ' ' x ($ok_line_length - length("Checking $obs_data_app_context_xml_file_name"));
my $obs_data_app_context_xml_errors = 0;
my (
    %reader_bean_names,
    %reader_bean_resources,
    %reader_bean_resource_by_prefix,
    %ref_line_mapper_bean_names,
);
for my $reader_bean (@{$obs_data_app_context_xml_data->{bean}}) {
    $reader_bean_names{$reader_bean->{name}}++;
    (my $reader_bean_prefix = $reader_bean->{name}) =~ s/Reader$//;
    for my $property (@{$reader_bean->{property}}) {
        if ($property->{name} eq 'resources') {
            $property->{value} =~ s/(^\${|}$)//g;
            $reader_bean_resources{$property->{value}}++;
            $reader_bean_resource_by_prefix{$reader_bean_prefix} = $property->{value};
            if (!exists $admin_properties{$property->{value}}) {
                print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                      ": '$property->{value}' not found in $admin_properties_file_name";
                $obs_data_app_context_xml_errors++;
            }
        }
        elsif ($property->{name} eq 'delegate') {
            for my $delegate_bean (@{$property->{bean}}) {
                for my $property (@{$delegate_bean->{property}}) {
                    if ($property->{name} eq 'lineMapper') {
                        $ref_line_mapper_bean_names{$property->{ref}}++;
                        (my $ref_bean_prefix = $property->{ref}) =~ s/LineMapper$//;
                        if ($ref_bean_prefix ne $reader_bean_prefix) {
                            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                                  ": prefixes don't match in '$reader_bean->{name}' -> '$property->{ref}'";
                            $obs_data_app_context_xml_errors++;
                        }
                    }
                    elsif ($property->{name} eq 'linesToSkip') {
                        if ($property->{value} != $submission_file_num_header_lines) {
                            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                                  ": invalid linesToSkip value in '$reader_bean->{name}'";
                            $obs_data_app_context_xml_errors++;
                        }
                    }
                }
            }
        }
    }
}
for my $admin_property (natsort keys %admin_properties) {
    # submission data file admin properties only
    if ($admin_properties{$admin_property} =~ m/^file:$ctd2_data_dir\/submissions\//) {
        if (!exists $reader_bean_resources{$admin_property}) {
            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                  ": '$admin_property' not found in $obs_data_app_context_xml_file_name";
            $obs_data_app_context_xml_errors++;
        }
    }
}
print '[', colored('OK', 'green'), ']' unless $obs_data_app_context_xml_errors;
# check observationDataSharedApplicationContext.xml
print "\nChecking $obs_data_shared_app_context_xml_file_name", 
      ' ' x ($ok_line_length - length("Checking $obs_data_shared_app_context_xml_file_name"));
my $obs_data_shared_app_context_xml_errors = 0;
my %batch_step_reader_bean_names;
for my $batch_job (@{$obs_data_shared_app_context_xml_data->{'batch:job'}}) {
    if ($batch_job->{id} eq 'observationDataImporterJob') {
        my ($prev_batch_step, $at_last_step);
        for my $batch_step (@{$batch_job->{'batch:step'}}) {
            (my $batch_step_prefix = $batch_step->{id}) =~ s/Step$//;
            for my $batch_tasklet (@{$batch_step->{'batch:tasklet'}}) {
                for my $batch_chunk (@{$batch_tasklet->{'batch:chunk'}}) {
                    $batch_step_reader_bean_names{$batch_chunk->{reader}}++;
                    if (!exists $reader_bean_names{$batch_chunk->{reader}}) {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$batch_chunk->{reader}' not found in $obs_data_app_context_xml_file_name";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                    (my $reader_bean_prefix = $batch_chunk->{reader}) =~ s/Reader$//;
                    if ($reader_bean_prefix ne $batch_step_prefix) {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": prefixes don't match in '$batch_chunk->{reader}' -> '$batch_step->{id}'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
            }
            if (defined $prev_batch_step and $prev_batch_step ne $batch_step->{id}) {
                print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                      ": '$batch_step->{id}' doesn't match next batch step from previous";
                $obs_data_shared_app_context_xml_errors++;
            }
            if (defined $batch_step->{next}) {
                $prev_batch_step = $batch_step->{next};
            }
            elsif ($at_last_step) {
                print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                      ": '$batch_step->{id}' missing next batch step attribute";
            }
            else {
                $at_last_step++;
            } 
        }
    }
}
for my $reader_bean_name (natsort keys %reader_bean_names) {
    if (!exists $batch_step_reader_bean_names{$reader_bean_name}) {
        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
              ": '$reader_bean_name' batch:step not defined";
        $obs_data_shared_app_context_xml_errors++;
    }
}
my (
    %line_mapper_bean_names,
    %line_tokenizer_bean_names,
    %ref_line_tokenizer_bean_names,
    %line_tokenizer_col_names_by_template_name,
    %observation_template_map,
);
for my $bean (@{$obs_data_shared_app_context_xml_data->{'bean'}}) {
    if ($bean->{class} eq 'org.springframework.batch.item.file.mapping.DefaultLineMapper') {
        $line_mapper_bean_names{$bean->{name}}++;
        if (!exists $ref_line_mapper_bean_names{$bean->{name}}) {
            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                  ": '$bean->{name}' not found in $obs_data_app_context_xml_file_name";
            $obs_data_shared_app_context_xml_errors++;
        }
        (my $line_mapper_bean_prefix = $bean->{name}) =~ s/LineMapper$//;
        for my $property (@{$bean->{property}}) {
            if ($property->{name} eq 'lineTokenizer') {
                $ref_line_tokenizer_bean_names{$property->{ref}}++;
                (my $ref_bean_prefix = $property->{ref}) =~ s/LineTokenizer$//;
                if ($ref_bean_prefix ne $line_mapper_bean_prefix) {
                    print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                          ": prefixes don't match in '$bean->{name}' -> '$property->{ref}'";
                    $obs_data_shared_app_context_xml_errors++;
                }
            }
        }
    }
    elsif ($bean->{class} eq 'org.springframework.batch.item.file.transform.DelimitedLineTokenizer') {
        $line_tokenizer_bean_names{$bean->{name}}++;
        (my $line_tokenizer_bean_prefix = $bean->{name}) =~ s/LineTokenizer$//;
        for my $property (@{$bean->{property}}) {
            if ($property->{name} eq 'delimiter') {
                if ($property->{value} ne '\u0009') {
                    print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                          ": '$bean->{name}' has invalid delimiter '$property->{value}'";
                    $obs_data_shared_app_context_xml_errors++;
                }
            }
            elsif ($property->{name} eq 'names') {
                my @tokenizer_col_names = split ',', $property->{value};
                (my $submission_file = $admin_properties{$reader_bean_resource_by_prefix{$line_tokenizer_bean_prefix}}) =~ s/^file://;
                open(my $submission_fh, '<', $submission_file)
                    or die "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                           ": could not open $submission_file: $!\n";
                my (@file_col_names, %file_col_idx_by_name, $template_name);
                while (<$submission_fh>) {
                    s/\s+$//;
                    my @fields = split /\t/;
                    if ($. == 1) {
                        @file_col_names = @fields;
                        # add 'dummy' column name
                        $file_col_names[0] = 'dummy';
                        %file_col_idx_by_name = map { $fields[$_] => $_ } 0 .. $#fields;
                    }
                    else {
                        # skip other header lines
                        next unless $. > $submission_file_num_header_lines;
                        $template_name = $fields[$file_col_idx_by_name{'template_name'}];
                        last;
                    }
                }
                close($submission_file);
                push @{$line_tokenizer_col_names_by_template_name{$template_name}}, @tokenizer_col_names;
                my $ea = each_array(@tokenizer_col_names, @file_col_names);
                while (my ($tokenizer_col_name, $file_col_name) = $ea->()) {
                    if (!defined $tokenizer_col_name or
                        !defined $file_col_name or
                        $tokenizer_col_name ne $file_col_name) {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$bean->{name}' $property->{name} csv string doesn't match columns in $submission_file\n",
                              "Tokenizer: ", join(',', @tokenizer_col_names), "\n",
                              "     File: ", join(',', @file_col_names);
                        $obs_data_shared_app_context_xml_errors++;
                        last;
                    }
                }
            }
        }
    }
    elsif ($bean->{class} eq 'org.springframework.batch.core.step.item.SimpleStepFactoryBean') {
        # do nothing for now
    }
    elsif ($bean->{class} eq 'java.util.HashMap') {
        if ($bean->{name} eq 'observationTemplateMap') {
            for my $entry (@{$bean->{'constructor-arg'}->{'map'}->{'entry'}}) {
                if (!exists $observation_template_map{$entry->{key}}) {
                    $observation_template_map{$entry->{key}} = $entry->{value};
                }
                else {
                    print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                          ": '$entry->{key}' defined more than once";
                    $obs_data_shared_app_context_xml_errors++;
                }
            }
        }
    }
    else {
        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
              ": unrecognized class '$bean->{class}'";
        $obs_data_shared_app_context_xml_errors++;
    }
}
for my $ref_bean_name (natsort keys %ref_line_mapper_bean_names) {
    if (!exists $line_mapper_bean_names{$ref_bean_name}) {
        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
              ": '$ref_bean_name' line mapper is referenced by not defined";
        $obs_data_shared_app_context_xml_errors++;
    }
}
for my $ref_bean_name (natsort keys %ref_line_tokenizer_bean_names) {
    if (!exists $line_tokenizer_bean_names{$ref_bean_name}) {
        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
              ": '$ref_bean_name' line tokenizer is referenced by not defined";
        $obs_data_shared_app_context_xml_errors++;
    }
}
for my $template_name (natsort keys %line_tokenizer_col_names_by_template_name) {
    # don't check first 4 columns: dummy,submission_name,submission_date,template_name
    my @tokenizer_col_names_to_check = 
        @{$line_tokenizer_col_names_by_template_name{$template_name}}[4..$#{$line_tokenizer_col_names_by_template_name{$template_name}}];
    for my $tokenizer_col_name (@tokenizer_col_names_to_check) {
        if (!exists $observation_template_map{"$template_name:$tokenizer_col_name"}) {
            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                  ": '$template_name:$tokenizer_col_name' entry missing from $obs_data_shared_app_context_xml_file_name";
            $obs_data_shared_app_context_xml_errors++;
        }
    }
}
for my $key (natsort keys %observation_template_map) {
    my ($template_name, $col_name) = split ':', $key, 2;
    if (none { $col_name eq $_ } @{$line_tokenizer_col_names_by_template_name{$template_name}}) {
        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
              ": '$template_name:$col_name' entry missing from tokenizer definition";
        $obs_data_shared_app_context_xml_errors++;
    }
}
for my $template_name (natsort keys %submission_column_info) {
    for my $column_info (sort { $a->{id} <=> $b->{id} } @{$submission_column_info{$template_name}}) {
        if (!exists $observation_template_map{"$template_name:$column_info->{column_name}"}) {
            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                  ": '$template_name:$column_info->{column_name}' entry missing from $obs_data_shared_app_context_xml_file_name";
            $obs_data_shared_app_context_xml_errors++;
        }
        else {
            if (defined $column_info->{subject} and $column_info->{subject} ne '') {
                if ($column_info->{subject} eq 'animal_model') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findAnimalModelByName') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'subject:findAnimalModelByName'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{subject} eq 'tissue_sample') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findTissueSampleByName') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'subject:findTissueSampleByName'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{subject} eq 'cell_sample') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findCellLineByName') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'subject:findCellLineByName'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{subject} eq 'compound') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findCompoundsByName') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'subject:findCompoundsByName'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{subject} eq 'gene') {
                    if ($column_info->{column_name} =~ /gene_id$/i) {
                        if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findGenesByEntrezId') {
                            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                                  ": '$template_name:$column_info->{column_name}' needs value 'subject:findGenesByEntrezId'";
                            $obs_data_shared_app_context_xml_errors++;
                        }
                    }
                    else {
                        if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findGenesBySymbol') {
                            print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                                  ": '$template_name:$column_info->{column_name}' needs value 'subject:findGenesBySymbol'";
                            $obs_data_shared_app_context_xml_errors++;
                        }
                    }
                }
                elsif ($column_info->{subject} eq 'shrna') {
                    #if ($template_name eq 'dfci_ataris_analysis' and $column_info->{column_name} eq 'shRNA_id') {
                    #    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findSubjectsByXref-BROAD_SHRNA') {
                    #        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                    #              ": '$template_name:$column_info->{column_name}' needs value 'subject:findSubjectsByXref-BROAD_SHRNA'";
                    #        $obs_data_shared_app_context_xml_errors++;
                    #    }
                    #}
                    #elsif ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findSiRNAByReagentName') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findSiRNAByReagentName' and
                        $observation_template_map{"$template_name:$column_info->{column_name}"} ne 'subject:findSiRNAByTargetSequence') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value ",
                              "'subject:findSiRNAByReagentName' or 'subject:findSiRNAByTargetSequence";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                else {
                    print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                          ": unrecognized subject '$column_info->{subject}'";
                    $obs_data_shared_app_context_xml_errors++;
                }
            }
            if (defined $column_info->{evidence} and $column_info->{evidence} ne '') {
                if ($column_info->{evidence} eq 'file') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'evidence:readString:createObservedFileEvidence') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'evidence:readString:createObservedFileEvidence'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{evidence} eq 'label') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'evidence:readString:createObservedLabelEvidence') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'evidence:readString:createObservedLabelEvidence'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{evidence} eq 'numeric') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'evidence:readDouble:createObservedNumericEvidence' and
                        $observation_template_map{"$template_name:$column_info->{column_name}"} ne 'evidence:readInt:createObservedNumericEvidence') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value ",
                              "'evidence:readDouble:createObservedNumericEvidence' or 'evidence:readInt:createObservedNumericEvidence'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                elsif ($column_info->{evidence} eq 'url') {
                    if ($observation_template_map{"$template_name:$column_info->{column_name}"} ne 'evidence:readString:createObservedUrlEvidence') {
                        print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                              ": '$template_name:$column_info->{column_name}' needs value 'evidence:readString:createObservedUrlEvidence'";
                        $obs_data_shared_app_context_xml_errors++;
                    }
                }
                else {
                    print "\n", (-t STDOUT ? colored('ERROR', 'red') : 'ERROR'), 
                          ": unrecognized evidence '$column_info->{evidence}'";
                    $obs_data_shared_app_context_xml_errors++;
                }
            }
        }
    }
}
print '[', (-t STDOUT ? colored('OK', 'green') : 'OK'), ']' unless $obs_data_shared_app_context_xml_errors;

print "\n";
exit;

__END__

=head1 NAME 

check_ctd2_dashboard_config.pl - CTD2 Dashboard Config Checker

=head1 SYNOPSIS

 check_ctd2_dashboard_config.pl [options]
 
 Options:
    --ctd2-home-dir     CTD2 Dashboard software home directory (default: $CTD2_HOME env var)
    --ctd2-data-dir     CTD2 Dashboard data directory (default: $CTD2_DATA_HOME env var)
    --verbose           Be verbose
    --help              Display usage message and exit
    --version           Display program version and exit

=cut
