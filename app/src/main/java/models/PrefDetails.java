package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PrefDetails {

        @SerializedName("id")
        @Expose
        private int id;
        @SerializedName("client_email")
        @Expose
        private String clientEmail;
        @SerializedName("case_type")
        @Expose
        private String caseType;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("location")
        @Expose
        private String location;
        @SerializedName("other")
        @Expose
        private String other;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;

        public PrefDetails(String client_email, String case_type, String description, String location, String other) {
            this.clientEmail = clientEmail;
            this.caseType = caseType;
            this.description = this.description;
            this.location = this.location;
            this.other = this.other;
        }

        public PrefDetails(int id, String clientEmail, String caseType, String description, String location) {
            this.id = id;
            this.clientEmail = clientEmail;
            this.caseType = caseType;
            this.description = description;
            this.location = location;
            this.other = other;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getClientEmail() {
            return clientEmail;
        }

        public void setClientEmail(String clientEmail) {
            this.clientEmail = clientEmail;
        }

        public String getCaseType() {
            return caseType;
        }

        public void setCaseType(String caseType) {
            this.caseType = caseType;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getOther() {
            return other;
        }

        public void setOther(String other) {
            this.other = other;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

    }



