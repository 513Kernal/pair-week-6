using CampgroundReservations.Models;
using System;
using System.Collections.Generic;

namespace CampgroundReservations.DAO
{
    interface ISiteDAO
    {
        IList<Site> GetSitesThatAllowRVs(int parkId);
    }
}
