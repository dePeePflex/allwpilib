// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package edu.wpi.first.math.kinematics;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

class MecanumDriveOdometryTest {
  private final Translation2d m_fl = new Translation2d(12, 12);
  private final Translation2d m_fr = new Translation2d(12, -12);
  private final Translation2d m_bl = new Translation2d(-12, 12);
  private final Translation2d m_br = new Translation2d(-12, -12);

  private final MecanumDriveKinematics m_kinematics =
      new MecanumDriveKinematics(m_fl, m_fr, m_bl, m_br);

  private final MecanumDriveWheelPositions zero = new MecanumDriveWheelPositions();

  private final MecanumDriveOdometry m_odometry =
      new MecanumDriveOdometry(m_kinematics, Rotation2d.kZero, zero);

  @Test
  void testMultipleConsecutiveUpdates() {
    var wheelPositions = new MecanumDriveWheelPositions(3.536, 3.536, 3.536, 3.536);

    m_odometry.resetPosition(Rotation2d.kZero, wheelPositions, Pose2d.kZero);

    m_odometry.update(Rotation2d.kZero, wheelPositions);
    var secondPose = m_odometry.update(Rotation2d.kZero, wheelPositions);

    assertAll(
        () -> assertEquals(secondPose.getX(), 0.0, 0.01),
        () -> assertEquals(secondPose.getY(), 0.0, 0.01),
        () -> assertEquals(secondPose.getRotation().getDegrees(), 0.0, 0.01));
  }

  @Test
  void testTwoIterations() {
    // 5 units/sec  in the x-axis (forward)
    final var wheelPositions = new MecanumDriveWheelPositions(0.3536, 0.3536, 0.3536, 0.3536);
    m_odometry.resetPosition(Rotation2d.kZero, new MecanumDriveWheelPositions(), Pose2d.kZero);

    m_odometry.update(Rotation2d.kZero, new MecanumDriveWheelPositions());
    var pose = m_odometry.update(Rotation2d.kZero, wheelPositions);

    assertAll(
        () -> assertEquals(0.3536, pose.getX(), 0.01),
        () -> assertEquals(0.0, pose.getY(), 0.01),
        () -> assertEquals(0.0, pose.getRotation().getDegrees(), 0.01));
  }

  @Test
  void test90degreeTurn() {
    // This is a 90 degree turn about the point between front left and rear left wheels
    // fl -13.328649 fr 39.985946 rl -13.328649 rr 39.985946
    final var wheelPositions = new MecanumDriveWheelPositions(-13.328, 39.986, -13.329, 39.986);
    m_odometry.resetPosition(Rotation2d.kZero, new MecanumDriveWheelPositions(), Pose2d.kZero);

    m_odometry.update(Rotation2d.kZero, new MecanumDriveWheelPositions());
    final var pose = m_odometry.update(Rotation2d.kCCW_Pi_2, wheelPositions);

    assertAll(
        () -> assertEquals(8.4855, pose.getX(), 0.01),
        () -> assertEquals(8.4855, pose.getY(), 0.01),
        () -> assertEquals(90.0, pose.getRotation().getDegrees(), 0.01));
  }

  @Test
  void testGyroAngleReset() {
    var gyro = Rotation2d.kCCW_Pi_2;
    var fieldAngle = Rotation2d.kZero;
    m_odometry.resetPosition(
        gyro, new MecanumDriveWheelPositions(), new Pose2d(Translation2d.kZero, fieldAngle));
    var speeds = new MecanumDriveWheelPositions(3.536, 3.536, 3.536, 3.536);
    m_odometry.update(gyro, new MecanumDriveWheelPositions());
    var pose = m_odometry.update(gyro, speeds);

    assertAll(
        () -> assertEquals(3.536, pose.getX(), 0.1),
        () -> assertEquals(0.0, pose.getY(), 0.1),
        () -> assertEquals(0.0, pose.getRotation().getRadians(), 0.1));
  }

  @Test
  void testAccuracyFacingTrajectory() {
    var kinematics =
        new MecanumDriveKinematics(
            new Translation2d(1, 1), new Translation2d(1, -1),
            new Translation2d(-1, -1), new Translation2d(-1, 1));

    var wheelPositions = new MecanumDriveWheelPositions();

    var odometry =
        new MecanumDriveOdometry(kinematics, Rotation2d.kZero, wheelPositions, Pose2d.kZero);

    var trajectory =
        TrajectoryGenerator.generateTrajectory(
            List.of(
                Pose2d.kZero,
                new Pose2d(20, 20, Rotation2d.kZero),
                new Pose2d(10, 10, Rotation2d.kPi),
                new Pose2d(30, 30, Rotation2d.kZero),
                new Pose2d(20, 20, Rotation2d.kPi),
                new Pose2d(10, 10, Rotation2d.kZero)),
            new TrajectoryConfig(0.5, 2));

    var rand = new Random(5190);

    final double dt = 0.02;
    double t = 0.0;

    double maxError = Double.NEGATIVE_INFINITY;
    double errorSum = 0;
    double odometryDistanceTravelled = 0;
    double trajectoryDistanceTravelled = 0;
    while (t <= trajectory.getTotalTime()) {
      var groundTruthState = trajectory.sample(t);

      trajectoryDistanceTravelled +=
          groundTruthState.velocity * dt + 0.5 * groundTruthState.acceleration * dt * dt;

      var wheelSpeeds =
          kinematics.toWheelSpeeds(
              new ChassisSpeeds(
                  groundTruthState.velocity,
                  0,
                  groundTruthState.velocity * groundTruthState.curvature));

      wheelSpeeds.frontLeft += rand.nextGaussian() * 0.1;
      wheelSpeeds.frontRight += rand.nextGaussian() * 0.1;
      wheelSpeeds.rearLeft += rand.nextGaussian() * 0.1;
      wheelSpeeds.rearRight += rand.nextGaussian() * 0.1;

      wheelPositions.frontLeft += wheelSpeeds.frontLeft * dt;
      wheelPositions.frontRight += wheelSpeeds.frontRight * dt;
      wheelPositions.rearLeft += wheelSpeeds.rearLeft * dt;
      wheelPositions.rearRight += wheelSpeeds.rearRight * dt;

      var lastPose = odometry.getPose();

      var xHat =
          odometry.update(
              groundTruthState.pose.getRotation().plus(new Rotation2d(rand.nextGaussian() * 0.05)),
              wheelPositions);

      odometryDistanceTravelled += lastPose.getTranslation().getDistance(xHat.getTranslation());

      double error = groundTruthState.pose.getTranslation().getDistance(xHat.getTranslation());
      if (error > maxError) {
        maxError = error;
      }
      errorSum += error;

      t += dt;
    }

    assertEquals(0.0, errorSum / (trajectory.getTotalTime() / dt), 0.35, "Incorrect mean error");
    assertEquals(0.0, maxError, 0.35, "Incorrect max error");
    assertEquals(
        1.0,
        odometryDistanceTravelled / trajectoryDistanceTravelled,
        0.05,
        "Incorrect distance travelled");
  }

  @Test
  void testAccuracyFacingXAxis() {
    var kinematics =
        new MecanumDriveKinematics(
            new Translation2d(1, 1), new Translation2d(1, -1),
            new Translation2d(-1, -1), new Translation2d(-1, 1));

    var wheelPositions = new MecanumDriveWheelPositions();

    var odometry =
        new MecanumDriveOdometry(kinematics, Rotation2d.kZero, wheelPositions, Pose2d.kZero);

    var trajectory =
        TrajectoryGenerator.generateTrajectory(
            List.of(
                Pose2d.kZero,
                new Pose2d(20, 20, Rotation2d.kZero),
                new Pose2d(10, 10, Rotation2d.kPi),
                new Pose2d(30, 30, Rotation2d.kZero),
                new Pose2d(20, 20, Rotation2d.kPi),
                new Pose2d(10, 10, Rotation2d.kZero)),
            new TrajectoryConfig(0.5, 2));

    var rand = new Random(5190);

    final double dt = 0.02;
    double t = 0.0;

    double maxError = Double.NEGATIVE_INFINITY;
    double errorSum = 0;
    double odometryDistanceTravelled = 0;
    double trajectoryDistanceTravelled = 0;
    while (t <= trajectory.getTotalTime()) {
      var groundTruthState = trajectory.sample(t);

      trajectoryDistanceTravelled +=
          groundTruthState.velocity * dt + 0.5 * groundTruthState.acceleration * dt * dt;

      var wheelSpeeds =
          kinematics.toWheelSpeeds(
              new ChassisSpeeds(
                  groundTruthState.velocity * groundTruthState.pose.getRotation().getCos(),
                  groundTruthState.velocity * groundTruthState.pose.getRotation().getSin(),
                  0));

      wheelSpeeds.frontLeft += rand.nextGaussian() * 0.1;
      wheelSpeeds.frontRight += rand.nextGaussian() * 0.1;
      wheelSpeeds.rearLeft += rand.nextGaussian() * 0.1;
      wheelSpeeds.rearRight += rand.nextGaussian() * 0.1;

      wheelPositions.frontLeft += wheelSpeeds.frontLeft * dt;
      wheelPositions.frontRight += wheelSpeeds.frontRight * dt;
      wheelPositions.rearLeft += wheelSpeeds.rearLeft * dt;
      wheelPositions.rearRight += wheelSpeeds.rearRight * dt;

      var lastPose = odometry.getPose();

      var xHat = odometry.update(new Rotation2d(rand.nextGaussian() * 0.05), wheelPositions);

      odometryDistanceTravelled += lastPose.getTranslation().getDistance(xHat.getTranslation());

      double error = groundTruthState.pose.getTranslation().getDistance(xHat.getTranslation());
      if (error > maxError) {
        maxError = error;
      }
      errorSum += error;

      t += dt;
    }

    assertEquals(0.0, errorSum / (trajectory.getTotalTime() / dt), 0.15, "Incorrect mean error");
    assertEquals(0.0, maxError, 0.3, "Incorrect max error");
    assertEquals(
        1.0,
        odometryDistanceTravelled / trajectoryDistanceTravelled,
        0.05,
        "Incorrect distance travelled");
  }
}
