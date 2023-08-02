// Import custom classes
import videoInfo.VideoInfo;

// Import classes from java main
import java.io.IOException;
import java.security.GeneralSecurityException;

// Import classes for JavaFx
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

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

    @Override
    public void start(Stage stage){
        

        stage.setTitle("UToob");

        if (stage.getUserData() == null) {
            stage.setUserData("Video to load not loaded yet");
        }


        // ####################### Video Player Screen ################################

        Button returnHome = new Button("Return to home screen");
        returnHome.setAlignment(Pos.TOP_LEFT);

        WebView webview = new WebView();
        webview.setPrefHeight(1080);
        webview.setPrefWidth(1920);

        Pane elements = new Pane();
        elements.getChildren().add(webview);
        elements.getChildren().add(returnHome);

        Scene videoPlayer = new Scene( elements, 1920, 1080 );

               

        // ################### Elements of the home screen ############################


        int parameterLength = getParameters().getRaw().size();
        VBox column = new VBox();
        column.setAlignment(Pos.CENTER_RIGHT);
        column.setSpacing(25);


        for (int rowe = 0; rowe < 3; rowe++) {

            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_RIGHT);
            row.setSpacing(50);

            for (int col = 0; col < 4; col++) {

                currentCol = col;
                currentRowe = rowe;

                Text title = new Text( getParameters().getRaw().get( (col*4)+(rowe*16) ) );
                title.setWrappingWidth(350);
                
                Button playButton = new Button("Play");
                playButton.setUserData( getParameters().getRaw().get( (col*4)+(rowe*16) + 1 ) );
                playButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent arg0){
                        String videoId = playButton.getUserData().toString();
                        String videoUrl = "https://www.youtube.com/embed/" + videoId + "?controls=0?rel=0";
                        webview.getEngine().load( videoUrl );
                        stage.setScene(videoPlayer);
                    }
                });

                Image thumbnail = new Image(getParameters().getRaw().get( (currentCol*4)+(currentRowe*16)+3 ));
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

        Scene homePage = new Scene(column, 1920, 1080);

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
        System.out.println("Main method called in createWindow \n");
        VideoInfo videoInfo = new VideoInfo();

        String argumentList[];
        int argumentLength = videoInfo.videoList.size() * 4;
        argumentList = new String [argumentLength];
        for (int i = 0; i < videoInfo.videoList.size(); i++) {
            argumentList[i*4] = videoInfo.videoList.get(i).title;
            argumentList[i*4 + 1] = videoInfo.videoList.get(i).vidId;
            argumentList[i*4 + 2] = videoInfo.videoList.get(i).channelName;
            argumentList[i*4 + 3] = videoInfo.videoList.get(i).thumbnailURL;
        }

        launch( argumentList );
    }
}