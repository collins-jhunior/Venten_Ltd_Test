package ventenltd.com;

class CarData {
    private String first_name, last_name, email, country, model, color, gender, title, bio;
    private int year;

    CarData(String first_name, String last_name, String email, String country, String model, String color, String gender, String title, String bio, int year) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.country = country;
        this.model = model;
        this.color = color;
        this.gender = gender;
        this.title = title;
        this.bio = bio;
        this.year = year;
    }

    String getFirst_name() {
        return first_name;
    }

    String getLast_name() {
        return last_name;
    }

    String getEmail() {
        return email;
    }

    String getCountry() {
        return country;
    }

    String getModel() {
        return model;
    }

    String getColor() {
        return color;
    }

    String getGender() {
        return gender;
    }

    String getTitle() {
        return title;
    }

    String getBio() {
        return bio;
    }

    int getYear() {
        return year;
    }
}
