package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult;

import java.util.List;

public class Vision {

    Limelight3A limelight;
    public Vision(Limelight3A limelight) {
        this.limelight = limelight;
    }

    /**
     * Gets the game-specific "Motif" based on detected AprilTag IDs (21, 22, 23).
     * Returns "Void" if no relevant tag is found.
     * @return A string representing the identified motif (GPP, PGP, PPG, or Void).
     */
    public String getMotif() {
        // Initialize Motif. We will set it to "Void" later if no relevant tag is found.
        String motif = "";

        // Use a temporary list to avoid calling getLatestResult multiple times
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();

        // Loop through all detected fiducial tags
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId(); // The ID number of the fiducial

            // Check if the ID is one of the target motif tags (21, 22, or 23)
            if (id >= 21 && id <= 23) {
                switch (id) {
                    case 21:
                        motif = "GPP";
                        break;
                    case 22:
                        motif = "PGP";
                        break;
                    case 23:
                        motif = "PPG";
                        break;
                }
                // Once we find a relevant tag and set the motif, we can stop searching.
                return motif;
            }
        }

        // If the loop finishes without returning (meaning no relevant tag was found),
        // return the default "Void" motif.
        return "Void";
    }

    /**
     * Gets the yaw (rotation) needed to align to a specific target tag (ID 20).
     * @return The yaw angle (in degrees or radians, depending on the Limelight's configuration)
     * from the robot's pose relative to the target tag, or 0 if tag 20 is not found.
     */
    public double getRotationTO() {
        double Angle = 0;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId(); // The ID number of the fiducial
            if (id == 20) {
                // Assuming getRobotPoseTargetSpace() returns a valid Pose3D
                Angle = fiducial.getRobotPoseTargetSpace().getOrientation().getYaw();
                // Since you only care about tag 20, we can exit the loop early
                break;
            }
        }
        return Angle;
    }
}