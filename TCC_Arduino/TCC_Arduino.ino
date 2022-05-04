#include "HX711.h"            //Balança                    
#include <SoftwareSerial.h>   ///Bluetooth

#define DOUT 7                      
#define CLK  6

SoftwareSerial serial1(10, 11); /// DECLARA RX, TX                       
SoftwareSerial serial(10, 11); /// DECLARA RX, TX                       

HX711 bau(DOUT, CLK);             // instancia Balança HX711 

float calibration_factor = 188810;     // fator de calibração aferido na Calibração 
String textoRecebido = "";      ///PARA BLUETOOTH
unsigned long delay1 = 0;       ///PARA BLUETOOTH


void setup()
{
      Serial.begin(9600); 
      bau.set_scale(calibration_factor);             // ajusta fator de calibração
      serial1.begin(9600); /// BLUETOOTH
      bau.tare();  // zera a Balança
      bau.tare();  // zera a Balança
}


void loop(){
                /// BLUETOOTH
                 //Serial.print("{");
                 serial1.print("{");  
                 Serial.println(bau.get_units(), 3);
                 serial1.print(bau.get_units(), 3);
                 //Serial.print("}");
                 serial1.println("}");
                 delay(2000);
       
                if (Serial.available()|| serial1.available()){                            // se a serial estiver disponivel
                
                    char temp = Serial.read(); // le carcter da serial 
                    char temp1 = serial1.read(); // lê o que a pessoa escreve
                    
                        if (temp == 't' || temp == 'T' || temp1 =='t'|| temp1 == 'T'){                  // se pressionar t ou T
                              //Serial.print("{");
                              serial1.print("{");
                              bau.tare();                                // zera a balança
                              Serial.println("Z");             // imprime no monitor serial
                              //Serial.print("}");
                              serial1.print("}");
                        }
                }
  }
