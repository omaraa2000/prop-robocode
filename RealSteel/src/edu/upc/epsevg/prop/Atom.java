package edu.upc.epsevg.prop;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.AdvancedRobot;
import robocode.Rules;
import robocode.ScannedRobotEvent;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aibar
 */
public class Atom extends AdvancedRobot {

    private byte scanDirection = 1;

    public void run() {
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot
        while(true) {
            setAhead(1000);                    //hacer que se mueva
            setTurnRadarRight (360);      //girar el radar en sentido positivo
            execute();
        }
    }
   
    public void onScannedRobot(ScannedRobotEvent event) {
        scanDirection *= -1;
        setTurnRadarRight(1000 * scanDirection);
        //fire(Rules.MAX_BULLET_POWER);
        execute();
    }
    public void onHitByBullet(HitByBulletEvent event) {
        setTurnLeft(180);
    }

}
