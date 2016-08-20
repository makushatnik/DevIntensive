package com.softdesign.devintensive.data.network.res;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ageev Evgeny on 19.08.2016.
 */
public class LikeRes {
    @SerializedName("success")
    @Expose
    public boolean success;
    @SerializedName("data")
    @Expose
    private List<LikeData> data = new ArrayList<>();

    public List<LikeData> getData() {
        return data;
    }

    public class LikeData {
        @SerializedName("homeTask")
        @Expose
        private int homeTask;
        @SerializedName("projects")
        @Expose
        private int projects;
        @SerializedName("linesCode")
        @Expose
        private int linesCode;
        @SerializedName("rait")
        @Expose
        private int rait;

        @SerializedName("likesBy")
        @Expose
        private List<LikesBy> likesList = new ArrayList<>();

        @SerializedName("updated")
        @Expose
        private String updated;
        @SerializedName("rating")
        @Expose
        private int rating;

        public List<LikesBy> getLikesList() {
            return likesList;
        }
    }

    public class LikesBy {
        @SerializedName("_id")
        @Expose
        private String id;
    }
}
