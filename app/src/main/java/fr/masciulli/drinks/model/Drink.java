package fr.masciulli.drinks.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Drink implements Parcelable {
    private String name;
    private String imageUrl;
    private String history;
    private String wikipedia;
    private String instructions;
    private List<String> ingredients = new ArrayList<>();

    public Drink(Parcel source) {
        name = source.readString();
        imageUrl = source.readString();
        history = source.readString();
        wikipedia = source.readString();
        instructions = source.readString();
        source.readStringList(ingredients);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(imageUrl);
        dest.writeString(history);
        dest.writeString(wikipedia);
        dest.writeString(instructions);
        dest.writeStringList(ingredients);
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getHistory() {
        return history;
    }

    public String getWikipedia() {
        return wikipedia;
    }

    public String getInstructions() {
        return instructions;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public static final Creator<Drink> CREATOR = new Creator<Drink>() {
        @Override
        public Drink createFromParcel(Parcel source) {
            return new Drink(source);
        }

        @Override
        public Drink[] newArray(int size) {
            return new Drink[size];
        }
    };
}
