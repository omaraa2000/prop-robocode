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
    private double RadarDirection;
    private double GunDirection;

    public void run() {
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot
        while(true) {
            setAhead(1000);                    //hacer que se mueva hacia adelante
            setTurnRadarRight (360);        //girar el radar 360
            execute();
        }
    }
   
    public void onScannedRobot(ScannedRobotEvent event) {
        //scanDirection *= -1;
        //setTurnRadarRight(1000 * scanDirection);
        RadarDirection = (getHeading() - getRadarHeading() + event.getBearing());
        setTurnRadarRight(RadarDirection); //hacemos la diferencia entre el rumbo de nuestro tanque ( getHeading () ) y el rumbo de nuestro radar ( getRadarHeading () ) y agregamos el rumbo al robot escaneado ( event.getBearing () ) 
        GunDirection = RadarDirection * (math.PI/180);
        getGunHeadingRadians(GunDirection);
        fire(Rules.MAX_BULLET_POWER);
        execute();
    }
    public void onHitByBullet(HitByBulletEvent event) {
        setTurnLeft(180);
    }

}
