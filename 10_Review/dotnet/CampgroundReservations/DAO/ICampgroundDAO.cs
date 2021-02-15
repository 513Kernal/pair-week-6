using CampgroundReservations.Models;
using System.Collections.Generic;

namespace CampgroundReservations.DAO
{
    interface ICampgroundDAO
    {
        IList<Campground> GetCampgroundsByParkId(int parkId);
    }
}
