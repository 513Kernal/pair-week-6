package com.techelevator.dao.jdbc;

import com.techelevator.dao.ReservationDAO;
import com.techelevator.model.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCReservationDAO implements ReservationDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCReservationDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int createReservation(int siteId, String name, LocalDate fromDate, LocalDate toDate) {
        String sql = "INSERT INTO reservation (site_id, name, from_date, to_date, create_date)\n" +
                    "VALUES (?,?,?,?,?)\n" +
                    "RETURNING reservation_id;";
        int id =  jdbcTemplate.queryForObject(sql, Integer.class, siteId, name, fromDate, toDate, LocalDate.now());
        System.out.println("Your confirmation ID is: " + id);
        return id;
    }

    @Override
   public List<Reservation> getUpcomingReservationsForPark(int parkId){
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT reservation.reservation_id, reservation.site_id, reservation.name, reservation.from_date, reservation.to_date, reservation.create_date\n" +
                "FROM reservation\n" +
                "JOIN site ON site.site_id = reservation.site_id\n" +
                "JOIN campground ON campground.campground_id = site.campground_id\n" +
                "JOIN park ON park.park_id = campground.park_id\n" +
                "WHERE reservation.from_date <= current_date + INTERVAL '30 DAYS' AND reservation.from_date >= current_date AND park.park_id = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()){
            Reservation reservation = mapRowToReservation(results);
            reservations.add(reservation);
        }
        return reservations;
    }


  /*  public List<Reservation> getUpcomingReservations(){

    } */

    private Reservation mapRowToReservation(SqlRowSet results) {
        Reservation r = new Reservation();
        r.setReservationId(results.getInt("reservation_id"));
        r.setSiteId(results.getInt("site_id"));
        r.setName(results.getString("name"));
        r.setFromDate(results.getDate("from_date").toLocalDate());
        r.setToDate(results.getDate("to_date").toLocalDate());
        r.setCreateDate(results.getDate("create_date").toLocalDate());
        return r;
    }


}
