package Group4.Guards;

import java.util.ArrayList;
import Group9.Game;
import Interop.Action.*;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.*;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Vision.*;

public class PatrolGuard implements Guard{

    boolean justTeleported = false;
    private int counter = 0;
    boolean inRandomMoves = false;
    boolean targetFound = false;
    private double error = 7.5;
    ArrayList<Action> trace = new ArrayList<Action>();
    private boolean positioning = false;
    private boolean chasing = false;
    private double totalAngle;
    static final int MAXIMUM_MOVES_BEFORE_THRESHOLD_CHANGE_GUARD = 500;


    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        if(percepts.getAreaPercepts().isJustTeleported()){
            justTeleported = true;
        }

        if (counter > MAXIMUM_MOVES_BEFORE_THRESHOLD_CHANGE_GUARD) {
            inRandomMoves = true;
            chasing = false;
            positioning = false;
        }

        if(percepts.getAreaPercepts().isJustTeleported()){
            justTeleported = true;
        }
        if(!percepts.wasLastActionExecuted()) {
            return new Rotate(Angle.fromRadians(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians() * Game._RANDOM.nextDouble()));
        }
        for(ObjectPercept obj : percepts.getVision().getObjects().getAll()) {
            if (obj.getType() == ObjectPerceptType.TargetArea && !targetFound) {
                positioning = true;
                targetFound = true;

                totalAngle = obj.getPoint().getClockDirection().getRadians();
                if (totalAngle > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()) {
                    totalAngle = totalAngle - percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians();
                    return new Rotate(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle());
                }else if (totalAngle < 0.5 && totalAngle > -0.5) {
                    positioning = false;
                    totalAngle = Math.PI / 2;
                    return new Move(new Distance(10));
                } else {
                    positioning = false;
                    totalAngle = Math.PI / 2;
                    return new Rotate(obj.getPoint().getClockDirection());
                }
            }

            if (targetFound) {
                if ((totalAngle > Math.PI / 6 || totalAngle < -1 * Math.PI / 6) && counter < 10 || !percepts.wasLastActionExecuted()) {
                    counter++;
                    if (totalAngle > percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians()) {
                        totalAngle = totalAngle - percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle().getRadians();
                        return new Rotate(percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle());
                    } else {
                        totalAngle = 0;
                        return new Rotate(Angle.fromRadians(totalAngle));
                    }
                } else {
                    counter = 0;
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                }
            }
            if(obj.getType() == ObjectPerceptType.Teleport && !justTeleported) {
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
            if((obj.getType() == ObjectPerceptType.Teleport) && (justTeleported)) {
                if (obj.getPoint().getClockDirection().getDegrees() < error || 360 - obj.getPoint().getClockDirection().getDegrees() < error) {
                    return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
                } else {
                    if (obj.getPoint().getClockDirection().getDegrees() > 180) {
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians() - 2 * Math.PI)));
                    } else {
                        return new Rotate(Angle.fromRadians(-1 * (obj.getPoint().getClockDirection().getRadians())));
                    }
                }
            }
        }
        return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
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
