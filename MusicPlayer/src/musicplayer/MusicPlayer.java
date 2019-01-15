package musicplayer;
import java.awt.HeadlessException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * This program creates a music player GUI
 * @author nathan.hibbetts
 */
public class MusicPlayer extends Application
{
    //Text field to hold what song is currently playing/paused
    final private TextField text = new TextField();
    
    final private Controller con = new Controller();  //Controller class object
    
    //For moving the window
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage stage) throws IOException
    {
        //Create a borderpane
        BorderPane borderPane = new BorderPane();
        
        //Create a progress bar and give it a width
        ProgressBar pb = new ProgressBar();
        pb.setPrefWidth(500);
        pb.setProgress(0);
        Tooltip tooltipBar = new Tooltip("Shows where the song is"
                + " currently at in its length.");
        pb.setTooltip(tooltipBar);
        
        //Create a menu
        final Menu fileMenu = new Menu("File");
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().add(fileMenu);
        
        //Add options to the File section in the menu
        MenuItem closeMenu = new MenuItem("Exit");
        MenuItem addMenu = new MenuItem("Add Songs");
        fileMenu.getItems().add(addMenu);
        fileMenu.getItems().add(closeMenu);
        
        //Describes what's happening
        Label aLabel = new Label("Waiting for songs to play.");
        aLabel.setStyle("-fx-font-size: 20px");
        
        //hiddenLabel controls the toggle button
        Label hiddenLabel = new Label("Normal");
        hiddenLabel.setVisible(false);
 
        //Slider information
        Slider volumeSlider = new Slider(0, 100, 40);
        Tooltip tooltipVolume = new Tooltip("Controls the volume.");
        volumeSlider.setTooltip(tooltipVolume);
        volumeSlider.setMajorTickUnit(20);
        volumeSlider.setMinorTickCount(3);
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setOrientation(Orientation.HORIZONTAL);
        
        //Set the textfield properties
        text.setPrefColumnCount(30);
        text.setEditable(false);
        text.setAlignment(Pos.CENTER);
        Tooltip tooltipText = new Tooltip("Holds the song currently playing.");
        text.setTooltip(tooltipText);
        text.setStyle("-fx-font-size: 15");
        
        //ComboBox holds playlists
        ComboBox playlistsBox = new ComboBox(FXCollections.observableArrayList(getolPlaylists()));
        playlistsBox.getSelectionModel().select("No Playlist");
        Tooltip tooltipPlaylists = new Tooltip("Holds all of your playlists.");
        playlistsBox.setTooltip(tooltipPlaylists);

        //Skip button skips the currently playing song
        Image imageSkip = new Image(getClass().getResourceAsStream("skip.png"), 50, 30, true, true);
        Button skipButton = new Button();
        skipButton.setGraphic(new ImageView(imageSkip));
        Tooltip tooltipSkip = new Tooltip("Skips the currently playing song.");
        skipButton.setTooltip(tooltipSkip);
        skipButton.setId("ControlButton");
        
        //Get songs button opens a file locator to get songs
        Button getSongsButton = new Button("Add Songs");
        Tooltip tooltipgetSongs = new Tooltip("Opens a file selection box to locate new mp3 files to add.");
        getSongsButton.setTooltip(tooltipgetSongs);
        getSongsButton.setId("SideButton");
        
        //Manage playlists button opens a menu of options for the playlists
        Button managePlaylistsButton = new Button("Manage Playlists");
        Tooltip tooltipmanagePlaylists = new Tooltip("Opens a menu of options "
                + "to choose from\nthat deals with playlists.");
        managePlaylistsButton.setTooltip(tooltipmanagePlaylists);
        managePlaylistsButton.setId("PlaylistButton");
        
        //Previous button goes back a song
        Image imagePrevious = new Image(getClass().getResourceAsStream("previous.png"), 50, 30, true, true);
        Button previousButton = new Button();
        previousButton.setGraphic((new ImageView(imagePrevious)));
        Tooltip tooltipPrevious = new Tooltip("Goes back to the previous song.");
        previousButton.setTooltip(tooltipPrevious);
        previousButton.setId("ControlButton");
        
        //playButton plays the current song
        Image imagePlay = new Image(getClass().getResourceAsStream("play.png"), 50, 30, true, true);
        Button playButton = new Button();
        playButton.setGraphic(new ImageView(imagePlay));
        Tooltip tooltipPlay = new Tooltip("Play the current song.");
        playButton.setTooltip(tooltipPlay);
        playButton.setId("ControlButton");
        
        //Favorite button adds the current song to the favorites playlist
        Button favoriteButton = new Button("Favorite");
        Tooltip tooltipFavorite = new Tooltip("Add this song to your favorites.");
        favoriteButton.setTooltip(tooltipFavorite);
        favoriteButton.setId("SideButton");
        
        //pauseButton pauses the current song
        Image imagePause = new Image(getClass().getResourceAsStream("pause.png"), 50, 30, true, true);
        Button pauseButton = new Button();
        pauseButton.setGraphic(new ImageView(imagePause));
        Tooltip tooltipPause = new Tooltip("Pause the currently playing song.");
        pauseButton.setTooltip(tooltipPause);
        pauseButton.setId("ControlButton");
        
        //ordertoggleButton toggles the song order
        //between Normal, Reverse, and Shuffle
        Button orderToggleButton = new Button("Normal");
        Tooltip tooltipToggle = new Tooltip("Toggle song order between 'Normal',"
                + " 'Reverse', and 'Shuffle'.");
        orderToggleButton.setTooltip(tooltipToggle);
        orderToggleButton.setId("SideButton");
        
        //deleteSongButton deletes the currently playing song
        Button deleteSongButton = new Button("Delete Song");
        Tooltip tooltipDelete = new Tooltip("Delete the currently playing song.");
        deleteSongButton.setTooltip(tooltipDelete);
        deleteSongButton.setId("SideButton");

        //Create a left HBox for the getSongs, favorite, orderToggle, and deleteSong buttons
        VBox leftColumn = new VBox(10);
        leftColumn.setAlignment(Pos.CENTER);
        leftColumn.setPadding(new Insets(10));
        leftColumn.setSpacing(15);
        leftColumn.getChildren().addAll(getSongsButton, favoriteButton, orderToggleButton,
                deleteSongButton);
        leftColumn.setId("leftColumn");

        //Create a HBox for the play, pause, and skip button to appear
        //Underneath the now playing text box
        HBox bottomRow = new HBox(10);
        bottomRow.setAlignment(Pos.CENTER);
        bottomRow.setPadding(new Insets(10));
        bottomRow.setId("bottomRow");
        bottomRow.getChildren().addAll(previousButton, playButton, pauseButton, skipButton);
        
        //Create a VBox for the now playing box and above HBox
        VBox centerBlock = new VBox(10);
        centerBlock.setAlignment(Pos.CENTER);
        centerBlock.setPadding(new Insets(10));
        centerBlock.getChildren().addAll(aLabel, text, pb, bottomRow);
        centerBlock.setId("centerBlock");
        
        //Create a vBox for the playlist options, playlist box, and volume slider
        VBox rightColumn = new VBox(10);
        rightColumn.setAlignment(Pos.CENTER);
        rightColumn.setPadding(new Insets(10));
        rightColumn.setSpacing(30);
        rightColumn.getChildren().addAll(playlistsBox, managePlaylistsButton,
                volumeSlider);
        rightColumn.setId("rightColumn");
        
        //Put everything into a BorderPane
        borderPane.setTop(menuBar);
        borderPane.setLeft(leftColumn);
        borderPane.setCenter(centerBlock);
        borderPane.setRight(rightColumn);
        borderPane.setId("borderPane");
        
        //Create a timeline object for the progress bar
        Timeline progressbarUpdater = new Timeline();

        //Create a timeline object for changing the "now playing" label
        Timeline nowplayinglabelChanger = new Timeline(); 
        
        //Creates a timeline object for changing the box containg
        //what song is currently playing
        Timeline updateSongPlaying = new Timeline();
        
        //Load in all the songs already in the Songs folder
        //and start the program
        File folder = new File("Songs");
        if (!folder.exists())
        {
            new File("Songs").mkdir();
        }
        
        File[] listOfFiles = folder.listFiles();
        
        for (File song : listOfFiles) 
        {
            con.add(song.getName());
        }
        
        if (!con.isEmpty())
        {
            //Create and update the progress bar
            progressbarUpdater.getKeyFrames().add(new KeyFrame(Duration.millis(100),
            ae -> pb.setProgress(1.0 * con.mediaPlayer.getCurrentTime().toMillis() / con.mediaPlayer.getTotalDuration().toMillis())));   
            progressbarUpdater.setCycleCount(Timeline.INDEFINITE);
            progressbarUpdater.play();
      
            //Sort the songs and start playing
            con.sort();
            setsongDisplay(con.getSong());
            con.start();
            aLabel.setText("Now Playing");
        }
        
        /**
         * Lambdas event-handler
         */
        EventHandler<ActionEvent> handler = (ActionEvent event) -> {
            //Opens up a window to select a folder containing songs
            //to add to the music player
            if (event.getSource() == getSongsButton)
            {
                try
                {
                    File[] songs = null;
                    
                    try
                    {
                        songs = getSongs();
                        for (File song : songs)
                        {
                            con.add(song.getName());      
                        }
                    }
                    catch (NullPointerException a)
                    {

                    }

                    try
                    {
                        con.stop();
                    }
                    catch (NullPointerException c)
                    {
                        System.out.println(c.getMessage());
                    }
                    if (!con.isEmpty())
                    {
                        con.sort();
                        con.start();
                        setsongDisplay(con.getSong());
                        //Create and update the progress bar
                        progressbarUpdater.stop();
                        progressbarUpdater.getKeyFrames().add(new KeyFrame(Duration.millis(100),
                        ae -> pb.setProgress(1.0 * con.mediaPlayer.getCurrentTime().toMillis() / con.mediaPlayer.getTotalDuration().toMillis())));   
                        progressbarUpdater.setCycleCount(Timeline.INDEFINITE);
                        progressbarUpdater.play();
                    }
                }
                catch(IOException e)
                {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            
            //Plays the currently selected song
            else if (event.getSource() == playButton)
            {
                if (!con.isEmpty())
                {
                    aLabel.setText("Now Playing");
                    con.play();
                }
            }
            //Pauses the currently selected song
            else if (event.getSource() == pauseButton)
            {
                    if (!con.isEmpty())
                    {
                        con.pause();
                        aLabel.setText("Paused");
                    }
            }
            //Skips the currently selected song
            else if (event.getSource() == skipButton)
            {
                try
                {
                    if (!con.isEmpty())
                    {
                        con.skip();
                        setsongDisplay(con.getSong());
                        updateSongPlaying.stop();
                        updateSongPlaying.play();
                        aLabel.setText("Now Playing");
                    }
                }
                catch (IOException b)
                {
                    setsongDisplay("No song to skip to");
                    System.out.println(b.getMessage());
                }
            }
            
            //Goes back one song
            else if (event.getSource() == previousButton)
            {
                try
                {
                    if (!con.isEmpty())
                    {
                        con.previous();
                        setsongDisplay(con.getSong());
                        updateSongPlaying.stop();
                        updateSongPlaying.play();
                        aLabel.setText("Now Playing");
                    }
                }
                catch (IOException e)
                {
                    setsongDisplay("No song to go back to");
                    System.out.println(e.getMessage());
                }
            }
            
            //Adds the current song to the favorites playlist
            else if (event.getSource() == favoriteButton)
            {
                try
                {
                    if (!con.isEmpty())
                    {
                        File fav = new File("Playlists/Favorites/" + con.getSong());
                        if (!fav.exists())
                        {
                            new File("Playlists/Favorites/").mkdir();
                            new File("Playlists/Favorites/" + con.getSong() + ".txt").createNewFile();
                        }
                    }
                }
                catch (IOException e)
                {
                    
                }
            }
            //Delete the current song
            else if (event.getSource() == deleteSongButton)
            {
                if (!con.isEmpty())
                {
                    Alert alert = new Alert(AlertType.CONFIRMATION);
                    alert.setTitle("Delete song");
                    alert.setHeaderText("Are you sure you want to permanently "
                            + "delete '" + con.getSong() + "'?");
                    ButtonType buttonTypeYes = new ButtonType("Yes");
                    ButtonType buttonTypeNo = new ButtonType("No");

                    alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);

                    Optional<ButtonType> result = alert.showAndWait();
                    //Remove the song from the playlist and delete it
                    if (result.get()== buttonTypeYes)
                    {
                        //Files to delete the song
                        String todelete = con.getSong() + ".mp3";
                        File songs = new File("Songs");
                        File[] allSongs = songs.listFiles();
                        
                        //Files to delete it from all playlists
                        String playlistdelete = con.getSong() + ".txt";
                        File playlists = new File("Playlists");
                        File[] allPlaylists = playlists.listFiles();
                        
                        con.remove();
                        
                        try
                        {
                            con.skip();
                            setsongDisplay(con.getSong());
                        }
                        catch (IOException e)
                        {

                        }
                        for (File allSong : allSongs)
                        {
                            if (allSong.getName().equals(todelete))
                            {
                                allSong.deleteOnExit();
                            }
                        }
                        //Remove the song from all playlists
                        for (File allPlaylist : allPlaylists)   
                        {
                            File[] specificPlaylists = allPlaylist.listFiles();
                            for (File specificPlaylist : specificPlaylists) 
                            {
                                if (specificPlaylist.getName().equals(playlistdelete))
                                {
                                    specificPlaylist.deleteOnExit();
                                }
                            }
                        }
                    }
                    alert.close();
                }
            }
            
            //Controls the manage playlists button
            //Holds the code for adding and removing songs,
            //creating and deleting playlists
            else if (event.getSource() == managePlaylistsButton)
            {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Manage Playlists");
                alert.setHeaderText("How would you like to manage your playlists?");
                alert.setContentText("Choose your option.");
                
                ButtonType buttonTypeCreate = new ButtonType("Create new playlist");
                ButtonType buttonTypeDelete = new ButtonType("Delete playlist");
                ButtonType buttonTypeAdd = new ButtonType("Add song to playlist");
                ButtonType buttonTypeRemove = new ButtonType("Remove song from playlist");
                ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);
                
                alert.getButtonTypes().setAll(buttonTypeCreate, buttonTypeDelete,
                        buttonTypeAdd, buttonTypeRemove, buttonTypeCancel);
                
                Optional<ButtonType> result = alert.showAndWait();
                
                //Create a playlist
                if (result.get() == buttonTypeCreate)
                {
                        String name = JOptionPane.showInputDialog(null, "Name of playlist.");
                        if (name != null)
                        {
                            new File("Playlists/" + name).mkdir();
                            playlistsBox.setItems(FXCollections.observableArrayList(getolPlaylists()));
                        }
                }

                //Delete a playlist
                else if (result.get() == buttonTypeDelete)
                {
                    List<String> choices = new ArrayList<>();
                    String[] choice = getolPlaylists();
                    choices.addAll(Arrays.asList(choice));
                    choices.remove(0);
                    ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
                    dialog.setTitle("Delete Playlist");
                    dialog.setHeaderText("Which playlist would you like to delete?");
                    dialog.setContentText("Choose playlist:");
                    Optional<String> result2 = dialog.showAndWait();
                    
                    if (result2.isPresent())
                    {
                        String selectedPlaylist = result2.get();
                        File pl = new File("Playlists/" + selectedPlaylist);
                        File[] songs = pl.listFiles();
                        for (File song : songs)
                        {
                            song.delete();
                        }
                        pl.delete();
                        playlistsBox.setItems(FXCollections.observableArrayList(getolPlaylists()));
                    }
                }
                
                //Add song to playlist
                else if (result.get() == buttonTypeAdd)
                {
                    if (!con.isEmpty())
                    {
                        List<String> choices = new ArrayList<>();
                        String[] choice = getolPlaylists();
                        choices.addAll(Arrays.asList(choice));
                        choices.remove(0);
                        ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(0), choices);
                        dialog.setTitle("Add Playlist");
                        dialog.setHeaderText("Which playlist to add this song to?");
                        dialog.setContentText("Choose playlist:");

                        Optional<String> result3 = dialog.showAndWait();
                        try
                        {
                            if (result3.isPresent())
                            {
                                String selectedPlaylist = result3.get();
                                File pl = new File("Playlists/" + selectedPlaylist + "/"
                                        + con.getSong() + ".txt");
                                if (!pl.exists())
                                {
                                    pl.createNewFile();
                                }
                            }
                        }
                        catch (IOException e)
                        {
                            //Displays an alert window if there is an error
                            Alert alert2 = new Alert(AlertType.INFORMATION);
                            alert2.setTitle("Information Dialog");
                            alert2.setHeaderText(null);
                            alert2.setContentText("Please make sure a song is playing");
                            alert2.show();
                            System.out.println("Alert: Make sure a song is playing"
                                    + " before adding to playlist");
                        }
                    }
                }
                
                //Remove song from playlist
                else if (result.get() == buttonTypeRemove)
                {
                    if (!con.isEmpty())
                    {
                    String selectedPlaylist = (String) playlistsBox.getValue();
                    File pl = new File("Playlists/" + selectedPlaylist + "/"
                            + con.getSong() + ".txt");
                    if (pl.exists())
                        pl.delete();
                    try
                    {
                        con.stop();
                        con.remove();
                        con.skip();
                        if (!con.isEmpty())
                            setsongDisplay(con.getSong());
                    }
                    catch (IOException e)
                    {
                        e.getMessage();
                    }
                    }
                }
                else
                {
                    
                }
                alert.close();
            }
            
            //Toggle button changes that order the songs are played in
            else if (event.getSource() == orderToggleButton)
            {
                switch (hiddenLabel.getText()) {
                    case "Normal":  //Reversed Mode
                    {
                        orderToggleButton.setText("Reversed");
                        hiddenLabel.setText("Reversed");
                        con.reverse();
                        setsongDisplay(con.getSong());
                        aLabel.setText("Reversed order");
                        nowplayinglabelChanger.stop();
                        nowplayinglabelChanger.getKeyFrames().add(new KeyFrame(Duration.millis(1300),
                                ae -> aLabel.setText("Now Playing")));
                        nowplayinglabelChanger.play();
                        break;
                    }
                    case "Reversed":    //Shuffled mode
                    {
                        orderToggleButton.setText("Shuffled");                        
                        aLabel.setText("Shuffled");
                        con.shuffle();
                        setsongDisplay(con.getSong());
                        nowplayinglabelChanger.stop();
                        nowplayinglabelChanger.getKeyFrames().add(new KeyFrame(Duration.millis(1300),
                                ae -> aLabel.setText("Now Playing")));
                        nowplayinglabelChanger.play();
                        hiddenLabel.setText("Shuffle");
                        break;
                    }
                    case "Shuffle": //Normal mode
                    {
                        orderToggleButton.setText("Normal");
                        con.stop();
                        con.sort();
                        try
                        {
                            con.start();
                        }
                        catch (IOException e)
                        {
                            
                        }
                        aLabel.setText("Normal");
                        setsongDisplay(con.getSong());
                        nowplayinglabelChanger.stop();
                        nowplayinglabelChanger.getKeyFrames().add(new KeyFrame(Duration.millis(1300),
                        ae -> aLabel.setText("Now Playing")));
                        nowplayinglabelChanger.play();
                        hiddenLabel.setText("Normal");
                        break;
                    }
                    default:
                        break;
                }
            }
        };

        //Set the handler on all the buttons
        skipButton.setOnAction(handler);
        getSongsButton.setOnAction(handler);
        playButton.setOnAction(handler);
        pauseButton.setOnAction(handler);
        previousButton.setOnAction(handler);
        favoriteButton.setOnAction(handler);
        orderToggleButton.setOnAction(handler);
        managePlaylistsButton.setOnAction(handler);
        deleteSongButton.setOnAction(handler);

        /**
         * ChangeListener for the playlist combobox
         * Changes the playlist being played
         */
        playlistsBox.valueProperty().addListener(new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> ov, String t, String t1)
        {
            con.stop();
            con.empty();
            try
            {
            if (!playlistsBox.getValue().equals("No Playlist"))
            {
                String selectedPlaylist = (String) playlistsBox.getValue();
                File folder = new File("Playlists/" + selectedPlaylist);
                File songs = new File("Songs/");
                File[] listOfFiles = folder.listFiles(); //Songs in the playlist
                File[] listOfSongs = songs.listFiles();  //Songs in the songs folder
                
                //Compare songs and the playlist files for matches
                //and add them to the song list
                for (File song : listOfFiles) 
                {
                    boolean found = false;
                    String fileName = song.getName();
                    if (fileName.indexOf(".") > 0)
                        fileName = fileName.substring(0, fileName.lastIndexOf("."));
                    int i = 0;
                        while (!found && i < listOfSongs.length)
                        {
                            String fileName2 = listOfSongs[i].getName();
                            if (fileName2.indexOf(".") > 0)
                                fileName2 = fileName2.substring(0, fileName2.lastIndexOf("."));
                            
                            if (fileName.equals(fileName2))
                            {
                                con.add(listOfSongs[i].getName());
                                found = true;
                            }
                            i++;
                        }
                    }
                //if the song list isn't empty, sort and play the songs
                if (!con.isEmpty())
                {
                    try
                    {
                        con.sort();
                        con.start();
                    }
                    catch(IOException e)
                    {
                        
                    }
                    setsongDisplay(con.getSong());
                }
                else
                {
                    setsongDisplay("No songs are in this playlist.");
                }
            }
            else
            {   //If no playlist is selected, play all of the songs
                File folder = new File("Songs");
                File[] listOfFiles = folder.listFiles();
                for (File song : listOfFiles) 
                {
                    con.add(song.getName());
                }
                if (!con.isEmpty())
                {
                    try
                    {
                        con.sort();
                        con.start();
                    }
                    catch(IOException e)
                    {
                        
                    }
                    setsongDisplay(con.getSong());
                }
            }
            }
            catch (NullPointerException e)
            {
                
            }
        }
    });
        //Have the updateSongPlaying timeline update the song currently
        //playing every two (2) seconds
        updateSongPlaying.getKeyFrames().add(new KeyFrame(Duration.seconds(2),
        ae -> text.setText(con.getSong())));
        updateSongPlaying.setCycleCount(Timeline.INDEFINITE);
        updateSongPlaying.play();

        //Volume controller
        volumeSlider.valueProperty().addListener((Observable observable) -> {
        con.mediaPlayer.setVolume(volumeSlider.getValue() / 100);
        con.volume = volumeSlider.getValue() / 100;
        });
        
        //Create the scene
        Scene scene = new Scene(borderPane);
        
        //Menu action handler
        addMenu.setOnAction(e ->{
            getSongsButton.fire();
        });
        closeMenu.setOnAction(e -> {
           stage.close(); 
        });
        
        //Keyboard key shortcuts
        scene.setOnKeyPressed((final KeyEvent keyEvent) -> {
            if (null != keyEvent.getCode())
            switch (keyEvent.getCode()) {
                case UP:    //Up-arrow increases volume by 10
                    con.mediaPlayer.setVolume((volumeSlider.getValue() / 100) + 0.1);
                    con.volume = ((volumeSlider.getValue() / 100) + 0.1);
                    volumeSlider.setValue(volumeSlider.getValue() + 10);
                    keyEvent.consume();
                    break;
                case DOWN: //Down-arrow decreases volume by 10
                    con.mediaPlayer.setVolume((volumeSlider.getValue() / 100) - 0.1);
                    con.volume = ((volumeSlider.getValue() / 100) - 0.1);
                    volumeSlider.setValue(volumeSlider.getValue() - 10);
                    keyEvent.consume();
                    break;
                case PAUSE: //Pause fires the pauseButton
                    pauseButton.fire();
                    keyEvent.consume();
                    break;
                case PLAY:  //Play fires the playButton
                    playButton.fire();
                    keyEvent.consume();
                    break;
                case RIGHT: //Right-arrow fires the skipButton
                    skipButton.fire();
                    keyEvent.consume();
                    break;
                case LEFT:  //Left-arrow fires the previousButton
                    previousButton.fire();
                    keyEvent.consume();
                    break;
                default:
                    break;
            }
        });
        
        //Move the GUI around
        scene.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        
        //Move the GUI around
        scene.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        //Set the stage and show
        scene.getStylesheets().add(MusicPlayer.class.getResource("MusicPlayer.css").toExternalForm());
        stage.setScene(scene);
        stage.setTitle("Music Player");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Gets songs from a designated folder and puts them into the 
     * media player's songs folder
     * @return File[] of songs
     * @throws IOException 
     */
     private File[] getSongs() throws IOException
     {
         //Get the path to the songs to add
        try
        {   
            File[] listOfFiles = null;
            String path = null;
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int status = fileChooser.showOpenDialog(null);
            if (status == JFileChooser.APPROVE_OPTION)
            {
                File selectedFile = fileChooser.getSelectedFile();
                path = selectedFile.getPath();
            }
            File folder = new File(path);
            if (folder.isDirectory())
                listOfFiles = folder.listFiles();

            //Popup alert asking if you would like to move or copy files
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Get Songs");
            alert.setHeaderText("Would you like to move or copy your song(s) over?\n"
                    + "The player will be non-responsive until it is finished.");
            ButtonType buttonTypeOne = new ButtonType("Move");
            ButtonType buttonTypeTwo = new ButtonType("Copy");
            ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

            alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

            Optional<ButtonType> result = alert.showAndWait();
            
            //Move songs from folder to MusicPlayer/Songs
            if (result.get()== buttonTypeOne)
            {
                if (listOfFiles != null)    //Directory selected
                {
                    for (File filetocopy : listOfFiles)
                    {
                        String extention = filetocopy.getPath();
                        String file = filetocopy.getPath();
                        System.out.println(file);
                        
                        int i = file.lastIndexOf('.');
                        int p = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));
                        
                        if (i > p)
                        {
                            extention = file.substring(i+1);
                        }
                        
                        if ("mp3".equals(extention))
                        {
                            Path sourcePath = filetocopy.toPath();
                            String filename = filetocopy.getName();
                            Path targetpath = Paths.get("Songs", filename);
                            try
                            {
                                Files.move(sourcePath, targetpath, StandardCopyOption.REPLACE_EXISTING);
                            }
                            catch (IOException e)
                            {
                                System.out.println(e.getMessage());
                            }
                        }
                        else
                            System.out.println("Invalid file type");
                    }
                }
                else    //One file selected
                {
                    String extention = folder.getPath();
                    String file = folder.getPath();
                    System.out.println(file);

                    int i = file.lastIndexOf('.');
                    int p = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));

                    if (i > p)
                    {
                        extention = file.substring(i+1);
                    }
                    if ("mp3".equals(extention))
                    {
                        Path sourcePath = folder.toPath();
                        String filename = folder.getName();
                        Path targetpath = Paths.get("Songs", filename);
                            try
                            {
                                Files.move(sourcePath, targetpath, StandardCopyOption.REPLACE_EXISTING);
                                con.add(folder.getName());
                            }
                            catch (IOException a)
                            {

                            }
                    }
                    else
                        System.out.println("Invalid file type.");
                }
            }
            
            //Copy songs from folder to MusicPlayer/Songs
            if (result.get()== buttonTypeTwo)
            {
                if (listOfFiles != null)    //Directory selected
                {
                    for (File filetocopy : listOfFiles)
                    {
                        
                        String extention = filetocopy.getPath();
                        String file = filetocopy.getPath();
                        System.out.println(file);

                        int i = file.lastIndexOf('.');
                        int p = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));

                        if (i > p)
                        {
                            extention = file.substring(i+1);
                        }
                        if ("mp3".equals(extention))
                        {
                            Path sourcePath = filetocopy.toPath();
                            String filename = filetocopy.getName();
                            Path targetpath = Paths.get("Songs", filename);
                            try
                            {
                                Files.copy(sourcePath, targetpath, StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("HELP");
                            }
                            catch (IOException a)
                            {

                            }
                        }
                        else
                            System.out.println("Invalid file type.");
                    }
                }
                else //Single file selected
                {
                    String extention = folder.getPath();
                    String file = folder.getPath();
                    
                    int i = file.lastIndexOf('.');
                    int p = Math.max(file.lastIndexOf('/'), file.lastIndexOf('\\'));

                    if (i > p)
                    {
                        extention = file.substring(i+1);
                    }
                    if ("mp3".equals(extention))
                    {
                        Path sourcePath = folder.toPath();
                        String filename = folder.getName();
                        Path targetpath = Paths.get("Songs", filename);

                        try //Try statement skips file if it's already being played
                        {
                            Files.copy(sourcePath, targetpath, StandardCopyOption.REPLACE_EXISTING);
                            con.add(folder.getName());
                        }
                        catch (IOException e)
                        {
                            System.out.println(e.getMessage());
                        }
                    }
                    else
                        System.out.println("Invalid file type.");
                }   
            }
            else    //Blank else statement for cancel button
            {

            }
            alert.close();
            if (listOfFiles == null)
                listOfFiles[0] = folder;

            return listOfFiles;
        }
        catch (HeadlessException e)
        {
            System.out.println("Songs not found");
            System.out.println(e.getMessage());
            return null;
        }
    }

     /**
      * Gets all of the playlists from MediaPlayer/Playlists
      * @return File[] of playlists
      * @author nathan.hibbetts
      */
     private File[] getPlaylists()
     {
         try
         {
            File pl = new File("Playlists"); 
            if (!pl.exists())
            {
                new File("Playlists").mkdir();
                new File("Playlists/Favorites").mkdir();
            }
            else
                pl.createNewFile();
            File[] pla = pl.listFiles();
            return pla;
         }
         catch (IOException e)
         {
             return null;
         }
     }

     /**
      * Gets all of the playlists to be put into
      * an observable list
      * @return String[] of playlists
      * @author nathan.hibbetts
      */
     private String[] getolPlaylists()
     {
         //Create and fill in the ComboBox
        File[] files = getPlaylists();
        String[] names = new String[files.length + 1];
        names[0] = "No Playlist";
        for (int i = 0; i < files.length; i++)
        {
            names[i + 1] = files[i].getName();
        }
        return names;
     }

     /**
      * Changes the text box containing what song is being played
      * @param song The song being played
      */
     private void setsongDisplay(String song)
     {
        //Changes the text box on every method call except from the controller class
        //Textbox value changes but GUI doesn't update
        text.setText(song);
     }

     public static void main(String[] args)
    {
        launch(args);
    }
}