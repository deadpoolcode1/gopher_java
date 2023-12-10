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

