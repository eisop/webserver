# The purpose of this module is to return the metadata information in a json format or to get a specific part of information of the metadata.
# we can get the metadata as a json format by calling the function getMetadata
# additionally we can get any specific information of the metadata by calling the method getSpecific and passing the name of the value you want to get.
# Example getSpecific('Name') return Checker_Framework.

import json
def getMetaData():

    
    # dump the data inside MetadataInfo.json into data
    with open('MetadataInfo.json','r') as f:
        data = json.load(f)

    samples=[]


    # gets the code inside the samples and created a dictornary containing the Name of the sample and it source code
    # run this loop for each dictionary inside data['Samples']
    for sample in data['Samples']:


        # the path of sample = sample['Path']
        # input the source of sample into x
        with open(sample['Path'],'r') as x:

            # create a dictonary with the key = Name, value = sample['Name'] and key = Source, value =  x.read()
            sampledict={"Name":sample['Name'],"Source":x.read()}

        samples.append(sampledict)


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
    with open('MetadataInfo.json','r') as f:
        data = json.load(f)
        #return the a value of metadata depending on its key. Example if key = 'Name' the return value is Checker_Framework.
        return data[key]




