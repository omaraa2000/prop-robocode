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
public class Torre extends Robot {
    private static double bearingThreshold = 5;
    public void run() {
       turnLeft(getHeading());
       while(true) {
           turnGunLeft(90);
           turnRadarLeft(90);
       }
    } 
    public double normalizeBearing(double bearing) {
        while (bearing> 180) bearing -= 360;   
        while (bearing< -180) bearing+= 360; 
        return bearing;
    }
    public void onScannedRobot(ScannedRobotEvent e) {
        if (normalizeBearing(e.getBearing())<bearingThreshold) {
            fire(1);
        }
    }
    public void onHitBullet(HitByBulletEvent e) {
       //turnLeft(180);
   }
}
