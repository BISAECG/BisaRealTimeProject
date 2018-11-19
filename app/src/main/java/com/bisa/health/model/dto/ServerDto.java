package com.bisa.health.model.dto;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/7/30.
 */

public class ServerDto implements Serializable{


        private String phoneCode;
        private String countryCode;
        private String domain;
        private String shopserver;
        private String datserver;
        private int status;
        private String time_zone;
        private String en_country;
        private String country;

        public String getDatserver() {
            return datserver;
        }
        public void setDatserver(String datserver) {
            this.datserver = datserver;
        }
        public String getPhoneCode() {
            return phoneCode;
        }

        public void setPhoneCode(String phoneCode) {
            this.phoneCode = phoneCode;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(String countryCode) {
            this.countryCode = countryCode;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getTime_zone() {
            return time_zone;
        }

        public void setTime_zone(String time_zone) {
            this.time_zone = time_zone;
        }

        public String getEn_country() {
            return en_country;
        }

        public void setEn_country(String en_country) {
            this.en_country = en_country;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getShopserver() {
            return shopserver;
        }

        public void setShopserver(String shopserver) {
            this.shopserver = shopserver;
        }



}
