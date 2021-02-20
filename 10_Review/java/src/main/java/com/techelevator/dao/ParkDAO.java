package com.techelevator.dao;

import com.techelevator.model.Park;

import java.util.List;

public interface ParkDAO {

    //returns a list of all parks, sorted by Location name in alphabetical order
    List<Park> getAllParks();
}
