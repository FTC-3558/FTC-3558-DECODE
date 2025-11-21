package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo; // <-- USING DEFAULT FTC SERVO

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dev.nextftc.core.subsystems.Subsystem;

// NOTE: Removed the 'implements Subsystem' (from nextftc) as the class structure changes.
public class Shuffler implements Subsystem {

    // Initialize Class
    public static final Shuffler INSTANCE = new Shuffler();

    // Class Constructor

    // Hardware Maps
    // Using the default FTC Servo class
    private Servo servo;
    private NormalizedColorSensor Color1;
    private NormalizedColorSensor Color2;

    // Simple State Machine Vars
    public enum BallColor { GREEN, PURPLE, VOID }
    public static BallColor[] ballColors;

    // --- SERVO POSITION CONSTANTS ---
    // These constants replace the 'SetPosition' commands from the original file.
    // They represent the servo positions (0.0 to 1.0) for the physical slot index (0, 1, 2).
    // The servo name "Shuffler" is assumed for hardware mapping.

    // Maps the physical slot index (0, 1, 2) to the required servo position to align it for INTAKE/DETECTION.
    private final ArrayList<Double> intakePosMap;

    // Maps the physical slot index (0, 1, 2) to the required servo position to align it for SHOOTING.
    private final ArrayList<Double> shootPosMap;

    private Shuffler() {
        ballColors = new BallColor[]{BallColor.VOID, BallColor.VOID, BallColor.VOID};
    }

    {

        intakePosMap = new ArrayList<>();
        intakePosMap.add(0.15); // Slot 0 Intake Position (was -0.15)
        intakePosMap.add(0.91); // Slot 1 Intake Position (was -0.91)
        intakePosMap.add(0.57); // Slot 2 Intake Position (was -0.57)

        shootPosMap = new ArrayList<>();
        shootPosMap.add(0.7);  // Slot 0 Shoot Position (was -0.7)
        shootPosMap.add(0.35); // Slot 1 Shoot Position (was -0.35)
        shootPosMap.add(0.0);  // Slot 2 Shoot Position (was 0)
    }



    // Color Sensor Hardware Injector (Unchanged)
    public void sethardware(NormalizedColorSensor sensor1,NormalizedColorSensor sensor2, Servo servo) {

        this.Color1 = sensor1;
        this.Color2 = sensor2;
        this.servo = servo;
    }

    // --- STATE MACHINE FUNCTIONS ---
    public boolean isBallPresent() {
        if (Color1 == null || Color2 == null) return false;

        NormalizedRGBA colors = getBestColorSensor().getNormalizedColors();

        // Using the alpha channel (total intensity) from normalized colors for detection.
        // This value needs to be calibrated on your robot!
        final float DETECTION_THRESHOLD_ALPHA = 0.6f;

        return colors.alpha > DETECTION_THRESHOLD_ALPHA;
    }

    // Reads the color sensor and determines the color of the ball using normalized values.
    public BallColor readAndDetermineColor() {
        //if (Color1 == null || Color2 == null) return BallColor.VOID; //I Commented this out however it might throw a null pointer exception so if it does
                                                                    //just recoment it

        NormalizedRGBA colors = getBestColorSensor().getNormalizedColors();
        float red = colors.red;
        float green = colors.green;
        float blue = colors.blue;

        // Color logic based on normalized values (where R+G+B is roughly constant).


        //ALTER THESE SO IN THE IF CONDITIONS YOU CAN ADD "&& blue > 0.02" and then tune that number (for green it would be "&& green > 0.2)

        if (green > red && green > blue) {
            return BallColor.GREEN;
        } else if (blue > red && blue > green) {
            return BallColor.PURPLE;
        }
        else {
            return BallColor.VOID;
        }
    }

    /*
     * Finds the first empty slot and rotates the servo to its intake position.
     * Automatically aligns for intake after a shot (where a slot becomes VOID).
     */
    public void rotateToEmptySlotForIntake() {
        if (servo == null) return;

        int targetSlot = -1;
        for (int i = 0; i < ballColors.length; i++) {
            if (ballColors[i] == BallColor.VOID) {
                targetSlot = i;
                break; // Found the first empty slot
            }
        }

        if (targetSlot != -1) {
            // Set position directly instead of scheduling a command
            servo.setPosition(intakePosMap.get(targetSlot));
        } else {
            // If all slots are full, default to the first slot's intake position (Slot 0)
            servo.setPosition(intakePosMap.get(0));
        }
    }

    /* Rotates the servo to the SHOOTING position for the given slot index (0, 1, or 2). */
    public void rotateSlotToShoot(int slotIndex) {
        if (servo == null) return;

        if (slotIndex >= 0 && slotIndex < 3) {
            // Set position directly instead of scheduling a command
            servo.setPosition(shootPosMap.get(slotIndex));
        } else {
            throw new IllegalArgumentException("Invalid Shuffler slot index: " + slotIndex);
        }
    }

    /* Stores the detected color in the designated slot. (Unchanged) */
    public void storeColorInSlot(BallColor color, int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            ballColors[slotIndex] = color;
        }
    }

    /* Marks a slot as empty after a shot. (Unchanged) */
    public void markSlotAsEmpty(int slotIndex) {
        if (slotIndex >= 0 && slotIndex < 3) {
            ballColors[slotIndex] = BallColor.VOID;
        }
    }

    // Getter for command logic (Unchanged)
    public List<BallColor> getBallColors() {
        return Arrays.asList(ballColors);
    }

    /* Finds the physical slot index of a specific color. (Unchanged) */
    public int findSlotByColor(BallColor targetColor) {
        for (int i = 0; i < ballColors.length; i++) {
            if (ballColors[i] == targetColor) {
                return i;
            }
        }
        return -1; // Not found
    }


    //Takes the Alpha Value of Both Color Sensors (higher the more likely it has a good reading of the ball)
    //it compares them the higher one is used for sensor comparison

    public NormalizedColorSensor getBestColorSensor() {
        if (Color1.getNormalizedColors().alpha>Color2.getNormalizedColors().alpha) {
            return Color1;
        }
        else {
            return Color2;
        }
    }


}