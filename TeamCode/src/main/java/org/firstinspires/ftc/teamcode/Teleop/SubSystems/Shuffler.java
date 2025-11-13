package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.HardwareMap;
import dev.nextftc.core.commands.Command;
import dev.nextftc.core.subsystems.Subsystem;
import dev.nextftc.hardware.impl.ServoEx;
import dev.nextftc.hardware.positionable.SetPosition;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Shuffler implements Subsystem {
    public static final Shuffler INSTANCE = new Shuffler();
    private Shuffler() { }

    private ServoEx servo = new ServoEx("Shuffler");
    private NormalizedColorSensor colorSensor; // Changed type to NormalizedColorSensor

    public enum BallColor { GREEN, PURPLE, VOID }

    // The state of the three physical slots (0, 1, 2).
    // Slot 0 is aligned with the intake when at position 0.1
    private final BallColor[] ballColors = new BallColor[]{BallColor.VOID, BallColor.VOID, BallColor.VOID};

    // Maps the physical slot index (0, 1, 2) to the required servo command to align it for INTAKE/DETECTION.
    private final Map<Integer, Command> intakePosMap;

    // Maps the physical slot index (0, 1, 2) to the required servo command to align it for SHOOTING.
    private final Map<Integer, Command> shootPosMap;

    {
        // Initialize Maps to link index to specific position commands
        Command pos0 = new SetPosition(servo, 0.1).requires(this);  // Slot 0 Intake Position
        Command pos1 = new SetPosition(servo, 0.87).requires(this); // Slot 1 Intake Position
        Command pos2 = new SetPosition(servo, 0.5).requires(this);  // Slot 2 Intake Position

        intakePosMap = new HashMap<>();
        intakePosMap.put(0, pos0);
        intakePosMap.put(1, pos1);
        intakePosMap.put(2, pos2);

        shootPosMap = new HashMap<>();
        shootPosMap.put(0, new SetPosition(servo, 0.30).requires(this));
        shootPosMap.put(1, new SetPosition(servo, 0.69).requires(this));
        // NOTE: Adjusted the negative value to a valid servo range (0.0 to 1.0)
        shootPosMap.put(2, new SetPosition(servo, 0.05).requires(this));
    }

    /**
     * Public setter to inject the color sensor hardware from the OpMode constructor.
     * This is the correct way to link hardware when the subsystem does not have an init(HardwareMap).
     */
    public void setColorSensor(NormalizedColorSensor sensor) {
        this.colorSensor = sensor;
    }

    @Override
    public void initialize() {
        // Standard NextFTC Subsystem initialization. No HardwareMap here.
        // All initialization logic for ServoEx is handled in its constructor.
    }

    /** Checks if a ball is physically present under the color sensors using normalized values. */
    public boolean isBallPresent() {
        if (colorSensor == null) return false;

        NormalizedRGBA colors = colorSensor.getNormalizedColors();

        // Using the alpha channel (total intensity) from normalized colors for detection.
        // This value needs to be calibrated on your robot!
        final float DETECTION_THRESHOLD_ALPHA = 0.05f;

        return colors.alpha > DETECTION_THRESHOLD_ALPHA;
    }

    /** Reads the color sensor and determines the color of the ball using normalized values. */
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
        if (green > red && green > blue && green > 0.4f) {
            return BallColor.GREEN;
        } else if (blue > red && blue > green && blue > 0.4f) {
            return BallColor.PURPLE;
        }

        return BallColor.VOID;
    }

    /**
     * Finds the first empty slot and returns the command to rotate to its intake position.
     * Automatically aligns for intake after a shot (where a slot becomes VOID).
     */
    public Command rotateToEmptySlotForIntake() {
        for (int i = 0; i < ballColors.length; i++) {
            if (ballColors[i] == BallColor.VOID) {
                return intakePosMap.get(i);
            }
        }
        // If all slots are full, default to the first slot's intake position (Slot 0)
        return intakePosMap.get(0);
    }

    /** Returns the command to rotate the given slot index (0, 1, or 2) to the SHOOTING position. */
    public Command rotateSlotToShoot(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            return shootPosMap.get(slotIndex);
        }
        throw new IllegalArgumentException("Invalid Shuffler slot index: " + slotIndex);
    }

    /** Stores the detected color in the designated slot. */
    public void storeColorInSlot(BallColor color, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            ballColors[slotIndex] = color;
        }
    }

    /** Marks a slot as empty after a shot. */
    public void markSlotAsEmpty(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            ballColors[slotIndex] = BallColor.VOID;
        }
    }

    // Getter for command logic
    public List<BallColor> getBallColors() {
        return Arrays.asList(ballColors);
    }

    /** Finds the physical slot index of a specific color. */
    public int findSlotByColor(BallColor targetColor) {
        for (int i = 0; i < ballColors.length; i++) {
            if (ballColors[i] == targetColor) {
                return i;
            }
        }
        return -1; // Not found
    }
}