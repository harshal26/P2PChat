# P2PChat



#Project Title
Peer to Peer Chat application using Bluetooth or Wi-Fi Direct

#Team Member
Harshal Shah (013675114)

#Problem definition and motivation

To develop an android peer to peer secure chat application which uses Bluetooth or Wi-Fi direct to connect peer device and transfer of chat text message , audio file etc. for short range.
Today we are using many chat application which generally uses cloud or network as a middle layer. Instead we can use Bluetooth or Wi-Fi direct to connect device and we can securely transfer data for short range.
Goal is to provide the better authenticated connection using ZRTP between two devices and transfer data for short range. To provide confidentiality in data transfer. If possible I will try to implement it for multiple peers.
Only limitation here is, it is useful for short range data transfer.

#Solution Approach:

•	Establish connection between two devices using Bluetooth or WIFI Direct

•	Encryption of sending data using Crypto library on the sender side.

•	Decryption of Receiving data using Crypto library on the receiver side

•	Retrieval of actual data

#Implementation Details

Android Bluetooth API and Java

Android Crypto API 

Platform: Android and JAVA

ZRTP : ZRTP (composed of Z and Real-time Transport Protocol) is a cryptographic key-agreement protocol to negotiate the keys for encryption between two end points in a Voice over Internet Protocol (VoIP) phone telephony call based on the Real-time Transport Protocol. It uses Diffie–Hellman key exchange and the Secure Real-time Transport Protocol (SRTP) for encryption. (Wikipedia)

#Timeline (Approximate)
•	Learn Android API and other require fundamentals : September
•	Create GUI and other structure : September
•	Learning Bluetooth and establishing connection : October
•	Study Crypto library and implementation : October – November
•	Testing of application - November
•	Implementation for multiple user (if possible) – November-December
