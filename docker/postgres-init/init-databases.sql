create user user_Service_user with password 'zAnfmWjabuctTIJC3FhQ';
create user flight_service_user with password 'F8wLqDEs06bPorUnV2WI';
create user booking_service_user with password '16cwSDG8hBAEIsqZx7RU';

create database userdb owner user_Service_user;
create database flightdb owner flight_service_user;
create database bookingdb owner booking_service_user;

grant all privileges on database userdb to user_service_user;
grant all privileges on database flightdb to flight_service_user;
grant all privileges on database bookingdb to booking_service_user;