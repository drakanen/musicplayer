package musicplayer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * This program acts as the controller for the MusicPlayer GUI
 * @author nathan.hibbetts
 */
public class Controller {
    
    /**
     * The Node class stores a list element and a reference to the next node
     */
    private class Node
    {
        String value;
        Node next;
        Node prev;
        
        /**
         * Constructor
         * @param val The element to store in the node
         * @param n The reference to the successor node
         */
        Node(String val, Node n, Node p)
        {
            value = val;
            next = n;
            prev = p;
        }
        
        /**
         * Constructor
         * @param val The element to store in the node
         */
        Node(String val)
        {
            //Call the other constructor
            this(val, null, null);
        }
    }
    private Node first;     //List head
    private Node last;      //last element in list
    private Node currentsong; //Holds the current selected song
    
    /**
     * Constructor
     */
    public Controller()
    {
        first = null;
        last = null;
    }
    
    /**
     * The isEmpty method checks to see if the list is empty
     * @return true if list is empty
     * @author nathan.hibbetts
     */
    protected boolean isEmpty()
    {
        return first == null;
    }
    
    /**
     * The size method returns the length of the list
     * @return the number of elements in the list
     * @author nathan.hibbetts
     */
    private int size()
    {
        int count = 0;
        Node p = first;
        while (p != null)
        {
            //There is an element at p
            count++;
            p = p.next;
        }
        return count;
    }
    
    /**
     * The add method adds an element to the end of the list
     * @param e the value to add
     * @author nathan.hibbetts
     */
    protected void add(String e)
    {
        if (isEmpty())
        {
            first = new Node(e);
            last = first;
            currentsong = first;
        }
        else
        {
            //Add to end of existing list
            last.next = new Node(e);
            first.prev = last.next;
            last.next.prev = last;
            last = last.next;
        }
    }
    
    /**
     * The remove method removes an element
     * @return Stops the method, nothing else
     * @author nathan.hibbetts
     */
    protected boolean remove()
    {
        stop();
        if (!isEmpty())
        {
            if (currentsong.value.equals(first.value))
            {
                //Removal of the first item in the list
                first.next.prev = first.prev;
                first = first.next;
                if (first == null)
                    last = null;
                return true;
            }
            // Find the predeccessor of the element to remove
            Node pred = first;
            while (pred.next != null && 
                    !pred.next.value.equals(currentsong.value))
            {
                pred = pred.next;
            }

            if (pred.next == last)
            {
                first.prev = pred;
                pred.next = first;
            }
            else
            {
                //pred.next == null OR pred.next.value is element
                if (pred.next != null)
                {
                    //pred.next.value is element
                    pred.next = pred.next.next;
                    pred.next.prev = pred;
                }
                //Check if pred is now last
                if (pred.next == null)
                    last = pred;
            }
        }
        return false;
    }

    /**
     * This method sorts the songs alphabetically
     * @author nathan.hibbetts
     */
    protected void sort() 
    {
        boolean exchangeMade = true;
            while(exchangeMade) 
            {
                Node current = first;
                exchangeMade = false;
                // Start at the beginning and loop over all elements                           
                while(current != null && current.next != null) 
                {
                    if(current.value.compareTo(current.next.value) > 0 ) 
                    {
                        String temp = current.value;
                        current.value = current.next.value;
                        current.next.value = temp;
                        exchangeMade = true;
                    }
                    current = current.next;
                }  
            }
    }
    
    //Controls which song to play   
    private Media sound;
    protected MediaPlayer mediaPlayer;
    
    //Holds the volume
    protected Double volume = 0.4;
    
    /**
     * Initializes the media player and plays it
     * @throws IOException 
     * @author nathan.hibbetts
     */
    protected void start() throws IOException
    {
        try
       {
           sound = new Media(new File("Songs/" + currentsong.value).toURI().toString());
           mediaPlayer = new MediaPlayer(sound);
           mediaPlayer.setVolume(volume);
           mediaPlayer.play();
       }
       catch (NullPointerException a)
       {
           System.out.println(a.getMessage());
       }

       // play each audio file in turn.
       mediaPlayer.setOnEndOfMedia(() -> {
       try
       {
           currentsong = currentsong.next;
           if (currentsong == null)
               currentsong = first;
           mediaPlayer.setVolume(volume);
           //System.out.println("Next song: " + getSong());
           start();
       }
       catch(IOException e)
       {
           System.out.println("Error going to next song");
       }
       });
    }
    
    /**
     * Skips the current song
     * @throws IOException 
     * @author nathan.hibbetts
     */
    protected void skip() throws IOException
    {
        if (currentsong == last)
        {
            mediaPlayer.stop();
            currentsong = first;
            start();
        }
        else
        {
            try
            {
            mediaPlayer.stop();
            System.out.println("Skipped: " + getSong());
            currentsong = currentsong.next;
            start();
            }
            catch (IOException e)
            {
                System.out.println("No song to skip to");
            }
        }
    }

    /**
     * Goes back to the previous song
     * @author nathan.hibbetts
     * @throws java.io.IOException
     */
    protected void previous() throws IOException
    {
        try
        {
            mediaPlayer.stop();
            currentsong = currentsong.prev;
            System.out.println("Went back to: " + getSong());
            start();
        }
        catch (IOException e)
        {
            System.out.println("No song to go back to");
        }
    }
    
    /**
     * Plays the current song
     * @author nathan.hibbetts
     */
    protected void play()
    {
        if (currentsong == null)
        {
            currentsong = first;
        }
        try
        {
        mediaPlayer.play();
        System.out.println("Currently playing: " + getSong());
        }
        catch (NullPointerException e)
        {
            System.out.println("No song to play");
        }
    }
    
    /**
     * Pauses the current song
     * @author nathan.hibbetts
     */
    protected void pause()
    {
        if (currentsong == null)
        {
            currentsong = first;
        }
        try
        {
            mediaPlayer.pause();
            System.out.println("Paused: " + getSong());
        }
        catch (NullPointerException e)
        {
            System.out.println("No song to pause");
        }
    }
    
    /**
     * Stops the current song
     * @author nathan.hibbetts
     */
    protected void stop()
    {
        try
        {
        mediaPlayer.stop();
        }
        catch (NullPointerException e)
        {
            
        }
    }
    
    /**
     * Empties the media player of all songs
     * @author nathan.hibbetts
     */
    protected void empty()
    {
        first = null;
        last = null;
        currentsong = null;
    }
    
    /**
     * Gets the song that's currently playing
     * and returns it as a string without file extentions
     * @return The song name
     * @author nathan.hibbetts
     */
    protected String getSong()
    {
        String fileName = "";
        try
        {
            fileName = currentsong.value;
            if (fileName != null)
            {
                if (fileName.indexOf(".") > 0)
                    fileName = fileName.substring(0, fileName.lastIndexOf("."));
            }
            else
                fileName = "No songs are available.";
            }
        catch (NullPointerException e)
        {
            fileName = "No songs are in this playlist.";
        }
        return fileName;
    }

    /**
     * Puts the playlist in reverse
     * @author nathan.hibbetts
     */
    protected void reverse()
    {
        mediaPlayer.stop();
        sort();
        boolean exchangeMade = true;
        while(exchangeMade) 
        {
            Node current = first;
            exchangeMade = false;
            // Start at the beginning and loop over all elements                           
            while(current != null && current.next != null) 
            {
                if(current.value.compareTo(current.next.value) < 0 ) 
                {
                    String temp = current.value;
                    current.value = current.next.value;
                    current.next.value = temp;
                    exchangeMade = true;
                }
                current = current.next;
            }  
        }
        try
        {
            start();
        }
        catch(IOException e)
        {

        }
    }
    
    /**
     * Shuffles the song playlist
     * @author nathan.hibbetts
     */
    protected void shuffle()
    {
        mediaPlayer.stop();
        ArrayList al = new ArrayList<>();
        currentsong = first;
        while (currentsong != null)
        {
            al.add(currentsong.value);
            currentsong = currentsong.next;
        }
        empty();
        Random ran = new Random();
        Collections.shuffle(al, ran);
        while (!al.isEmpty())
        {
            add((String) al.get(0));
            al.remove(0);
        }
        try
        {
            start();
        }
        catch(IOException e)
        {

        }
    }
}