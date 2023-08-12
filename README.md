<h1> UTooB </h1>

<h2> Motivation </h2>
<p> After I wasted about 5,000 hours mindlessly watching YouTube shorts, I dreamt up this little project to take
more active control of what I'm watching. That lead me to spend 5,000 hours mindlessly debugging and fighting OAuth credentials. 
UTooB is designed to be a healthier user experience for watching YouTube videos. The user specifies a list of 
channels and is only presented with those channels. As such it should force the user (me) to be more intentional
about the way that they spend their time watching videos. </p>

<h2> Credentials </h2>
<p> The user needs two credentials for full functionality of the program. The first is a Google OAuth 2.0. Since the
app is not published, the user should be sure to add their email as a test user. The second needed credential is an API
key for python script to modify whitelisted channels. Both can be obtained from the Google Developer page by making a new
project and enabling the YouTube Data API v3.
<br> https://console.cloud.google.com/cloud-resource-manager 
<br>
<br> Download the OAuth 2.0 as a json and rename it and relocate it as the following within the project directory:
<br> <i> ./videoInfo/src/main/resources/client_secret.json </i> 
<br>
<br> Copy the API key into a new text file called apiKey.txt without any formatting. Move the text file to the project home directory.
<br> <i> ./apiKey.txt </i> </p>

<h2> Changing Whitelist of Creators </h2>
<h3> Setup </h3>
<p> The user needs a working python installation and the json and YouTube API libraries installed. The requisite libraries can
be installed with the following commands:
<br> <i> python3 -m pip install --upgrade google-api-python-client </i>
<br>
<br> The whitelist of creators is stored in a file called <i>./videoInfo/src/main/resources/userPrefs.json </i>. After cloning the sample 
the user will need to make a copy of the file <i> userPrefs1.json </i> in the same directory. <i> userPrefs1.json </i> Should 
not be deleted as the python script tends to delete the contents of the whitelist when it has a bug and <i> userPrefs1.json </i>
can serve as a backup.
<h3> Modification </h3>
Users can add a creator, delete a creator, or see what creators are whitelisted by running the <i> modifyUserPrefs.py </i> script 
in the main project directory. In theory, the whitelist can be manually altered to avoid using python, but this is cumbersome. 
<br> <i> python3 modifyUserPrefs.py list </i>
<br> This command will print a list of the whitelisted channel names to console. 
<br>
<br> <i> python3 modifyUserPrefs.py add "channel name" </i>
<br> This command will search for the channel using the user provided string and if the channel is found, it will be writted to the whitelist.
<br>
<br> <i> python3 modifyUserPrefs.py remove "channel name" </i>
<br> In the case, the channel name must be exact. The command will look for the channel in the whitelist and remove it if it is found.

<h2> Running the Application </h2>
Once the inordinate amount of setup is complete, the sample should run fairly easily using the gradle wrapper batch file (<i> ./gradlew.bat </i>) on Windows or the normal wrapper (<i> ./gradlew </i>) on Linux. The user only needs a working Java installation and know how to use the command line. 
<h3> On Windows </h3>
From project directory:
<i> gradlew.bat run </i>

<h3> On Linux </i>
From project directory:
<i> ./gradlew run </i>

<h2> Contact and Future Improvements </h2>
<ul> Please see the issues list to see what functionality I intend to add. </ul>
<ul> I welcome suggestions and contributions! Please email ryty.boyce@gmail.com with any questions. </ul>
