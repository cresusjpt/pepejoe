package com.saltechdigital.pizzeria.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class UserDeserializer implements JsonDeserializer<User> {


    @Override
    public User deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        User user = new User();

        JsonObject jsonObject = json.getAsJsonObject();

        user.setStatut(jsonObject.get("status").getAsBoolean());
        user.setMessage(jsonObject.get("message").getAsString());

        JsonElement jsonElement = jsonObject.get("data");
        JsonObject dataJsonObject = jsonElement.getAsJsonObject();

        if (dataJsonObject.has("id_client")) {
            user.setIdClient(dataJsonObject.get("id_client").getAsInt());
        }

        if (dataJsonObject.has("code_agent")) {
            user.setCodeAgent(dataJsonObject.get("code_agent").toString());
        }

        if (dataJsonObject.has("password")) {
            user.setPassword(dataJsonObject.get("password").getAsString());
        }

        if (dataJsonObject.has("nom")) {
            user.setNom(dataJsonObject.get("nom").getAsString());
        }

        if (dataJsonObject.has("email")) {
            user.setEmail(dataJsonObject.get("email").getAsString());
        }

        if (dataJsonObject.has("date_creation")) {
            user.setDateCreation(dataJsonObject.get("date_creation").getAsString());
        }

        if (dataJsonObject.has("contact")) {
            user.setContact(dataJsonObject.get("contact").getAsString());
        }

        if (dataJsonObject.has("auth_key")) {
            user.setAuthKey(dataJsonObject.get("auth_key").getAsString());
        }

        if (dataJsonObject.has("access_token")) {
            user.setAccessToken(dataJsonObject.get("access_token").getAsString());
        }

        if (dataJsonObject.has("actif")) {
            user.setActif(dataJsonObject.get("actif").getAsBoolean());
        }

        if (dataJsonObject.has("photo_user")) {
            user.setUserPhoto(dataJsonObject.get("photo_user").getAsString());
        }

        return user;
    }
}
