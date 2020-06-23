package Group4.Guards;

/**
 * Description: Idea of this guard
    * if he sees an intruder he follows it
    * if he lost him again, he drops a pheromone telling the guards he lost an intruder
        * basically saying an intruder has to be near by
**/

import Group9.Game;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Smell.SmellPerceptType;
import Interop.Percept.Smell.SmellPercept;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

public class FollowGuard implements Guard{
    boolean justTeleported = false;
    private double error = 7.5;
    boolean sawintruder = false;
    boolean positioning = false;
    double totalfromRadians;
    Distance oldSmellDist = null;

    public FollowGuard() {}

    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        if(percepts.getAreaPercepts().isJustTeleported()){
            justTeleported = true;
        }

        for(ObjectPercept obj : percepts.getVision().getObjects().getAll()) {
            if (obj.getType() == ObjectPerceptType.Intruder) {
                positioning = false;
                sawintruder = true;
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                } else {
                    if (obj.getPoint().getClockDirection().getDegrees() > 180){
                        return new Rotate(Angle.fromRadians(-1* (obj.getPoint().getClockDirection().getRadians()-2*Math.PI)));
                    }
                    else {
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
        }

        if(sawintruder){
            sawintruder = false;
            return new DropPheromone(SmellPerceptType.values()[1]);
        }

        for(SmellPercept smell : percepts.getSmells().getAll()) {
            if(smell.getType() == SmellPerceptType.Pheromone1){
                if(oldSmellDist.equals(null)) oldSmellDist = smell.getDistance();
                if (oldSmellDist.getValue() < smell.getDistance().getValue()){
                    return  new Rotate(Angle.fromRadians(10));
                } else {
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                }
            }
        }

        //look for intruders out of the SentryTower
        /*
        if(percepts.getAreaPercepts().isInSentryTower()){
            return  new Rotate(Angle.fromDegrees(15));
        }

         */

        for(ObjectPercept obj : percepts.getVision().getObjects().getAll()) {
            if(obj.getType() == ObjectPerceptType.Teleport && !justTeleported) {
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                } else {
                    if (obj.getPoint().getClockDirection().getDegrees() > 180){
                        return new Rotate(Angle.fromRadians(-1* (obj.getPoint().getClockDirection().getRadians()-2*Math.PI)));
                    }
                    else {
                        //System.out.println("Rotating: " + (Math.toDegrees(obj.getPoint().getClockDirection().getRadians())));
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
            if((obj.getType() == ObjectPerceptType.Teleport) && (justTeleported)) {
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                } else {
                    if (obj.getPoint().getClockDirection().getDegrees() > 180) {
                        //System.out.println("Rotating: " + (Math.toDegrees(obj.getPoint().getClockDirection().getRadians() - 2 * Math.PI)));
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians() - 2 * Math.PI)));
                    } else {
                        //System.out.println("Rotating: " + (Math.toDegrees(obj.getPoint().getClockDirection().getRadians())));
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
            if (obj.getType() == ObjectPerceptType.SentryTower) {
                sawintruder = true;
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                } else {
                    //System.out.println("Degrees towards target: " + obj.getPoint().getClockDirection().getDegrees());
                    if (obj.getPoint().getClockDirection().getDegrees() > 180){
                        // System.out.println("Rotating: " + (Math.toDegrees(obj.getPoint().getClockDirection().getRadians()-2*Math.PI)));
                        return new Rotate(Angle.fromRadians(-1* (obj.getPoint().getClockDirection().getRadians()-2*Math.PI)));
                    }
                    else {
                        //System.out.println("Rotating: " + (Math.toDegrees(obj.getPoint().getClockDirection().getRadians())));
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
        }

        if(!percepts.wasLastActionExecuted()) {
            return new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
        }else{
            return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
        }
    }

    private double getSpeedModifier(GuardPercepts guardPercepts)
    {
        SlowDownModifiers slowDownModifiers =  guardPercepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
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
