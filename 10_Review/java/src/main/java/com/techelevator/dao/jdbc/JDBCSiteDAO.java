package com.techelevator.dao.jdbc;

import com.techelevator.dao.SiteDAO;
import com.techelevator.model.Site;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JDBCSiteDAO implements SiteDAO {

    private JdbcTemplate jdbcTemplate;

    public JDBCSiteDAO(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Site> getSitesThatAllowRVs(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = " SELECT site.site_id, site.campground_id, site.site_number, site.max_occupancy, site.max_rv_length, site.accessible, site.utilities\n" +
                "FROM site \n" +
                "JOIN campground ON campground.campground_id = site.campground_id\n" +
                "JOIN park ON campground.park_id = park.park_id\n" +
                "WHERE park.park_id = ? AND site.max_rv_length > 0;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while (results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    @Override
    public List<Site> getAvailableSites(int parkId) {
        List<Site> sites = new ArrayList<>();
        String sql = "SELECT site.site_id, site.campground_id, site.site_number, site.max_occupancy, site.max_rv_length, site.accessible, site.utilities\n" +
                "FROM site\n" +
                "JOIN campground ON site.campground_id = campground.campground_id\n" +
                "JOIN park ON campground.park_id = park.park_id\n" +
                "JOIN reservation ON reservation.site_id = site.site_id\n" +
                "WHERE park.park_id = ? AND current_date < reservation.from_date OR current_date > reservation.to_date;\n";
        /*"SELECT s.site_id, s.campground_id, site_number, max_occupancy, accessible, max_rv_length, utilities, reservation.from_date, reservation.to_date\n" +
                "                FROM site s\n" +
                "                INNER JOIN campground c ON c.campground_id = s.campground_id\n" +
                "                JOIN reservation ON reservation.site_id = s.site_id\n" +
                "                WHERE park_id = ?\n" +
                "                AND s.site_id NOT IN (\n" +
                "                SELECT site_id FROM reservation\n" +
                "                WHERE current_date  BETWEEN from_date AND to_date);";*/
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, parkId);
        while(results.next()){
            Site site = mapRowToSite(results);
            sites.add(site);
        }
        return sites;
    }

    private Site mapRowToSite(SqlRowSet results) {
        Site site = new Site();
        site.setSiteId(results.getInt("site_id"));
        site.setCampgroundId(results.getInt("campground_id"));
        site.setSiteNumber(results.getInt("site_number"));
        site.setMaxOccupancy(results.getInt("max_occupancy"));
        site.setAccessible(results.getBoolean("accessible"));
        site.setMaxRvLength(results.getInt("max_rv_length"));
        site.setUtilities(results.getBoolean("utilities"));
        return site;
    }
}
