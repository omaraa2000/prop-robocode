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

    private byte moveDirection = 1;
    private double enemigoX = 0;
    private double enemigoY = 0;
    private int margin = 60;
    double desplazamientoY;
    double desplazamientoX;

    
    public void run() {
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot
        
        setBodyColor(new Color(93, 193, 185));
        setGunColor(new Color (247, 191, 190));
        setRadarColor(new Color (255, 255, 255));
        
        while(true) {
           setAhead(1000 * moveDirection);    //hacer que se mueva hacia adelante
          // moviment();
           setTurnRadarRight (360);        //girar el radar 360
           execute(); 
            
        }
    }

    //Evento para cuando detectamos el robot
    public void onScannedRobot(ScannedRobotEvent event) {
        setTurnRight(event.getBearing() + 90 - (20 * moveDirection)); //Perseguir al robot enemigo
        setTurnRadarRight(getHeading() - getRadarHeading() + event.getBearing()); //hacemos la diferencia entre el rumbo de nuestro tanque ( getHeading () ) y el rumbo de nuestro radar ( getRadarHeading () ) y agregamos el rumbo al robot escaneado ( event.getBearing () ) 
        Disparar(event);
        execute();
    }
    
    //Evento para cuando choca con una bala
    public void onHitByBullet(HitByBulletEvent event) {
        setAhead(1000*moveDirection);
        setTurnLeft(90);
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

    //Funcion para movernos de una forma determinada
    public void moviment () { 
        /*
        desplazamientoY = (getBattleFieldHeight()-getY()- margin*moveDirection);
        setAhead(desplazamientoY);
        desplazamientoX = (getBattleFieldWidth()-(getX()+ margin)*moveDirection);
        setAhead(desplazamientoX);
        setTurnRight(45);
        */
        //setAhead(1000 * moveDirection);
    }
    
    //Funcion que permite diparar teniendo en cuenta la posicion futura del enemigo
    public void Disparar(ScannedRobotEvent event) {
         double time = TiempoBala(event);
         PosicionActualEnemigo(event.getBearing(), event.getDistance()); 
         double xEnemiga = enemigoX + Math.sin(Math.toRadians(event.getHeading())) * event.getVelocity() * time; //calcular la posicion X futura del enemigo
         double yEnemiga = enemigoY + Math.cos(Math.toRadians(event.getHeading())) * event.getVelocity() * time; //calcular la posicion Y futura del enemigo
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
        double distance = event.getDistance(); //obtener la distancia respecto al enemigo
            //Si la distancia en menor a 200 pixeles disparamos con la maxima potencia y la bala sera de color rojo
            if(distance<200) {
              fire(Rules.MAX_BULLET_POWER);
              setBulletColor(new Color (255, 0, 0));
           }
            //Si la distancia en menor a 500 pixeles disparamos con la una potencia de 3.5 y la bala sera de color naranja
           else if(distance<500) {
              fire(2.5);
              setBulletColor(new Color (255, 128, 0));
           }
            //Si la distancia en menor a 800 pixeles disparamos con la una potencia de 1.5 y la bala sera de color amarillo
           else if(distance<800) {
              fire(1.5);
              setBulletColor(new Color (255, 233, 0));
           }
            //Si la distancia es superior a 800 pixeles disparamos con la una potencia de 0.5 y la bala sera de color blanco
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
        double arcSinRadians =(Math.asin(distanciaX / h)); //calculo del arcoseno en radianes
        double arcSinGrados = Math.toDegrees(arcSinRadians); //cpnversion del arcoseno en grados
        double angulo = 0;
        
        if (distanciaX> 0 && distanciaY>0)  angulo = arcSinGrados;
        else if (distanciaX<0 && distanciaY>0) angulo = 360+arcSinGrados;
        else if (distanciaX>0 && distanciaY<0) angulo=180-arcSinGrados;
        else if (distanciaX<0 && distanciaY<0) angulo=180-arcSinGrados;
        
        return angulo;       
    }
    
    //Funcion para hacer que el angulo este en el intervalo de -180 y 180 grados.
     public double normalizeBearing(double bearing) {
        while (bearing> 180) bearing -= 360;   
        while (bearing< -180) bearing+= 360; 
        return bearing;
    }
    
     
    //Funcion para calcular el tiempo que tarda la bala en llegar al enemigo 
    public double TiempoBala(ScannedRobotEvent event) {
        double distance = event.getDistance(); //obtener la distancia respecto al enemigo
        double potencia = 500 / distance; //calcular la potencia de la bala
        //Si la potencia es superior a la maxima, diremos que es la maxima
        if (potencia > Rules.MAX_BULLET_POWER) {
            potencia = Rules.MAX_BULLET_POWER;
        }
        double velocity = 20 - potencia * 3; //calcular la velocidad de la bala
        long time = (long)(distance / velocity); //calcular el tiempo que tarda em llegar la bala
        return time;
    }
    
    //Funcion para saber la posicion actual del enemigo
    public void PosicionActualEnemigo(double orientation, double distance) {

        double angle = Math.toRadians(getHeading() + orientation % 360);

        enemigoX = (getX() + Math.sin(angle) * distance);
        enemigoY = (getY() + Math.cos(angle) * distance);
    }
}
