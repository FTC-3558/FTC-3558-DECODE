package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.HardwareMap;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Shuffler implements Subsystem {

    // Initialize Class
    public static final Shuffler INSTANCE = new Shuffler();

    // Class Constructor
    private Shuffler() { }

    // Hardware Maps
    private ServoEx servo = new ServoEx("Shuffler");
    private NormalizedColorSensor colorSensor; // Changed type to NormalizedColorSensor

    // Simple State Machine Vars
    public enum BallColor { GREEN, PURPLE, VOID }
    private final BallColor[] ballColors = new BallColor[]{BallColor.VOID, BallColor.VOID, BallColor.VOID};

    // Maps the physical slot index (0, 1, 2) to the required servo command to align it for INTAKE/DETECTION.
    private final ArrayList<Command> intakePosMap;

    // Maps the physical slot index (0, 1, 2) to the required servo command to align it for SHOOTING.
    private final ArrayList<Command> shootPosMap;

    {

        // Slightly Unnecessary but allows for just passing an int instead of using strings
        intakePosMap = new ArrayList<>();
        intakePosMap.add(new SetPosition(servo, 0.3).requires(this));
        intakePosMap.add(new SetPosition(servo, 0.69).requires(this));
        intakePosMap.add(new SetPosition(servo, 0.05).requires(this));

        shootPosMap = new ArrayList<>();
        shootPosMap.add(new SetPosition(servo, 0.1).requires(this));
        shootPosMap.add(new SetPosition(servo, 0.87).requires(this));
        shootPosMap.add(new SetPosition(servo, 0.5).requires(this));
    }

    // Color Sensor Hardware Injector
    public void setColorSensor(NormalizedColorSensor sensor) {
        this.colorSensor = sensor;
    }

    // --- STATE MACHINE FUNCTIONS ---
    public boolean isBallPresent() {
        if (colorSensor == null) return false;

        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        // Using the alpha channel (total intensity) from normalized colors for detection.
        // This value needs to be calibrated on your robot!
        final float DETECTION_THRESHOLD_ALPHA = 0.4f;

        return colors.alpha > DETECTION_THRESHOLD_ALPHA;
    }

    // Reads the color sensor and determines the color of the ball using normalized values.
    public BallColor readAndDetermineColor() {
        if (colorSensor == null) return BallColor.VOID;

        NormalizedRGBA colors = colorSensor.getNormalizedColors();
        float red = colors.red;
        float green = colors.green;
        float blue = colors.blue;

        // Ensure there is enough light/intensity to make a reliable reading
        final float MIN_INTENSITY = 0.05f;
        if (colors.alpha < MIN_INTENSITY) {
            return BallColor.VOID;
        }

        // Color logic based on normalized values (where R+G+B is roughly constant).
        // These threshold values need careful calibration for your specific game balls.
        if (green > red && green > blue && green > 0.02f) {
            return BallColor.GREEN;
        } else if (blue > red && blue > green && blue > 0.02f) {
            return BallColor.PURPLE;
        }

        return BallColor.VOID;
    }

    /*
     * Finds the first empty slot and returns the command to rotate to its intake position.
     * Automatically aligns for intake after a shot (where a slot becomes VOID).
     */
    public void rotateToEmptySlotForIntake() {
        for (int i = 0; i < ballColors.length; i++) {
            if (ballColors[i] == BallColor.VOID) {
                intakePosMap.get(i).schedule();
            }
            else {
                intakePosMap.get(0).schedule();
            }
        }
        // If all slots are full, default to the first slot's intake position (Slot 0)
    }

    /* Returns the command to rotate the given slot index (0, 1, or 2) to the SHOOTING position. */
    public Command rotateSlotToShoot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            return shootPosMap.get(slotIndex);
        }
        throw new IllegalArgumentException("Invalid Shuffler slot index: " + slotIndex);
    }

    /* Stores the detected color in the designated slot. */
    public void storeColorInSlot(BallColor color, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            ballColors[slotIndex] = color;
        }
    }

    /* Marks a slot as empty after a shot. */
    public void markSlotAsEmpty(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            ballColors[slotIndex] = BallColor.VOID;
        }
    }

    // Getter for command logic
    public List<BallColor> getBallColors() {
        return Arrays.asList(ballColors);
    }

    /* Finds the physical slot index of a specific color. */
    public int findSlotByColor(BallColor targetColor) {
        for (int i = 0; i < ballColors.length; i++) {
            if (ballColors[i] == targetColor) {
                return i;
            }
        }
        return -1; // Not found
    }
}