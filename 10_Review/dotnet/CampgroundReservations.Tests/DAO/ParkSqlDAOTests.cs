using CampgroundReservations.DAO;
using CampgroundReservations.Models;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using System.Collections.Generic;

namespace CampgroundReservations.Tests.DAO
{
    [TestClass]
    public class ParkSqlDAOTests : BaseDAOTests
    {
        [TestMethod]
        public void GetParksTest_Should_ReturnAllParksInLocationAlphaOrder()
        {
            // Arrange
            ParkSqlDAO dao = new ParkSqlDAO(ConnectionString);

            // Act
            IList<Park> parks = dao.GetAllParks();

            // Assert
            Assert.AreEqual(2, parks.Count);
            Assert.AreEqual("Ohio", parks[0].Location);
            Assert.AreEqual("Pennsylvania", parks[1].Location);
        }
    }
}
