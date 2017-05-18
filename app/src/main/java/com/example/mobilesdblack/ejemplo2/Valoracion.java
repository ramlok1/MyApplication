package com.example.mobilesdblack.ejemplo2;

/**
 * Created by MobileSD Black on 15/08/2016.
 */
public class Valoracion {

        private float ratingStar;
        private String name;
        private String country;

        public Valoracion(int ratingStar, String name, String country) {
            this.ratingStar = ratingStar;
            this.name = name;
            this.country = country;
        }

        public float getRatingStar() {
            return ratingStar;
        }

        public void setRatingStar(float ratingStar) {
            this.ratingStar = ratingStar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

}
