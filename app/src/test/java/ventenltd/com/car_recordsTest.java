package ventenltd.com;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class car_recordsTest {

    @Test
    public void filteration_function() {
        String[] tokens = {"1", "Ugonna", "Chimezie", "ugiezie@gmail.com", "Nigeria", "Tesla", "2019", "Black", "Male", "Android Developer", "Internet Entrepreneur"};
        String expected = "YES", filter_start_year = "2017", filter_end_year = "2020", filter_gender = "All", filter_colors = "All", filter_countries = "Nigeria";
        car_records cr = new car_records();
        String output = cr.filteration_function(tokens, filter_gender, filter_start_year, filter_end_year, filter_countries, filter_colors);
        assertEquals(output, expected);
    }
}