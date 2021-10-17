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
    
    /**
     * Funcionamiento principal del robot: Movemos el robot hacia delante y giramos
     * el radar 360º constantemente.
     */
    
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
    
    /**
     * Cuando detectamos un robot enemigo enfocamos el radar hacia el y miramos 
     * si el enemigo es Crazy o otro. Si es crazy lo seguimos de cerca y si no lo 
     * seguimos en dirección perpendicular a él con un poco de desviamiento y en 
     * los dos casos disparamos.
     * @param event Es el robot escaneado.
     */

    //Evento para cuando detectamos el robot
    public void onScannedRobot(ScannedRobotEvent event) {
        if(event.getName() == "Crazy") setTurnRight(event.getBearing()); // Si el enemigo es el Crazy lo perseguimos de muy cerca
        else setTurnRight(event.getBearing() + 90 - (20 * moveDirection)); //Perseguir al robot enemigo con una distancia prudente siempre que no sea Crazy
        setTurnRadarRight(getHeading() - getRadarHeading() + event.getBearing()); //hacemos la diferencia entre el rumbo de nuestro tanque ( getHeading () ) y el rumbo de nuestro radar ( getRadarHeading () ) y agregamos el rumbo al robot escaneado ( event.getBearing () ) 
        DispararEnemigo(event);
        execute();
    }
    
    /**
     * En caso de que una bala nos alcance paramos lo que estamos haciendo y seguimos 
     * hacia delante.
     * @param event Es la bala que nos toca.
     */
    
    //Evento para cuando choca con una bala
    public void onHitByBullet(HitByBulletEvent event) {
        setStop(true);
        setAhead(1000*moveDirection);
        execute();
        
    } 
    
    /**
     * En caso de chocar con una pared cambiamos el sentido de nuestro rumbo.
     * @param event Es la pared con la que chocamos.
     */
    
    //Evento para cuando choca con la pared
    public void onHitWall(HitWallEvent event){
        moveDirection *= -1;
    }
    
    /**
     * Se encarga de disparar al enemigo calculando la posición futura indicando 
     * la potencia.
     * Para calcular la posición futura necesitamos el tiempo que tarda la bala 
     * en llegar y la posición actual del enemigo. Sabiendo esto podemos calcular 
     * el angulo en el cual tenemos que disparar y lo haremos siempre y cuando el
     * arma no este demasiado caliente y nos aseguremos que estamos apuntando donde 
     * queremos disparar.
     * @param event Indica el robot escaneado.
     */
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
    }

    /**
     * Nos indica la potencia con la cual disparamos segun a la distancia a la que estamos.<br>
     * Si estamos a una distencia menor de 100 pixeles dispara con la maxima potencia 
     * y las balas son de color rojo.<br>
     * Si estamos a menos de 300 pixeles disparamos con una potencia de 2.0 y la 
     * bala es de color naranja.<br>
     * Si estamos a menos de 900 pixeles disparamos con una potencia de 1.0 y la
     * bala es de color amarillo.<br>
     * En los demas casos disparamos con una potencia de 0.5 y la bala es de color blanca.
     * @param event Nos indica el robot escaneado.
     * @return Nos devuelve la potencia del disparo.
     */
    
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
    
    /**
     * Calcula el angulo absoluto repecto nuestra posiciom actual y la posicion 
     * futura del enemigo.
     * Para calcular el angulo necesitaremos saber cual es la hipotenusa y hacer 
     * el arcoseno de ella.
     * Una vez tenemos el arcoseno calculado el ángulo dependerá de la diferencia 
     * de nuestra posición con la del enemigo.
     * @param xA Nuestra posicion X actual
     * @param yA Nuestra posicion Y actual
     * @param xE Futura posición X del enemigo
     * @param yE Futura posición Y del enemigo
     * @return Devuelve el angulo absoluto entre nuestra posición actual y la posicion 
     * futura del enemigo.
     */
    
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
    
    /**
     * Hace que el angulo este en el intervalo de -180 a 180.
     * @param bearing Es el angulo que queremos convertir
     * @return Devuelve la conversion del angulo
     */
    
    //Funcion para hacer que el angulo este en el intervalo de -180 y 180 grados.
     public double normalizeBearing(double bearing) {
        while (bearing> 180) bearing -= 360;   
        while (bearing< -180) bearing+= 360; 
        return bearing;
    }
    
     /**
      * Calcula el tiempo que tarda la bala en llegar al enemigo. Para ello 
      * necesitamos tener la distancia a la que estamos del enemigo y la velocidad 
      * de la bala. 
      * La distancia la obtenemos con el metodo getDistance(), y la velocidad se 
      * calcula segun la potencia v = 20-potencia*3.
      * Una vez tenemos esta información dividimos la distancia entre la velocidad 
      * para tener el tiempo.
      * @param event Nos indica el robot enemigo escaneado.
      * @param potencia Nos indica la potencia de la bala.
      * @return Devuelve el tiempo calculado que tarda la bala en llegar al enemigo.
      */
    //Funcion para calcular el tiempo que tarda la bala en llegar al enemigo 
    public double TiempoBala(ScannedRobotEvent event, double potencia) {
        double distance = event.getDistance(); //obtener la distancia respecto al enemigo
        double velocity = 20 - potencia * 3; //calcular la velocidad de la bala
        long time = (long)(distance / velocity); //calcular el tiempo que tarda em llegar la bala
        return time;
    }
    
    /**
     * Nos indica cual es la posicion actual del enemigo, y para ello necesitamos 
     * saber el ángulo actual entre nosotros y el robot enemigo. 
     * Para calcular el angulo necesitaremos saber nuestro rumbo y el rumbo de 
     * nuestro enemigo, si sumamos estos dos valores aplicando un modulo de 360 
     * podremos saber el angulo actual. 
     * Sabiendo el angulo podemos saber las coordenadas aplicando seno para la X 
     * y coseno para la Y.
     * @param orientation Indicamos el rumbo de nuestro enemigo que obtenemos con el metodo getBearing().
     * @param distance Indicamos la distancia que tenemos respecto a nuestro enemigo.
     */
    
    //Funcion para saber la posicion actual del enemigo
    public void PosicionActualEnemigo(double orientation, double distance) {

        double angle = Math.toRadians(getHeading() + orientation % 360);

        enemigoX = (getX() + Math.sin(angle) * distance);
        enemigoY = (getY() + Math.cos(angle) * distance);
    }
}
