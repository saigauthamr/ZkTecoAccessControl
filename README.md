#ZkTecoAccessControl
Overview
This project contains a Java application designed to unlock office door access control devices using the ZKTeco protocol. The application was developed as part of a security enhancement initiative at eCorpRx, aiming to integrate with a multi-factor authentication (MFA) system.

Features
Unlocks office door access control devices using the ZKTeco protocol.
Designed for potential integration with MFA applications.
Provides a foundation for enhancing office security mechanisms.

Getting Started

Prerequisites
Java Development Kit (JDK) 8 or higher
Maven

Installation

Clone the repository:
git clone https://github.com/saigauthamr/ZkTecoAccessControl.git

cd ZkTecoAccessControl

Build the project using Maven:

mvn clean install

Usage:
Run the application:

java -jar target/ZkTecoAccessControl-1.0.jar

To unlock the door.

Configuration
Ensure the access control device is connected to the same network as the computer running the application.
Change the configuration in the main method.
Contributing
Contributions are welcome! Please fork this repository and submit pull requests to the develop branch.

License
This project is licensed under the MIT License.
