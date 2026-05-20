mvn -pl server exec:java
# ChatApp

A simple Java-based client-server chat application for local networks. It supports text messaging between multiple clients, file upload/download, and a basic Swing GUI for client and server administration.

## Features
- Multi-client chat server
- Swing GUI for client (`client/src/main/java/app/ClientMain.java`) and server (`server/src/main/java/app/ServerMain.java`)
- File transfer support 
- Lightweight SQLite-based server storage (see `database/chatapp.sql`)

## Project Structure
- `server/` – server application, UI, networking, and database code
- `client/` – client application, GUI, networking, upload/download services
- `shared/` – common packet and config classes used by both client and server
- `database/` – SQL schema and local DB resources

## Requirements
- Java 11 or newer
- Maven
## Build
From the repository root run:

```
mvn clean package
```

To run only the server or client modules using Maven Exec:

```
mvn -pl server exec:java -Dexec.mainClass="app.ServerMain"
mvn -pl client exec:java -Dexec.mainClass="app.ClientMain"
```

## Quickstart
1. Start the server.
2. Launch one or more clients and log in.
3. Exchange messages or transfer files via the GUI.

## Notes
- Default configuration values are in `shared/src/constants/Config.java` and `server/src/util/Constants.java`.
- Received files are saved to `recv_files/` and downloaded files go to `downloads/`.

## License
No license specified. Add a `LICENSE` file to define reuse terms.

---