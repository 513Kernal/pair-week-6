package com.techelevator.dao.jdbc;

import com.techelevator.dao.SiteDAO;
import com.techelevator.model.Site;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCSiteDAOTests extends BaseDAOTests {

    private SiteDAO dao;

    @Before
    public void setup() {
        dao = new JDBCSiteDAO(dataSource);
    }

    @Test
    public void getSitesThatAllowRVs_Should_ReturnSites() {
        List<Site> sites = dao.getSitesThatAllowRVs(99);

        assertEquals(2,sites.size());
    }

    @Test
    public void getAvailableSites_Should_ReturnSites() {
        List<Site> sites = dao.getAvailableSites(99);
        assertEquals(2, sites.size());

    }
    @Test
    public void getAvailableSitesDateRange_Should_ReturnSites() {
        List<Site> sites = dao.getAvaialableSitesDateRange(99, LocalDate.now().plusDays(3),
                LocalDate.now().plusDays(5));
        assertEquals(2, sites.size());
    }
}
