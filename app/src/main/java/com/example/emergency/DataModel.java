package com.example.emergency;

    public class DataModel {

        String name;
        String city;
        String date;
        String category;
        String image;

        public DataModel(String name, String city, String date, String category,String image ) {
            this.name=name;
            this.city=city;
            this.date=date;
            this.category=category;
            this.image=image;

        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public String getDate() {
            return date;
        }

        public String getCategory() {
            return category;
        }
        public String getImage() {
            return image;
        }

    }
