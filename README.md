# 🧩 Client-Server Application (MPP Project - Java)
A distributed **Client–Server application** developed in **Java** as part of the "Medii de Proiectare și Programare (MPP)" course at **Universitatea Babeș-Bolyai, Facultatea de Matematică și Informatică**.

## 🚀 Overview
This project demonstrates the implementation of a **multi-tier distributed system** built using **Java and Gradle**, following clean architectural principles.
It consists of:
- A **Server** component that manages data persistence, business logic, and concurrent client connections.
- A **Client** component that provides a graphical user interface (JavaFX) for interacting with the system in real time.
- Support for **REST communication**, **socket-based networking**, and **multi-threaded request handling**.

Main modules:
- **Client** — UI logic, observers, and event handling
- **Server** — handles requests from clients concurrently
- **Model** — domain entities and DTOs
- **Service** — business logic layer
- **Persistence** — repository pattern implementation (JDBC/Hibernate)
- **Networking** — custom socket protocol implementation
- **Rest** — REST API integration
- **Utils** — helper classes and configuration
- **proto** — (optional) message serialization files

## 🧩 Technologies Used
- **Java 17 (Amazon Corretto)**
- **Gradle** (build automation)
- **JavaFX** (client GUI)
- **Sockets / REST API**
- **Repository Pattern**
- **Observer Pattern**
- **MVC Architecture**

## ⚙️ How to Run

🖥️ Run the Server
Open the project in IntelliJ IDEA.
In the right sidebar, open the Gradle tab.
Navigate to:
Tasks → application → run
inside the Server module.
Click Run — this will start the server and make it listen for client connections.
🖥️ Run the Client
In IntelliJ, open the Gradle tab again.
Navigate to:
Tasks → application → run
inside the Client module.
Click Run — this will launch the JavaFX client interface.
Log in or interact with the GUI; communication with the server happens automatically via sockets or REST.

## 🧑‍💻 Author

**Goia Darius Ioan**  
📚 Student, 3rd year – Computer Science  
**Universitatea Babeș-Bolyai, Cluj-Napoca**
