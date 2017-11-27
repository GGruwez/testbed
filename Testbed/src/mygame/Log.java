package mygame;

import java.io.*;

/**
 * Created by daanlenaerts on 26/10/2017.
 */
public class Log {

    String lines = "";

    Log(){
        lines += "Position; Velocity; Acceleration; Pitch; Roll; Heading; LeftWingInclination; rightWingInclination; horStabInclination; verStabInclination; LliftX; LliftY; LliftZ; RliftX; RliftY; RliftZ; horLiftX; horLiftY; horLiftZ; verLiftX; verLiftY; verLiftZ; momentX; momentY; momentZ";
    }

    public void addLine(Aircraft aircraft){
        lines += "\r\n";
        lines += aircraft.getCalcCoordinates().toString() + "; ";
        lines += aircraft.getVelocity().toString() + "; ";
        lines += aircraft.getAcceleration().toString() + "; ";
        lines += aircraft.getPitch() + "; ";
        lines += aircraft.getRoll() + "; ";
        lines += aircraft.getHeading() + "; ";
        lines += aircraft.getLeftWingInclination() + "; ";
        lines += aircraft.getRightWingInclination() + "; ";
        lines += aircraft.getHorStabInclination() + "; ";
        lines += aircraft.getVerStabInclination() + "; ";
        lines += aircraft.getForce().getLeftWingLift().getX() + "; ";
        lines += aircraft.getForce().getLeftWingLift().getY() + "; ";
        lines += aircraft.getForce().getLeftWingLift().getZ() + "; ";
        lines += aircraft.getForce().getRightWingLift().getX() + "; ";
        lines += aircraft.getForce().getRightWingLift().getY() + "; ";
        lines += aircraft.getForce().getRightWingLift().getZ() + "; ";
        lines += aircraft.getForce().getHorizontalStabilizerLift().getX() + "; ";
        lines += aircraft.getForce().getHorizontalStabilizerLift().getY() + "; ";
        lines += aircraft.getForce().getHorizontalStabilizerLift().getZ() + "; ";
        lines += aircraft.getForce().getVerticalStabilizerLift().getX() + "; ";
        lines += aircraft.getForce().getVerticalStabilizerLift().getY() + "; ";
        lines += aircraft.getForce().getVerticalStabilizerLift().getZ() + "; ";
        lines += aircraft.getForce().getTotalMoment().getX() + "; ";
        lines += aircraft.getForce().getTotalMoment().getY() + "; ";
        lines += aircraft.getForce().getTotalMoment().getZ() + "; ";
        lines += aircraft.getElapsedTime();

    }

    public void save(){
        BufferedWriter writer = null;
        try
        {
            writer = new BufferedWriter( new FileWriter("log.csv"));
            writer.write( this.lines);

        }
        catch ( IOException e)
        {
        }
        finally
        {
            try
            {
                if ( writer != null)
                    writer.close( );
            }
            catch ( IOException e)
            {
            }
        }
    }

}
