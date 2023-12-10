#!/bin/bash

# Your SQL Server password
LOCAL_PASSWORD='Ss6399812'
DB_USER="SA"
DB_PASS="Ss6399812"
DB_NAME="my_data"



setup_database() {
# Install curl if it's not already installed
if ! command -v curl &> /dev/null; then
    echo "Installing curl..."
    sudo apt-get install curl
fi

# Install lsb-release if it's not already installed
if ! command -v lsb_release &> /dev/null; then
    echo "Installing lsb-release..."
    sudo apt-get install lsb-release
fi

# Install Microsoft SQL tools and unixODBC developer package
echo "Installing Microsoft SQL tools and unixODBC developer package..."
curl https://packages.microsoft.com/keys/microsoft.asc | sudo apt-key add -
curl https://packages.microsoft.com/config/ubuntu/$(lsb_release -rs)/prod.list | sudo tee /etc/apt/sources.list.d/msprod.list
sudo apt-get update
sudo apt-get install -y mssql-tools unixodbc-dev
echo 'export PATH="$PATH:/opt/mssql-tools/bin"' >> ~/.bashrc
source ~/.bashrc

# Install Microsoft SQL Server
echo "Installing Microsoft SQL Server..."
sudo add-apt-repository "$(wget -qO- https://packages.microsoft.com/config/ubuntu/18.04/mssql-server-2019.list)"
sudo apt-get update
sudo apt-get install -y mssql-server

# Configure Microsoft SQL Server
echo "Configuring Microsoft SQL Server..."
sudo /opt/mssql/bin/mssql-conf setup
}

database_clear_initial_table() {
    # SQL Command to drop the database
    SQL="DROP DATABASE IF EXISTS $DB_NAME;"

    # Execute SQL Command to drop the database
    sqlcmd -S localhost -U $DB_USER -P $DB_PASS -Q "$SQL"
    echo "Database $DB_NAME has been deleted."
}

database_create_initial_table() {
    # Create the database
    sqlcmd -S localhost -U $DB_USER -P $DB_PASS -Q "CREATE DATABASE $DB_NAME;"

    # SQL Commands to create tables, triggers, and the new read_data_info table
    SQL="USE $DB_NAME;
         CREATE TABLE read_data (
             id INT IDENTITY(1,1) PRIMARY KEY,
             request_pending BIT,
             request_to_stop BIT,
             com VARCHAR(255),
             baud_rate INT,
             ip VARCHAR(255),
             port INT,
             tcp BIT,
             udp BIT,
             reply_pending BIT,
             timestamp_pending_issued DATETIME,
             timestamp_pending_process DATETIME,
             timestamp_reply_arrived DATETIME,
             timestamp_reply_acknowledged DATETIME,
             error_code INT,
             read_data TEXT
         );
         GO
         CREATE TRIGGER trg_update_read_timestamp
         ON read_data
         AFTER UPDATE
         AS
         BEGIN
             SET NOCOUNT ON;
             IF UPDATE(request_pending) OR UPDATE(reply_pending)
             BEGIN
                 UPDATE read_data
                 SET timestamp_pending_issued = CASE WHEN INSERTED.request_pending = 1 THEN GETDATE() ELSE read_data.timestamp_pending_issued END,
                     timestamp_reply_arrived = CASE WHEN INSERTED.reply_pending = 1 THEN GETDATE() ELSE read_data.timestamp_reply_arrived END,
                     timestamp_reply_acknowledged = CASE WHEN INSERTED.reply_pending = 0 THEN GETDATE() ELSE read_data.timestamp_reply_acknowledged END
                 FROM read_data INNER JOIN INSERTED ON read_data.id = INSERTED.id;
             END
         END;
         GO
         CREATE TABLE write_data (
             id INT IDENTITY(1,1) PRIMARY KEY,
             request_pending BIT,
             com VARCHAR(255),
             baud_rate INT,
             ip VARCHAR(255),
             port INT,
             tcp BIT,
             udp BIT,
             reply_pending BIT,
             error_code INT,
             timestamp_pending_issued DATETIME,
             timestamp_pending_process DATETIME,
             timestamp_reply_arrived DATETIME,
             timestamp_reply_acknowledged DATETIME
         );
         GO
         CREATE TRIGGER trg_update_write_timestamp
         ON write_data
         AFTER UPDATE
         AS
         BEGIN
             SET NOCOUNT ON;
             IF UPDATE(request_pending) OR UPDATE(reply_pending)
             BEGIN
                 UPDATE write_data
                 SET timestamp_pending_issued = CASE WHEN INSERTED.request_pending = 1 THEN GETDATE() ELSE write_data.timestamp_pending_issued END,
                     timestamp_reply_arrived = CASE WHEN INSERTED.reply_pending = 1 THEN GETDATE() ELSE write_data.timestamp_reply_arrived END,
                     timestamp_reply_acknowledged = CASE WHEN INSERTED.reply_pending = 0 THEN GETDATE() ELSE write_data.timestamp_reply_acknowledged END
                 FROM write_data INNER JOIN INSERTED ON write_data.id = INSERTED.id;
             END
         END;
         GO
         CREATE TABLE read_data_info (
             id INT IDENTITY(1,1) PRIMARY KEY,
             read_data_id INT,
             timestamp DATETIME DEFAULT GETDATE(),
             data TEXT,
             FOREIGN KEY (read_data_id) REFERENCES read_data(id)
         );
         GO"

    # Execute SQL Commands to create tables, triggers, and the read_data_info table
    sqlcmd -S localhost -U $DB_USER -P $DB_PASS -Q "$SQL"
}



draw_data() {
    echo "----------------------------------------------"
    echo "Instructions:"
    echo "1. Open your web browser and navigate to https://dbdiagram.io/d"
    echo "2. Copy the following DSL code."
    echo "3. Paste the DSL code into the dbdiagram.io editor to generate the diagram."
    echo "----------------------------------------------"

    # Output the DSL code for the database schema
    echo "// Database: $DB_NAME"
    echo "Table read_data {"
    echo "  id int [pk, increment]"  # Auto-incrementing primary key
    echo "  request_pending Boolean // Triggers update on timestamp_pending_issued when set to true"
    echo "  request_to_stop Boolean"
    echo "  com varchar(255)"
    echo "  baud_rate int"
    echo "  ip varchar(255)"
    echo "  port int"
    echo "  tcp Boolean"
    echo "  udp Boolean"
    echo "  reply_pending Boolean // Triggers update on timestamp_reply_arrived and timestamp_reply_acknowledged"
    echo "  timestamp_pending_issued datetime // Automatically updated when request_pending is set"
    echo "  timestamp_pending_process datetime"
    echo "  timestamp_reply_arrived datetime // Automatically updated when reply_pending is set to true"
    echo "  timestamp_reply_acknowledged datetime // Automatically updated when reply_pending is set to false"
    echo "  error_code int"
    echo "  read_data text"
    echo "}"
    
    echo "Table write_data {"
    echo "  id int [pk, increment]"  # Auto-incrementing primary key
    echo "  request_pending Boolean // Triggers update on timestamp_pending_issued when set to true"
    echo "  com varchar(255)"
    echo "  baud_rate int"
    echo "  ip varchar(255)"
    echo "  port int"
    echo "  tcp Boolean"
    echo "  udp Boolean"
    echo "  reply_pending Boolean // Triggers update on timestamp_reply_arrived and timestamp_reply_acknowledged"
    echo "  error_code int"
    echo "  timestamp_pending_issued datetime // Automatically updated when request_pending is set"
    echo "  timestamp_pending_process datetime"
    echo "  timestamp_reply_arrived datetime // Automatically updated when reply_pending is set to true"
    echo "  timestamp_reply_acknowledged datetime // Automatically updated when reply_pending is set to false"
    echo "}"

    echo "Table read_data_info {"
    echo "  id int [pk, increment] // Auto-incrementing primary key"
    echo "  read_data_id int [ref: > read_data.id] // References id in read_data"
    echo "  timestamp datetime // Automatically updated on insert"
    echo "  data text"
    echo "}"

    echo "----------------------------------------------"
    echo "End of DSL code."
    echo "----------------------------------------------"
}



help()
{
	echo "setup_database - initial database setup"
	echo "database_test - test connection to local and remote database"
	echo "database_create_initial_table - create first database"
    echo "database_clear_initial_table - totaly delete Gopher database"
	echo "draw_data - draw database scheme"
}

if [ -n "$*" ]; then
	eval "$*" # execute arguments
	#echo $* finished, ret=$?
else
	if [ "$0" != "$BASH_SOURCE" ]; then
		help
	else
		echo $BASH_SOURCE - none
		help
	fi
fi
