import java.util.*;
import java.util.concurrent.TimeUnit;
import java.lang.Math;

/**
 * A three-horse race, each horse running in its own lane
 * for a given distance
 * 
 * @author McRaceface
 * @version 1.0
 */
public class Race
{
    private int raceLength;
    private ArrayList<Horse> horses = new ArrayList<>();
    public static void main(String[] args) {
        Race race = new Race(10);
        Horse horse1 = new Horse('\u265E', "Pippi Longstocking", 0.6);
        Horse horse2 = new Horse('\u2658',"Kokomo" , 0.6);
        Horse horse3 = new Horse('\u2655', "El Jefe", 0.4);
        race.horses.add(horse1);
        race.horses.add(horse2);
        race.horses.add(horse3);
        race.startRace();
    }

    /**
     * Constructor for objects of class Race
     * Initially there are no horses in the lanes
     * 
     * @param distance the length of the racetrack (in metres/yards...)
     */
    public Race(int distance)
    {
        // initialise instance variables
        raceLength = distance;
    }
    
    /**
     * Start the race
     * The horse are brought to the start and
     * then repeatedly moved forward until the 
     * race is finished
     */
    public void startRace()
    {
        //declare a local variable to tell us when the race is finished
        boolean finished = false;
        
        //reset all the lanes (all horses not fallen and back to 0). 
        for (int i = 0; i < horses.size(); i++) {
            horses.get(i).goBackToStart();
        }
                      
        while (!finished)
        {
            for (int i = 0; i < horses.size(); i++) {
                //move each horse
                Horse theHorse = horses.get(i);
                moveHorse(theHorse);

                //print the race positions
                printRace();
                if (raceWonBy(theHorse)) {
                    //print winner
                    System.out.println("And the winner is... " +theHorse.getName()+ "!");
                    theHorse.setConfidence(theHorse.getConfidence() + 0.05);
                    finished = true;
                    break;
                }
                //check if all horses fell
                if(allHorsesFell()) {
                    System.out.println("All horses have fallen! There is no winner.");
                    finished = true;
                    break;
                }
            }
            //wait for 100 milliseconds
            try{ 
                TimeUnit.MILLISECONDS.sleep(100);
            }catch(Exception e){}
        }
    }
    
    /**
     * Randomly make a horse move forward or fall depending
     * on its confidence rating
     * A fallen horse cannot move
     * 
     * @param theHorse the horse to be moved
     */
    private void moveHorse(Horse theHorse)
    {
        //if the horse has fallen it cannot move, 
        //so only run if it has not fallen
        if  (!theHorse.hasFallen())
        {
            //the probability that the horse will move forward depends on the confidence;
            if (Math.random() < theHorse.getConfidence())
            {
               theHorse.moveForward();
            }
            
            //the probability that the horse will fall is very small (max is 0.1)
            //but will also will depends exponentially on confidence 
            //so if you double the confidence, the probability that it will fall is *2
            if (Math.random() < (0.1*theHorse.getConfidence()*theHorse.getConfidence()))
            {
                theHorse.fall();
            }
        }
    }
        
    /** 
     * Determines if a horse has won the race
     *
     * @param theHorse The horse we are testing
     * @return true if the horse has won, false otherwise.
     */
    private boolean raceWonBy(Horse theHorse)
    {
        if (theHorse.getDistanceTravelled() == raceLength)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    /***
     * Print the race on the terminal
     */
    private void printRace()
    {
        System.out.print('\u000C');  //clear the terminal window
        
        multiplePrint('=',raceLength+3); //top edge of track
        System.out.println();

        for (int i = 0; i < horses.size(); i++) {
            printLane(horses.get(i));
            System.out.println();
        }
        
        multiplePrint('=',raceLength+3); //bottom edge of track
        System.out.println();    
    }
    
    /**
     * print a horse's lane during the race
     * for example
     * |           X                      |
     * to show how far the horse has run
     */
    private void printLane(Horse theHorse)
    {
        //calculate how many spaces are needed before
        //and after the horse
        int spacesBefore = theHorse.getDistanceTravelled();
        int spacesAfter = raceLength - theHorse.getDistanceTravelled();
        
        //print a | for the beginning of the lane
        System.out.print('|');
        
        //print the spaces before the horse
        multiplePrint(' ',spacesBefore);
        
        //if the horse has fallen then print dead
        //else print the horse's symbol
        if(theHorse.hasFallen())
        {
            System.out.print('\u274c');
        }
        else
        {
            System.out.print(theHorse.getSymbol());
        }
        
        //print the spaces after the horse
        multiplePrint(' ',spacesAfter);
        
        //print the | for the end of the track
        System.out.print('|');

        // print name and horse confidence
        System.out.print(' '+ theHorse.getName() + " (Current confidence " + theHorse.getConfidence() + ')' );
    }
        
    
    /***
     * print a character a given number of times.
     * e.g. printmany('x',5) will print: xxxxx
     * 
     * @param aChar the character to Print
     */
    private void multiplePrint(char aChar, int times)
    {
        int i = 0;
        while (i < times)
        {
            System.out.print(aChar);
            i = i + 1;
        }
    }

    private boolean allHorsesFell() {
        for (int i = 0; i < horses.size(); i++) {
            if (!horses.get(i).hasFallen()) {
                return false;
            }
        }
        // All horses have fallen
        return true;
    }
}
