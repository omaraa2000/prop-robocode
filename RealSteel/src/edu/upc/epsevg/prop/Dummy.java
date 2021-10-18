/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.upc.epsevg.prop;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;

/**
 *
 * @author aibar
 */
public class Dummy extends Robot {
   public void run() {
       turnLeft(getHeading());
       while(true) {
           ahead(500);
           turnRight(90);
       }
   } 
   public void onScannedRobot(ScannedRobotEvent e) {
       fire(1);
   }
   public void onHitBullet(HitByBulletEvent e) {
       //turnLeft(180);
   }
}
