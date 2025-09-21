
package tests;

import api.booker.BookingAPI;
import api.pojo.Booking;
import io.restassured.response.Response;
import listeners.ExtentReportExtension;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(ExtentReportExtension.class)
@Tag("Booking_Regression")

public class BookingTest {

    private static final Logger logger = LoggerFactory.getLogger(BookingTest.class);
    //This comment from the Local Project
    //This is a comment from the remote Repo
    static BookingAPI bookingAPI;
    static int bookingId;

    @BeforeAll
    static void beforeAll() {
        bookingAPI =  new BookingAPI();
    }

//    @Disabled
//    @Test
//    void getBooking() {
//
//        Response response = given().contentType(ContentType.JSON)
//                .when().log().all().get("https://restful-booker.herokuapp.com/booking/2");
//
//        logger.info(response.asString());
//
//        Assertions.assertNotNull(response);
//
//        Assertions.assertEquals(200,response.statusCode(),"Get Booking Status Code is not 200");
//
//    }

    @Test
    @Order(1)
    void createBooking() {

        Booking booking = new Booking("Teresa","Lopez",126,true,
                "2025-10-10","2025-10-17","Lunch");


        Response response = bookingAPI.createBooking(booking);

        Assertions.assertEquals(200,response.statusCode(),"Failed: Status code was not 200.");

        bookingId = response.jsonPath().getInt("bookingid");
        logger.info("bookingId: "+bookingId);
        Assertions.assertTrue(bookingId>0,"Failed: Booking Id should be greater than 0.");

        String firstName = response.jsonPath().getString("booking.firstname");
        logger.info("firstName: "+firstName);
        Assertions.assertEquals(booking.getFirstname(),firstName,"Failed: firstName is incorrect.");

        String lastName = response.jsonPath().getString("booking.lastname");
        logger.info("lastName: "+lastName);
        Assertions.assertEquals(booking.getLastname(),lastName,"Failed: lastName is incorrect.");

        int totalPrice = response.jsonPath().getInt("booking.totalprice");
        logger.info("totalPrice: "+totalPrice);
        Assertions.assertEquals(booking.getTotalprice(),totalPrice,"Failed: Price is incorrect.");

        //Implementar el Get

    }

    @Test
    @Order(2)
    void testGetBooking() {

        Response response = bookingAPI.getBooking(bookingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(200,response.statusCode(),"Failed: Status code was not 200.");

        String firstName = response.jsonPath().getString("firstname");
        logger.info("firstName: "+firstName);
        Assertions.assertEquals("Teresa",firstName,"Failed: FirstName is not correct");

    }

    @Test
    @Order(3)
    void updateBooking() {

        Booking booking = new Booking("Teresa","Martinez",126,true,
                "2025-10-10","2025-10-17","Lunch");

        Response response = bookingAPI.updateBooking(booking,bookingId,bookingAPI.getToken());

        Assertions.assertEquals(200,response.statusCode(),"Failed: Status code was not 200.");

        String updatedLastName = response.jsonPath().getString("lastname");
        logger.info("updatedLastName: "+updatedLastName);
        Assertions.assertEquals(booking.getLastname(),updatedLastName,
                "Failed: Updated Lastname is not correct");
    }

    @Test
    @Order(4)
    void deleteBooking() {

        String token = bookingAPI.getToken();
        Response response = bookingAPI.deleteBooking(bookingId,token);
        Assertions.assertEquals(201,response.statusCode());
    }

    @Test
    @Order(5)
    void verifyBookingWasDeleted() throws InterruptedException {

        Thread.sleep(1000); //Waiting 1sec for the booking to be deleted before checking
        Response response = bookingAPI.getBooking(bookingId);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(404,response.statusCode(),"Failed: Status code was not 404.");

    }


    @Test
    void createAndUpdateBooking() {

        Booking booking = new Booking("Teresa","Martinez",126,true,
                "2025-10-10","2025-10-17","Lunch");

        // Crear booking
        Response create_response = bookingAPI.createBooking(booking);
        logger.info("create_response: "+ create_response.asString());

        Assertions.assertEquals(200, create_response.statusCode(),
                "Failed to create booking: " + create_response.statusCode());

        int newBookingId = create_response.jsonPath().getInt("bookingid");
        logger.info("bookingId: " + newBookingId);

        // Actualizar booking
        booking.setLastname ("Rojas");

        String token = bookingAPI.getToken();
        Response response = bookingAPI.updateBooking(booking, newBookingId,token);

        logger.info("Update response status: " + response.statusCode());
        String responseBody = response.asString();
        logger.info("Update response body: " + responseBody);

        Assertions.assertEquals(200, response.statusCode(), "Failed: Status code was not 200.");

        try {
            String updatedLastName = response.jsonPath().getString("lastname");
            logger.info("updatedLastName: " + updatedLastName);
            Assertions.assertEquals(booking.getLastname(), updatedLastName,
                    "Failed: Updated Lastname is not correct");
        } catch (Exception e) {
            logger.error("Failed to parse JSON response: " + e.getMessage());
            logger.error("Response body was: " + responseBody);
            throw e;
        }
    }

}