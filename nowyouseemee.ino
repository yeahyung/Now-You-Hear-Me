#include <SoftwareSerial.h>
#define NUMREADINGS 5 // Number of readings (increase for a slow curve)
#define MINSOUND 20   // Min sound intensity
#define MAXSOUND 300  // Max sound intensity
int TxPin = 2;
int RxPin = 3;
int leftPin = 6;
int middlePin = 10;
int rightPin = 11;
int readings[NUMREADINGS] = {0,},readings2[NUMREADINGS]={0,},readings3[NUMREADINGS]={0,};
int index = 0,index2=0,index3=0;
int total=0,total2=0,total3=0;
int average=0,average2=0,average3=0;
SoftwareSerial BTSerial(TxPin, RxPin); 
void setup() {
  // put your setup code here, to run once:
  pinMode(A0,INPUT); // ���� ���弾��
  pinMode(A1,INPUT); // �߾�
  pinMode(A2,INPUT); // ������
  
  pinMode(leftPin,OUTPUT); // ���� led
  pinMode(middlePin,OUTPUT); // �߾� led
  pinMode(rightPin,OUTPUT); // ������ led
    BTSerial.begin(9600);
  Serial.begin(9600);

}

void loop() {
int real=0,real2=0,real3=0;
 int sound = abs(analogRead(A0)-334);
 int sound2= abs(analogRead(A1)-334);
 int sound3= abs(analogRead(A2)-334);
 
total -= readings[index];     
readings[index] = abs(analogRead(A0)-334);     
total += readings[index];     
index++;       
if (index >= NUMREADINGS)     
  index = 0;         
average = abs(total / NUMREADINGS);

total2 -= readings2[index2];     
readings2[index2] = abs(analogRead(A1)-334);     
total2 += readings2[index2];     
index2++;       
if (index2 >= NUMREADINGS)     
  index2 = 0;         
average2 = abs(total2 / NUMREADINGS);

total3 -= readings3[index3];     
readings3[index3] = abs(analogRead(A2)-334);     
total3 += readings3[index3];     
index3++;       
if (index3 >= NUMREADINGS)     
  index3 = 0;         
average3 = abs(total3 / NUMREADINGS);
Serial.println(average);
Serial.println(average2);
Serial.println(average3);
if(average>average2){
  if(average>average3){   //���ʿ��� �Ҹ��� ���� Ŭ ��
    if(average>70){
      analogWrite(leftPin,HIGH); BTSerial.println("a");delay(100);}
  }
  else if(average<average3){ //�����ʿ��� �Ҹ��� ���� Ŭ ��
    if(average3>70){
      analogWrite(rightPin,HIGH); BTSerial.println("c");delay(100);}
  }
  else{   // ����, ������ ���� ������ ���� ���� ���� �� -> �տ��� �Ҹ�?
    if(average>70){
    analogWrite(leftPin,HIGH);
    analogWrite(rightPin,HIGH);
     BTSerial.println("d");delay(100);}
  }
}
else if(average<average2){
  if(average2>average3){ // �߾ӿ��� ���� �Ҹ��� Ŭ ��
    if(average2>70){
    analogWrite(middlePin,HIGH); BTSerial.println("b");delay(100);}
  }
  else if(average2<average3){ // ������
    if(average3>70){
    analogWrite(rightPin,HIGH); BTSerial.println("c");delay(100);}
  }
  else{   // �߾�, �����ʿ��� ���� �Ҹ�
    if(average3>70){
    analogWrite(rightPin,HIGH);
    analogWrite(middlePin,HIGH);delay(100);}
  }
}
else{
  if(average>70){
  if(average == average3) { // 3���⿡�� ��� ���� �Ҹ�?
    analogWrite(leftPin,HIGH);
    analogWrite(middlePin,HIGH);
    analogWrite(rightPin,HIGH);BTSerial.println("e");delay(100);
  }
  else if(average>average3){
    analogWrite(leftPin,HIGH);
    analogWrite(middlePin,HIGH);delay(100);
  }
  else{
    analogWrite(rightPin,HIGH);BTSerial.println("c");delay(100);
  }
  }
}
 delay(10);
 analogWrite(leftPin,LOW);
 analogWrite(middlePin,LOW);
 analogWrite(rightPin,LOW);
 
}