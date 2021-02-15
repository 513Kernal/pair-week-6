using CampgroundReservations.DAO;
using CampgroundReservations.Models;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace CampgroundReservations.Tests.DAO
{
    [TestClass]
    public class CampgroundSqlDAOTests : BaseDAOTests
    {
        [TestMethod]
        public void GetCampgroundsTest_Should_ReturnAllCampgrounds()
        {
            // Arrange
            CampgroundSqlDAO dao = new CampgroundSqlDAO(ConnectionString);

            // Act
            IList<Campground> campgrounds = dao.GetCampgroundsByParkId(ParkId);

            // Assert
            Assert.AreEqual(2, campgrounds.Count);
        }
    }
}
