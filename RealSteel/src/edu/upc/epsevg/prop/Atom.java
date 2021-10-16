package edu.upc.epsevg.prop;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.AdvancedRobot;
import robocode.HitWallEvent;
import robocode.Rules;
import robocode.ScannedRobotEvent;
import robocode.*;
import java.awt.Color;
import java.awt.geom.Rectangle2D;


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
    private int margin = 80;
    private int innerSquare = margin + 80;
    private double desplazamientoY;
    private double desplazamientoX;
    private boolean Wall=false;
    private int CloseWall=0;
    
    public void run() {
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot
        
        setBodyColor(new Color(93, 193, 185));
        setGunColor(new Color (247, 191, 190));
        setRadarColor(new Color (255, 255, 255));
        
        while(true) {
          setAhead(1000 * moveDirection);    //hacer que se mueva hacia adelante
           setTurnRadarRight (360);        //girar el radar 360
           execute(); 
            
        }
    }

    //Evento para cuando detectamos el robot
    public void onScannedRobot(ScannedRobotEvent event) {
        if(event.getName() == "Crazy") setTurnRight(event.getBearing()); //Perseguir al robot enemigo
        else setTurnRight(event.getBearing() + 90 - (20 * moveDirection)); //Perseguir al robot enemigo
        setTurnRadarRight(getHeading() - getRadarHeading() + event.getBearing()); //hacemos la diferencia entre el rumbo de nuestro tanque ( getHeading () ) y el rumbo de nuestro radar ( getRadarHeading () ) y agregamos el rumbo al robot escaneado ( event.getBearing () ) 
        DispararEnemigo(event);
        execute();
    }
    
    //Evento para cuando choca con una bala
    public void onHitByBullet(HitByBulletEvent event) {
        setStop(true);
        setAhead(1000*moveDirection);
        execute();
        
    } 
    
    //Evento para cuando choca con la pared
    public void onHitWall(HitWallEvent event){
        moveDirection *= -1;
    }
  
    
    //Funcion que permite diparar teniendo en cuenta la posicion futura del enemigo
    public void DispararEnemigo(ScannedRobotEvent event) {
         double potencia = PotenciaDisparo(event);
         double time = TiempoBala(event, potencia);
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
    public double PotenciaDisparo (ScannedRobotEvent event) {
        double distance = event.getDistance(); //obtener la distancia respecto al enemigo
        double potencia;
            //Si la distancia en menor a 200 pixeles disparamos con la maxima potencia y la bala sera de color rojo
            if(distance<100) {
              potencia=Rules.MAX_BULLET_POWER;
              fire(potencia);
              setBulletColor(new Color (255, 0, 0));
              
           }
            //Si la distancia en menor a 500 pixeles disparamos con la una potencia de 3.5 y la bala sera de color naranja
           else if(distance<300) {
              potencia=2.0;
              fire(potencia);
              setBulletColor(new Color (255, 128, 0));
           }
            //Si la distancia en menor a 800 pixeles disparamos con la una potencia de 1.5 y la bala sera de color amarillo
           else if(distance<900) {
              potencia=1.0;
              fire(potencia);
              setBulletColor(new Color (255, 233, 0));
           }
            //Si la distancia es superior a 800 pixeles disparamos con la una potencia de 0.5 y la bala sera de color blanco
           else {
              potencia=0.5;
              fire(potencia);
              setBulletColor(new Color (255, 255, 255));
              
           }
            return potencia;
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
    public double TiempoBala(ScannedRobotEvent event, double potencia) {
        double distance = event.getDistance(); //obtener la distancia respecto al enemigo
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
