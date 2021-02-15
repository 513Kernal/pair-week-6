using CampgroundReservations.Models;
using System.Collections.Generic;

namespace CampgroundReservations.DAO
{
    interface IParkDAO
    {
        IList<Park> GetAllParks();
    }
}
