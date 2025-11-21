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
    public double getRotationTOBlue() {
        if (limelight.getLatestResult() == null) return 0;

        double Angle = 0;
        List<FiducialResult> fiducials = limelight.getLatestResult().getFiducialResults();
        for (FiducialResult fiducial : fiducials) {
            int id = fiducial.getFiducialId();
            if (id == 20) {
                Angle = fiducial.getTargetXDegrees(); //fiducial.getTargetXDegrees();//fiducial.getRobotPoseFieldSpace().getOrientation().getYaw() - fiducial.getTargetPoseCameraSpace().getOrientation().getYaw();
                break;
            }
        }
        return Angle;
    }
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

        double Power = .9;
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

        double Power = .9;
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