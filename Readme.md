# Gopher
A software used as mediator between database based API and communication devices

# Using scripts

. modular-tools is a generic script utility used for several options, use:

. modular-tools.sh help 

mainlly used tools : 

. modular-tools.sh setup_database     - installs local database used for testing

. modular-tools.sh database_test - generic test for the local and remote database, read write abilities

. modular-tools.sh database_create_initial_table - creates the Gopher specific database structure

. modular-tools.sh draw_data - creates graphical database representation

![Database Diagram](Documentation/database.png "Database Diagram")

# Compile software

make clean 

make

# Run software

./gopher

# Software flow

continually monitor database for new lines in two tables: read_data, write_data

## read_data: 

check each line with "True" on column "request_pending", gopher will "fetch" this

line info and performed the described task according to the rest of table parameters

when done it will update fields: 

timestamp_pending_process - when command processing started by gopher

timestamp_reply_arrived - when action performed by gopher completed

error_code - the action error code (0 no error)

notice data will be saved at table called read_data_info which contains line for each incoming data:

id int [pk, increment] // Auto-incrementing primary key

read_data_id int [ref: > read_data.id] // References id in read_data

timestamp datetime // Automatically updated on insert

data text

it will keep logging untill field in table read_data, "request_to_stop" is set to 1

## write_data: 

check each line with "True" on column "request_pending", gopher will "fetch" this

line info and performed the described task according to the rest of table parameters

when done it will update fields: 

timestamp_pending_process - when command processing started by gopher

timestamp_reply_arrived - when action performed by gopher completed

error_code - the action error code (0 no error)

value1-N - values used to compose command data

## LMDS protocol

notice LMDS protocol is used for communication to device, this is protocol documentation

##testing functionality

sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "INSERT INTO read_data (request_pending, com, baud_rate, ip, port, tcp, udp, reply_pending, timestamp_pending_issued, timestamp_pending_process, timestamp_reply_arrived, timestamp_reply_acknowledged, error_code, read_data) VALUES (1, 'COM1', 9600, '192.168.1.1', 1234, 1, 0, 0, GETDATE(), GETDATE(), GETDATE(), GETDATE(), 0, 'Sample data');"

### check trigger functionality

sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "SELECT TOP 1 id, timestamp_pending_issued FROM read_data ORDER BY id DESC;"

sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "UPDATE read_data SET request_pending = 0 WHERE id = 12;"

sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "SELECT timestamp_pending_issued FROM read_data WHERE id = 12;"

### check read functionality
sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "INSERT INTO read_data (request_pending, com, baud_rate, ip, port, tcp, udp, reply_pending, timestamp_pending_issued, timestamp_pending_process, timestamp_reply_arrived, timestamp_reply_acknowledged, error_code, read_data) VALUES (1, 'COM1', 9600, '192.168.1.1', 1234, 1, 0, 0, GETDATE(), GETDATE(), GETDATE(), GETDATE(), 0, 'Sample data');"


sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "SELECT * FROM read_data_info ORDER BY id DESC;"



sqlcmd -S localhost -U SA -P 'Ss6399812' -d my_data -Q "UPDATE read_data SET request_to_stop = 1 WHERE id = 4;"

## java install

sudo apt install maven
nano ~/.bashrc
export MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"
source ~/.bashrc


# maven local install

mvn install:install-file -Dfile=/home/administrator/java/gopher/IP_Radio_Interface.jar -DgroupId=local -DartifactId=ip-radio-interface -Dversion=1.0 -Dpackaging=jar

## maven create deploy

mvn clean install

mvn clean compile assembly:single

## maven run on windows PC

Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://


choco install openjdk17

choco install maven

create directory with the files:

gopher-1.0-SNAPSHOT-jar-with-dependencies.jar

Parameters.json


run using: 

java -jar gopher-1.0-SNAPSHOT-jar-with-dependencies.jar

## Release notes

### version 1.20
```
New Features:
Conditional Logging for Incoming and Outgoing LMDS Messages:
Incoming Messages: The system now records incoming LMDS messages when in simulated mode, enhancing debugging and system monitoring.
Outgoing Messages: Added functionality to record outgoing LMDS messages in a JSON format. Each message is logged with a unique log number for easy tracking and reference.
Both features are conditional on the MConfig.getFakeDatabase() setting, which is designed for use in testing environments.
Enhancements:
Centralized Logging:

Both incoming and outgoing messages are logged with detailed information, including headers and message bodies, in a structured JSON format.
Efficient Logging Mechanism:

Consecutive messages with the same msg_code are not logged to prevent redundancy, ensuring that the logs are concise and relevant.
How to Run:
Activating Logging:
To enable logging for both incoming and outgoing messages, ensure the system is configured to use a fake database by setting the fakeDatabase parameter to true.
No additional runtime parameters are needed. The system automatically starts logging based on the configuration.
What Will It Log:
Incoming Messages (Simulated Mode):

Log entries for incoming messages include details such as message length, checksum validity, and the entire message content in a human-readable format.
Files are named according to the port they are associated with, following the pattern <port_name>.txt.
Outgoing Messages:

Each log entry contains:
log_number: A sequential identifier for each log entry.
LMDS_HDR: The header of the LMDS message in JSON format.
MessageBody: The body of the LMDS message in JSON format.
Files are named outgoing_lmds<portIx>.txt where <portIx> is the index of the port used.
File Location:
Logs are stored in the running directory of the application.
Incoming messages are logged to files named after their respective ports.
Outgoing messages are logged to files named outgoing_lmds<portIx>.txt.
Important Notes:
The logging feature is primarily intended for development and testing. Continuous logging in a production environment might impact performance and disk usage.
```
