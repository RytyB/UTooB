// Import custom classes
import videoInfo.VideoInfo;
import videoInfo.VideoInfo.video;

// Import classes from java main
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

// Import classes for JavaFx
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.Node;

import javafx.event.*;
import javafx.application.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;

import javafx.geometry.Pos;

// Import exceptions from other packages
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import org.json.simple.parser.ParseException;





public class createWindow extends Application {

    public int currentCol;
    public int currentRowe;

    public VBox refreshVideoMatrix(ArrayList<String> params, Stage stage, Scene videoPlayer, WebView webview) {


        // ######################  Video Matrix  ##################################
        
        int numberOfParameters = 4;

        VBox column = new VBox();
        column.setAlignment(Pos.CENTER_RIGHT);
        column.setSpacing(25);
        
        for (int rowe = 0; rowe < 3; rowe++){

            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_RIGHT);
            row.setSpacing(50);

            // There should be four columns by 3 rows for a total of 12 videos
            for (int col = 0; col < 4; col++){

                int currentVidIndex = col*numberOfParameters + rowe*numberOfParameters*4;
                
                Text title = new Text( params.get( currentVidIndex ) );
                title.setWrappingWidth(350);

                Button playButton = new Button("Play");
                playButton.setUserData( params.get(currentVidIndex + 1) );
                playButton.setOnAction(new EventHandler<ActionEvent>(){
                    @Override
                    public void handle(ActionEvent arg0){
                        String videoId = playButton.getUserData().toString();
                        String videoUrl = "https://www.youtube.com/embed/" + videoId + "?controls=0?rel=0";
                        webview.getEngine().load( videoUrl );
                        stage.setScene(videoPlayer);
                    }
                });

                Image thumbnail = new Image( params.get( currentVidIndex + 3 ) );
                ImageView thumbDisplay = new ImageView(thumbnail);
                thumbDisplay.setFitWidth(350);
                thumbDisplay.setPreserveRatio(true);

                VBox videoBox = new VBox();
                videoBox.setAlignment(Pos.TOP_CENTER);
                videoBox.getChildren().add(playButton);
                videoBox.getChildren().add(thumbDisplay);
                videoBox.getChildren().add(title);

                row.getChildren().add(videoBox);
            }
            column.getChildren().add(row);
        }

        return column;
    }


    @Override
    public void start(Stage stage){


        // ####################### Video Player Screen ################################

        Button returnHome = new Button("Return to home screen");
        returnHome.setAlignment(Pos.TOP_LEFT);

        WebView webview = new WebView();
        webview.setPrefHeight(1060);
        webview.setPrefWidth(1920);

        Pane elements = new Pane();
        elements.getChildren().add(webview);
        elements.getChildren().add(returnHome);

        Scene videoPlayer = new Scene( elements, 1920, 1080 );
        // #############################################################################

        int endOfChannels = Integer.parseInt( getParameters().getRaw().get(0) ) + 1;
        

        stage.setTitle("UToob");
        stage.setUserData( getParameters().getRaw() );

        
        // ################### Setting Check Box Functionality ############################

        VBox checkBoxes = new VBox();
        checkBoxes.setAlignment(Pos.CENTER_LEFT);
        checkBoxes.setSpacing(5);

        for (int channelNo = 0; channelNo < Integer.parseInt(getParameters().getRaw().get(0)); channelNo++) {
            
            CheckBox checkbox = new CheckBox( getParameters().getRaw().get(channelNo + 1) );
            checkbox.setSelected(true);
            checkbox.setUserData( checkbox.getText() );

            // Every time the user clicks a check box, the bulk of the program should run and refresh all of the elements on the homepage
            EventHandler<ActionEvent> event = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent e){

                    if (checkbox.isSelected()){
                        checkbox.setUserData( checkbox.getText() );
                    }
                    else {
                        checkbox.setUserData("none");
                    }

                    ArrayList<String> checkedCreators = new ArrayList<>();
                    for (int box = 0; box < checkBoxes.getChildren().size(); box ++){
                        Node currentBox = checkBoxes.getChildren().get(box);
                        if ( currentBox.getUserData().equals("none") ){
                            ;  // Do nothing
                        }
                        else {
                            checkedCreators.add( currentBox.getUserData().toString().toLowerCase() );
                            // Everybody has to be made lowercase, because evidently the creator name
                            //  can be different between the video and channel objects in YT API
                        }
                    }

                    if (checkedCreators.size() != 0) {

                        ArrayList<String> information = new ArrayList<>();
                        List<String> parameters = getParameters().getRaw();

                        for (int info = endOfChannels; info+3 <= parameters.size(); info+=4){
                            if (checkedCreators.contains(parameters.get(info + 2).toString().toLowerCase())){
                                information.add( parameters.get(info) );            // Title
                                information.add( parameters.get(info + 1) );        // VidId
                                information.add( parameters.get(info + 2) );        // Channel Name
                                information.add( parameters.get(info + 3) );        // Thumbnail URL
                            }
                        }

                        VBox column = refreshVideoMatrix(information, stage, videoPlayer, webview);

                        HBox screen = new HBox();
                        screen.getChildren().add(checkBoxes);
                        screen.getChildren().add(column);
                        screen.setSpacing(75);
                        screen.setAlignment(Pos.CENTER_RIGHT);

                        Scene homePage = new Scene(screen, 1920, 1080);

                        stage.setScene( homePage );
                    }
                }
            };

            checkbox.setOnAction(event);
            checkBoxes.getChildren().add(checkbox);

        }

        ArrayList<String> justVideos = new ArrayList<>();
        for (int i = endOfChannels; i < getParameters().getRaw().size(); i++) {
            justVideos.add( getParameters().getRaw().get(i) );
        }
        
        VBox column = refreshVideoMatrix(justVideos, stage, videoPlayer, webview);

        HBox screen = new HBox();
        screen.getChildren().add(checkBoxes);
        screen.getChildren().add(column);
        screen.setSpacing(75);
        screen.setAlignment(Pos.CENTER_RIGHT);

        Scene homePage = new Scene(screen, 1920, 1080);

        // Finalizing parameters for video player
        returnHome.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent arg0){
                stage.setScene(homePage);
            }
        });

        stage.setScene(homePage);
        stage.show();
    }


    public static void main(String[] args) 
    throws GeneralSecurityException, IOException, GoogleJsonResponseException, ParseException{
        
        VideoInfo videoInfo = new VideoInfo();

        // Typically the parameters are [title, videoId, channel name, thumbnail]
        int numberOfParameters = 4;


        // For some reason arguments can only be passed into the application as a list of strings
        String argumentList[];
        int videosLength = videoInfo.videoList.size() * numberOfParameters;
        int channelsLength = videoInfo.channelList.size();
    
        /*
         * - The first element is the number of channels in the list
         * - After that the elements are the names of the channels in userPrefs
         * - Finally, the elements are parameters of videos clustered in groups
         */
        argumentList = new String [videosLength + channelsLength + 1];  

        // Add the number of channels so that we can tell where the channels end
        argumentList[0] = Integer.toString( channelsLength ); 

        // Add the channel names
        for (int j = 1; j < channelsLength+1; j++) {
            argumentList[j] = videoInfo.channelList.get(j-1);
        }

        // Add the videos and their parameters
        for (int i = 0; i < videoInfo.videoList.size(); i++) {
            argumentList[i*numberOfParameters + channelsLength+1] = videoInfo.videoList.get(i).title;
            argumentList[i*numberOfParameters + channelsLength+2] = videoInfo.videoList.get(i).vidId;
            argumentList[i*numberOfParameters + channelsLength+3] = videoInfo.videoList.get(i).channelName;
            argumentList[i*numberOfParameters + channelsLength+4] = videoInfo.videoList.get(i).thumbnailURL;
        }

        launch( argumentList );
    }
}