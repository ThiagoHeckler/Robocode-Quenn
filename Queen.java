package aaaa;

import robocode.*;
import robocode.util.Utils;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

public class Queen extends AdvancedRobot {

    // ====== ESTADO GERAL ======
    private double moveDirection = 1;
    private double enemyEnergy = 100;

    // ====== MEMÓRIA / "IA" POR INIMIGO ======
    private static class EnemyStats {
        double preferredDistance = 300; // distância que funcionou melhor até agora
        double avgVelocity = 0;         // velocidade média do inimigo
        int scans = 0;                  // quantas vezes foi escaneado
        int hits = 0;                   // quantos tiros acertamos
        int shots = 0;                  // quantos tiros disparamos

        double hitRate() {
            return shots > 0 ? (double) hits / shots : 0.5;
        }
    }

    private static final Map<String, EnemyStats> ENEMIES = new HashMap<>();

    private EnemyStats currentEnemyStats;
    private String currentEnemyName;

    @Override
    public void run() {
        setColors(Color.RED, Color.BLACK, Color.YELLOW);
        setAdjustGunForRobotTurn(true);
        setAdjustRadarForGunTurn(true);

        while (true) {
            if (getRadarTurnRemaining() == 0) {
                setTurnRadarRight(360);
            }
            execute();
        }
    }

    @Override
    public void onScannedRobot(ScannedRobotEvent e) {

        double distance = e.getDistance();
        double absBearing = getHeading() + e.getBearing();

        currentEnemyName = e.getName();
        currentEnemyStats = ENEMIES.get(currentEnemyName);
        if (currentEnemyStats == null) {
            currentEnemyStats = new EnemyStats();
            ENEMIES.put(currentEnemyName, currentEnemyStats);
        }

        currentEnemyStats.scans++;
        currentEnemyStats.avgVelocity +=
                (Math.abs(e.getVelocity()) - currentEnemyStats.avgVelocity) / currentEnemyStats.scans;

        currentEnemyStats.preferredDistance +=
                (distance - currentEnemyStats.preferredDistance) * 0.05;

        boolean isDodger = currentEnemyStats.avgVelocity > 4;
        boolean isCamper = currentEnemyStats.avgVelocity < 1.5;

        double drop = enemyEnergy - e.getEnergy();
        if (drop > 0 && drop <= 3) {
            moveDirection = -moveDirection;
            setAhead(150 * moveDirection);
            setTurnRight(30 * moveDirection);
        }
        enemyEnergy = e.getEnergy();

        double angle = Utils.normalRelativeAngleDegrees(
                absBearing + 90 * moveDirection - getHeading()
        );
        setTurnRight(angle);

        double ideal = currentEnemyStats.preferredDistance;

        if (isDodger) ideal += 40;
        else if (isCamper) ideal -= 40;

        double delta = distance - ideal;

        if (distance < 110) {
            setAhead(-200 * moveDirection);
        } else if (Math.abs(delta) > 60) {
            setAhead((delta > 0 ? 1 : -1) * 200 * moveDirection);
        } else {
            setAhead(110 * moveDirection);
        }

        double basePower = Math.min(3.0, Math.max(1.3, 450.0 / distance));

        double hitRate = currentEnemyStats.hitRate();
        if (hitRate > 0.65) basePower = Math.min(3.0, basePower + 0.5);
        else if (hitRate < 0.35) basePower = Math.max(1.0, basePower - 0.5);

        if (getEnergy() < 20) basePower = Math.min(basePower, 1.8);
        if (getEnergy() < 10) basePower = 1.2;

        double firePower = basePower;
        double bulletSpeed = 20 - 3 * firePower;

        double myX = getX();
        double myY = getY();
        double absBearingRad = Math.toRadians(absBearing);

        double enemyX = myX + distance * Math.sin(absBearingRad);
        double enemyY = myY + distance * Math.cos(absBearingRad);

        double enemyHeadingRad = Math.toRadians(e.getHeading());
        double enemyVel = e.getVelocity();

        double time = distance / bulletSpeed;

        double predictedX = enemyX + enemyVel * time * Math.sin(enemyHeadingRad);
        double predictedY = enemyY + enemyVel * time * Math.cos(enemyHeadingRad);

        predictedX = Math.max(18, Math.min(getBattleFieldWidth() - 18, predictedX));
        predictedY = Math.max(18, Math.min(getBattleFieldHeight() - 18, predictedY));

        double theta = Math.toDegrees(Math.atan2(predictedX - myX, predictedY - myY));
        double gunTurn = Utils.normalRelativeAngleDegrees(theta - getGunHeading());
        setTurnGunRight(gunTurn);

        if (getGunHeat() == 0 && Math.abs(gunTurn) < 10) {
            setFire(firePower);
            if (currentEnemyStats != null) currentEnemyStats.shots++;
        }

        double radarTurn = Utils.normalRelativeAngleDegrees(
                absBearing - getRadarHeading()
        );
        setTurnRadarRight(radarTurn * 2);
    }

    @Override
    public void onBulletHit(BulletHitEvent e) {
        EnemyStats stats = ENEMIES.get(e.getName());
        if (stats != null) stats.hits++;
    }

    @Override
    public void onBulletMissed(BulletMissedEvent e) { }

    @Override
    public void onHitByBullet(HitByBulletEvent e) {
        moveDirection = -moveDirection;
        setTurnRight(45 - e.getBearing());
        setAhead(160 * moveDirection);
    }

    @Override
    public void onHitWall(HitWallEvent e) {
        moveDirection = -moveDirection;
        setBack(80);
        setTurnRight(90);
        setAhead(150 * moveDirection);
    }

    @Override
    public void onHitRobot(HitRobotEvent e) {
        if (e.isMyFault()) {
            setBack(100);
        } else {
            setAhead(60 * moveDirection);
        }

        if (getGunHeat() == 0 && getEnergy() > 10) {
            setFire(2.5);
            if (currentEnemyStats != null) currentEnemyStats.shots++;
        }

        moveDirection = -moveDirection;
    }

    @Override
    public void onWin(WinEvent e) {
        turnRight(720);
    }
}
