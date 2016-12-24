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
META_DATA_INFO_PATH= os.path.join(ROOT_DIR, 'metadataInfo.json')

print(EXAMPLE_DIR)

def get_metadata():
    ''' function purpose: To return the metadata informatation needed to run the
                          to showcase the tool in rise4fun. Please look to the website
                          http://www.rise4fun.com/dev for more information about the
                          metadata.
    
    Returns:
            returns a string containing all the metadata information in a json format
            Example : {"InstitutionImageUrl": "http://openjml.cs.ucf.edu/images/jml-logo-small.png",
                       "DisplayName": "Checker Framework Demo",
                       "Email": "werner.dietl@uwaterloo.ca",
                       "MimeType": "text/plain",
                       "Version": "1.0", "SupportsLanguageSyntax": false}
    '''

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


    # Replace the the value of the key 'Samples' with the one or more dicionaries containing
    # the name and source of the sample.
    data['Samples']=samples

    # convert the the dictornary data into a string with Json format
    json_data = json.dumps(data)
    return json_data

def get_specific( key ):
    ''' function purpose: To return the value of a specific key of the json file metadataInfo.json.
        Args: key: the name of the key which we want its value.
        Returns:
                returns a string containing of the specified key inside the metadataInfo.json file
        Example :
                get_specific("Name")

                Output:
                Checker_Framework
        '''

    # dump all the information of metadataInfo into data
    with open(META_DATA_INFO_PATH,'r') as f:
        data = json.load(f)
    # return the a value of metadata depending on its key. Example if key = 'Name'
    # the return value is Checker_Framework.
    # if key does not exist inside the data dictonary then we will print 'Unable to retrieve value of "key" '
    # and return nothing
    try:
        return data[key]
    except KeyError:
        print('Unable to retrieve value of',key)
        return ""




