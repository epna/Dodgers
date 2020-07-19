
// Include Library 
#include "FlexCAN.h"
#include <kinetis_flexcan.h>
#include <WiFiEsp.h>
#include <WiFiEspUdp.h>
#include <SoftwareSerial.h>
#include <ESP8266_Lib.h>
#include <stdlib.h>

#include <NMEA0183.h>
#include <NMEA0183Msg.h>
#include <NMEA0183Messages.h>


#ifndef __MK66FX1M0__
#error "Teensy 3.6 with dual CAN bus is required to run this example"
#endif



// Défintion WIFI 
#define ESP8266_BAUD 115200
#define HWSERIAL Serial1 //pin RX1=0, TX1=1 du TeensyLC, Teensy 3.2
SoftwareSerial EspSerial(0, 1);
ESP8266 wifi(&Serial1);
char ssid[] = "Dodgers";         // your network SSID (name)
char pass[] = "dodgers52";        // your network password
int status = WL_IDLE_STATUS;     // the Wifi radio's status

// Défintion UDP 
unsigned int localPort = 9001;  // local port to listen on
char packetBuffer[255];          // buffer to hold incoming packet
char ReplyBuffer[] = "";      // a string to send back
WiFiEspUDP Udp;
IPAddress broadcastIp(30, 30, 30, 255);

// Définition CAN  
/* 
		CAN 0 pin 3 et 4 OGA le 05/01/2020
		CAN 1 PIN 33 et  34 OGA le 05/01/2020
*/
#define BAUDRATE0 250000
#define BAUDRATE1 250000
#define BABORD 0
#define TRIBORD 1  
static CAN_message_t rxmsg;


// definition  serial sonde + GPS 
//tNMEA0183Msg NMEA0183Msg_2;
//tNMEA0183Msg NMEA0183Msg_3;
tNMEA0183 NMEA0183_2;
tNMEA0183 NMEA0183_3;




// Working Zone 
//long Indicateur [2][10]; 
//String Lib[20]; 
//int moteur; 
//long debut; 
//long ColonneDisplay = 0; 





String  EngineData[2][30];
String oldTrame;

struct str_RefJ1939
{
    char const *ref_libelle; 
    int ref_SPN; 
    long ref_PGN; 
    int ref_length; 
    int ref_start;
	char const *ref_libconversion; 
    float ref_multiplicateur;  
    char const *ref_unite;
	int offset;
};

str_RefJ1939  refJ1939[30];
// data issues des CAN 
struct TrameJ1939 
{
    long PGN; 
    unsigned long SPNvalue[8];  // Les data sont rangées de 0 à 7 
   
};
struct TrameJ1939 RecvTrame; 

void setup() {
	// initilisation Serial 
	Serial.begin(115200);
	Can0.begin(BAUDRATE0);
	Can1.begin(BAUDRATE1);
	CAN_filter_t allPassFilter;
	allPassFilter.id=0;      
	allPassFilter.ext=1;
	allPassFilter.rtr=0;    
	for (uint8_t filterNum = 4; filterNum < 16;filterNum++){
		Can0.setFilter(allPassFilter,filterNum); 
		Can1.setFilter(allPassFilter,filterNum); 
	}
	wifi.kick();
	StartWIFI(); 
    Udp.begin(localPort);
	Serial.print("Listening on port ");
	Serial.println(localPort);
	NMEA0183_2.Begin(&Serial2,2, 4800);
	NMEA0183_3.Begin(&Serial3,2, 4800);

 
	loadData(); 
}

void loop() {
	if (Can0.available()) {
		Can0.read(rxmsg);
		AnalyserTrame(rxmsg,BABORD);
	}
	GetDataTransducer(); 
	sendUDP();

}

void AnalyserTrame(CAN_message_t maTrame, int moteur)
	{
		//Extract J1939 data fields from the extended CAN ID
		uint8_t SA = (rxmsg.id & 0xFF); 				//mask the last two hex digits (8 bits) //Source Address
		uint32_t PGN = (rxmsg.id & 0x03FFFF00) >> 8; 	//Parameter Group Number

		//-------------------------------------------------------
		// exclusion volontaire
		if (SA==1 && PGN == 0xFEFC ) return; 
		if (SA==0 && (PGN==0xFEE0)) return; 
		//-------------------------------------------------------
		

		uint8_t DA;
		if (PGN >= 0xF000) DA = 0xFF; //Broadcast message to a global address
		else 
		{
			DA = (rxmsg.id & 0x0000FF00) >> 8; //Destination specific address
			PGN = (PGN & 0x03FF00); //set the PGN value to have zeros on the second byte.
		}
   
		uint8_t priority = (rxmsg.id & 0x1C000000) >> 26;
   //Serial.print ("PGN = "); 
   //Serial.print(PGN);
		for (uint8_t i = 0; i < rxmsg.len; i++) 
			{
				RecvTrame.SPNvalue[i]= rxmsg.buf[i];
			}
     //Serial.println(" "); 
		RecvTrame.PGN=PGN; 
		parseJ1939(SA); 
	}
void StartWIFI()
{
	Serial1.begin(115200);    // initialize serial for ESP module
	while(!Serial1)	{}
	WiFi.init(&Serial1);    // initialize ESP module
	if (WiFi.status() == WL_NO_SHIELD) 
	{
		Serial.println("WiFi shield not present");
		while (true); // don't continue
	}
	Serial.print("Attempting to start AP ");
	Serial.println(ssid);
	IPAddress localIp(30, 30, 30, 30);
	WiFi.configAP(localIp);
	status = WiFi.beginAP(ssid, 7,pass, ENC_TYPE_WPA2_PSK );
	Serial.println("Access point started");
	printWifiStatus();
}


void printWifiStatus()
{
	IPAddress ip = WiFi.localIP();
	Serial.print("IP Address: ");
	Serial.println(ip);
	Serial.println();
	Serial.print("To see this page in action, connect to ");
	Serial.print(ssid);
	Serial.print(" and open a browser to http://");
	Serial.println(ip);
	Serial.println();
}


void sendUDP()
{
	// if there's data available, read a packet
		String tempTrame; 
		for (int mMoteur=0; mMoteur<2; mMoteur++) 
		{
			for (int mIndexData=0; mIndexData<26; mIndexData++) 
			{
				if (EngineData[mMoteur][mIndexData]> "" ) 
				{
					tempTrame += String(mMoteur) + "," +String(mIndexData)+ "," + EngineData[mMoteur][mIndexData]+";"+"\0" ; 
				}
			}
		}
   if (oldTrame != tempTrame)
   {
    oldTrame=tempTrame;
   
		Udp.beginPacket(broadcastIp, 11111);
		char  toSend[1500]; 
		tempTrame.toCharArray(toSend, tempTrame.length()+1); 
		//Serial.print("==================");
		//Serial.println(tempTrame); 
   
		Udp.write(toSend, tempTrame.length()+1);
		Udp.endPacket();
   }
}
void parseJ1939(int moteur  )
	{
  bool PGNSelect=false;
	for (int mIndexData = 0; refJ1939[mIndexData].ref_PGN !=0; mIndexData++) 
	{ 
		if (refJ1939[mIndexData].ref_PGN == RecvTrame.PGN)
		{
		int dataStart = refJ1939[mIndexData].ref_start;
		int dataLength = refJ1939[mIndexData].ref_length; 
		long resultat=0;
		bool calcul=false; 
   PGNSelect=true;
		for (int mPosData=dataLength+dataStart-1; mPosData >= dataStart; mPosData--)
			{
				resultat +=  RecvTrame.SPNvalue[mPosData] * (pow (16,(mPosData-dataStart)*2 ));  
				calcul=true; 
			}
		if (calcul) 
			{
				resultat += refJ1939[mIndexData].offset; 
				resultat= resultat * refJ1939[mIndexData].ref_multiplicateur; 
				EngineData[moteur ][mIndexData]=String(resultat);
		  }
        
    
  	} 

    }
	if (!PGNSelect)
 {  
      return;
      if  (RecvTrame.PGN==64514) return;
      if  (RecvTrame.PGN==64527) return;
      if  (RecvTrame.PGN==64533) return;
      
      if  (RecvTrame.PGN==65256) return;
      if  (RecvTrame.PGN==65257) return;
      if  (RecvTrame.PGN==65270) return;
      if  (RecvTrame.PGN==65272) return;
      if  (RecvTrame.PGN==65283) return;

      if  (RecvTrame.PGN==65352) return;
      if  (RecvTrame.PGN==127494) return;
      if  (RecvTrame.PGN==129545) return;
      

      Serial.print(RecvTrame.PGN); 
      for (int ii=0; ii<7; ii++)
      {
        Serial.print(" ");
        Serial.print(RecvTrame.SPNvalue[ii], HEX );
        
      }
      Serial.println("");
      }
}

void loadData()
{
  refJ1939[0] = {"Engine Speed",190,61444,2,3,"0.125 rpm bit",0.125,"RPM",0 };                 
  refJ1939[1] = {"Engine Oil Pressure",100,65263,1,	3,"4 kPa/bit",4,"KPA",0};                   
  refJ1939[2] = {"Engine Coolant Temperature",110,65262,1,0,"1 deg C/bit",1,"°C",-40};
  refJ1939[3] = {"Engine Fuel Rate",183,65266,2,0,"0.05 L/h per bit",0.05,"L/H",0};
  refJ1939[4] = {"Engine Total Hours of Operation",247,65253,4,	0,"0.05 hr/bit",0.05,"H",0};
	refJ1939[5] = {"Trip distance",244,65248,4,0,"0.125 km/bit",0.125,"KM",0};
	refJ1939[6] = {"Battery potential (voltage), switched",158,65271,2,6,"0.05 V/bit",0.05,"V",0};
	refJ1939[7] = {"Fuel level",96,65276,1,1,"0.4 %/bit",2,"%",0};     ///// 2 = 0.4 *500/100% L du réservoir 
	refJ1939[8] = {"Instaneous Fuel economy",184,65266,0,2,"0.05L/h/bit",0.05,"L/H",0};
	refJ1939[9] = {"Average fuel economy",185,65266,2,2,"0.05L/h/bit",0.05,"L/H",0};
	refJ1939[10] = {"",0,0,0,0,"",0,"",0};
	Serial.println("Référentiel chargé  ");
}


void GetDataTransducer() {
	/*
	20 = speed en knots 
	21 = pos lattitue
	22 = pos longitude 
	23 = cap 	
	24 = depth 
	25 = water temp 
	*/
	tNMEA0183Msg NMEA0183Msg_2;
	if (NMEA0183_2.GetMessage(NMEA0183Msg_2)) 
		{
    String tmpCode =  NMEA0183Msg_2.MessageCode();
    
    //Serial.println(NMEA0183Msg_2);
		if (tmpCode == "RMC") 
			{
				EngineData[0][20]= NMEA0183Msg_2.Field(6);
				EngineData[0][21]= NMEA0183Msg_2.Field(2);   
				EngineData[0][22]= NMEA0183Msg_2.Field(4);   
				//EngineData[0][23]= NMEA0183Msg_2.Field(7);
        //return;
			}
     if (tmpCode == "DPT") 
     {
      EngineData[0][24]= NMEA0183Msg_2.Field(0);
      return;
     }
     if (tmpCode == "VTG") 
     {
      EngineData[0][23]= NMEA0183Msg_2.Field(2);
      return;
     }
     if (tmpCode == "MTW")
     {
      EngineData[0][25]= NMEA0183Msg_2.Field(0); 
      return;
     }
    Serial.print(tmpCode); 
    Serial.print( "  ");
    Serial.print(NMEA0183Msg_2.FieldCount());
    for(int iii=0; iii<NMEA0183Msg_2.FieldCount()-1;iii++)
    {
      Serial.print( "  ");
      Serial.print(NMEA0183Msg_2.Field(iii)); 
    }
    Serial.println("");
    }
    
		

 }
