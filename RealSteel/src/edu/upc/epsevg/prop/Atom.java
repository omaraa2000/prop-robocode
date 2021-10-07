/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package edu.upc.epsevg.prop;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.Rules;
import robocode.ScannedRobotEvent;

/**
 *
 * @author aibar
 */


public class Atom extends Robot {
    public void run() {
        turnLeft(getHeading());
        while(true) {
            ahead(1000);
            turnRight(90);
        }
    }
    public void onScannedRobot(ScannedRobotEvent event) {
        //fire(1);
        fire(Rules.MAX_BULLET_POWER);
    }
    public void onHitByBullet(HitByBulletEvent event) {
        turnLeft(180);
    }
}
