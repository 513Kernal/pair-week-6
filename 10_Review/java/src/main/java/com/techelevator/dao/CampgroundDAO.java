package com.techelevator.dao;

import com.techelevator.model.Campground;

import java.util.List;

public interface CampgroundDAO {

    //The application needs the ability to view a list of all campgrounds for a park
    List<Campground> getCampgroundsByParkId(int parkId);

}
