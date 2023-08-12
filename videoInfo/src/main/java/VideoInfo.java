package videoInfo;


// YouTube API Dependencies
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.ResourceId;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

// Simple JSON for reading user preferences from resource folder
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

// java dependencies
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;




// Class should create a list of recent video objects from the selected creators and package them nicely for 
//  use in the window application. This should never be called as main
public class VideoInfo {
    private static final String CLIENT_SECRETS= "/client_secret.json";
    private static final Collection<String> SCOPES =
        Arrays.asList("https://www.googleapis.com/auth/youtube.readonly");

    private static final String APPLICATION_NAME = "ApiExample";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();


    /*
     * This class is just to handle grabbing the channel Ids from a json file in resources
     */
    public class ChannelRead {

        private static final String channelPath = "/userPrefs.json";

        public ArrayList<String> channelIds;
        public ArrayList<String> channelNames;

        public ChannelRead() 
        throws ParseException, IOException {
            JSONParser parser = new JSONParser();
            InputStream input = VideoInfo.class.getResourceAsStream(channelPath);

            JSONObject wholeJson = (JSONObject) parser.parse(
                new InputStreamReader(input, "UTF-8")
            );

            JSONArray slightlyLessJson = (JSONArray) wholeJson.get("channel");

            channelIds = new ArrayList<String>();
            channelNames = new ArrayList<String>();

            for (int index = 0; index < slightlyLessJson.size(); index++) {
                JSONObject aChannel = (JSONObject) slightlyLessJson.get(index);
                channelIds.add( aChannel.get("channelId").toString() );
                channelNames.add( aChannel.get("name").toString() );
            }
            
        }
    }

    /*
     * This is a redundant class to make life easier
     * Takes an item from an API call and extracts all of the relevant data for easier access
     */
    public class video{

        // Define the class attributes that will be accessed by the window application
        public String vidId;
        public String thumbnailURL;
        public String channelName;
        public String title;

        public DateTime publishedAt;
        public int duration;

        public video(SearchResult result, Video deets) {

            vidId = result.getId().getVideoId();
            title = result.getSnippet().getTitle();
            channelName = result.getSnippet().getChannelTitle();
            thumbnailURL = result.getSnippet().getThumbnails().getHigh().getUrl();
            publishedAt = result.getSnippet().getPublishedAt();

            String isoToParse = deets.getContentDetails().getDuration();
            if (isoToParse.contains("H")) {
                duration = 60;
            }
            else {
                if (isoToParse.contains("M") != true) {
                    duration = 0;
                }
                else {
                    boolean toAdd = false;
                    StringBuffer minutes = new StringBuffer();

                    for (int i = 0; i < isoToParse.length(); i++) {
                        if ( isoToParse.charAt(i) == 'M' ) {
                            toAdd = false;
                        }

                        if (toAdd) {
                            minutes.append( isoToParse.charAt(i) );
                        }

                        if ( isoToParse.charAt(i) == 'T' ) {
                            toAdd = true;
                        }

                    } 

                    duration = Integer.parseInt( minutes.toString() );
                }
            }

        }

    }

    // Class attributes to be accessed in window application
    public ArrayList<video> videoList;
    public ArrayList<String> channelList;


    /*
     * This class is to take two sorted lists of video objects and return a third list containing 
     * all the sorted videos
     */
    public class videoListOps {

        public static ArrayList<video> mergeVideoLists( ArrayList<video> oldArray1, ArrayList<video> oldArray2 ) {
            
            ArrayList<video> newArray = new ArrayList<video>();
            int totalVideos = oldArray1.size() + oldArray2.size();
            int i = 0;
            int j = 0;
            for (int k = 0; k < totalVideos; k++) {
                
                if (i>=oldArray1.size()){
                    newArray.add(oldArray2.get(j));
                    j++;
                } else {
                    if (j >= oldArray2.size()) {
                        newArray.add(oldArray1.get(i));
                        i++;
                    }
                    else {
                        if (oldArray1.get(i).publishedAt.getValue() > oldArray2.get(j).publishedAt.getValue()) {
                            newArray.add(oldArray1.get(i));
                            i++;
                        } 
                        else { 
                            if (oldArray1.get(i).publishedAt.getValue() <= oldArray2.get(j).publishedAt.getValue()) {
                                newArray.add(oldArray2.get(j));
                                j++;
                            }
                        
                        }
                    }
                } //End of nested ifs
            } // End of for loop

            return newArray;

        }

    }

    //==============================BOILERPLATE CODE FROM YOUTUBE API DOCUMENTATION=====================================
    /**
     * Create an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    public static Credential authorize(final NetHttpTransport httpTransport) throws IOException {
        // Load client secrets.
        InputStream in = VideoInfo.class.getResourceAsStream(CLIENT_SECRETS);
        GoogleClientSecrets clientSecrets =
          GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
            new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
            .build();
        Credential credential =
            new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");

        return credential;
    }

    /**
     * Build and return an authorized API client service.
     *
     * @return an authorized API client service
     * @throws GeneralSecurityException, IOException
     */
    public static YouTube getService() throws GeneralSecurityException, IOException, GoogleJsonResponseException {
        final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        Credential credential = authorize(httpTransport);
        return new YouTube.Builder(httpTransport, JSON_FACTORY, credential)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }
    //=========================================END OF BOILERPLATE CODE===================================================
    

    // This is the constructor class that will give me an ApiExample object which is just 
    //  the string containing the videoId
    /**
     * Call function to create API service object. Define and
     * execute API request. Print API response.
     *
     * @throws GeneralSecurityException, IOException, GoogleJsonResponseException
     */
    public VideoInfo()
        throws GeneralSecurityException, IOException, GoogleJsonResponseException, ParseException {
            
        videoList = new ArrayList<>();
        YouTube youtubeService = getService();
        int numberOfVideos;

        // read in list of channel ids from json in resources
        ChannelRead channels = new ChannelRead();
        channelList = channels.channelNames;
        // for channel in the json

        for (int counter = 0; counter < channels.channelIds.size(); counter++){

            YouTube.Search.List request = youtubeService.search()
                .list("snippet");
            SearchListResponse response = request.setChannelId( channels.channelIds.get(counter) )
                .setOrder("date")
                .setMaxResults(30L)  // We need 12 videos per channel after filtering shorts
                .execute();


            numberOfVideos = response.getItems().size();
            // for video in channel

            ArrayList<video> tempList = new ArrayList<video>();
            for (int creatorVid = 0; creatorVid < numberOfVideos; creatorVid++) {
                
                YouTube.Videos.List moreDetails = youtubeService.videos()
                    .list("contentDetails");
                VideoListResponse theDeets = moreDetails
                    .setId( response.getItems().get(creatorVid).getId().getVideoId() )
                    .execute();

                video myVideo = new video( response.getItems().get(creatorVid), theDeets.getItems().get(0) );

                if ( myVideo.duration > 2 && tempList.size() < 12 ) {
                    tempList.add( myVideo );
                }
            }

            videoList = videoListOps.mergeVideoLists( videoList, tempList );

        }

        
    }

}