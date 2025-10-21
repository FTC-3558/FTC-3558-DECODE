package org.firstinspires.ftc.teamcode.Teleop.SubSystems;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.telemetry;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLResultTypes.FiducialResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

import java.util.List;

public class Vision {

    Limelight3A limelight;
    public Vision(Limelight3A limelight) {
        this.limelight = limelight;
    }

    public String getMotif() {
        String Motif = "";
       List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId(); // The ID number of the fiducial
            if (id > 20 && id < 24) {
                switch (id) {
                    case 21:
                        Motif = "GPP";
                    case 22:
                        Motif = "PGP";
                    case 23:
                        Motif = "PPG";
                }
            }
            else {
                if (Motif.isEmpty()) {
                    Motif = "Void";
                }
            }
        }
        return Motif;
    }

    public double getRotationTO() {
        double Angle = 0;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId(); // The ID number of the fiducial
            if (id == 20) {
                Angle = fiducial.getRobotPoseTargetSpace().getOrientation().getYaw();
            }
        }
        return Angle;
    }
}
