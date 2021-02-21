package com.techelevator.dao;

import com.techelevator.model.Reservation;
import com.techelevator.model.Site;

import java.time.LocalDate;
import java.util.List;

public interface SiteDAO {

    //get all sites that allow RVs, searched by park_id
    List<Site> getSitesThatAllowRVs(int parkId);
    List<Site> getAvailableSites(int siteId);
    List<Site> getAvaialableSitesDateRange(int parkId, LocalDate fromDate, LocalDate toDate);
}
