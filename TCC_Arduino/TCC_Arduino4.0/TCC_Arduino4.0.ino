#include "HX711.h"            //Módulo HX711 Conversor digital                    
#include <SoftwareSerial.h>   //Bluetooth
#include <LinkedList.h>       //\Média Gaussiana
#include <Gaussian.h>         //\Média Gaussiana
#include <GaussianAverage.h>  //\Média Gaussiana
#include "Thread.h"           //Thread
#include "ThreadController.h" //Thread
#include <SimpleKalmanFilter.h> //Filtro de kalman




#define DOUT 7               // Definição HX711 (DATA OUT)       
#define CLK  6               // Definição HX711 (Clock)


SoftwareSerial serial1(10, 11); /// DECLARA RX, TX                       
SoftwareSerial serial(10, 11); /// DECLARA RX, TX                       

GaussianAverage myMovingAverage(25);    // Declara o tamanho da média gaussiana

SimpleKalmanFilter simpleKalmanFilter(0.01, 0.01, 0.1); //Declara o Filtro de Kalman
                                                           //(erro/nSei/precisão)

HX711 bau(DOUT, CLK);                  // instancia Balança HX711 

float calibration_factor = 205265;     // fator de calibração aferido na Calibração 
int precisao = 3;                      // limita as casas decimais
float amortecimento = 0.005;           // seta para valores entre -0.002 até 0.002 = 0 
float filtrado=0;                      // variável para receber peso
float filtroKalman=0;                  // declara variavel de valor do kalman
float escolhaFiltro=0.010;             // se erro maior q 0,010 usa Kalman
float k1=0;                            // operador do kalman (n-1)
                 

ThreadController enviaDados;           // Thread pai 

Thread dadoSerial;                     // Thread filho serial
Thread dadoBluetooth;                  // Thread filho bluetooth

void setup(){
      Serial.begin(9600);                            // Inicia o serial
      serial1.begin(9600);                          /// BLUETOOTH
      bau.set_scale(calibration_factor);             // ajusta fator de calibração
      bau.tare();                                    // zera a Balança

      dadoSerial.setInterval(200);                   // seta o tempo de espera para exec.
      dadoSerial.onRun(enviaDadoSerial);             // função que deve ser chamada

      dadoBluetooth.setInterval(200);                // seta o tempo de espera para exec.
      dadoBluetooth.onRun(enviaDadoBluetooth);       // função que deve ser chamada

      enviaDados.add(&dadoSerial);                   // Anexa a Thread pai
      enviaDados.add(&dadoBluetooth);                // Anexa a Thread pai
      bau.tare();                                    // zera a Balança
      
}//end setup



void enviaDadoSerial(){ // FUNÇÃO DADOS SERIAL
                 float operadork=0;
                 float resultadok=0;              // operador para botar resultados
                 float resultadoG=0;              // operador para botar resultados
                 
                 Serial.print("Gauss: ");
                 Serial.print(filtrado, precisao);
                 Serial.print(" kg Kalman: ");
                 Serial.print(filtroKalman, precisao);
                 Serial.print(" kg");
                 resultadok=filtroKalman;
                 resultadoG=filtrado;
                 operadork = filtroKalman-amortecimento;
                 
                 if (((abs(k1-operadork))<escolhaFiltro) && ((abs(resultadok-resultadoG))<escolhaFiltro)){
                          
                            
                            
                            Serial.print(" Precisão Gauss: ");
                            Serial.print(resultadoG, precisao);
                            Serial.println(" kg");
                       
                 }
                 else{
                      Serial.print(" Precisão Kalman: ");
                      Serial.print(resultadok, precisao);
                      Serial.println(" kg");
                  }
                 
                 k1=filtroKalman;
                 
}//end enviaDadoSerial



void enviaDadoBluetooth(){ // FUNÇÃO DADOS BLUETOOTH
  
            /// BLUETOOTH
                 serial1.print("{");  
                 serial1.print(filtrado, precisao);
                 serial1.println("}");
                 
}//end enviaDadoBluetooth



void loop(){
                 float value = bau.get_units();
                 
             ///////////////////////////// Média Gaussiana
                 myMovingAverage += value;
                 myMovingAverage.process();
                 filtrado = myMovingAverage.mean;


             ///////////////////////////// Kalman

                 filtroKalman = simpleKalmanFilter.updateEstimate(bau.get_units());
                 
             ///////////////////////////// Amortecimento de 0
                  
                 if(filtrado !=0.000 && (filtrado < amortecimento && filtrado > -amortecimento)){ 
                        filtrado=0;
                        
                 }
                 
                 if(filtroKalman !=0.000 && (filtroKalman < amortecimento && filtroKalman > -amortecimento)){ 
                       
                        filtroKalman=0;
                 }

                 
             ///////////////////////////// Chama Threads
             
                enviaDados.run();           // Vai printar os dados no Bt e no Serial


                
            ///////////////////////////// Tare
            
                if (Serial.available()|| serial1.available()){    // se a serial estiver disponivel
                
                    char temp = Serial.read(); // le carcter da serial 
                    char temp1 = serial1.read(); // lê o que a pessoa escreve
                    
                        if (temp == 't' || temp == 'T' || temp1 =='t'|| temp1 == 'T'){                  // se pressionar t ou T
                              //Serial.print("{");
                              //serial1.print("{");
                              bau.tare();                      // zera a balança
                              Serial.println("Z");             // imprime na serial
                              //Serial.print("}");
                              //serial1.print("}");
                        }
                }
  }//end loop
