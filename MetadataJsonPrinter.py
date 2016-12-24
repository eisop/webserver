# The purpose of this module is to return the metadata information in a json format or to
# get a specific part of information of the metadata.we can get the metadata as a json format
# by calling the function getMetadata additionally we can get any specific information of the
#  metadata by calling the method getSpecific and passing the name of the value you want to get.
# Example getSpecific('Name') return Checker_Framework.

import json
import os.path
#global constants
#ROOT_DIR = connanical path of the directory where thsi file is locaded it.
#EXAMPLE_DIR = connanical path of the examples directory.
#META_DATA_INFO_PATH = connanical path of the examples directory.
ROOT_DIR = os.path.dirname(os.path.realpath(__file__))
EXAMPLE_DIR = os.path.join(ROOT_DIR, "static",'examples')
META_DATA_INFO_PATH= os.path.join(ROOT_DIR, 'MetadataInfo.json')

print(EXAMPLE_DIR)

def getMetaData():
    # dump the data inside MetadataInfo.json into data
    with open(META_DATA_INFO_PATH,'r') as f:
        data = json.load(f)

    samples = []
    # gets the code inside the samples and created a dictornary containing the Name of the sample and it source code
    # run this loop for each dictionary inside data['Samples']
    for sample in data['Samples']:
        # get the path of the sample by using the os.path.join() function and the CheCheckerName and FileName.
        # which are both contained inside Samples
        sample_path = os.path.join(EXAMPLE_DIR, sample['CheckerName'], sample['FileName'])


        # input the source of sample into x
        with open(sample_path,'r') as x:

            # create a dictonary with the key = Name, value = sample['Name'] and key = Source, value =  x.read()
            sample_dict={"Name":sample['Name'],"Source":x.read()}

        samples.append(sample_dict)


    # this massive dictionary contains all the inforation that rize4fun needs as metadata
    MetadataJson = {
    "Name": data['Name'],
    "DisplayName": data['DisplayName'],
    "Version": data['Version'],
    "Email": data['Email'],
    "SupportEmail": data['SupportEmail'],
    "TermsOfUseUrl": data['TermsOfUseUrl'],
    "PrivacyUrl": data['PrivacyUrl'],
    "Institution": data['Institution'],
    "InstitutionnstitutionUrl": data['InstitutionnstitutionUrl'],
    "InstitutionImageUrl": data['InstitutionImageUrl'],
    "MimeType": data['MimeType'],
    "SupportsLanguageSyntax": data['SupportsLanguageSyntax'],
    "Title": data['Title'],
    "Description": data['Description'],
    "Question": data['Question'],
    "Url": data['Url'],
    "VideoUrl": data['VideoUrl'],
    "DisableErrorTable": data['DisableErrorTable'],
    "Samples": samples,
    "Tutorials":data['Tutorials']
      }

    # convert the the dictornary MedataJson into a Json object  
    jsonData = json.dumps(MetadataJson)
    return jsonData

def getSpecific( key ):
    # dump all the inforaton of MetadataInfo into data
    with open(META_DATA_INFO_PATH,'r') as f:
        data = json.load(f)
        #return the a value of metadata depending on its key. Example if key = 'Name' the return value is Checker_Framework.
        return data[key]


