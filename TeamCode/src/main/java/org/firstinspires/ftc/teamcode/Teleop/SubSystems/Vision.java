package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import dev.nextftc.core.subsystems.Subsystem;

public class Vision implements Subsystem {

    public static final Vision INSTANCE = new Vision();
    private Limelight3A limelight;

    private Vision() {}

    /** Setter used by the OpMode to link the hardware instance. */
    public void setLimelight(Limelight3A ll) {
        this.limelight = ll;
    }

    /** Gets the scoring motif (GPP, PGP, PPG) from the Limelight camera. */
    public String getMotif() {
        if (limelight == null) return "VOID";

        String motif = "VOID";
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();

        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id >= 21 && id <= 23) { // Check for the target motifs
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
                return motif; // Return the first detected motif
            }
        }
        return motif; // Returns "VOID" if no relevant tag is found
    }

    public double getRotationTO() {
        if (limelight == null) return 0;

        double Angle = 0;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 20) {
                Angle = fiducial.getRobotPoseTargetSpace().getOrientation().getYaw();
                break;
            }
        }
        return Angle;
    }
}