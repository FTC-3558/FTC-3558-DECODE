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
<<<<<<< Updated upstream
}
=======
    public double getRotationTORed() {
        if (limelight.getLatestResult() == null) return 0;

        double Angle = 0;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 24) { //Only Difference between red and blue is the tag it looks for
                Angle = fiducial.getTargetXDegrees(); //fiducial.getTargetXDegrees();//fiducial.getRobotPoseFieldSpace().getOrientation().getYaw() - fiducial.getTargetPoseCameraSpace().getOrientation().getYaw();
                break;
            }
        }
        return Angle;
    }

    public boolean HasAprilTagInSightBlue() {
        if (limelight.getLatestResult() == null) return false;

        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 20) {
                return true;
            }
        }
        return false;
    }
    public boolean HasAprilTagInSightRed() {
        if (limelight.getLatestResult() == null) return false;

        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 24) {
                return true;
            }
        }
        return false;
    }

    public double getShooterPowerBlue() {
        if (limelight.getLatestResult() == null) return 0;

        double Power = .92;
        double distance;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 20) {
                distance = fiducial.getTargetPoseCameraSpace().getPosition().z;
                if (distance > 3) { //DISTANCE FROM TAG THIS IS CORRECT
                    return 1; //THis is what it returns if outside of goal scoring zone
                }
                else {
                    return Power; //THIS IS THE VALUE YOU NEED TO TUNE MADDEN
                }
            }
        }
        return Power;
    }
    public double getShooterPoweRed() {
        if (limelight.getLatestResult() == null) return 0;

        double Power = 0.92;
        double distance;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 24) {
                distance = fiducial.getTargetPoseCameraSpace().getPosition().z;
                if (distance > 3) { //DISTANCE FROM TAG THIS IS CORRECT
                    return 1; //THis is what it returns if outside of goal scoring zone
                }
                else {
                    return Power; //THIS IS THE VALUE YOU NEED TO TUNE MADDEN
                }
            }
        }
        return Power;
    }
}
>>>>>>> Stashed changes
