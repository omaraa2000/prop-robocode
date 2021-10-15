package edu.upc.epsevg.prop;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.*;
import java.awt.Color;


/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author aibar
 */
public class Atom extends AdvancedRobot {

    private byte RadarDirection = 1;
    //private double GunDirection;

    private byte moveDirection = 1;
    private double enemigoX = 0;
    private double enemigoY = 0;

    
    public void run() {
        
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot
        
        setBodyColor(new Color(93, 193, 185));
        setGunColor(new Color (247, 191, 190));
        setRadarColor(new Color (255, 255, 255));
        
        while(true) {
           setAhead(1000 * moveDirection);    //hacer que se mueva hacia adelante
           setTurnRadarRight (10000);        //girar el radar 360
           execute(); 
            
        }
    }


    public void onScannedRobot(ScannedRobotEvent event) {
        setStop(true);
        execute();
        //setTurnRadarRight(1000 * scanDirection);
        setTurnRadarRight(getHeading() - getRadarHeading() + event.getBearing()); //hacemos la diferencia entre el rumbo de nuestro tanque ( getHeading () ) y el rumbo de nuestro radar ( getRadarHeading () ) y agregamos el rumbo al robot escaneado ( event.getBearing () ) 
        //GunDirection = (getHeading() - getGunHeading() + event.getBearing ());
        //setTurnGunRight(GunDirection);
        Disparar(event);
        //setTurnRight (event.getBearing () + 90);
        //fire(Rules.MAX_BULLET_POWER);
        
    }
    
    //Evento para cuando choca con una bala
    public void onHitByBullet(HitByBulletEvent event) {
        setTurnLeft(180);
    } 
    
    //Funcion que permite diparar teniendo en cuenta la posicion futura del enemigo
    public void Disparar(ScannedRobotEvent event) {
         double time = TiempoBala(event);
         PosicionActualEnemigo(event.getBearing(), event.getDistance());
         double xEnemiga = FuturaXenemigo(time, event);
         double yEnemiga = FuturaYenemigo(time, event);
         double anguloGiro = AnguloAbsolutoEnemigo(getX(), getY(), xEnemiga, yEnemiga);
         setTurnGunRight(normalizeBearing(anguloGiro - getGunHeading()));
         if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            PotenciaDisparo(event);
        }
        RadarDirection *= -1;
        setTurnRadarRight(10000 * RadarDirection);
    }

    //Funcion para calcular la potencia del disparo segun la distancia a la que estamos del enemigo
    public void PotenciaDisparo (ScannedRobotEvent event) {
        double distance = event.getDistance();
            if(distance<200) {
              fire(Rules.MAX_BULLET_POWER);
              setBulletColor(new Color (255, 0, 0));
           }
           else if(distance<500) {
              fire(2.5);
              setBulletColor(new Color (255, 128, 0));
           }
           else if(distance<800) {
              fire(1.5);
              setBulletColor(new Color (255, 233, 0));
           }
           else {
              fire(0.5);
              setBulletColor(new Color (255, 255, 255));
              
           }
    }
    
    //Funcion para calcular el angulo absoluto respecto al enemigo
    public double AnguloAbsolutoEnemigo (double xA, double yA, double xE, double yE) {
        double distanciaX = xE-xA; //cateto 1
        double distanciaY = yE-yA; //cateto2
        double cuadradoX = Math.pow(distanciaX,2); //cuadrado del cateto 1
        double cuadradoY = Math.pow(distanciaY,2); //cuadrado del cateto 2
        double h = Math.sqrt(cuadradoX + cuadradoY); //hipotensa
        double arcSin = Math.toDegrees(Math.asin(distanciaX / h));
        //double arcCos = Math.toDegrees(Math.acos(distanciaY / h));
        double angulo = 0;
        
        if (distanciaX> 0 && distanciaY>0)  angulo = arcSin;
        else if (distanciaX<0 && distanciaY>0) angulo = 360+arcSin;
        else if (distanciaX>0 && distanciaY<0) angulo=180-arcSin;
        else if (distanciaX<0 && distanciaY<0) angulo=180-arcSin;
        //else angulo = 360 - arcCos;
        
        return angulo;       
    }
    
    //Funcion para convertir un angulo en el intervalo de -180 y 180 grados.
     public double normalizeBearing(double ang) {
        while (ang> 180) {
            ang -= 360;
        }
        while (ang< -180) {
            ang+= 360;
        }
        return ang;
    }
    

     
    //Funcion para calcular el tiempo que tarda la bala en llegar al enemigo 
    public double TiempoBala(ScannedRobotEvent event) {
        double potencia = 500 / event.getDistance();
        if (potencia > Rules.MAX_BULLET_POWER) {
            potencia = Rules.MAX_BULLET_POWER;
        }
        double velocity = 20 - potencia * 3;
        return (long) (event.getDistance() / velocity);
    }
    
    public void PosicionActualEnemigo(double orientation, double distance) {

        double angle = Math.toRadians(getHeading() + orientation % 360);

        enemigoX = (getX() + Math.sin(angle) * distance);
        enemigoY = (getY() + Math.cos(angle) * distance);
    }
    //Funcion para calcular la posicion X futura del enemigo
    public double FuturaXenemigo(double tiempo, ScannedRobotEvent event) {
        return enemigoX + Math.sin(Math.toRadians(event.getHeading())) * event.getVelocity() * tiempo;
    }
    
    //Funcion para calcular la posicion Y futura del enemigo
     public double FuturaYenemigo(double tiempo, ScannedRobotEvent event) {
        return enemigoY + Math.cos(Math.toRadians(event.getHeading())) * event.getVelocity() * tiempo;
    }

    //Evento para cuando choca con la pared
    public void onHitWall(HitWallEvent event){
        if (event.getBearing() > -90 && event.getBearing() <= 90) {
           setBack(180);
           setTurnLeft(180);
       } else {
           setAhead(180);
           setTurnLeft(180);
       }
        execute();
        /*
        double bearing = e.getBearing(); //get the bearing of the wall
        setTurnRight(-bearing); //This isn't accurate but release your robot.
        setBack(100); //The robot goes away from the wall.
        */
        
    }
    
}
