package com.saltechdigital.pizzeria.models;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ClientDeserializer implements JsonDeserializer<Client> {
    @Override
    public Client deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Client client = new Client();

        JsonObject jsonObject = json.getAsJsonObject();
        //get a string
        client.setStatut(jsonObject.get("status").getAsBoolean());

        // get a json
        JsonElement ownerJsonElement = jsonObject.get("data");
        JsonObject dataJsonObject = ownerJsonElement.getAsJsonObject();

        if (dataJsonObject.has("nom_client")) {
            client.setNomClient(dataJsonObject.get("nom_client").getAsString());
        }
        if (dataJsonObject.has("tel1_client")) {
            client.setPhone1(dataJsonObject.get("tel1_client").getAsString());
        }
        if (dataJsonObject.has("email_client")) {
            client.setEmailClient(dataJsonObject.get("email_client").getAsString());
        }
        if (dataJsonObject.has("tel2_client")) {
            client.setPhone1(dataJsonObject.get("tel2_client").getAsString());
        }
        if (dataJsonObject.has("adresse")) {
            client.setAdresse(dataJsonObject.get("adresse").getAsString());
        }
        if (dataJsonObject.has("photo_client")) {
            client.setPhoto(dataJsonObject.get("photo_client").getAsString());
        }
        return client;
    }
}
