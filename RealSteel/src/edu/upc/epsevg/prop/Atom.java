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
    
    public void run() {
        setTurnLeft(getHeading());
        setAdjustGunForRobotTurn (true);       //activamos el cañon del robot indepenciente del robot
        setAdjustRadarForRobotTurn(true);      //activamos el radar del robot indepenciente del robot
        
        setBodyColor(new Color(93, 193, 185)); //Color del tanque turquesa
        setGunColor(new Color (247, 191, 190)); //color del cañon rosa
        setRadarColor(new Color (255, 255, 255)); //color del radar blanco
        
        while(true) {
           setAhead(1000 * moveDirection);    //hacer que se mueva hacia adelante
           setTurnRadarRight (360);        //girar el radar 360
           execute(); 
            
        }
    }

    //Evento para cuando detectamos el robot
    public void onScannedRobot(ScannedRobotEvent event) {
        if(event.getName() == "Crazy") setTurnRight(event.getBearing()); // Si el enemigo es el Crazy lo perseguimos de muy cerca
        else setTurnRight(event.getBearing() + 90 - (20 * moveDirection)); //Perseguir al robot enemigo con una distancia prudente siempre que no sea Crazy
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
            fire(potencia);
        }
        RadarDirection *= -1;
        setTurnRadarRight(10000 * RadarDirection);
    }

    //Funcion para calcular la potencia del disparo segun la distancia a la que estamos del enemigo
    public double PotenciaDisparo (ScannedRobotEvent event) {
        double distance = event.getDistance(); //obtener la distancia respecto al enemigo
        double potencia;
            //Si la distancia en menor a 100 pixeles disparamos con la maxima potencia y la bala sera de color rojo
            if(distance<100) {
              potencia=Rules.MAX_BULLET_POWER;
              setBulletColor(new Color (255, 0, 0));
              
           }
            //Si la distancia en menor a 300 pixeles disparamos con la una potencia de 2.0 y la bala sera de color naranja
           else if(distance<300) {
              potencia=2.0;
              setBulletColor(new Color (255, 128, 0));
           }
            //Si la distancia en menor a 900 pixeles disparamos con la una potencia de 1.0 y la bala sera de color amarillo
           else if(distance<900) {
              potencia=1.0;
              setBulletColor(new Color (255, 233, 0));
           }
            //Si la distancia es superior a 900 pixeles disparamos con la una potencia de 0.5 y la bala sera de color blanco
           else {
              potencia=0.5;
              setBulletColor(new Color (255, 255, 255));
              
           }
            return potencia;
    }
    
    //Funcion para calcular el angulo absoluto respecto al enemigo
    public double AnguloAbsolutoEnemigo (double xA, double yA, double xE, double yE) {
        //Calculo de la hipotenusa
        double Cateto1 = xE-xA; //cateto 1
        double Cateto2 = yE-yA; //cateto2
        double cuadradoX = Math.pow(Cateto1,2); //cuadrado del cateto 1
        double cuadradoY = Math.pow(Cateto2,2); //cuadrado del cateto 2
        double h = Math.sqrt(cuadradoX + cuadradoY); //hipotensa
        
        //Calculo del arcoseno
        double arcSinRadians =(Math.asin(Cateto1 / h)); //calculo del arcoseno en radianes
        double arcSinGrados = Math.toDegrees(arcSinRadians); //conversion del arcoseno en grados
        double angulo = 0;
        
        if (Cateto1> 0 && Cateto2>0)  angulo = arcSinGrados;
        else if (Cateto1<0 && Cateto2>0) angulo = 360+arcSinGrados;
        else if (Cateto1>0 && Cateto2<0) angulo=180-arcSinGrados;
        else if (Cateto1<0 && Cateto2<0) angulo=180-arcSinGrados;
        
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
