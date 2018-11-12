package com.mugua.enterprise.bean;

import java.io.Serializable;

/**
 * Created by Lenovo on 2017/12/7.
 */

public class HomeBean implements Serializable {
    public SayOnes[] sayOnes;
    public DynaMics[] dynamics;
    public homeActivity activity;

    public SayOnes[] getSayOnes() {
        return sayOnes;
    }

    public void setSayOnes(SayOnes[] sayOnes) {
        this.sayOnes = sayOnes;
    }

    public DynaMics[] getDynamics() {
        return dynamics;
    }

    public void setDynamics(DynaMics[] dynamics) {
        this.dynamics = dynamics;
    }

    public homeActivity getActivity() {
        return activity;
    }

    public void setActivity(homeActivity activity) {
        this.activity = activity;
    }

    public class SayOnes {
        private String id;
        private String titleOne;
        private String newType;
        private String coverPhotoOneUrl;
        private String browseNumber;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitleOne() {
            return titleOne;
        }

        public void setTitleOne(String titleOne) {
            this.titleOne = titleOne;
        }

        public String getNewType() {
            return newType;
        }

        public void setNewType(String newType) {
            this.newType = newType;
        }

        public String getCoverPhotoOneUrl() {
            return coverPhotoOneUrl;
        }

        public void setCoverPhotoOneUrl(String coverPhotoOneUrl) {
            this.coverPhotoOneUrl = coverPhotoOneUrl;
        }

        public String getBrowseNumber() {
            return browseNumber;
        }

        public void setBrowseNumber(String browseNumber) {
            this.browseNumber = browseNumber;
        }
    }

    public class DynaMics {
        private String dynamicId;
        private String dynamicTitle;
        private String dynamicContent;
        private String productId;
        private String cardId;

        public String getDynamicId() {
            return dynamicId;
        }

        public void setDynamicId(String dynamicId) {
            this.dynamicId = dynamicId;
        }

        public String getDynamicTitle() {
            return dynamicTitle;
        }

        public void setDynamicTitle(String dynamicTitle) {
            this.dynamicTitle = dynamicTitle;
        }

        public String getDynamicContent() {
            return dynamicContent;
        }

        public void setDynamicContent(String dynamicContent) {
            this.dynamicContent = dynamicContent;
        }

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public String getCardId() {
            return cardId;
        }

        public void setCardId(String cardId) {
            this.cardId = cardId;
        }
    }

    public class homeActivity {
        private String id;
        private String exists;
        private String title;
        private String content;
        private String url;
        private String photo;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getExists() {
            return exists;
        }

        public void setExists(String exists) {
            this.exists = exists;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getPhoto() {
            return photo;
        }

        public void setPhoto(String photo) {
            this.photo = photo;
        }
    }
}
