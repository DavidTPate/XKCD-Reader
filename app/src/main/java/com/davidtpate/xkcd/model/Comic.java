package com.davidtpate.xkcd.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

public class Comic implements Parcelable {
    @SerializedName("num") Integer number;
    @SerializedName("title") String title;
    @SerializedName("safe_title") String safeTitle;
    @SerializedName("alt") String subTitle;
    @SerializedName("img") String imageUrl;
    @SerializedName("transcript") String transcript;
    @SerializedName("link") String link;
    @SerializedName("month") String month;
    @SerializedName("day") String day;
    @SerializedName("year") String year;

    public Comic(Parcel source) {
        if (source != null) {
            number = source.readInt();
            title = source.readString();
            safeTitle = source.readString();
            subTitle = source.readString();
            imageUrl = source.readString();
            transcript = source.readString();
            link = source.readString();
            month = source.readString();
            day = source.readString();
            year = source.readString();
        }
    }

    public Integer getNumber() {
        return number;
    }

    public String getTitle() {
        return title;
    }

    public String getSafeTitle() {
        return safeTitle;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTranscript() {
        return transcript;
    }

    public String getLink() {
        return link;
    }

    public String getMonth() {
        return month;
    }

    public String getDay() {
        return day;
    }

    public String getYear() {
        return year;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     * May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(number);
        dest.writeString(title);
        dest.writeString(safeTitle);
        dest.writeString(subTitle);
        dest.writeString(imageUrl);
        dest.writeString(transcript);
        dest.writeString(link);
        dest.writeString(month);
        dest.writeString(day);
        dest.writeString(year);
    }

    public static final Parcelable.Creator<Comic> CREATOR = new Parcelable.Creator<Comic>() {

        @Override
        public Comic createFromParcel(Parcel source) {
            return new Comic(source);
        }

        public Comic[] newArray(int size) {
            return new Comic[size];
        }

        ;
    };
}
