#include "HX711.h"            //Módulo HX711 Conversor digital                    
#include <SoftwareSerial.h>   //Bluetooth
#include <LinkedList.h>       //\Média Gaussiana
#include <Gaussian.h>         //\Média Gaussiana
#include <GaussianAverage.h>  //\Média Gaussiana
#include "Thread.h"           //Thread
#include "ThreadController.h" //Thread



#define DOUT 7               // Definição HX711 (DATA OUT)       
#define CLK  6               // Definição HX711 (Clock)


SoftwareSerial serial1(10, 11); /// DECLARA RX, TX                       
SoftwareSerial serial(10, 11); /// DECLARA RX, TX                       

GaussianAverage myMovingAverage(20);    // Declara o tamanho da média gaussiana

HX711 bau(DOUT, CLK);                  // instancia Balança HX711 

float calibration_factor = 205265;     // fator de calibração aferido na Calibração 
int precisao = 3;                      // limita as casas decimais
float amortecimento = 0.005;           // seta para valores entre -0.002 até 0.002 = 0 
float filtrado=0;                      // variável para receber peso

ThreadController enviaDados;           // Thread pai 

Thread dadoSerial;                     // Thread filho serial
Thread dadoBluetooth;                  // Thread filho bluetooth

void setup(){
      Serial.begin(9600);                            // Inicia o serial
      serial1.begin(9600);                          /// BLUETOOTH
      bau.set_scale(calibration_factor);             // ajusta fator de calibração
      bau.tare();                                    // zera a Balança

      dadoSerial.setInterval(800);                   // seta o tempo de espera para exec.
      dadoSerial.onRun(enviaDadoSerial);             // função que deve ser chamada

      dadoBluetooth.setInterval(800);                // seta o tempo de espera para exec.
      dadoBluetooth.onRun(enviaDadoBluetooth);       // função que deve ser chamada

      enviaDados.add(&dadoSerial);                   // Anexa a Thread pai
      enviaDados.add(&dadoBluetooth);                // Anexa a Thread pai
      bau.tare();                                    // zera a Balança
      
}//end setup



void enviaDadoSerial(){ // FUNÇÃO DADOS SERIAL
  
                 Serial.print("Peso: ");
                 Serial.print(filtrado, precisao);
                 Serial.println(" kg");

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

                 
             ///////////////////////////// Amortecimento de 0
                  
                 if(filtrado !=0.000){ 
                     if (filtrado < amortecimento && filtrado > -amortecimento){
                        filtrado=0;
                     }
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
