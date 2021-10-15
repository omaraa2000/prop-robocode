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

    //private byte scanDirection = 1;
    private double RadarDirection = 1.0;
    private double GunDirection;
    //private int wallMargin = 80;
    //private int tooCloseToWall = 0;
    private int moveDirection = 1;
    //private boolean CloseWall = false;
    //private int AvoidWall = 0;
    
    private int enemigoX = 0;
    private int enemigoY = 0;

    
    public void run() {
        setBodyColor(new Color(93, 193, 185));
        setGunColor(new Color (247, 191, 190));
        setRadarColor(new Color (255, 255, 255));
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot
        while(true) {
            setAhead(1000 * moveDirection);    //hacer que se mueva hacia adelante
            //CloseWall = nearWall();
            //move();
            //setTurnLeft(90);
            setTurnRadarRight (10000);        //girar el radar 360
            execute();
        }
    }

    /*
    public boolean nearWall() {
            return (
                // we're too close to the left wall
                (getX() <= wallMargin ||
                 // or we're too close to the right wall
                 getX() >= getBattleFieldWidth() - wallMargin ||
                 // or we're too close to the bottom wall
                 getY() <= wallMargin ||
                 // or we're too close to the top wall
                 getY() >= getBattleFieldHeight() - wallMargin)
            );
    }
  
    public void move() {
        if (CloseWall) {
            if (AvoidWall==0) {
                moveDirection*=-1;
                AvoidWall++;
            }
            setAhead(1000 * moveDirection);
        }
        else {
            setAhead(1000 * moveDirection);
            setTurnLeft(90);
            AvoidWall=0;
        }
   
    }*/
   

    public void onScannedRobot(ScannedRobotEvent event) {
        //scanDirection *= -1;
        //setTurnRadarRight(1000 * scanDirection);
        RadarDirection = (getHeading() - getRadarHeading() + event.getBearing());
        setTurnRadarRight(RadarDirection); //hacemos la diferencia entre el rumbo de nuestro tanque ( getHeading () ) y el rumbo de nuestro radar ( getRadarHeading () ) y agregamos el rumbo al robot escaneado ( event.getBearing () ) 
        
        //GunDirection = (getHeading() - getGunHeading() + event.getBearing ());
        //setTurnGunRight(GunDirection);
        Disparar(event);

        //setTurnRight (event.getBearing () + 90);
        if (getGunHeat() == 0 && Math.abs (getGunTurnRemaining())<10) {
            PotenciaDisparo(event);
        }
        //fire(Rules.MAX_BULLET_POWER);
        execute();
       
    }
    public void onHitByBullet(HitByBulletEvent event) {
        setTurnLeft(180);
    } 
    
    public void Disparar(ScannedRobotEvent event) {
         double tiempo = TiempoBala(event);
         double xEnemiga = FuturaXenemigo(tiempo, event);
         double yEnemiga = FuturaYenemigo(tiempo, event);
         double anguloGiro = AnguloAbsolutoEnemigo(getX(), getY(), xEnemiga, yEnemiga);
         setTurnGunRight(normalizeBearing(anguloGiro - getGunHeading()));
         if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
            
            double potencia = 500 / event.getDistance();
            if (potencia > Rules.MAX_BULLET_POWER) {
                potencia = Rules.MAX_BULLET_POWER;
            }
            setFire(potencia);
        }
        RadarDirection *= -1;
        setTurnRadarRight(10000 * RadarDirection);
    }

    public void PotenciaDisparo (ScannedRobotEvent event) {
        double distance = event.getDistance();
            if(distance<200) {
              fire(3.5);
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
        double arcCos = Math.toDegrees(Math.acos(distanciaY / h));
        double angulo = 0;
        
        if (distanciaX > 0 && distanciaY >0)  angulo = arcSin;
        else if (distanciaX > 0 && distanciaY < 0) angulo = arcCos;
        else angulo = 360 - arcCos;
        
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
     
    public double TiempoBala(ScannedRobotEvent event) {
        double potencia = 500 / event.getDistance();
        if (potencia > Rules.MAX_BULLET_POWER) {
            potencia = Rules.MAX_BULLET_POWER;
        }
        // Aplicamos formula y averiguamos la velocidad de la bala a partir de su potencia
        double velocidad = 20 - potencia * 3;
        // distancia = velocidad * tiempo donde aislamos el tiempo
        return (long) (event.getDistance() / velocidad);
    }
    
    
    //Funcion para calcular la posicion X futura del enemigo
    public double FuturaXenemigo(double tiempo, ScannedRobotEvent e) {
        return enemigoX + Math.sin(Math.toRadians(e.getHeading())) * e.getVelocity() * tiempo;
    }
    
    //Funcion para calcular la posicion Y futura del enemigo
     public double FuturaYenemigo(double tiempo, ScannedRobotEvent e) {
        return enemigoY + Math.cos(Math.toRadians(e.getHeading())) * e.getVelocity() * tiempo;
    }

    
    public void onHitWall(HitWallEvent event){
        if (event.getBearing() > -90 && event.getBearing() <= 90) {
           setBack(180);
           setTurnLeft(180);
       } else {
           setAhead(180);
           setTurnLeft(180);
       }/*
        double bearing = e.getBearing(); //get the bearing of the wall
        setTurnRight(-bearing); //This isn't accurate but release your robot.
        setBack(100); //The robot goes away from the wall.
        */
        execute();
    }
    
}
