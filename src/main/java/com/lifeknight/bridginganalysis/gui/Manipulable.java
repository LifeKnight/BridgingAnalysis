package com.lifeknight.bridginganalysis.gui;

import com.lifeknight.bridginganalysis.utilities.Misc;
import com.lifeknight.bridginganalysis.variables.LifeKnightInteger;

import java.util.ArrayList;

public abstract class Manipulable {
    public static ArrayList<Manipulable> manipulableComponents = new ArrayList<>();
    public final LifeKnightInteger positionX;
    public final LifeKnightInteger positionY;

    public Manipulable(String name) {
        manipulableComponents.add(this);
        this.positionX = new LifeKnightInteger(name + "PositionX", "Invisible", 0, 0, 1920);
        this.positionY = new LifeKnightInteger(name + "PositionY", "Invisible", 0, 0, 1080);
    }

    public void updatePosition(int x, int y) {
        positionX.setValue(Misc.scaleTo1080pWidth(x + 3));
        positionY.setValue(Misc.scaleTo1080pHeight(y + 3));
    }

    public int getXCoordinate() {
        int returnValue;
        if ((returnValue = Misc.scaleFrom1080pWidth(positionX.getValue())) < 0) {
            positionX.setValue(3);
            returnValue = 3;
        }
        return returnValue;
    }

    public int getYCoordinate() {
        int returnValue;
        if ((returnValue = Misc.scaleFrom1080pWidth(positionY.getValue())) < 0) {
            positionY.setValue(3);
            returnValue = 3;
        }
        return returnValue;
    }

    public void resetPosition() {
        positionX.reset();
        positionY.reset();
    }

    public abstract String getDisplayText();
}
