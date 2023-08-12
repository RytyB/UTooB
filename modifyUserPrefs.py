from googleapiclient.discovery import build
import json

secretsFile = open('apiKey.txt', 'r')
api_key = secretsFile.readlines()[0]
secretsFile.close()


class SetUserPrefs:

    def __init__(self):

        userFile = open('./videoInfo/src/main/resources/userPrefs.json', 'r')
        prefs = json.load(userFile)
        userFile.close()
               
        channelNames = []
        channelIds = []
        for i in range(0, len(prefs["channel"])):
            channelNames.append( prefs["channel"][i]["name"] )
            channelIds.append( prefs["channel"][i]["channelId"] )

        self.channelNames = channelNames
        self.channelIds = channelIds
        self.jsonPrefs = prefs

    def writeCreator(self, creator):       
        '''
        Program reads all preferences into memory, deletes .json file, then
            overwrites it entirely with modified preferences. Be careful while
            editing.
        '''

        outputFile = open('./videoInfo/src/main/resources/userPrefs.json', 'w')
        outputFile.truncate()

        if (creator.channelId  in self.channelIds):
            print('Creator is already whitelisted. Exiting program.')
            exit()

        creatorAsDict = {"name":creator.name, "channelId":creator.channelId}

        newPrefs = self.jsonPrefs.copy()
        newPrefs['channel'].append(creatorAsDict)

        json.dump( newPrefs , outputFile )
        outputFile.close()
        print('New creator written to whitelist.')

        return SetUserPrefs()
    
    def removeCreator(self, creator):
        '''
        Program reads all preferences into memory, deletes .json file, then
            overwrites it entirely with modified preferences. Be carefule while
            editing and debugging.
        '''

        outputFile = open('./videoInfo/src/main/resources/userPrefs.json', 'w')
        outputFile.truncate()

        if not (creator.channelId in self.channelIds):
            print('Creator is not in whitelist. Exiting program.')
            exit()

        locIndex = []
        newPrefs = self.jsonPrefs.copy()
        for i in range(0, len(newPrefs["channel"]) ):
            if creator.channelId == newPrefs["channel"][i]["channelId"]:
                locIndex.append(i)

        for index in locIndex:
            newPrefs['channel'].pop(index)

        json.dump( newPrefs, outputFile )
        outputFile.close()
        print('Creator removed from whitelist.')

        self.__init__()

        return SetUserPrefs()
    
    
class Creator:

    def findCreator(self, searchPhrase):
        '''
        Queries the user from command line to find a channel from 
            text search input. Returns a dictionary object containing
            channel from API call
        '''
        youtube = build('youtube', 'v3', developerKey=api_key)

        request = youtube.search().list(
            part = 'snippet',
            q = searchPhrase,
            type = 'channel'
        )
        response = request.execute()
        results = response['items']

        userResponse = 'n'
        i = -1
        while ( userResponse != 'y' and i < len( results )-1 ):
            i+=1

            print('====================================================')
            print( results[i]['snippet']['title'] )
            print('\n')
            print( results[i]['snippet']['description'] )
            print('\n')

            userResponse = input("Is this the channel you're looking for? (y/n)\n")

            channel = results[i]

        print()
        if (i == len( results )-1):
            return 1

        return channel
    
    def __init__(self, searchPhrase):

        creator = self.findCreator(searchPhrase)

        if (type(creator) is int):
            print('Creator not found. Exiting program.')
            exit()

        self.name = creator['snippet']['title']
        self.channelId = creator['snippet']['channelId']
    

if __name__ == '__main__':

    import sys

    if len( sys.argv ) == 3:
        searchString = sys.argv[2]
        newChannel = Creator(searchString)

    if sys.argv[1] == 'add':
        myPrefs = SetUserPrefs().writeCreator(newChannel)
        print(myPrefs.channelNames, '\n')
    elif sys.argv[1] == 'remove':
        myPrefs = SetUserPrefs().removeCreator(newChannel)
        print(myPrefs.channelNames, '\n')
    elif sys.argv[1] == 'list':
        myPrefs = SetUserPrefs()
        print(myPrefs.channelNames, '\n')
    else:
        print("Usage:\npython3 modifyUserPrefs.py opt \"Search-term\" ")


    

    

    

    


    
