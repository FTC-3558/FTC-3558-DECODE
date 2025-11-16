package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.List;

import dev.nextftc.core.subsystems.Subsystem;

public class Vision implements Subsystem {

    // Initialize Vision Class
    public static final Vision INSTANCE = new Vision();

    // Hardware Map
    private Limelight3A limelight;

    // Current Motif
    public String Motif = "Void";

    // Vision Constructor
    private Vision() {}


    // Setter used by the OpMode to link the hardware instance.
    public void setLimelight(Limelight3A ll) {
        this.limelight = ll;
    }


    // --- SCORING MOTIF ---
    public void UpdateMotif() {
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();

        if (Motif == "VOID") {
            for (FiducialResult fiducial : fiducials) {
                int id = fiducial.getFiducialId();
                if (id >= 21 && id <= 23) { // Check for the target motifs
                    switch (id) {
                        case 21:
                            Motif = "GPP";
                            break;
                        case 22:
                            Motif = "PGP";
                            break;
                        case 23:
                            Motif = "PPG";
                            break;
                    }
                }
            }
        }
    }

    // Simple return function
    public String getMotif() {
        return Motif;
    }


    // --- TARGET LOCK ---
    public double getRotationTO() {
        if (limelight.getLatestResult() == null) return 0;

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