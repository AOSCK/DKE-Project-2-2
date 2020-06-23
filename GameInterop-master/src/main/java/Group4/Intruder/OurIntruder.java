package Group4.Intruder;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import Group9.Game;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.*;
import Interop.Percept.Scenario.*;
import Interop.Percept.Vision.*;

import static java.lang.Math.abs;

public class OurIntruder implements Intruder{
    private int numberOfMoves = 0;
    private int counter = 0;
    boolean justTeleported = false;
    boolean inRandomMoves = false;
    private double error = 10;
    private final static Charset ENCODING = StandardCharsets.UTF_8;
    static final int MAXIMUM_MOVES_BEFORE_THRESHOLD_CHANGE = 600;

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        if(percepts.getAreaPercepts().isJustTeleported()){
            counter = 0;
            inRandomMoves = false;
            justTeleported = true;
            return new Move(new Distance(4));
        }

        double rand = Math.random();
        if (counter > MAXIMUM_MOVES_BEFORE_THRESHOLD_CHANGE) {
            inRandomMoves = true;
        }

        for(ObjectPercept obj : percepts.getVision().getObjects().getAll()){
            if(obj.getType() == ObjectPerceptType.TargetArea){
                if (percepts.getTargetDirection().getDegrees() < error || 360 - percepts.getTargetDirection().getDegrees()<error) {
                    counter++;
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                } else {
                    counter++;
                    if (percepts.getTargetDirection().getDegrees()> 180){
                        return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()-2*Math.PI));
                    }
                    else {
                        return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()));
                    }
                }
            }
            if(obj.getType() == ObjectPerceptType.Teleport && !justTeleported) {
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                } else {
                    if (obj.getPoint().getClockDirection().getDegrees() > 180){
                        return new Rotate(Angle.fromRadians(-1* (obj.getPoint().getClockDirection().getRadians()-2*Math.PI)));
                    }
                    else {
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
            if((obj.getType() == ObjectPerceptType.Teleport) && (justTeleported)) {
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                } else {
                    if (obj.getPoint().getClockDirection().getDegrees() > 180) {
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians() - 2 * Math.PI)));
                    } else {
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
        }

            if (!inRandomMoves) {
                if (!percepts.wasLastActionExecuted()) {
                    counter++;
                    return new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
                } else {
                    if (abs(percepts.getTargetDirection().getDegrees()) < 1) {
                        counter = counter + 100;
                        return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                    } else {
                        counter = counter + 100;
                        if(percepts.getTargetDirection().getRadians() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() && (360 - percepts.getTargetDirection().getDegrees()) > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees() && percepts.getTargetDirection().getDegrees()>180){
                            return new Rotate(Angle.fromRadians(-1*percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
                        }
                        else if(percepts.getTargetDirection().getRadians() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() && percepts.getTargetDirection().getDegrees() > percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getDegrees()&& percepts.getTargetDirection().getDegrees()<=180){
                            return new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()));
                        }
                        else{
                            if(percepts.getTargetDirection().getDegrees() > 180) {
                                return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()- 2 * Math.PI));
                            }
                            else{
                                return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()));
                            }
                        }

                    }
                }
            }else{
                counter--;
                if(counter == 0){
                    inRandomMoves = false;
                }
                if(!percepts.wasLastActionExecuted())
                {
                    return new Rotate(Angle.fromRadians(percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
                }
                else
                {
                    return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
                }
            }
    }

    private double getSpeedModifier(IntruderPercepts guardPercepts)
    {
        SlowDownModifiers slowDownModifiers =  guardPercepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers();
        if(guardPercepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(guardPercepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(guardPercepts.getAreaPercepts().isInDoor())
        {
            return slowDownModifiers.getInDoor();
        }

        return 1;
    }

}
