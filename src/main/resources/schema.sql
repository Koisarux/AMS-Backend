-- View: flight_booking_summary
CREATE OR REPLACE VIEW flight_booking_summary AS
SELECT 
    f.flight_number,
    f.origin,
    f.destination,
    f.departure_time,
    f.arrival_time,
    f.price,
    f.total_seats,
    f.available_seats,
    (f.total_seats - f.available_seats) AS booked_seats
FROM flights f //

-- Trigger: decrement available seats after booking
DROP TRIGGER IF EXISTS after_booking_insert //

CREATE TRIGGER after_booking_insert
AFTER INSERT ON bookings
FOR EACH ROW
BEGIN
    UPDATE flights
    SET available_seats = available_seats - NEW.number_of_tickets
    WHERE flight_number = NEW.flight_number;
END //

-- Procedure: cancel_booking
DROP PROCEDURE IF EXISTS cancel_booking //

CREATE PROCEDURE cancel_booking(IN b_id BIGINT)
BEGIN
    DECLARE v_flight_number VARCHAR(255);
    DECLARE v_tickets INT;

    SELECT flight_number, number_of_tickets INTO v_flight_number, v_tickets
    FROM bookings
    WHERE booking_id = b_id;

    UPDATE flights
    SET available_seats = available_seats + v_tickets
    WHERE flight_number = v_flight_number;

    DELETE FROM bookings
    WHERE booking_id = b_id;
END //
