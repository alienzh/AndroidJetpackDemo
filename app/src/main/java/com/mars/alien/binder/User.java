package com.mars.alien.binder;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private int age;
    private String name;
    private boolean isMale;


    protected User(Parcel in) {
        age = in.readInt();
        name = in.readString();
        isMale = in.readInt() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(age);
        dest.writeString(name);
        dest.writeInt(isMale ? 1 : 0);
    }
}
