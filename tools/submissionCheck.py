#-------------------------------------------------------------------------------
# Name:     submissionCheck
# Purpose:  cript to verify that CTD^2 submissions will load to the Dashboard
#
# Author:   vdancik
#
# Created:  17 Oct 2014
#-------------------------------------------------------------------------------

import sys
import os
import gzip
from html.parser import HTMLParser
from urllib.request import urlopen
from urllib.parse import quote

CENTERS = {
'Broad Institute':'Stuart L. Schreiber, Ph.D.',
'Cold Spring Harbor Laboratory':'Scott Powers, Ph.D.',
'Columbia University':'Andrea Califano, Ph.D.',
'Dana-Farber Cancer Institute':'William C. Hahn, M.D., Ph.D.',
'Emory University':'Haian Fu, Ph.D.',
'Fred Hutchinson Cancer Research Center (1)':'Christopher Kemp, Ph.D.',
'Fred Hutchinson Cancer Research Center (2)':'Martin McIntosh, Ph.D.',
'Stanford University':'Calvin J. Kuo, M.D., Ph.D.',
'Translational Genomics Research Institute':'Michael E. Berens, Ph.D.',
'University of California San Francisco (1)':'Michael McManus, Ph.D.',
'University of California San Francisco (2)':'William A. Weiss, M.D., Ph.D.',
'University of Texas MD Anderson Cancer Center':'Gordon B. Mills, M.D., Ph.D.',
'University of Texas Southwestern Medical Center':'Michael Roth, Ph.D.',
}

ROLES = {
    'gene': {'target','biomarker','oncogene','perturbagen','master regulator','candidate master regulator','interactor','background'},
    'shrna': {'perturbagen'},
    'tissue_sample': {'metastasis','disease','tissue'},
    'cell_sample': {'cell line'},
    'compound': {'candidate drug','perturbagen','metabolite','control compound'},
    'animal_model': {'strain'},
    'numeric': {'measured','observed','computed','background'},
    'label': {'measured','observed','computed','species','background'},
    'file': {'literature','measured','observed','computed','written','background'},
    'url': {'measured','computed','reference','resource','link'}
}

SUBJECT_TYPES = {'', 'gene', 'transcript', 'shrna', 'protein', 'compound', 'cell_sample', 'tissue_sample', 'animal_model'}

EVIDENCE_TYPES = {'', 'numeric', 'label', 'file', 'url'}

SUBJECT = 'subject'
EVIDENCE = 'evidence'
ROLE = 'role'
MIMETYPE = 'mime_type'
NUMERICUNITS = 'numeric_units'
DISPLAYTEXT = 'display_text'
METADATA = ['', SUBJECT, EVIDENCE, ROLE, MIMETYPE, NUMERICUNITS,DISPLAYTEXT]

SUBMISSION_NAME_HEADER = 'submission_name'
SUBMISSION_DATE_HEADER = 'submission_date'
TEMPLATE_NAME_HEADER = 'template_name'

MAX_LENGTH = {
    'display_name':255, # DashboardEntity
    'template_description':1024, 'observation_summary':1024, 'submission_name':128, 'submission_description':1024, 'project':1024, 'principal_investigator': 64, # ObservationTemplate
    'url': 2048, # UrlEvidence
    'file_path':1024, 'file_name':1024, 'mime_type': 256, # FileEvidence
    'subject_display_text':10240, 'subject_column_name': 1024, # ObservedSubjectRole
    'evidence_display_text':10240, 'evidence_column_name': 1024, 'attribute': 128, # ObservedEvidenceRole
    'units': 32, # DataNumericValue
    'role': 128, # SubjectWithSummaries
    'sequence':2048, 'type':5, 'reagent_name':255, # ShRna
    'smiles':2048, # CompoundImpl
    'gene_id':32, 'gene_symbol':32, # GeneImpl
    'annotation_source':128, 'annotation_type':128, # AnnotationImpl
    'database_id':128, 'database_name':128 # XrefImpl
}

CHECK_FILE_CACHE = {}
CHECK_URL_CACHE = set()
CHECK_URLS = False

def main():
    global CHECK_URLS
    if len(sys.argv) < 2:
        print("ERROR: Submission folder not specified", file=sys.stderr)
        sys.exit(2)
    submissionFolder = sys.argv[1]
    if len(sys.argv) > 2 and sys.argv[2] == '+':
        CHECK_URLS = True
    print("INFO: Submission folder = '"+submissionFolder+"'")
    columns = loadColumns(submissionFolder)
    backgroundData = loadBackgroundData(submissionFolder)
    (submissions, storyTitles, tiers) = checkTemplates(submissionFolder, columns)
    checkColumns(submissionFolder, set(submissions.values()))
    for submission in sorted(submissions):
        checkSubmission(submissionFolder, submission, storyTitles.get(submission), tiers.get(submission), submissions, columns, backgroundData)


def checkTemplates(submissionFolder, columns):
    """ Check dashboard-CV-per-template.txt file,
    return submission -> template, story -> storyTitle, and submission -> tier mappings
    """
    submissions = {}
    storyTitles = {}
    tiers = {}
    print("INFO: Processing dashboard-CV-per-template.txt")
    with open(submissionFolder+"/dashboard-CV-per-template.txt",'r') as templates:
        rowIndex = 0
        storyRanks = []

        for template in templates:
            if rowIndex == 0:
                headers = template.strip().split('\t')
                if checkEmptyColumns(templates):
                    print('ERROR: Empty column in dashboard-CV-per-template.txt', file=sys.stderr)
                tierIndex = findIndex(headers, 'observation_tier')
                templateNameIndex = findIndex(headers, TEMPLATE_NAME_HEADER)
                summaryIndex = findIndex(headers, 'observation_summary')
                templateDescriptionIndex = findIndex(headers, 'template_description')
                submissionNameIndex = findIndex(headers, SUBMISSION_NAME_HEADER)
                submissionDescriptionIndex = findIndex(headers, 'submission_description')
                projectIndex = findIndex(headers, 'project')
                storyIndex = findIndex(headers, 'submission_story')
                storyRankIndex = findIndex(headers, 'submission_story_rank')
                centerIndex = findIndex(headers, 'submission_center')
                piIndex = findIndex(headers, 'principal_investigator')
            else:
                row = template.strip().split('\t')
                templateName = row[templateNameIndex]
                submissionName = row[submissionNameIndex]
                submissions[submissionName] = templateName
                tier = checkTier(row, tierIndex, submissionNameIndex)
                tiers[submissionName] = tier
                checkTemplateName(row, templateNameIndex, submissionNameIndex)
                checkSummary(row, summaryIndex, columns.get(templateName, set()), submissionNameIndex)
                checkTemplateDescription(row, templateDescriptionIndex, submissionNameIndex)
                checkSubmissionDescription(row, submissionDescriptionIndex, storyIndex, submissionNameIndex)
                checkSubmissionName(row, submissionNameIndex, templateNameIndex, submissionNameIndex)
                checkProject(row, projectIndex, submissionNameIndex)
                checkStory(row, storyIndex, submissionNameIndex)
                rank = checkStoryRank(row, storyIndex, storyRankIndex, submissionNameIndex)
                if (rank > 0):
                    storyRanks.append(rank)
                    storyTitles[submissionName] = row[templateDescriptionIndex]
                checkCenter(row, centerIndex, submissionNameIndex)
                checkPI(row, piIndex, centerIndex, submissionNameIndex)
            rowIndex = rowIndex + 1
        for i, rank in enumerate(sorted(storyRanks)):
            if rank != i+1:
                print('ERROR: Wrong story_rank @' + str(i+1) + ' != ' + str(rank), file=sys.stderr)
    return (submissions, storyTitles, tiers)


def checkTier(row, index, submissionNameIndex):
    if index >= 0:
        tier = row[index]
        if tier == '':
            print('ERROR: Missing observation_tier @ ' + row[submissionNameIndex], file=sys.stderr)
            return 0
        if tier != '1' and tier != '2' and tier != '3':
            print('ERROR: Wrong tier @' + row[submissionNameIndex] + ': ' + tier, file=sys.stderr)
            return 0
        return tier


def checkTemplateName(row, index, submissionNameIndex):
    if index >= 0:
        templateName = row[index]
        if templateName == '':
            print('ERROR: Missing template_name @' + row[submissionNameIndex], file=sys.stderr)


def checkSummary(row, index, columnSet, submissionNameIndex):
    if index >= 0:
        summary = row[index]
        if summary == '':
            print('ERROR: Missing observation_summary @' + row[submissionNameIndex], file=sys.stderr)
            return
        maxLen = MAX_LENGTH['observation_summary']
        if len(summary) >= maxLen:
            print('ERROR: The length of observation_summary @ ' + row[submissionNameIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if summary[-1] != '.':
            print('WARNING: The last symbol of observation_summary @ ' + row[submissionNameIndex] + ' is not period', file=sys.stderr)
        for fragment in summary.split('<'):
            if fragment.find('>') > 0:
                columnName = fragment[:fragment.find('>')]
                if columnName not in columnSet:
                    print('ERROR: Wrong summary column @' + row[submissionNameIndex] + ': ' + columnName, file=sys.stderr)


def checkTemplateDescription(row, index, submissionNameIndex):
    if index >= 0:
        templateDescription = row[index]
        if templateDescription == '':
            print('ERROR: Missing template_description @' + row[submissionNameIndex], file=sys.stderr)
        maxLen = MAX_LENGTH['template_description']
        if len(templateDescription) >= maxLen:
            print('ERROR: The length of template_description @ ' + row[submissionNameIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)


def checkSubmissionDescription(row, index, storyIndex, submissionNameIndex):
    if index >= 0:
        story = row[storyIndex]
        submissionDescription = row[index]
        if story == 'TRUE' and submissionDescription == '':
            print('ERROR: Missing submission_description @' + row[submissionNameIndex], file=sys.stderr)
        maxLen = MAX_LENGTH['submission_description']
        if len(submissionDescription) >= maxLen:
            print('ERROR: The length of submission_description @ ' + row[submissionNameIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)


def checkSubmissionName(row, index, templateNameIndex, submissionNameIndex):
    if index >= 0:
        submissionName = row[index]
        templateName = row[templateNameIndex]
        if submissionName == '':
            print('ERROR: Missing submission_name @' + row[submissionNameIndex], file=sys.stderr)
            return
        maxLen = MAX_LENGTH['submission_name']
        if len(submissionName) >= maxLen:
            print('ERROR: The length of submission_name @ ' + row[submissionNameIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if not submissionName[0:8].isdigit():
            print('ERROR: Wrong submission_name format @' + row[submissionNameIndex] + ': ' + submissionName, file=sys.stderr)
        if submissionName[8] != '-' or submissionName[9:9+len(templateName)] != templateName:
            print('WARNING: Wrong submission_name @' + row[submissionNameIndex] + ': ' + submissionName, file=sys.stderr)


def checkProject(row, index, submissionNameIndex):
    if index >= 0:
        project = row[index]
        if project == '':
            print('ERROR: Missing project @' + row[submissionNameIndex], file=sys.stderr)
        maxLen = MAX_LENGTH['project']
        if len(project) >= maxLen:
            print('ERROR: The length of project @ ' + row[submissionNameIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)


def checkStory(row, index, submissionNameIndex):
    if index >= 0:
        story = row[index]
        if story == '':
            print('ERROR: Missing submission_story @' + row[submissionNameIndex], file=sys.stderr)
            return
        if story != 'TRUE' and story != 'FALSE':
            print('ERROR: Wrong submission_story @' + row[submissionNameIndex] + ': ' + story, file=sys.stderr)


def checkStoryRank(row, storyIndex, index, submissionNameIndex):
    if index >= 0:
        story = row[storyIndex]
        storyRank = row[index]
        if storyRank == '':
            print('ERROR: Missing submission_story_rank @' + row[submissionNameIndex], file=sys.stderr)
            return 0
        if story == 'TRUE':
            try:
                rank = int(storyRank)
                if rank <= 0:
                    print('ERROR: Wrong submission_story_rank @' + row[submissionNameIndex] + ': ' + storyRank, file=sys.stderr)
                return rank
            except ValueError:
                print('ERROR: Wrong number submission_story_rank @' + row[submissionNameIndex] + ': ' + storyRank, file=sys.stderr)
                return 0
        if story == 'FALSE':
            if storyRank == '0':
                return 0
            print('ERROR: Wrong submission_story_rank @' + row[submissionNameIndex] + ': ' + storyRank, file=sys.stderr)
        return 0


def checkCenter(row, index, submissionNameIndex):
    if index >= 0:
        center = row[index]
        if center == '':
            print('ERROR: Missing submission_center @' + row[submissionNameIndex], file=sys.stderr)
            return
        if center not in CENTERS.keys():
            print('WARNING: Wrong submission_center @' + row[submissionNameIndex] + ': ' + center, file=sys.stderr)


def checkPI(row, index, centerIndex, submissionNameIndex):
    if index >= 0:
        center = row[centerIndex]
        pi = row[index].strip('"')
        if pi == '':
            print('ERROR: Missing principal_investigator @' + row[submissionNameIndex], file=sys.stderr)
            return
        if pi != CENTERS.get(center):
            print('WARNING: Wrong principal_investigator @' + row[submissionNameIndex] + ': ' + pi, file=sys.stderr)
        maxLen = MAX_LENGTH['principal_investigator']
        if len(pi) >= maxLen:
            print('ERROR: The length of principal_investigator @ ' + row[submissionNameIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)


def getColumn(row, index):
    if index >= 0:
        return row[index]
    return ''


def checkColumns(submissionFolder, templateSet):
    """ Check dashboard-CV-per-column.txt file
    """
    print("INFO: Processing dashboard-CV-per-column.txt")
    with open(submissionFolder+"/dashboard-CV-per-column.txt",'r') as columnsFile:
        rowIndex = 0
        for line in columnsFile:
            if rowIndex == 0:
                headers = line.strip().split('\t')
                if checkEmptyColumns(line):
                    print('ERROR: Empty column in dashboard-CV-per-column.txt', file=sys.stderr)
                templateIndex = findIndex(headers, TEMPLATE_NAME_HEADER)
                columnNameIndex = findIndex(headers, 'column_name')
                subjectIndex = findIndex(headers, SUBJECT)
                evidenceIndex = findIndex(headers, EVIDENCE)
                roleIndex = findIndex(headers, ROLE)
                mimetypeIndex = findIndex(headers, MIMETYPE)
                unitsIndex = findIndex(headers, NUMERICUNITS)
                descriptionIndex = findIndex(headers, DISPLAYTEXT)
            else:
                row = line.strip().split('\t')
                checkColumnTemplateName(row, templateIndex, templateSet, rowIndex)
                checkColumnName(row, columnNameIndex, rowIndex)
                checkColumnSubject(row, subjectIndex, rowIndex)
                checkColumnEvidence(row, evidenceIndex, rowIndex)
                checkColumnSubjectEvidence(row, subjectIndex, evidenceIndex, rowIndex)
                checkColumnRole(row, roleIndex, subjectIndex, evidenceIndex, templateIndex, rowIndex)
                checkColumnMimeType(row, mimetypeIndex, evidenceIndex, rowIndex)
                checkColumnUnits(row, unitsIndex, evidenceIndex, rowIndex)
                checkColumnDisplayText(row, descriptionIndex, rowIndex)
            rowIndex = rowIndex + 1


def checkColumnTemplateName(row, index, templateSet, rowIndex):
    if index >= 0:
        templateName = row[index]
        if templateName == '':
            print('ERROR: Missing template_name @ row ' + str(rowIndex), file=sys.stderr)
            return
        if templateName not in templateSet:
            print('ERROR: Wrong template_name @ row ' + str(rowIndex) + ': ' + templateName, file=sys.stderr)


def checkColumnName(row, index, rowIndex):
    if index >= 0:
        columnName = row[index]
        maxLen = min(MAX_LENGTH['subject_column_name'],MAX_LENGTH['evidence_column_name'])
        if len(columnName) >= maxLen:
            print('ERROR: The length of column_name @ row ' + str(rowIndex) + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if columnName == '':
            print('ERROR: Missing column_name @ row ' + str(rowIndex), file=sys.stderr)


def checkColumnSubject(row, index, rowIndex):
    if index >= 0:
        subject = row[index]
        if subject not in SUBJECT_TYPES:
            print('ERROR: Wrong subject @ row ' + str(rowIndex) + ': ' + subject, file=sys.stderr)


def checkColumnEvidence(row, index, rowIndex):
    if index >= 0:
        evidence = row[index]
        if evidence not in EVIDENCE_TYPES:
            print('ERROR: Wrong evidence @ row ' + str(rowIndex) + ': ' + evidence, file=sys.stderr)


def checkColumnSubjectEvidence(row, subjectIndex, evidenceIndex, rowIndex):
    if subjectIndex >= 0 and evidenceIndex:
        subject = row[subjectIndex]
        evidence = row[evidenceIndex]
        if subject == '':
            if evidence == '':
                print('ERROR: Missing subject or evidence @ row ' + str(rowIndex), file=sys.stderr)
        else:
            if evidence != '':
                print('ERROR: Both subject and evidence are specified @ row ' + str(rowIndex) + ': ' + subject + '/' + evidence, file=sys.stderr)


def checkColumnRole(row, index, subjectIndex, evidenceIndex, templateIndex, rowIndex):
    if index >= 0:
        role = row[index]
        maxLen = MAX_LENGTH['role']
        if len(role) >= maxLen:
            print('ERROR: The length of role @ row ' + str(rowIndex) + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if role == '':
            print('ERROR: '+row[templateIndex]+':Missing role @ row ' + str(rowIndex), file=sys.stderr)
            return
        key = row[subjectIndex]
        if key == '':
            key = row[evidenceIndex]
        if key in ROLES.keys() and role not in ROLES[key]:
            print('WARNING: '+row[templateIndex]+': '+key+' role does not conform to controled vocabulary @ row ' + str(rowIndex) + ': ' + role, file=sys.stderr)


def checkColumnMimeType(row, index, evidenceIndex, rowIndex):
    if index >= 0:
        mimeType = row[index]
        evidence = row[evidenceIndex]
        maxLen = min(MAX_LENGTH['mime_type'],MAX_LENGTH['attribute'])
        if len(mimeType) >= maxLen:
            print('ERROR: The length of mime_type @ row ' + str(rowIndex) + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if evidence == 'file' and mimeType == '':
            print('ERROR: Missing mime_type @ row ' + str(rowIndex), file=sys.stderr)
        elif evidence != 'file' and mimeType != '':
            print('WARNING: mime_type not allowed for '+evidence+' @ row ' + str(rowIndex) + ': ' + mimeType, file=sys.stderr)


def checkColumnUnits(row, index, evidenceIndex, rowIndex):
    if index >= 0:
        units = row[index]
        evidence = row[evidenceIndex]
        maxLen = min(MAX_LENGTH['units'],MAX_LENGTH['attribute'])
        if len(units) >= maxLen:
            print('ERROR: The length of units @ row ' + str(rowIndex) + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if evidence != 'numeric' and units != '':
            print('WARNING: numeric_units not allowed for '+evidence+' @ row ' + str(rowIndex) + ': ' + units, file=sys.stderr)


def checkColumnDisplayText(row, index, rowIndex):
    if index >= 0:
        displayText = row[index]
        maxLen = min(MAX_LENGTH['subject_display_text'],MAX_LENGTH['evidence_display_text'])
        if len(displayText) >= maxLen:
            print('ERROR: The length of display_text @ row ' + str(rowIndex) + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
        if displayText == '':
            print('ERROR: Missing display_text @ row ' + str(rowIndex), file=sys.stderr)


def checkSubmission(submissionFolder, submissionName, storyTitle, tier, submissions, columns, backgroundData):
    """Check submission file
    """
    templateName = submissions[submissionName]
    if templateName not in columns:
        print('ERROR: Missing column info: ' + str(templateName), file=sys.stderr)
        return
    columnMap = columns[templateName]
    columnsBySubm = columnsBySubmission(submissions, columns)
    print("INFO: Processing "+submissionName)
    CHECK_URL_CACHE.clear()
    submissionFile = submissionFolder+'/submissions/'+submissionName+'/'+submissionName+'.txt'
    if not os.path.isfile(submissionFile):
        print('ERROR: Missing submission: ' + str(submissionName), file=sys.stderr)
        return
    try:
        with open(submissionFile,'r') as submission:
            rowIndex = 0
            for line in submission:
                row = line.split('\t')
                if rowIndex == 0:
                    checkSubmissionHeaders(row, columnMap, submissionName)
                    headers = row
                if 1 <= rowIndex and rowIndex <= 6:
                    checkSubmissionMetadata(row, METADATA[rowIndex], headers, columnMap, submissionName)
                if rowIndex >= 7:
                    checkSubmissionData(row, headers, rowIndex, columnMap, submissionFolder, submissionName, templateName, storyTitle, tier, backgroundData, columnsBySubm)
                rowIndex = rowIndex+1
    except Exception as e:
        print('ERROR: Unable to load '+submissionFile+': ' + str(e), file=sys.stderr)


def columnsBySubmission(submissions, columns):
    """ create map submissionName -> set(columnNames)
    """
    columnsBySubm = {}
    for submission in submissions.keys():
        template = submissions[submission]
        columnsBySubm[submission] = set(columns[template].keys())
    return columnsBySubm


def checkSubmissionHeaders(row, columnMap, submissionName):
    columnSet = set(columnMap.keys())
    if row[0] != '':
        print('WARNING: '+submissionName+': first column header expected to be empty, found: ' + row[0], file=sys.stderr)
    if row[1] != SUBMISSION_NAME_HEADER:
        print('ERROR: '+submissionName+': second column header should be submission_name, found: ' + row[1], file=sys.stderr)
    if row[2] != SUBMISSION_DATE_HEADER:
        print('ERROR: '+submissionName+': third column header should be submission_date, found: ' + row[2], file=sys.stderr)
    if row[3] != TEMPLATE_NAME_HEADER:
        print('ERROR: '+submissionName+': fourth column header should be template_name, found: ' + row[3], file=sys.stderr)
    for header in row[4:]:
        header = header.strip()
        if header in columnSet:
            columnSet.remove(header)
        else:
            print('ERROR: '+submissionName+': column '+header+' not found in dashboard-CV-per-column.txt file', file=sys.stderr)
    for column in columnSet:
        print('ERROR: '+submissionName+': column '+column+' not found', file=sys.stderr)


def checkSubmissionMetadata(row, metadata, headers, columnMap, submissionName):
    columnSet = set(columnMap.keys())
    for header, value in zip(headers[4:],row[4:]):
        header = header.strip()
        value = value.strip().strip('"')
        if header in columnSet:
            if value != columnMap[header].get(metadata,'').strip('"'):
                print('WARNING: '+submissionName+': Different '+metadata+' in '+header+': '+value, file=sys.stderr)


def checkSubmissionData(row, headers, rowIndex, columnMap, submissionFolder, submissionName, templateName, storyTitle, tier, backgroundData, columns):
    if row[0] != '':
        print('WARNING: '+submissionName+': First column expected to be empty @ row ' + str(rowIndex), file=sys.stderr)
    for header, value in zip(headers[1:],row[1:]):
        header = header.strip()
        value = value.strip()
        if value == '':
            print('WARNING: '+submissionName+': Missing value for '+header+' @ row ' + str(rowIndex), file=sys.stderr)
        else:
            if header == SUBMISSION_NAME_HEADER and value != submissionName:
                print('ERROR: '+submissionName+': Wrong submission_name @ row ' + str(rowIndex) + ': ' + value, file=sys.stderr)
            if header == SUBMISSION_DATE_HEADER:
                datestamp = value[0:4]+value[5:7]+value[8:10]
                if len(value) != 10 or value[4] != '.' or value[7] != '.' or not datestamp.isdigit():
                    print('ERROR: '+submissionName+': Wrong submission_date format @ row ' + str(rowIndex) + ': ' + value, file=sys.stderr)
                if datestamp != submissionName[0:8]:
                    print('ERROR: '+submissionName+': submission_name timestamp does not match submission_date @ row ' + str(rowIndex) + ': ' + datestamp, file=sys.stderr)
            if header == TEMPLATE_NAME_HEADER and value != templateName:
                print('ERROR: '+submissionName+': Wrong template_name @ row ' + str(rowIndex) + ': ' + value, file=sys.stderr)
            if header in columnMap:
                if columnMap[header][EVIDENCE] == 'file':
                    checkFileValue(value, rowIndex, submissionFolder, submissionName)
                    if columnMap[header][MIMETYPE] == 'text/html' and columnMap[header][ROLE] == 'written':
                        checkStoryFile(value, storyTitle, tier, rowIndex, submissionFolder, submissionName, columns[submissionName])
                if columnMap[header][EVIDENCE] == 'numeric':
                    checkNumericValue(value, rowIndex, submissionName)
                if columnMap[header][EVIDENCE] == 'url':
                    checkUrlValue(value.strip('"'), rowIndex, submissionName, columns)
                if columnMap[header][SUBJECT] != '':
                    checkSubjectData(value, columnMap[header][SUBJECT], rowIndex, submissionName, backgroundData)


def checkFileValue(value, rowIndex, submissionFolder, submissionName):
    if value[0:14] != './submissions/':
        print('ERROR: '+submissionName+': Wrong file prefix @ row ' + str(rowIndex) + ': ' + value, file=sys.stderr)
    (folder, filename) = os.path.split(submissionFolder+value[1:])
    maxLen = MAX_LENGTH['file_path']
    if len(folder) >= maxLen:
        print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': The length of file path exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
    maxLen = MAX_LENGTH['file_name']
    if len(filename) >= maxLen:
        print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': The length of file name exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
    if folder not in CHECK_FILE_CACHE:
        if os.path.isdir(folder):
            CHECK_FILE_CACHE[folder] = set(os.listdir(folder))
        else:
            CHECK_FILE_CACHE[folder] = set()
            print('ERROR: '+submissionName+': Missing folder @ row ' + str(rowIndex) + ': ' + folder, file=sys.stderr)
    if filename not in CHECK_FILE_CACHE[folder]:
        print('ERROR: '+submissionName+': Missing file or wrong file name @ row ' + str(rowIndex) + ': ' + value, file=sys.stderr)


def checkStoryFile(value, storyTitle, tier, rowIndex, submissionFolder, submissionName, columns):
    filename = submissionFolder+value[1:]
    parser = StoryParser(filename)
    parser.parseStory()
    if parser.storyTitle.strip() != storyTitle:
        print('ERROR: Story title for '+submissionName+': does not match template_description @ row ' + str(rowIndex) + ': ' + str(parser.storyTitle)+'/'+str(storyTitle), file=sys.stderr)
    if parser.tier != 0 and parser.tier != tier:
        print('ERROR: Story tier for '+submissionName+': does not match template tier @ row ' + str(rowIndex) + ': ' + str(parser.tier)+'/'+str(tier), file=sys.stderr)
    for link in parser.links:
        if link != 'submission_center' and link not in columns:
            print('ERROR: Story link for '+submissionName+': does not match column name @ row ' + str(rowIndex) + ': ' + link, file=sys.stderr)


def checkNumericValue(value, rowIndex, submissionName):
    try:
        numeric = float(value)
    except ValueError as e:
        print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': ' + str(e), file=sys.stderr)


def checkUrlValue(value, rowIndex, submissionName, columns):

    maxLen = MAX_LENGTH['url']
    if len(value) >= maxLen:
        print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': The length of URL exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
    if value[0:6] == 'ftp://':
        # check anonymous username in ftp links
        if value[0:22] != 'ftp://anonymous:guest@':
            print('WARNING: ftp link '+value+' is missing anonymous username', file=sys.stderr)
    if value[0:8] == 'https://' or value[0:7] == 'http://' or value[0:6] == 'ftp://':
        # check the site
        if CHECK_URLS and value not in CHECK_URL_CACHE:
            CHECK_URL_CACHE.add(value)
            try:
                code = urlopen(quote(value, safe='&:/?=%#'), data=None, timeout=5).code
                #code = urlopen(value, data=None, timeout=5).code
                if code != None and code >= 400:
                    print('WARNING: Unable to access '+value+', code: ' + str(code), file=sys.stderr)
            except Exception as e:
                print('WARNING: Unable to access '+value+': ' + str(e), file=sys.stderr)
    else:
        # check links to other submissions
        valueSplit = value.split(':')
        linkSubmission = valueSplit[0]
        if linkSubmission not in columns.keys():
            print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': wrong URL format "' + value + '"', file=sys.stderr)
            return
        if len(valueSplit) <= 1:
            return
        else:
            linkQuery = valueSplit[1]
            if len(valueSplit) > 2:
                print('WARNING: '+submissionName+': multiple queries in lower-tier links '+value+' @ row ' + str(rowIndex), file=sys.stderr)
            for query in linkQuery.split('&'):
                linkColumn = query.split('=')[0]
                if linkColumn not in columns[linkSubmission]:
                    print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': wrong URL format "' + value + '"', file=sys.stderr)


def checkSubjectData(value, subject, rowIndex, submissionName, backgroundData):
    subjectSet = backgroundData.get(subject, None)
    if subjectSet != None:
        if value.strip('"') not in subjectSet:
            print('ERROR: '+submissionName+' @ row ' + str(rowIndex) + ': ' + subject +' not found in background data: '+value, file=sys.stderr)


def findIndex(headers, column):
    for i, header in enumerate(headers):
        if header == column:
            return i
    print('ERROR: missing column: '+column, file=sys.stderr)
    return -1


def checkEmptyColumns(headers):
    for i, header in enumerate(str(headers).split('\t')):
        if header == '':
            return True
    return False


def loadColumns(submissionFolder):
    columns = {}
    print("INFO: Loading column names from dashboard-CV-per-column.txt")
    with open(submissionFolder+"/dashboard-CV-per-column.txt",'r') as columnsFile:
        #print(columnsFile.encoding)
        rowIndex = 0
        for line in columnsFile:
            #print(line)
            if rowIndex == 0:
                headers = line.strip().split('\t')
                templateIndex = findIndex(headers, 'template_name')
                columnIndex = findIndex(headers, 'column_name')
                if templateIndex < 0 or columnIndex < 0:
                    return columns
                subjectIndex = findIndex(headers, SUBJECT)
                evidenceIndex = findIndex(headers, EVIDENCE)
                roleIndex = findIndex(headers, ROLE)
                mimetypeIndex = findIndex(headers, MIMETYPE)
                unitsIndex = findIndex(headers, NUMERICUNITS)
                descriptionIndex = findIndex(headers, DISPLAYTEXT)
            else:
                row = line.strip().split('\t')
                templateName = row[templateIndex]
                columnName = row[columnIndex]
                map = {
                    SUBJECT: getColumn(row, subjectIndex),
                    EVIDENCE: getColumn(row, evidenceIndex),
                    ROLE: getColumn(row, roleIndex),
                    MIMETYPE: getColumn(row, mimetypeIndex),
                    NUMERICUNITS:getColumn(row, unitsIndex),
                    DISPLAYTEXT: getColumn(row, descriptionIndex)
                }

                columnMap = columns.get(templateName, {})
                if columnName in columnMap.keys():
                    print('ERROR: column '+columnName+' is present twice in ' + templateName, file=sys.stderr)
                if columnName == 'dummy':
                    print('ERROR: column '+columnName+' will be ignored ' + templateName, file=sys.stderr)
                columnMap[columnName] = map
                columns[templateName] = columnMap
            rowIndex = rowIndex + 1
    return columns


def loadBackgroundData(submissionFolder):
    """ Load background data, return map to sets of background data
    """
    backgroundData = {}
    backgroundData['animal_model'] = loadAnimalModels(submissionFolder)
    backgroundData['cell_sample'] = loadCellSamples(submissionFolder)
    checkCellAnnotations(submissionFolder)
    backgroundData['compound'] = loadCompounds(submissionFolder)
    backgroundData['gene'] = loadGenes(submissionFolder)
    backgroundData['protein'] = set()
    backgroundData['shrna'] = loadShRNA(submissionFolder)
    backgroundData['tissue_sample'] = loadTissueSamples(submissionFolder)
    backgroundData['transcript'] = set()

    maxLen = MAX_LENGTH['display_name']
    for subject in backgroundData:
        print('INFO: Checking '+subject+' string lengths')
        for name in backgroundData[subject]:
            if len(name) >= maxLen:
                print('ERROR: The length of '+subject+' "' + name + '" exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
    return backgroundData


def loadAnimalModels(submissionFolder):
    try:
        animalModels = set()
        animalModelsFile = submissionFolder+'/subject_data/animal_model/animal_model.txt'
        print('INFO: Loading '+animalModelsFile)
        rowIndex = 0
        with open(animalModelsFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'animal_model_name')
                else:
                    animalModels.add(row[nameColumnIndex])
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(len(animalModels))+' animal model names')
        return animalModels
    except Exception as e:
        print('ERROR: Unable to load animal_model background data: ' + str(e), file=sys.stderr)
        return None


def loadCellSamples(submissionFolder):
    try:
        cellSamples = set()
        cellSamplesFile = submissionFolder+'/subject_data/cell_sample/cell_sample_name.txt'
        print('INFO: Loading '+cellSamplesFile)
        rowIndex = 0
        with open(cellSamplesFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'CELL_SAMPLE_NAME')
                else:
                    cellSamples.add(row[nameColumnIndex])
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(len(cellSamples))+' cell sample names')
        return cellSamples
    except Exception as e:
        print('ERROR: Unable to load cell_sample background data: ' + str(e), file=sys.stderr)
        return None

def checkCellAnnotations(submissionFolder):
    checkCellAnnotationNames(submissionFolder)
    checkCellAnnotationSources(submissionFolder)
    checkCellAnnotationTypes(submissionFolder)


def checkCellAnnotationNames(submissionFolder):
    try:
        cellAnnotationFile = submissionFolder+'/subject_data/cell_sample/cell_anno_name.txt'
        print('INFO: Loading '+cellAnnotationFile)
        rowIndex = 0
        maxLen = MAX_LENGTH['display_name']
        with open(cellAnnotationFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    idColumnIndex = findIndex(row,'CELL_ANNO_NAME_ID')
                    nameColumnIndex = findIndex(row,'CELL_ANNO_NAME')
                else:
                    annotation = row[nameColumnIndex]
                    if len(annotation) >= maxLen:
                        print('ERROR: The length of annotation for CELL_ANNO_NAME_ID=' + row[idColumnIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(rowIndex-1)+' cell annotation names')
    except Exception as e:
        print('ERROR: Unable to load cell_sample annotation data: ' + str(e), file=sys.stderr)


def checkCellAnnotationSources(submissionFolder):
    try:
        cellAnnotationSourceFile = submissionFolder+'/subject_data/cell_sample/cell_anno_source.txt'
        print('INFO: Loading '+cellAnnotationSourceFile)
        rowIndex = 0
        maxLen = MAX_LENGTH['annotation_source']
        with open(cellAnnotationSourceFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    sourceColumnIndex = findIndex(row,'cell_anno_source')
                else:
                    source = row[sourceColumnIndex]
                    if len(source) >= maxLen:
                        print('ERROR: The length of annotation source "' + source + '" exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(rowIndex-1)+' cell annotation sources')
    except Exception as e:
        print('ERROR: Unable to load cell_sample annotation source data: ' + str(e), file=sys.stderr)


def checkCellAnnotationTypes(submissionFolder):
    try:
        cellAnnotationTypeFile = submissionFolder+'/subject_data/cell_sample/cell_anno_type.txt'
        print('INFO: Loading '+cellAnnotationTypeFile)
        rowIndex = 0
        maxLen = MAX_LENGTH['annotation_type']
        with open(cellAnnotationTypeFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    typeColumnIndex = findIndex(row,'CELL_ANNO_TYPE')
                else:
                    type = row[typeColumnIndex]
                    if len(type) >= maxLen:
                        print('ERROR: The length of annotation type "' + type + '" exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(rowIndex-1)+' cell annotation types')
    except Exception as e:
        print('ERROR: Unable to load cell_sample annotation type data: ' + str(e), file=sys.stderr)


def loadCompounds(submissionFolder):
    try:
        compounds = set()
        compoundFile = submissionFolder+'/subject_data/compound/Compounds.txt'
        imageFolder = submissionFolder+'/subject_data/compound/structures'
        images = set(os.listdir(imageFolder))
        print('INFO: Loading '+compoundFile)
        rowIndex = 0
        maxLen = MAX_LENGTH['smiles']
        with open(compoundFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    idColumnIndex = findIndex(row,'CPD_ID')
                    nameColumnIndex = findIndex(row,'CPD_PRIMARY_NAME')
                    structureFileColumnIndex = findIndex(row,'STRUCTURE_FILE')
                    smilesColumnIndex = findIndex(row,'SMILES')
                else:
                    compounds.add(row[nameColumnIndex])
                    if row[structureFileColumnIndex] not in images:
                        print('WARNING: Compounds.txt: image file not found: ' + row[structureFileColumnIndex], file=sys.stderr)
                    smiles = row[smilesColumnIndex]
                    if len(smiles) >= maxLen:
                        print('ERROR: The length of SMILES for CPD_ID=' + row[idColumnIndex] + ' exceeds threshold ('+str(maxLen)+')', file=sys.stderr)
                rowIndex = rowIndex + 1
        compoundFile = submissionFolder+'/subject_data/compound/CompoundSynonyms.txt'
        print('INFO: Loading '+compoundFile)
        rowIndex = 0
        with open(compoundFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'CPD_NAME')
                else:
                    compounds.add(row[nameColumnIndex])
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(len(compounds))+' compound names')
        return compounds
    except Exception as e:
        print('ERROR: Unable to load compound background data: ' + str(e), file=sys.stderr)
        return None


def loadGenes(submissionFolder):
    genes = set()
    humanGenes = loadSpeciesGenes(submissionFolder, 'Homo_sapiens', '9606')
    if (humanGenes != None):
        genes = genes | humanGenes
    mouseGenes = loadSpeciesGenes(submissionFolder, 'Mus_musculus', '10090')
    if (mouseGenes != None):
        genes = genes | mouseGenes
    print('INFO: Loaded '+str(len(genes))+' gene names')
    return genes

def loadSpeciesGenes(submissionFolder, species, taxid):
    try:
        genes = set()
        geneFile = submissionFolder+'/subject_data/gene/'+species+'.gene_info.gz'
        print('INFO: Loading '+geneFile)
        rowIndex = 0
        geneIdMaxLen = MAX_LENGTH['gene_id']
        symbolMaxLen = MAX_LENGTH['gene_symbol']
        with gzip.open(geneFile,'rt') as input:
            for line in input:
                if (rowIndex > 0):
                    row = line.strip().split('\t')
                    if (row[0] == taxid):
                        geneid = row[1]
                        genes.add(geneid)
                        if len(geneid) >= geneIdMaxLen:
                            print('ERROR: The length of gene ID "' + geneid + '" exceeds threshold ('+str(geneIdMaxLen)+')', file=sys.stderr)
                        symbol = row[2]
                        if len(symbol) >= symbolMaxLen:
                            print('ERROR: The length of gene symbol "' + symbol + '" exceeds threshold ('+str(symbolMaxLen)+')', file=sys.stderr)
                        genes.add(symbol)
                        synonyms = row[4]
                        if (synonyms != '-'):
                            for synonym in synonyms.split('|'):
                                genes.add(synonym)
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(len(genes))+' '+species+' gene names')
        return genes
    except Exception as e:
        print('ERROR: Unable to load '+species+' gene background data: ' + str(e), file=sys.stderr)
        return None


def loadShRNA(submissionFolder):
    try:
        shrna = set()
        shrnaFile = submissionFolder+'/subject_data/shrna/trc-shrnas-filter.txt'
        print('INFO: Loading '+shrnaFile)
        rowIndex = 0
        with open(shrnaFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'shRNA_id')
                else:
                    shrna.add(row[nameColumnIndex])
                rowIndex = rowIndex + 1

        seqMaxLen = MAX_LENGTH['sequence']
        reagentMaxLen = MAX_LENGTH['reagent_name']
        trcnFile = submissionFolder+'/subject_data/shrna/trc_public.05Apr11.txt'
        print('INFO: Loading '+trcnFile)
        rowIndex = 0
        with open(trcnFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    cloneIdIndex = findIndex(row,'cloneId')
                    sequenceIndex = findIndex(row,'targetSeq')
                else:
                    cloneId = row[cloneIdIndex]
                    if cloneId in shrna:
                        if len(cloneId) >= reagentMaxLen:
                            print('ERROR: The length of shRNA name "' + cloneId + '" exceeds threshold ('+str(reagentMaxLen)+')', file=sys.stderr)
                        sequence = row[sequenceIndex]
                        shrna.add(sequence)
                        if len(sequence) >= seqMaxLen:
                            print('ERROR: The length of shRNA sequence "' + sequence + '" exceeds threshold ('+str(seqMaxLen)+')', file=sys.stderr)
                rowIndex = rowIndex + 1

        sirnaFile = submissionFolder+'/subject_data/sirna/siRNA_reagents.txt'
        print('INFO: Loading '+sirnaFile)
        rowIndex = 0
        with open(sirnaFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'reagent_name')
                    sequenceIndex = findIndex(row,'target_sequence')
                else:
                    reagentName = row[nameColumnIndex]
                    shrna.add(reagentName)
                    if len(reagentName) >= reagentMaxLen:
                        print('ERROR: The length of siRNA name "' + reagentName + '" exceeds threshold ('+str(reagentMaxLen)+')', file=sys.stderr)
                    sequence = row[sequenceIndex]
                    if len(sequence) >= seqMaxLen:
                        print('ERROR: The length of siRNA sequence "' + sequence + '" exceeds threshold ('+str(seqMaxLen)+')', file=sys.stderr)
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(len(shrna))+' shrna names')
        return shrna
    except Exception as e:
        print('ERROR: Unable to load shrna background data: ' + str(e), file=sys.stderr)
        return None


def loadTissueSamples(submissionFolder):
    try:
        tissueSample = set()
        tissueSampleFile = submissionFolder+'/subject_data/tissue_sample/NCI_thesaurus_terms.txt'
        print('INFO: Loading '+tissueSampleFile)
        rowIndex = 0
        with open(tissueSampleFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'display_name')
                else:
                    tissueSample.add(row[nameColumnIndex])
                rowIndex = rowIndex + 1
        tissueSampleFile = submissionFolder+'/subject_data/tissue_sample/NCI_thesaurus_synonyms.txt'
        print('INFO: Loading '+tissueSampleFile)
        rowIndex = 0
        with open(tissueSampleFile,'r') as input:
            for line in input:
                row = line.strip().split('\t')
                if rowIndex == 0:
                    nameColumnIndex = findIndex(row,'synonym')
                else:
                    tissueSample.add(row[nameColumnIndex])
                rowIndex = rowIndex + 1
        print('INFO: Loaded '+str(len(tissueSample))+' tissue_sample names')
        return tissueSample
    except Exception as e:
        print('ERROR: Unable to load tissue_sample background data: ' + str(e), file=sys.stderr)
        return None


class StoryParser(HTMLParser):

    def __init__(self, filename):
        super().__init__()
        self.filename = filename
        self.titleTag = False
        self.h1Tag = False
        self.pTag = False
        self.pTagCount = 0
        self.storyTitle = None
        self.tier = 0
        self.links = []

    def handle_starttag(self, tag, attributes):
        self.titleTag = tag == 'title'
        self.h1Tag = tag == 'h1'
        if tag == 'a':
            href = None
            target = None
            for attribute in attributes:
                if attribute[0] == 'href':
                    href = attribute[1]
                if attribute[0] == 'target':
                    target = attribute[1]
            if target != '_blank':
                print('ERROR: Wrong or missing target ('+str(target)+') of href attribute "'+href+'" in: '+self.filename, file=sys.stderr)
            if href == None:
                print('ERROR: Missing href attribute in a link: '+self.filename, file=sys.stderr)
            else:
                if href[0] == '#':
                    href = href[1:]
                else:
                    print('ERROR: Wrong format of href attribute "'+href+'" in: '+self.filename, file=sys.stderr)
                self.links.append(href)
        if tag == 'p':
            self.pTag = True
            self.pTagCount = self.pTagCount + 1


    def handle_data(self, data):
        if self.titleTag:
            if self.storyTitle == None:
                self.storyTitle = data
            else:
                print('WARNING: Multiple story titles in story: '+self.filename, file=sys.stderr)

        if self.h1Tag and self.storyTitle != data:
            (folder, name) = os.path.split(self.filename)
            print('WARNING: Story title ('+self.storyTitle+') does not match story header in: '+name, file=sys.stderr)

        if self.pTag and (self.pTagCount == 2 or self.pTagCount == 3):
            if data[0:6] == 'Tier: ':
                self.tier = data[6]
        self.h1Tag = False
        self.titleTag = False
        self.pTag = False


    def parseStory(self):
        (folder, name) = os.path.split(self.filename)
        print("INFO: Loading HTML story: "+name)
        self.feed(open(self.filename).read())
        if self.tier == 0:
            print("WARNING: Missing tier in: "+name)


if __name__ == '__main__':
    main()


