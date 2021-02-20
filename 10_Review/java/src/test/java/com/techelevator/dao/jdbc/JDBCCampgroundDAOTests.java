package com.techelevator.dao.jdbc;

import com.techelevator.dao.CampgroundDAO;
import com.techelevator.model.Campground;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class JDBCCampgroundDAOTests extends BaseDAOTests {

    private CampgroundDAO dao;

    @Before
    public void setup() {
        dao = new JDBCCampgroundDAO(dataSource);
    }

    @Test
    public void getCampgrounds_Should_ReturnAllCampgrounds() {
        List<Campground> campgrounds = dao.getCampgroundsByParkId(99);

        assertEquals(2,campgrounds.size());

        //make sure it ONLY returns campgrounds for relevant search id

        List<Campground> campgroundsModified = dao.getCampgroundsByParkId(1);
        assertEquals(0, campgroundsModified.size());
    }

}
